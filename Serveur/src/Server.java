import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.sql.*;


// import java.nio.ByteBuffer;
import javax.imageio.ImageIO;

public class Server {

	private static final int SERVER_PORT = 8010;
	private static final String separator = ";";
	private static final String endSeparator = "!";

	public static void main(String[] args) throws Exception {

		try {

			ServerSocket serverSocket = new ServerSocket(Server.SERVER_PORT);
			System.out.println("SERVER STARTED");
			System.out.println("Waiting for client...");

			while (true) {

				Socket socket = serverSocket.accept();
				System.out.println("Connection to exchange messages with client accepted");
				InputStream inputStream = (InputStream) socket.getInputStream();

				if (inputStream != null) {
					System.out.println("An inputstream has been established");
				}

				BufferedReader ins = new BufferedReader(new InputStreamReader(inputStream)); // this stream will be used to read from client

				System.out.println("Local trace: ");
				String clientMessage = ins.readLine(); // reading the first message : the code 

				if (clientMessage != null) {

					if(clientMessage.equals("STOP")) {
						
						System.out.println(clientMessage);
						ins.close();
						inputStream.close();
						break;

					} else if (clientMessage.startsWith("MSGREQUEST")) {

						System.out.println("CODE : "+ clientMessage);
						System.out.println("Closing the current inputstream");

						ins.close();
						inputStream.close();

						Socket messageSendingSocket = serverSocket.accept();
						System.out.println("Connection to send a message accepted");

						PrintWriter outs = new PrintWriter(new BufferedWriter(
								new OutputStreamWriter(messageSendingSocket.getOutputStream())), true); // this stream will be used to write to client

						String serverMessage = "This is a SERVER message in a bottle"; 

						System.out.println("Message sent to client: " + serverMessage);
						System.out.println("Closing the current outputstream");
						outs.println(serverMessage); 

						outs.flush();
						outs.close();
				
						ins.close();
						inputStream.close();


					} 	else if (clientMessage.startsWith("LOGINREQUEST")) {

						String username;
						
						if (clientMessage.split(";").length == 2) {
							username = clientMessage.split(";")[1];
						
						} else { // If no username was given
							username = "";

						}

						System.out.println("Received by client LOGINREQUEST: username = "+username);

						String result = "OK";

						Socket messageSendingSocket = serverSocket.accept();
					
						PrintWriter outs = new PrintWriter(new BufferedWriter(
								new OutputStreamWriter(messageSendingSocket.getOutputStream())), true); // this stream will be used to write to client

						System.out.println("Server's result: "+result);
						System.out.println("Closing the current outputstream");
						outs.println(result); 

						outs.flush();
						outs.close();

						ins.close();
						inputStream.close();


					} else {
						
						ins.close();
						System.out.println("Message sent by client: "+ clientMessage);
						System.out.println("Closing the current inputstream");
					}

				} else {
					 
						System.out.println("The message is null"+ clientMessage);
					
				}

			}

			serverSocket.close();

		} catch (IOException e) {

			throw new IOException("Server failed: ",e);

		}
		
	}

}

//mettre le projet sur ton git + partager + leur dire comment faire pour avoir le serveur en local 




