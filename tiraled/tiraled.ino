#include <Ethernet.h>
#include <MQTT.h>    
uint8_t mac[6] = {0xC4, 0x65, 0x16, 0xB7, 0xB5, 0xF2};
byte ip[] = {192, 168, 100, 160}; 
int rojo=3;
int azul=5;
int verde=6;
int state;
EthernetClient net;
MQTTClient client;

unsigned long lastMillis = 0;

void connect() {
  Serial.print("connecting...");
  while (!client.connect("arduino", "irisled", "VxmO1nMHBJram4Pt")) {
    Serial.print(".");
    delay(1000);
  }

  Serial.println("\nconnected!");

  client.subscribe("arduino");
  // client.unsubscribe("/hello");
}
void apaga(){
  digitalWrite(rojo,LOW);
  digitalWrite(azul,LOW);
  digitalWrite(verde,LOW);
  state=0;
}
void enciende(){
   digitalWrite(rojo,HIGH);
   digitalWrite(azul,HIGH);
   digitalWrite(verde,HIGH);
   state=1;
}
void red(){
  digitalWrite(rojo,HIGH);
  digitalWrite(azul,LOW);
  digitalWrite(verde,LOW);
  state=1;
}
void blue(){
  digitalWrite(rojo,LOW);
  digitalWrite(azul,HIGH);
  digitalWrite(verde,LOW);
  state=1;
}
void green(){
  digitalWrite(rojo,LOW);
  digitalWrite(azul,LOW);
  digitalWrite(verde,HIGH);
  state=1;
}
void fade(){
  state=1;
  for(;;){
    for(int i=0;i>256;i++){
      analogWrite(rojo,i);
      analogWrite(azul,i);
      analogWrite(verde,i);
      delay(50);
    }
    for(int i=255;i>0;i--){
      analogWrite(rojo,i);
      analogWrite(azul,i);
      analogWrite(verde,i);
      delay(50);
    }
  }
}

void messageReceived(String &topic, String &payload) {
  Serial.println("incoming: " + topic + " - " + payload);
  if(payload=="0"){
    client.publish("arduino","Soy el arduino y recibí apagar");
    apaga();
  }
  if(payload=="1"){
    client.publish("arduino","Soy el arduino y recibí encender");
    enciende();
  }
  if(payload=="2"){
    client.publish("arduino","Soy el arduino y recibí rojo");
    red();
  }
  if(payload=="3"){
    client.publish("arduino","Soy el arduino y recibí azul");
    blue();
  }
  if(payload=="4"){
    client.publish("arduino","Soy el arduino y recibí verde");
    green();
  }
  if(payload=="5"){
    client.publish("arduino","Soy el arduino y recibí fade");
    fade();
  }
  if(payload=="000"){
    if(state==1){
      client.publish("arduino","true");
    }
    else{
      client.publish("arduino","false");
    }
  }
}

void setup() {
  Serial.begin(115200);
  Ethernet.begin(mac, ip);
  pinMode(rojo,OUTPUT);
  pinMode(azul,OUTPUT);
  pinMode(verde,OUTPUT);
  // Note: Local domain names (e.g. "Computer.local" on OSX) are not supported
  // by Arduino. You need to set the IP address directly.
  client.begin("irisled.cloud.shiftr.io", net);
  client.onMessage(messageReceived);

  connect();
}

void loop() {
  client.loop();

  if (!client.connected()) {
    connect();
  }

  // publish a message roughly every second.
  /*if (millis() - lastMillis > 1000) {
    lastMillis = millis();
    client.publish("arduino", "world");
  }*/
}
