# Iris
## Descripción
Iris es una solución de internet de las cosas. Controla un aurduino (tira led en este caso) por medio de un backend, conéctandose ambos al protocolo MQTT.
El arduino es controlado por una aplicación Android (versión 9 en adelante) a través de comandos de voz o por señas.
Las señas se identifican a través de MediaPipe de Google, lo que nos permite asignarle un significado a cada una de ellas y enviar la instrucción al backend.
La aplicación es capaz de recibir notificaciones push a través del servicio de Firebase Cloud Message de google.

## Conexión a servicios de firebase
Para conectar tanto la app como el backend a los servicios de firebase, es necesario registrar la app android en firebase y descargar el archivo de configuración google-services.json. Este archivo debe estar en la carpeta gradle de android y en el mismo directorio dentro del backend.
Asimismo, es necesario agregar el firebase admin sdk descargando el archivo que se genera en el ícono de settings que está a un lado de home y "Descripción general" de la consola de firebase. Una vez ahí, seleccionaremos la opción "Configuración del proyecto" y después "Cuentas de servicio". Debemos seleccionar nuestro lenguaje de programación y en la parte de abajo estará un botón que dirá "Generar clave privada" que debemos seleccionar para que nos descargue el archivo. Por comodidad, se le cambia el nombre a ese archivo y en el código de notificacion.js se lee como "noti.json". Puede cambiarle el nombre a gusto personal.

## Demostración
[![Alt text](https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRogOIELYbQZXOUA0orWgEHZeyZPf5DxAgbPLBRcyTwUCmAqnIX62c5CwYJnZLxWf6KpLE&usqp=CAU)](https://www.youtube.com/watch?v=GkUESsxwdN4)

## Colaboradores
Luz Yong <br>
[Joshua Soria](https://github.com/JoshJSL)<br>
[Francisco Vera](https://github.com/VR-Francisco)
