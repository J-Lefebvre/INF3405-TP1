import java.io.DataOutputStream;
import java.net.Socket;
import java.io.IOException;

/*
 * Une thread qui se charge de traiter la demande de chaque client
 * sur un socket particulier
 */

class ClientHandler extends Thread {
	private Socket socket;
	private int clientNumber;
	
	public ClientHandler (Socket socket, int clientNumber) {
		this.socket = socket;
		this.clientNumber = clientNumber;
		System.out.println("New connection with client#" + clientNumber + " at " + socket);
	}
	
	/*
	 * Une thread se charge d'envoyer au client un message de bienvenue
	 */
	public void run() {
		try {
			// Création d'un canal sortant our envoyer des messages au clients
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			
			// Envoi d'un message au client
			out.writeUTF("Hello from server - you are client#" + clientNumber);
		} catch (IOException e) {
			System.out.println("Error handling client#" + clientNumber +": " + e);
		}
		finally {
			try {
				// Fermeture de la connexion avec le client
				socket.close();
			}
			catch (IOException e) {
				System.out.println("Couldn't close a socket, what's going on?");
			}
			System.out.println("Connection with Client# " + clientNumber + " closed");
		}
	}
}
