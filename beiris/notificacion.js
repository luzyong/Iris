const admin = require("firebase-admin");


function initFirebase() {
    const serviceAccount = require('./noti.json');
    admin.initializeApp({
        credential: admin.credential.cert(serviceAccount),
    });
}

initFirebase();

async function sendPushToOneUser(notification) {

    const message = {
        token: notification.tokenId,
        data: {
            titulo: notification.titulo,
            mensaje: notification.mensaje,
            imagen: notification.imagen,
            click_action: notification.accion,
            url: notification.url,
        }
        
    }
    console.log('hola',message);
    
    await sendMessage(message);
    await sleep(1000)
   
    
}

function sendPushToTopic(notification) {
    const message = {
        topic: notification.topic,
        data: {
            titulo: notification.titulo,
            mensaje: notification.mensaje
        }
    }
    sendMessage(message);
}

module.exports = { sendPushToOneUser, sendPushToTopic }

function sendMessage(message) {
    admin.messaging().send(message)
        .then((response) => {
            // Response is a message ID string.
            console.log('Successfully sent message:', response);
        })
        .catch((error) => {
            console.log('Error sending message:', error);
        })
}
function sleep(ms) {
    return new Promise((resolve) => {
      setTimeout(resolve, ms);
    });
  }