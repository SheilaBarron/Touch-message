# Touch-message chat application

To use the server: In the Android project (app->java->...chatapp->ClientActivity you need to change the ip address according to your computer's address: 
    private static final String SERVER_IP = "192.168.1.64"; // this line should be changed to connect to your local server
    
Afterwards you can run the java server, then you can run the Android application.
The client's message is written by the user through the application. In this simple chat application the default message sent by the server in response to the client's message is: "This is a server message in a bottle" (the server's message is a default one and cannot be edited).
