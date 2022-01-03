package com.coco.seniasyvoz;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.exifinterface.media.ExifInterface;
// ContentResolver dependency
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.mediapipe.formats.proto.LandmarkProto;
import com.google.mediapipe.solutioncore.CameraInput;
import com.google.mediapipe.solutioncore.SolutionGlSurfaceView;
import com.google.mediapipe.solutioncore.VideoInput;
import com.google.mediapipe.solutions.hands.HandLandmark;
import com.google.mediapipe.solutions.hands.Hands;
import com.google.mediapipe.solutions.hands.HandsOptions;
import com.google.mediapipe.solutions.hands.HandsResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/** Main activity of MediaPipe Hands app. */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private RequestQueue rq;
    private Hands hands;
    private String token;
    // Run the pipeline and the model inference on GPU or CPU.
    private static final boolean RUN_ON_GPU = true;

    private enum InputSource {
        UNKNOWN,
        IMAGE,
        VIDEO,
        CAMERA,
    }
    private InputSource inputSource = InputSource.UNKNOWN;

    // Image demo UI and image loader components.
    private ActivityResultLauncher<Intent> imageGetter;
    private HandsResultImageView imageView;
    // Video demo UI and video loader components.
    private VideoInput videoInput;
    private ActivityResultLauncher<Intent> videoGetter;
    // Live camera demo UI and camera components.
    private CameraInput cameraInput;
    private TextView  funcion, estado, disp;
    private SolutionGlSurfaceView<HandsResult> glSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupLiveDemoUiComponents();
        funcion=findViewById(R.id.funcion);
        disp=findViewById(R.id.disp);
        estado=findViewById(R.id.estado);
        estado.setText("Leyendo ->"+TIPOS[tipo]);

        rq= Volley.newRequestQueue(this);

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        token = task.getResult();

                        Log.d("firebase", token);
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (inputSource == InputSource.CAMERA) {
            // Restarts the camera and the opengl surface rendering.
            cameraInput = new CameraInput(this);
            cameraInput.setNewFrameListener(textureFrame -> hands.send(textureFrame));
            glSurfaceView.post(this::startCamera);
            glSurfaceView.setVisibility(View.VISIBLE);
        } else if (inputSource == InputSource.VIDEO) {
            videoInput.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (inputSource == InputSource.CAMERA) {
            glSurfaceView.setVisibility(View.GONE);
            cameraInput.close();
        } else if (inputSource == InputSource.VIDEO) {
            videoInput.pause();
        }
    }

    private Bitmap downscaleBitmap(Bitmap originalBitmap) {
        double aspectRatio = (double) originalBitmap.getWidth() / originalBitmap.getHeight();
        int width = imageView.getWidth();
        int height = imageView.getHeight();
        if (((double) imageView.getWidth() / imageView.getHeight()) > aspectRatio) {
            width = (int) (height * aspectRatio);
        } else {
            height = (int) (width / aspectRatio);
        }
        return Bitmap.createScaledBitmap(originalBitmap, width, height, false);
    }

    private Bitmap rotateBitmap(Bitmap inputBitmap, InputStream imageData) throws IOException {
        int orientation =
                new ExifInterface(imageData)
                        .getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        if (orientation == ExifInterface.ORIENTATION_NORMAL) {
            return inputBitmap;
        }
        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.postRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.postRotate(180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.postRotate(270);
                break;
            default:
                matrix.postRotate(0);
        }
        return Bitmap.createBitmap(
                inputBitmap, 0, 0, inputBitmap.getWidth(), inputBitmap.getHeight(), matrix, true);
    }

    /** Sets up the UI components for the live demo with camera input. */
    private void setupLiveDemoUiComponents() {
        Button startCameraButton = findViewById(R.id.button_start_camera);
        startCameraButton.setOnClickListener(
                v -> {
                    if (inputSource == InputSource.CAMERA) {
                        return;
                    }
                    stopCurrentPipeline();
                    setupStreamingModePipeline(InputSource.CAMERA);
                });
    }

    /** Sets up core workflow for streaming mode. */
    private void setupStreamingModePipeline(InputSource inputSource) {
        this.inputSource = inputSource;
        // Initializes a new MediaPipe Hands solution instance in the streaming mode.
        hands =
                new Hands(
                        this,
                        HandsOptions.builder()
                                .setStaticImageMode(false)
                                .setMaxNumHands(1)
                                .setRunOnGpu(RUN_ON_GPU)
                                .build());
        hands.setErrorListener((message, e) -> Log.e(TAG, "MediaPipe Hands error:" + message));

        if (inputSource == InputSource.CAMERA) {
            cameraInput = new CameraInput(this);
            cameraInput.setNewFrameListener(textureFrame -> hands.send(textureFrame));

        } else if (inputSource == InputSource.VIDEO) {
            videoInput = new VideoInput(this);
            videoInput.setNewFrameListener(textureFrame -> hands.send(textureFrame));
        }

        // Initializes a new Gl surface view with a user-defined HandsResultGlRenderer.
        glSurfaceView =
                new SolutionGlSurfaceView<>(this, hands.getGlContext(), hands.getGlMajorVersion());
        glSurfaceView.setSolutionResultRenderer(new HandsResultGlRenderer());
        glSurfaceView.setRenderInputImage(true);
        hands.setResultListener(
                handsResult -> {
                    //logWristLandmark(handsResult, /*showPixelValues=*/ false);
                    try {
                        leerSenias(handsResult);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    glSurfaceView.setRenderData(handsResult);
                    glSurfaceView.requestRender();
                });

        // The runnable to start camera after the gl surface view is attached.
        // For video input source, videoInput.start() will be called when the video uri is available.
        if (inputSource == InputSource.CAMERA) {
            glSurfaceView.post(this::startCamera);
        }

        // Updates the preview layout.
        FrameLayout frameLayout = findViewById(R.id.preview_display_layout);
//        imageView.setVisibility(View.GONE);
        frameLayout.removeAllViewsInLayout();
        frameLayout.addView(glSurfaceView);
        glSurfaceView.setVisibility(View.VISIBLE);
        frameLayout.requestLayout();
    }

    private void startCamera() {
        cameraInput.start(
                this,
                hands.getGlContext(),
                CameraInput.CameraFacing.FRONT,
                glSurfaceView.getWidth(),
                glSurfaceView.getHeight());

    }

    private void stopCurrentPipeline() {
        if (cameraInput != null) {
            cameraInput.setNewFrameListener(null);
            cameraInput.close();
        }
        if (videoInput != null) {
            videoInput.setNewFrameListener(null);
            videoInput.close();
        }
        if (glSurfaceView != null) {
            glSurfaceView.setVisibility(View.GONE);
        }
        if (hands != null) {
            hands.close();
        }
    }
    private boolean buscarSenia(int[][] senia, List<LandmarkProto.NormalizedLandmark> lista){
        for(int i=0; i<senia.length;i++){
            switch (senia[i][2]){
                case 0:
                    if((senia[i][3]==1 && lista.get(senia[i][0]).getY()>lista.get(senia[i][1]).getY()) ||
                            (senia[i][3]==0 && lista.get(senia[i][0]).getX()>lista.get(senia[i][1]).getX())){
                        return false;
                    }
                    break;
                case 1:
                    if(!(lista.get(senia[i][0]).getY()+Senias.TOLEREANCIA > lista.get(senia[i][1]).getY() &&
                            lista.get(senia[i][0]).getY()-Senias.TOLEREANCIA < lista.get(senia[i][1]).getY() &&
                            lista.get(senia[i][0]).getX()-Senias.TOLEREANCIA < lista.get(senia[i][1]).getX() &&
                            lista.get(senia[i][0]).getX()+Senias.TOLEREANCIA > lista.get(senia[i][1]).getX())){
                        return false;
                    }
                    break;
                case 2:
                    if(Senias.distancia(lista.get(senia[i][0]),lista.get(senia[i][3]))<Senias.distancia(lista.get(senia[i][1]),lista.get(senia[i][3]))){
                        return false;
                    }
                    break;
            }

        }
        return true;
    }


    private ArrayList<Senias> accionesS=Senias.ACCIONES;
    private ArrayList<Senias> coloresS=Senias.COLORES;
    private ArrayList<Senias> funcionesS=Senias.FUNCIONES;
    private ArrayList<Senias> dispS=Senias.DISP;
    private int tipo = 0;
    private static int TIEMPO=10000;
    private void siguiente(){
        if(tipo>=3){
            tipo=0;
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject obj= new JSONObject();
                    try {
                        obj.put("token", token)
                                .put("disp", disp.getText().toString())
                                .put("comando", funcion.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    enviarDatos(obj);
                    disp.setText("");
                    funcion.setText("");


                }
            });
        }else{
            boolean bandera=true;
            switch (tipo){
                case 0:
                    bandera = !disp.getText().equals("");
                    break;
                case 1:
                    bandera = !funcion.getText().equals("");
                    break;
            }
            tipo=bandera?tipo+1:tipo;
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    estado.setText(tipo<3?"Leyendo ->"+TIPOS[tipo]:"Enviado");

                }
            });
        }
    }
    public final String TIPOS[]={"Dispositivo","Funcion", "Confirmar"};
    public void cancelar(){

                    tipo=0;
                    this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            disp.setText("");
                            funcion.setText("");
                            estado.setText("Leyendo ->"+TIPOS[0]);
                        }
                    });
        }

    private void leerSenias(HandsResult result) throws InterruptedException {
        if (result.multiHandLandmarks().isEmpty()) {
            return;
        }
        List<LandmarkProto.NormalizedLandmark> lista=result.multiHandLandmarks().get(0).getLandmarkList();

        for (Senias s: accionesS) {
            if(buscarSenia(s.senia,lista)){
                if(s.nombre.equals("pulgar_arriba")){
                    siguiente();
                }else if(s.nombre.equals("pulgar_abajo")){
                    cancelar();
                }
                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        (new Handler()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Log.i("","espera");
                            }
                        }, TIEMPO);
                    }
                });
                return;
            }
        }
        if(tipo==0){
            for(Senias s:dispS){
                if(buscarSenia(s.senia,lista)){

                    this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            disp.setText(s.nombre);
                            (new Handler()).postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                }
                            }, TIEMPO);
                        }
                    });

                    return;
                }
            }
        }else if(tipo==1){
            for(Senias s:funcionesS){
                if(buscarSenia(s.senia,lista)){

                    this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            funcion.setText(s.nombre);
                            (new Handler()).postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                }
                            }, TIEMPO);
                        }
                    });

                    return;
                }
            }
        }
    }

    private void enviarDatos(JSONObject request){
        Toast.makeText(this, request.toString(), Toast.LENGTH_SHORT).show();

        String url = "https://irisic.herokuapp.com/tira";
        Log.d("json",request.toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, url, request, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        String res="";
                        try {
                            res=response.getString("respuesta");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(MainActivity.this, res, Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "error en la peticion", Toast.LENGTH_SHORT).show();
                        Log.d("pruevba",error.toString());
                    }
                });
        rq.add(jsonObjectRequest);
    }
    public void pruba(View vw){
        JSONObject obj= new JSONObject();
        try {
            obj.put("token", token)
                    .put("disp", 1)
                    .put("comando", "info");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        enviarDatos(obj);


    }
    private static final int REQ_CALL_SPEECH_INPUT = 100;

    public void iniciarEntradaVoz( View vw){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hola, dime lo que sea");
        try{
            startActivityForResult(intent,REQ_CALL_SPEECH_INPUT);
        }
        catch(ActivityNotFoundException e){

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode){
            case REQ_CALL_SPEECH_INPUT:
                if (resultCode==RESULT_OK && null != data){
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String resultado=result.get(0).toLowerCase();
                    numeros:
                    for (String[] r:Senias.numeros) {
                        for (String n: r) {
                            if(resultado.contains(n)){
                                disp.setText(r[1]);
                                break numeros;
                            }
                        }
                    }
                    fun:
                    for (String[] r:Senias.funciones) {
                        for (String n: r) {
                            if(resultado.contains(n)){
                                funcion.setText(r[0]);
                                break fun;
                            }
                        }
                    }
                    JSONObject obj= new JSONObject();
                    try {
                        obj.put("token", token)
                                .put("disp", disp.getText().toString())
                                .put("comando", funcion.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    enviarDatos(obj);
                    disp.setText("");
                    funcion.setText("");


                }
                break;
        }
    }

}