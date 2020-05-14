import java.io.DataInputStream;
import java.net.Socket;

public class Client {
	
	private static Socket socket;
	
	/*
	 *  Application client
	 */	
	public static void main(String[] args) throws Exception {
		// Adresse et port du serveur
		String serverAddress = "127.0.0.1";
		int port = 5000;
		
		// Cr�ation d'une nouvelle connexion avec le serveur
		socket = new Socket(serverAddress, port);
		
		System.out.format("Client - The server is running on %s:%d%n", serverAddress, port);
		
		// Cr�ation d'un canal entrant pour recevoir les messages envoy�s par le serveur
		DataInputStream in = new DataInputStream(socket.getInputStream());
		
		// Attente de la r�ception d'un message envoy� par le serveur sur le canal
		String helloMesssageFromServer = in.readUTF();		
		System.out.println("Client - " + helloMesssageFromServer);
		
		// Fermeture de la connexion avec le serveur
		socket.close();	
    }    
}
