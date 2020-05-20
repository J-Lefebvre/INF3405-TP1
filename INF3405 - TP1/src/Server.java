import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
	private static ServerSocket listener;
	
	// Constantes
	private static int ADDRESS_LENGTH = 4;
	private static int PORT_RANGE_MIN = 5000;
	private static int PORT_RANGE_MAX = 5050;	
	
	/*
	 * Requis du serveur:
	 * 
	 * TODONE: Saisie des paramètres du serveur (adresse IP, port d’écoute entre 5000 et 5050)
	 * TODO: Pouvoir connecter des utilisateurs avec leurs mots de passe
	 * TODO: Pouvoir envoyer un historique des sessions de clavardage
	 * TODO: Pouvoir fermer le serveur puis le rouvrir, et avoir tous les profils usagers et messages disponibles (écriture sur disque et pas uniquement sur RAM)
	 * TODO: Recevoir les messages des clients
	 * TODO: Tenir un historique de toutes les messages.
	 * TODO: Tenir une base de données des usagers et leurs mots de passe
	 * TODO: Afficher en temps réel les messages
	 * TODO: Effectuer correctement la vérification nom d’utilisateur/ mot de passe
	 * TODO: Créer les comptes automatiquement s’ils n’existent pas
	 */
	
	/*
	 *	Application serveur 
	 */
	public static void main(String[] args) throws Exception {
		// Compteur incrémenté à chaque connexion d'un client au serveur
		int clientNumber = 0;
		
		// Création du scanner pour lire les entrées de l'utilisateur
		Scanner sc = new Scanner(System.in);
		
		// Adresse et port du serveur
		String serverAddress;
		int serverPort;
		
		// Entrée et vérification de l'adresse IP	
		boolean ipIsValid = false;		
		do {
			// Conditions de validité
			boolean hasFourBytes = false;
			boolean bytesAreValid = true;
						
			// Entrée de l'adresse IP du serveur		
			System.out.print("Enter IP address: ");
			serverAddress = sc.next();
			
			// Vérification de la longueur (nombre d'octets == 4)
			int[] ip = new int[ADDRESS_LENGTH];
			String[] parts = serverAddress.split("\\.");
			if (parts.length == ADDRESS_LENGTH) {
				hasFourBytes = true;
			}
			
			// Vérification de la validité des octets (positifs et inférieurs à 256)
			if (hasFourBytes) {
				for (int i = 0; i < ADDRESS_LENGTH; i++) {
					ip[i] = Integer.parseInt(parts[i]);
					bytesAreValid &= ip[i] >= 0 && ip[i] < 256;
				}	
			}		
			
			// Vérification de l'adresse IP du serveur
			ipIsValid = hasFourBytes && bytesAreValid;
			if (!ipIsValid) {
				System.err.println("Invalid IP address! Please try again...");
			}
		} while (!ipIsValid);	
		
		// Entrée et vérification du port
		boolean portIsValid = false;
		
		do {
			// Entrée de l'adresse IP du serveur		
			System.out.print("Enter port: ");
			serverPort = sc.nextInt();
			
			// Vérification du port du serveur
			if (serverPort >= PORT_RANGE_MIN && serverPort <= PORT_RANGE_MAX) {
				portIsValid = true;
			} else {
				System.err.println("Invalid port! Please try again...");
			}			
		} while (!portIsValid);
		
		// Fermeture du Scanner
		sc.close();
		
		// Création de la connexion pour communiquer avec les clients
		listener = new ServerSocket();
		listener.setReuseAddress(true);
		InetAddress serverIP = InetAddress.getByName(serverAddress);
		
		// Association de l'adresse et du port à la connexion
		listener.bind(new InetSocketAddress(serverIP, serverPort));
		
		System.out.format("The server is running on %s:%d%n", serverAddress, serverPort);
		
		try {
			/*
			 * À chaque fois qu'un nouveau client se connecte, on exécute la fonction run() de l'objet ClientHandler
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
	
	/*
	 * Une thread qui se charge de traiter la demande de chaque client sur un socket particulier
	 */
	private static class ClientHandler extends Thread {
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
				// Création d'un canal sortant pour envoyer des messages au clients
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
}

