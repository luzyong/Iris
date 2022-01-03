package com.coco.seniasyvoz;

import com.google.mediapipe.solutions.hands.HandLandmark;
import com.google.mediapipe.formats.proto.LandmarkProto;
import com.google.mediapipe.formats.proto.LandmarkProto.NormalizedLandmark;

import java.lang.Math;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Senias {
  public int[][] senia;
  public String nombre;
    private Senias(int[][] senia, String nombre){
      this.senia=senia;
      this.nombre=nombre;
    }
  /*[menor, mayor]
      [arriba, abajo] - axis y
      [izquierda, derecha] - axis x
      [...,...,tipo] - 0 -> comparar, 1 -> encima, 2 -> dsitancia
      tipo = 0
       [...,...,...,eje] 0 -> X, 1 -> Y
      tipo = 2
       [...,...,...,pivote]
     */
    public static float TOLEREANCIA=0.01f;

  public static ArrayList<Senias> ACCIONES=new ArrayList<>(Arrays.asList(
          new Senias(new int[][]{
          {HandLandmark.THUMB_MCP, HandLandmark.PINKY_TIP,0,1},
          {HandLandmark.THUMB_MCP, HandLandmark.RING_FINGER_TIP,0,1},
          {HandLandmark.THUMB_MCP, HandLandmark.INDEX_FINGER_TIP,0,1},
          {HandLandmark.THUMB_MCP, HandLandmark.WRIST,0,1},
          {HandLandmark.THUMB_TIP, HandLandmark.THUMB_IP,0,1},
          {HandLandmark.THUMB_IP, HandLandmark.THUMB_MCP,0,1}},
                  "pulgar_arriba"),
          new Senias(new int[][]{
                  {HandLandmark.PINKY_TIP,HandLandmark.THUMB_MCP,0,1},
                  {HandLandmark.RING_FINGER_TIP,HandLandmark.THUMB_MCP,0,1},
                  {HandLandmark.INDEX_FINGER_TIP,HandLandmark.THUMB_MCP,0,1},
                  {HandLandmark.WRIST, HandLandmark.THUMB_MCP,0,1},
                  {HandLandmark.THUMB_MCP, HandLandmark.THUMB_IP,0,1},
                  {HandLandmark.THUMB_IP, HandLandmark.THUMB_TIP,0,1}},
                  "pulgar_abajo"),
          new Senias(new int[][]{
          {HandLandmark.MIDDLE_FINGER_TIP,HandLandmark.INDEX_FINGER_DIP,0,1},
          {HandLandmark.RING_FINGER_TIP,HandLandmark.INDEX_FINGER_DIP,0,1},
          {HandLandmark.PINKY_TIP,HandLandmark.INDEX_FINGER_DIP,0,1},

          {HandLandmark.INDEX_FINGER_MCP, HandLandmark.INDEX_FINGER_PIP,0,0},
          {HandLandmark.INDEX_FINGER_PIP, HandLandmark.INDEX_FINGER_DIP,0,0},
          {HandLandmark.INDEX_FINGER_DIP, HandLandmark.INDEX_FINGER_TIP,0,0},

          {HandLandmark.THUMB_CMC, HandLandmark.THUMB_MCP,0,0},
          {HandLandmark.THUMB_MCP, HandLandmark.THUMB_IP,0,0},
          {HandLandmark.THUMB_IP, HandLandmark.THUMB_TIP,0,0}},
          "B")));

  public static ArrayList<Senias> COLORES=new ArrayList<>(Arrays.asList(

  ));

  public static ArrayList<Senias> FUNCIONES=new ArrayList<>(Arrays.asList(
          new Senias(new int[][]{
                  {HandLandmark.PINKY_DIP,HandLandmark.INDEX_FINGER_TIP,0,1},
                  {HandLandmark.PINKY_DIP,HandLandmark.MIDDLE_FINGER_TIP,0,1},
                  {HandLandmark.PINKY_DIP,HandLandmark.RING_FINGER_TIP,0,1},
                  {HandLandmark.THUMB_TIP,HandLandmark.THUMB_IP,0,1},
                  {HandLandmark.PINKY_TIP, HandLandmark.PINKY_DIP,0,1},
                  {HandLandmark.THUMB_TIP, HandLandmark.INDEX_FINGER_TIP,0,0},
                  {HandLandmark.INDEX_FINGER_TIP, HandLandmark.MIDDLE_FINGER_TIP,0,0}},
                  "apagado"),
          new Senias(new int[][]{
                  {HandLandmark.PINKY_DIP,HandLandmark.INDEX_FINGER_TIP,0,1},
                  {HandLandmark.PINKY_DIP,HandLandmark.MIDDLE_FINGER_TIP,0,1},
                  {HandLandmark.PINKY_DIP,HandLandmark.RING_FINGER_TIP,0,1},
                  {HandLandmark.THUMB_TIP,HandLandmark.THUMB_IP,0,1},
                  {HandLandmark.PINKY_TIP, HandLandmark.PINKY_DIP,0,1},
                  {HandLandmark.MIDDLE_FINGER_TIP, HandLandmark.INDEX_FINGER_TIP,0,0},
                  {HandLandmark.INDEX_FINGER_TIP, HandLandmark.THUMB_TIP,0,0}},
                  "apagado"),
          new Senias(new int[][]{
                  {HandLandmark.INDEX_FINGER_TIP,HandLandmark.INDEX_FINGER_DIP,0,1},
                  {HandLandmark.MIDDLE_FINGER_TIP,HandLandmark.MIDDLE_FINGER_DIP,0,1},
                  {HandLandmark.RING_FINGER_TIP,HandLandmark.RING_FINGER_DIP,0,1},
                  {HandLandmark.PINKY_TIP, HandLandmark.PINKY_DIP,0,1},
                  {HandLandmark.THUMB_TIP, HandLandmark.THUMB_IP,0,1},
                  {HandLandmark.THUMB_TIP, HandLandmark.INDEX_FINGER_MCP,0,0},
                  {HandLandmark.INDEX_FINGER_MCP, HandLandmark.MIDDLE_FINGER_MCP,0,0}},
                  "encendido"),
          new Senias(new int[][]{
                  {HandLandmark.INDEX_FINGER_TIP,HandLandmark.INDEX_FINGER_DIP,0,1},
                  {HandLandmark.MIDDLE_FINGER_TIP,HandLandmark.MIDDLE_FINGER_DIP,0,1},
                  {HandLandmark.RING_FINGER_TIP,HandLandmark.RING_FINGER_DIP,0,1},
                  {HandLandmark.PINKY_TIP, HandLandmark.PINKY_DIP,0,1},
                  {HandLandmark.THUMB_TIP, HandLandmark.THUMB_IP,0,1},

                  {HandLandmark.INDEX_FINGER_MCP, HandLandmark.THUMB_TIP,0,0},
                  {HandLandmark.MIDDLE_FINGER_MCP, HandLandmark.INDEX_FINGER_MCP,0,0}},
                  "encendido"),
          new Senias(new int[][]{
                  {HandLandmark.PINKY_DIP,HandLandmark.INDEX_FINGER_TIP,0,1},
                  {HandLandmark.PINKY_DIP,HandLandmark.MIDDLE_FINGER_TIP,0,1},
                  {HandLandmark.PINKY_DIP,HandLandmark.RING_FINGER_TIP,0,1},
                  {HandLandmark.THUMB_TIP,HandLandmark.THUMB_IP,0,1},
                  {HandLandmark.PINKY_TIP, HandLandmark.PINKY_DIP,0,1},
                  {HandLandmark.INDEX_FINGER_TIP, HandLandmark.THUMB_TIP,0,0},
                  {HandLandmark.INDEX_FINGER_TIP, HandLandmark.MIDDLE_FINGER_TIP,0,0}},
                  "info"),
          new Senias(new int[][]{
                  {HandLandmark.PINKY_DIP,HandLandmark.INDEX_FINGER_TIP,0,1},
                  {HandLandmark.PINKY_DIP,HandLandmark.MIDDLE_FINGER_TIP,0,1},
                  {HandLandmark.PINKY_DIP,HandLandmark.RING_FINGER_TIP,0,1},
                  {HandLandmark.THUMB_TIP,HandLandmark.THUMB_IP,0,1},
                  {HandLandmark.PINKY_TIP, HandLandmark.PINKY_DIP,0,1},
                  {HandLandmark.MIDDLE_FINGER_TIP, HandLandmark.INDEX_FINGER_TIP,0,0},
                  {HandLandmark.THUMB_TIP, HandLandmark.INDEX_FINGER_TIP,0,0}
                  },
                  "info"),

          new Senias(new int[][]{
                  {HandLandmark.MIDDLE_FINGER_TIP,HandLandmark.MIDDLE_FINGER_DIP,0,1},
                  {HandLandmark.MIDDLE_FINGER_DIP,HandLandmark.MIDDLE_FINGER_PIP,0,1},
                  {HandLandmark.RING_FINGER_TIP,HandLandmark.RING_FINGER_DIP,0,1},
                  {HandLandmark.RING_FINGER_DIP,HandLandmark.RING_FINGER_PIP,0,1},
                  {HandLandmark.PINKY_TIP,HandLandmark.PINKY_DIP,0,1},
                  {HandLandmark.PINKY_DIP,HandLandmark.PINKY_PIP,0,1},

                  {HandLandmark.INDEX_FINGER_TIP,HandLandmark.THUMB_TIP,0,1},
                  {HandLandmark.THUMB_TIP, HandLandmark.THUMB_IP,0,1},
                  {HandLandmark.THUMB_IP, HandLandmark.THUMB_CMC,0,1},
                  {HandLandmark.INDEX_FINGER_PIP,HandLandmark.INDEX_FINGER_MCP,0,1},
                  {HandLandmark.INDEX_FINGER_PIP, HandLandmark.INDEX_FINGER_DIP,0,1},
                  {HandLandmark.INDEX_FINGER_DIP, HandLandmark.INDEX_FINGER_TIP,0,1},

                  {HandLandmark.INDEX_FINGER_TIP, HandLandmark.INDEX_FINGER_DIP,0,0},
                  {HandLandmark.INDEX_FINGER_DIP, HandLandmark.INDEX_FINGER_PIP,0,0},
                  {HandLandmark.INDEX_FINGER_PIP, HandLandmark.INDEX_FINGER_MCP,0,0},
                  {HandLandmark.INDEX_FINGER_MCP, HandLandmark.MIDDLE_FINGER_MCP,0,0},
                  {HandLandmark.THUMB_TIP, HandLandmark.THUMB_IP,0,0},
                  {HandLandmark.THUMB_IP, HandLandmark.THUMB_MCP,0,0}
          },
                  "fade"),
          new Senias(new int[][]{
                  {HandLandmark.MIDDLE_FINGER_TIP,HandLandmark.MIDDLE_FINGER_DIP,0,1},
                  {HandLandmark.MIDDLE_FINGER_DIP,HandLandmark.MIDDLE_FINGER_PIP,0,1},
                  {HandLandmark.RING_FINGER_TIP,HandLandmark.RING_FINGER_DIP,0,1},
                  {HandLandmark.RING_FINGER_DIP,HandLandmark.RING_FINGER_PIP,0,1},
                  {HandLandmark.PINKY_TIP,HandLandmark.PINKY_DIP,0,1},
                  {HandLandmark.PINKY_DIP,HandLandmark.PINKY_PIP,0,1},

                  {HandLandmark.INDEX_FINGER_TIP,HandLandmark.THUMB_TIP,0,1},
                  {HandLandmark.THUMB_TIP, HandLandmark.THUMB_IP,0,1},
                  {HandLandmark.THUMB_IP, HandLandmark.THUMB_CMC,0,1},
                  {HandLandmark.INDEX_FINGER_PIP,HandLandmark.INDEX_FINGER_MCP,0,1},
                  {HandLandmark.INDEX_FINGER_PIP, HandLandmark.INDEX_FINGER_DIP,0,1},
                  {HandLandmark.INDEX_FINGER_DIP, HandLandmark.INDEX_FINGER_TIP,0,1},

                  { HandLandmark.INDEX_FINGER_DIP,HandLandmark.INDEX_FINGER_TIP,0,0},
                  { HandLandmark.INDEX_FINGER_PIP,HandLandmark.INDEX_FINGER_DIP,0,0},
                  { HandLandmark.INDEX_FINGER_MCP,HandLandmark.INDEX_FINGER_PIP,0,0},
                  { HandLandmark.MIDDLE_FINGER_MCP,HandLandmark.INDEX_FINGER_MCP,0,0},
                  { HandLandmark.THUMB_IP,HandLandmark.THUMB_TIP,0,0},
                  { HandLandmark.THUMB_MCP,HandLandmark.THUMB_IP,0,0}
          },
                  "fade"),
          new Senias(new int[][]{
                  {HandLandmark.INDEX_FINGER_TIP,HandLandmark.INDEX_FINGER_DIP,0,1},
                  {HandLandmark.MIDDLE_FINGER_TIP,HandLandmark.MIDDLE_FINGER_DIP,0,1},
                  {HandLandmark.RING_FINGER_TIP,HandLandmark.RING_FINGER_DIP,0,1},
                  {HandLandmark.PINKY_TIP, HandLandmark.PINKY_DIP,0,1},
                  {HandLandmark.THUMB_TIP, HandLandmark.THUMB_IP,0,1},
                  {HandLandmark.INDEX_FINGER_MCP, HandLandmark.THUMB_TIP,0,0},
                  {HandLandmark.INDEX_FINGER_MCP, HandLandmark.MIDDLE_FINGER_MCP,0,0}},
                  "azul"),
          new Senias(new int[][]{
                  {HandLandmark.INDEX_FINGER_TIP,HandLandmark.INDEX_FINGER_DIP,0,1},
                  {HandLandmark.MIDDLE_FINGER_TIP,HandLandmark.MIDDLE_FINGER_DIP,0,1},
                  {HandLandmark.RING_FINGER_TIP,HandLandmark.RING_FINGER_DIP,0,1},
                  {HandLandmark.PINKY_TIP, HandLandmark.PINKY_DIP,0,1},
                  {HandLandmark.THUMB_TIP, HandLandmark.THUMB_IP,0,1},
                  {HandLandmark.THUMB_TIP, HandLandmark.INDEX_FINGER_MCP,0,0},
                  {HandLandmark.MIDDLE_FINGER_MCP, HandLandmark.INDEX_FINGER_MCP,0,0}},
                  "azul"),
          new Senias(new int[][]{
                  {HandLandmark.THUMB_CMC, HandLandmark.WRIST,0,1},
                  {HandLandmark.PINKY_PIP, HandLandmark.PINKY_DIP,0,1},
                  {HandLandmark.PINKY_DIP, HandLandmark.PINKY_TIP,0,1},
                  {HandLandmark.RING_FINGER_PIP, HandLandmark.RING_FINGER_DIP,0,1},
                  {HandLandmark.RING_FINGER_DIP, HandLandmark.RING_FINGER_TIP,0,1},
                  {HandLandmark.MIDDLE_FINGER_PIP, HandLandmark.MIDDLE_FINGER_MCP,0,1},
                  {HandLandmark.INDEX_FINGER_PIP, HandLandmark.INDEX_FINGER_MCP,0,1},

                  {HandLandmark.MIDDLE_FINGER_TIP, HandLandmark.INDEX_FINGER_TIP,0,0},
                  {HandLandmark.INDEX_FINGER_PIP, HandLandmark.MIDDLE_FINGER_PIP,0,0}},
                  "rojo"),
          new Senias(new int[][]{
                  {HandLandmark.THUMB_CMC, HandLandmark.WRIST,0,1},
                  {HandLandmark.PINKY_PIP, HandLandmark.PINKY_DIP,0,1},
                  {HandLandmark.PINKY_DIP, HandLandmark.PINKY_TIP,0,1},
                  {HandLandmark.RING_FINGER_PIP, HandLandmark.RING_FINGER_DIP,0,1},
                  {HandLandmark.RING_FINGER_DIP, HandLandmark.RING_FINGER_TIP,0,1},
                  {HandLandmark.MIDDLE_FINGER_PIP, HandLandmark.MIDDLE_FINGER_MCP,0,1},
                  {HandLandmark.INDEX_FINGER_PIP, HandLandmark.INDEX_FINGER_MCP,0,1},

                  {HandLandmark.INDEX_FINGER_TIP, HandLandmark.MIDDLE_FINGER_TIP,0,0},
                  {HandLandmark.MIDDLE_FINGER_PIP, HandLandmark.INDEX_FINGER_PIP,0,0}},
                  "rojo"),
          new Senias(new int[][]{
                  {HandLandmark.INDEX_FINGER_TIP,HandLandmark.INDEX_FINGER_DIP,0,1},
                  {HandLandmark.INDEX_FINGER_DIP,HandLandmark.INDEX_FINGER_PIP,0,1},
                  {HandLandmark.INDEX_FINGER_PIP,HandLandmark.PINKY_TIP,0,1},
                  {HandLandmark.INDEX_FINGER_PIP, HandLandmark.RING_FINGER_TIP,0,1},
                  {HandLandmark.MIDDLE_FINGER_TIP, HandLandmark.MIDDLE_FINGER_DIP,0,1},
                  {HandLandmark.MIDDLE_FINGER_DIP, HandLandmark.MIDDLE_FINGER_PIP,0,1} },
                  "verde")
  ));

  public static ArrayList<Senias> DISP=new ArrayList<>(Arrays.asList(
          new Senias(new int[][]{
                  {HandLandmark.INDEX_FINGER_TIP,HandLandmark.INDEX_FINGER_DIP,0,1},
                  {HandLandmark.INDEX_FINGER_DIP,HandLandmark.INDEX_FINGER_PIP,0,1},
                  {HandLandmark.INDEX_FINGER_PIP,HandLandmark.PINKY_TIP,0,1},
                  {HandLandmark.INDEX_FINGER_PIP, HandLandmark.RING_FINGER_TIP,0,1},
                  {HandLandmark.INDEX_FINGER_PIP, HandLandmark.MIDDLE_FINGER_TIP,0,1}},
                  "1"),
          new Senias(new int[][]{
                  {HandLandmark.INDEX_FINGER_TIP,HandLandmark.INDEX_FINGER_DIP,0,1},
                  {HandLandmark.INDEX_FINGER_DIP,HandLandmark.INDEX_FINGER_PIP,0,1},
                  {HandLandmark.INDEX_FINGER_PIP,HandLandmark.PINKY_TIP,0,1},
                  {HandLandmark.INDEX_FINGER_PIP, HandLandmark.RING_FINGER_TIP,0,1},
                  {HandLandmark.MIDDLE_FINGER_TIP, HandLandmark.MIDDLE_FINGER_DIP,0,1},
                  {HandLandmark.MIDDLE_FINGER_DIP, HandLandmark.MIDDLE_FINGER_PIP,0,1} },
                  "2")
  ));
    public static float distancia(NormalizedLandmark punto1, NormalizedLandmark punto2){
      return (float) Math.sqrt(Math.pow(punto1.getX()-punto2.getX(), 2)+Math.pow(punto1.getY()-punto2.getY(), 2));
    }

    public static String[][] funciones={{"rojo"},{"azul"}, {"verde"}, {"fade","desvanecer"}, {"encendido","encender"},{"apagado","apagar"}, {"info","información","informacion"}};
  public static String[][] numeros={ {"uno","1"}, {"dos","2"},{"tres","3"}};
}
