import java.net.*;
import java.util.*;
import java.io.*;
import com.example.a92gde.chatapp.*;


class chatServerObj {

	static Vector clientSockets; // keeps track of the different clients' socket connections
	static Vector loginNames; // keeps track of the usernames of the clients who joined the chatroom
	static final int SERVER_PORT = 8010; // server's port must be the same on the client side
	
	// maps that associate a client's username to their streams 
	HashMap<String, ObjectInputStream> objectInputStreams = new HashMap<String, ObjectInputStream>();
	HashMap<String, ObjectOutputStream> objectOutputStreams = new HashMap<String, ObjectOutputStream>();

	// server's constructor
	chatServerObj() throws Exception {

		// initialization
		ServerSocket soc = new ServerSocket(SERVER_PORT);
		clientSockets = new Vector();
		loginNames = new Vector();

		// accept client connection
		while(true) {  
			Socket cSocket = soc.accept();        
			AcceptClient objectClient = new AcceptClient(cSocket);

		}
	}

	public static void main(String args[]) throws Exception {

		// creation of a new server -> call to it's constructor
		chatServerObj chatServer = new chatServerObj();
	}

	// thread inner class, one thread per connection with a single client 
	class AcceptClient extends Thread {

		// for a single client
		Socket ClientSocket;
		ObjectInputStream objectInputStream;
		ObjectOutputStream objectOutputStream;
		String LoginName;
		
		// constructor 
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
					
					// when the user logs in  
					LoginName = objectInputStream.readObject().toString();
					
					objectOutputStreams.put(LoginName, objectOutputStream);
					objectInputStreams.put(LoginName, objectInputStream);
			
					System.out.println("User Logged In :" + LoginName);

					// add the login to the loginNames vector
					// add the socket to the clientSockets vector 
					loginNames.add(LoginName);
					clientSockets.add(ClientSocket); 
					

					// display the "user logged in" message for all users
					for (int iCount = 0; iCount<loginNames.size(); iCount++) {

						String msg = "User " + LoginName +" joined the chatroom.";				
						Socket tSoc = (Socket)clientSockets.elementAt(iCount);      
					
						objectOutputStreams.get(loginNames.get(iCount)).writeObject(msg);
						objectOutputStreams.get(loginNames.get(iCount)).reset();		

					}
					
					// call the run method of the thread to handle the user's communication
					start(); 
					
					
				} else {
					System.out.println("streams are empty");
				}
				
			} catch (IOException | ClassNotFoundException e) {

				e.printStackTrace();
			}
			

		}

		public void run() {
			
			// listen to the user 
			while (true) {

				try {
					
					
					String msgFromClient = new String();
					
					// object received from user 
					Object objectFromClient = objectInputStreams.get(LoginName).readObject(); // could be a text message (String) or a gesture (Object)
					// convert the object to a String 		
					// A String text message will have the structure username + KEYWORD + textMessage
					// else the String will correspond to the serialized Gesture object 
					msgFromClient = objectFromClient.toString();
					
					StringTokenizer st = new StringTokenizer(msgFromClient);
					String firstItem = "";
					String MsgType = "";
					
					
					if (st.hasMoreTokens()) 
					firstItem = st.nextToken();   
					
					if (st.hasMoreTokens()) 
					MsgType = st.nextToken();

					int iCount = 0;

					// if the keyword is LOGOUT
					if (MsgType.equals("LOGOUT")) {

						for (iCount = 0; iCount<loginNames.size(); iCount++) {


							if (loginNames.elementAt(iCount).equals(firstItem)) {
								// remove the user from the loginNames of the chatroom 
								loginNames.removeElementAt(iCount);
								// remove his socket 
								clientSockets.removeElementAt(iCount);
								// local/server trace
								System.out.println("User " + firstItem +" Logged Out ...");

								break;

							}

						}

						// broadcast the logout message 
						for (iCount = 0; iCount<loginNames.size(); iCount++) {			
							// display a "user logged out message" for everybody 
							String msg="User " + firstItem +" Logged Out ...";
							Socket tSoc=(Socket)clientSockets.elementAt(iCount);   
							objectOutputStreams.get(loginNames.get(iCount)).writeObject(msg);
							objectOutputStreams.get(loginNames.get(iCount)).reset();
						
						}

					} else if (MsgType.equals("DATA")) { // TEXT MESSAGES: if the keyword is DATA

						String msg = firstItem+ " says: "; 	// username says: ....

						while(st.hasMoreTokens()) {
							msg = msg +" " +st.nextToken();
						}

						// we broadcast the user's message to all users except for the sender of the message
						for (iCount=0;iCount<loginNames.size();iCount++) {

							if(!loginNames.elementAt(iCount).equals(firstItem)) { 

								Socket tSoc=(Socket)clientSockets.elementAt(iCount); 
								objectOutputStreams.get(loginNames.get(iCount)).writeObject(msg);
								objectOutputStreams.get(loginNames.get(iCount)).reset();
					
							}
						}


					} else { // GESTURE OBJECT MESSAGES: if there was no keyword LOGOUT, nor DATA
						
						System.out.println("Received: " + firstItem);
						
						System.out.println("The class of the object is: " + objectFromClient.getClass()); // we verify it's a Gesture object
						
						Gesture receivedGesture = new Gesture((Gesture)objectFromClient);
						
						String sender_user = receivedGesture.getOwner_user(); // we identify the sender of the gesture
						
						System.out.println("The owner user of the received gesture is: " + receivedGesture.getOwner_user());
						
						// we broadcast the gesture message to all users except for the sender
						for (iCount=0;iCount<loginNames.size();iCount++) {

							if(!loginNames.elementAt(iCount).equals(sender_user)) { 
					
								System.out.println("The user : " + loginNames.elementAt(iCount)+ " receives the gestured colored: "+ receivedGesture.getColor());

								Socket tSoc=(Socket)clientSockets.elementAt(iCount); 
								objectOutputStreams.get(loginNames.get(iCount)).writeObject(receivedGesture);
								objectOutputStreams.get(loginNames.get(iCount)).reset();
								
							                      
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



