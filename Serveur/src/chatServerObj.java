import java.net.*;
import java.util.*;
import java.io.*;
import com.example.a92gde.chatapp.*;


class chatServerObj {

	static Vector ClientSockets;
	static Vector LoginNames;
	static final int SERVER_PORT = 8010;
	
	
	HashMap<String, ObjectInputStream> objectInputStreams = new HashMap<String, ObjectInputStream>();
	HashMap<String, ObjectOutputStream> objectOutputStreams = new HashMap<String, ObjectOutputStream>();

	chatServerObj() throws Exception {

		ServerSocket soc = new ServerSocket(SERVER_PORT);

		ClientSockets = new Vector();
		LoginNames = new Vector();

		while(true) {  
			// accept client connection
			Socket cSocket = soc.accept();        
			AcceptClient objectClient = new AcceptClient(cSocket);

		}
	}

	public static void main(String args[]) throws Exception {

		chatServerObj chatServer = new chatServerObj();
	}

	// inner class
	class AcceptClient extends Thread {

		Socket ClientSocket;

		
		ObjectInputStream objectInputStream;
		ObjectOutputStream objectOutputStream;

		String LoginName;
		
		AcceptClient (Socket CSoc) {

			try {

				ClientSocket = CSoc;

				// get the input stream from the connected socket
				InputStream inputStream = ClientSocket.getInputStream();

				// get the output stream from the socket.
				OutputStream outputStream = ClientSocket.getOutputStream();
			

				if (inputStream != null && outputStream != null) {
					
					// create an object output/input stream from the output stream so we can send/receive an object through it
					objectOutputStream = new ObjectOutputStream(outputStream);
					objectInputStream = new ObjectInputStream(inputStream);
					
	
					LoginName = objectInputStream.readObject().toString();
					
					objectOutputStreams.put(LoginName, objectOutputStream);
					objectInputStreams.put(LoginName, objectInputStream);
			
					System.out.println("User Logged In :" + LoginName);

					LoginNames.add(LoginName);
					ClientSockets.add(ClientSocket); 
					


					for (int iCount = 0; iCount<LoginNames.size(); iCount++) {

						String msg = "User " + LoginName +" joined the chatroom.";				
						Socket tSoc = (Socket)ClientSockets.elementAt(iCount);      
					
						objectOutputStreams.get(LoginNames.get(iCount)).writeObject(msg);
						objectOutputStreams.get(LoginNames.get(iCount)).reset();		

					}
					
			
					start(); // calls the run method 
					
					
				} else {
					System.out.println("streams empty");
				}
				
			} catch (IOException | ClassNotFoundException e) {

				e.printStackTrace();
			}
			
			

		}

		public void run() {
			
	
			while (true) {

				try {
					
					
					String msgFromClient = new String();
					
					Object objectFromClient = objectInputStreams.get(LoginName).readObject(); // could be a gesture
							
					msgFromClient = objectFromClient.toString();
					
		
					StringTokenizer st = new StringTokenizer(msgFromClient);
					
					String firstItem = "";
					String MsgType = "";
					
					if (st.hasMoreTokens()) 
					firstItem = st.nextToken();   
					
					if (st.hasMoreTokens()) 
					MsgType = st.nextToken();

					int iCount = 0;

					if (MsgType.equals("LOGOUT")) {

						for (iCount = 0; iCount<LoginNames.size(); iCount++) {


							if (LoginNames.elementAt(iCount).equals(firstItem)) {

								LoginNames.removeElementAt(iCount);
								ClientSockets.removeElementAt(iCount);
								System.out.println("User " + firstItem +" Logged Out ...");

								break;

							}

						}

						// broadcast the logout message 
						for (iCount = 0; iCount<LoginNames.size(); iCount++) {			

							String msg="User " + firstItem +" Logged Out ...";

							Socket tSoc=(Socket)ClientSockets.elementAt(iCount);   
						
							objectOutputStreams.get(LoginNames.get(iCount)).writeObject(msg);
							objectOutputStreams.get(LoginNames.get(iCount)).reset();
						
						}

					} else if (MsgType.equals("DATA")) {

						String msg = firstItem+ " says: ";

						while(st.hasMoreTokens()) {
							msg = msg +" " +st.nextToken();
						}

						for (iCount=0;iCount<LoginNames.size();iCount++) {

							if(!LoginNames.elementAt(iCount).equals(firstItem)) { 

								Socket tSoc=(Socket)ClientSockets.elementAt(iCount); 
							
								objectOutputStreams.get(LoginNames.get(iCount)).writeObject(msg);
								objectOutputStreams.get(LoginNames.get(iCount)).reset();
								
								//tdout.writeUTF(msg);                            

							}
						}


					} else {
						
						System.out.println("Received: " + firstItem);
						
						System.out.println("The class of the object is: " + objectFromClient.getClass());
						
						Gesture receivedGesture = new Gesture((Gesture)objectFromClient);
						
						System.out.println("The colors of the received gesture is: " + receivedGesture.getColor());
						
						for (iCount=0;iCount<LoginNames.size();iCount++) {

							if(!LoginNames.elementAt(iCount).equals(firstItem)) { 

								Socket tSoc=(Socket)ClientSockets.elementAt(iCount); 
							
								objectOutputStreams.get(LoginNames.get(iCount)).writeObject(receivedGesture);
								objectOutputStreams.get(LoginNames.get(iCount)).reset();
								
								//tdout.writeUTF(msg);                            

							}
						}

						
						
					}

					if (MsgType.equals("LOGOUT")) {
						break;
					}

				}

				catch(Exception ex) {

					ex.printStackTrace();

				}

			}    

		}

	}

}



