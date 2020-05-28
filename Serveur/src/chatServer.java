import java.net.*;
import java.util.*;
import java.io.*;

class chatServer {

	static Vector ClientSockets;
	static Vector LoginNames;
	static final int SERVER_PORT = 8010;

	chatServer() throws Exception {

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

		chatServer chatServer = new chatServer();
	}

	// inner class
	class AcceptClient extends Thread {

		Socket ClientSocket;
		DataInputStream din;
		DataOutputStream dout;

		AcceptClient (Socket CSoc) throws Exception {

			ClientSocket = CSoc;

			din = new DataInputStream(ClientSocket.getInputStream());
			dout = new DataOutputStream(ClientSocket.getOutputStream());

			String LoginName = din.readUTF();

			System.out.println("User Logged In :" + LoginName);
			
			LoginNames.add(LoginName);
			ClientSockets.add(ClientSocket); 
			
			
			for (int iCount = 0; iCount<LoginNames.size(); iCount++) {

				String msg = "User " + LoginName +" joined the chatroom.";				
				Socket tSoc = (Socket)ClientSockets.elementAt(iCount);                            
				DataOutputStream tdout = new DataOutputStream(tSoc.getOutputStream());
				tdout.writeUTF(msg); 
					
			}

			start(); // calls the run method 

		}

		public void run() {

			while (true) {

				try {
					String msgFromClient = new String();
					msgFromClient = din.readUTF();
					
					StringTokenizer st = new StringTokenizer(msgFromClient);
					
					String login = st.nextToken();    
					
					String MsgType = st.nextToken();
					
					int iCount = 0;

					if (MsgType.equals("LOGOUT")) {

						for (iCount = 0; iCount<LoginNames.size(); iCount++) {


							if (LoginNames.elementAt(iCount).equals(login)) {

								LoginNames.removeElementAt(iCount);
								ClientSockets.removeElementAt(iCount);
								System.out.println("User " + login +" Logged Out ...");
								
								break;

							}
							
						}
						
						// broadcast the logout message 
						for (iCount = 0; iCount<LoginNames.size(); iCount++) {			
					
								String msg="User " + login +" Logged Out ...";
								
								Socket tSoc=(Socket)ClientSockets.elementAt(iCount);                            
								DataOutputStream tdout=new DataOutputStream(tSoc.getOutputStream());
								tdout.writeUTF(msg); 				
							
						}

					} else {

						String msg=login+ " says: ";

						while(st.hasMoreTokens()) {
							msg = msg+" " +st.nextToken();
						}
						
						for (iCount=0;iCount<LoginNames.size();iCount++) {
							
							if(!LoginNames.elementAt(iCount).equals(login)) { 
								
								Socket tSoc=(Socket)ClientSockets.elementAt(iCount);                            
								DataOutputStream tdout=new DataOutputStream(tSoc.getOutputStream());
								tdout.writeUTF(msg);                            
								//break;
								
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



