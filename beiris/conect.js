var express = require('express');
var app = express();
const bodyParser = require('body-parser');
var mqtt = require("mqtt");
const Notification = require("./notificacion");
var client = mqtt.connect("mqtt://irisled:VxmO1nMHBJram4Pt@irisled.cloud.shiftr.io");
const PORT = process.env.PORT || 4000;
const encendido1 = ["Información","Tu dispositivo 1 está encendido","on"];
const apagado1 = ["Información","Tu dispositivo 1 está apagado", "off"]
var tok

app.use(bodyParser.json());

app.get('/', function (req,res) { 
    res.send('Hola')
});

app.post('/tira',(req,res)=>{
    var json={
        "respuesta":"enviado"
    }
    var instruccion = req.body.comando
    tok = req.body.token
    switch(instruccion){
        case 'apagado':
            client.publish("arduino", '0')
            client.end
            res.json(json)
            break;
        case 'encendido':
            client.publish("arduino", '1')
            client.end
            res.json(json)
            break;
        case 'rojo':
            client.publish("arduino", '2')
            client.end
            res.json(json)
            break;
        case 'azul':
            client.publish("arduino", '3')
            client.end
            res.json(json)
            break;
        case 'verde':
            client.publish("arduino", '4')
            client.end
            res.json(json)
            break;
      case 'fade':
            client.publish("arduino", '5')
            client.end
            res.json(json)
            break;
      case 'info':
            client.publish("arduino","000")
            client.end
            res.json(json)
            break;
    }

});
 
function EventoConectar() {
    client.subscribe("arduino", function (err) {
      if (!err) {
        client.publish("arduino", "Soy el servidor Node.js. A la orden pal desorden");
      }
    });
}
  
function EventoMensaje(topic, message) {
    if (topic == "arduino" && message == "true") {
      console.log("La instruccion es " + message.toString());
      enviarNotificacion(encendido1)
    }
    if(topic == "arduino" && message == "false"){
        enviarNotificacion(apagado1)
    }
}
  
async function enviarNotificacion (noti)  {
    control=false;
    const data = {
        tokenId: tok,                
        titulo: noti[0],
        mensaje: noti[1],
        imagen: noti[2],
        accion: "FLUTTER_NOTIFICATION_CLICK",
        url: "https://www.youtube.com/watch?v=BLBWAA14hf4&list=PL_SdewT3l4zu7a6txwr838vOAGHnyeppV&index=4"
    }
    //Método para hacer la conexión a Firebase Cloud Message y enviar la notificación al dispositivo
    Notification.sendPushToOneUser(data);
    
            
}

client.on("connect", EventoConectar);
client.on("message", EventoMensaje);

app.listen(PORT, () => console.log(`Server running on port ${PORT}`));