# Touch-message chat application

## Server

In the Android project (Module Android) go to app/src/main/java/...chatapp/DataExchangingActivity.java, you need to change the IP address according to your computer's IP address, modify the line: 
private static final String SERVER_IP = "192.168.1.64"; // this line should be changed to connect to your local server
The port used by the application is SERVER_PORT = 8010. You could verify it is not being used. 
Afterwards you can run the java server located in the file Serveur (run chatServerObj.java, for instance from Eclipse IDE, or by using the command line). Then you can run the Android application.

## Android application:

Use at least 2 emulators or devices, the chat application is designed to host a chatroom where at least 2 users interact. 

The clients’ messages are written by the users through the chat interface as a standard chat application. You can type messages thanks to your keyboard (appearing when taping on the text field). You can send you messages with the button “send” on the right of the text field.
On the left, you can find two buttons: a hand and a circular row. The row will let you replay the last gesture received (only once). The hand button will take you to the gesture menu where you can decide to go to the gestures library or draw your own gesture. On this screen you can also see an information button that takes you to an information screen, explaining how to draw a gesture. 
In the library, you can find two predefined gestures (slap and caress). When choosing one, the gesture will be drawn on your screen and then sent to your interlocutor.
In the drawing gesture mode, you first have to choose a color (blue, red, or green) and tap the “OK” button. Then a transparent grid appears on your screen and you can begin to draw. After releasing the pressure of your finger on the screen, the gesture is sent automatically and you come back to the chat screen.

Keep in mind that the “back” button located on the bottom of your phone/emulator will log you out from the chatroom, do not use it to go back to the last interface/view. 

IMPORTANT: to end the client-server application, first click on the back button of the devices/emulators to log out, then stop the java server (then, if you are using emulators on Android Studio, you can stop running the Android application). If you do otherwise, for instance, stop running the Android application before logging out and stopping the server, you could block the server’s port for future communication. 
