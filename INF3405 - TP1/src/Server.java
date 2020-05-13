import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	private static ServerSocket listener;
	
	/*
	 *	Application Serveur 
	 */
	public static void main(String[] args) throws Exception {
		// Compteur incrémenté à chaque connexion d'un client au serveur
		int clientNumber = 0;
		
		// Adress et port du serveur
		String serverAddress = "127.0.0.1";
		int serverPort = 5000;
		
		// Création de la connexion pour communiquer avec les clients
		listener = new ServerSocket();
		listener.setReuseAddress(true);
		InetAddress serverIP = InetAddress.getByName(serverAddress);
		
		// Association de l'adresse et du port à la connexion
		listener.bind(new InetSocketAddress(serverIP, serverPort));
		
		System.out.format("The server is running on %s:%d%n", serverAddress, serverPort);
		
		try {
			/*
			 * À chaque fois qu'un nouveau client se connecte, on exécute la fonction
			 * Run() de l'objet ClientHandler
			 */
			while (true) {
				// Important : la fonction accept() est bloquante : attend qu'un prochain client se connecte
				// Une nouvelle connection : on incrémente le compter clientNumber
				new ClientHandler(listener.accept(), clientNumber++).start();
			}
		}
		finally {
			// Fermeture de la connexion
			listener.close();
		}		
	}
}
