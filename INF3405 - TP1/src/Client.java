import java.io.DataInputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
	
	private static Socket socket;
	
	// Constantes
	private static int ADDRESS_LENGTH = 4;
	private static int PORT_RANGE_MIN = 5000;
	private static int PORT_RANGE_MAX = 5050;	
	
	/*
	 * Requis du client :
	 * 
	 * TODO : Saisie et validation des paramètres serveur (adresse IP, port)
	 * TODO : Saisie et validation nom d'utilisateur et mot de passe
	 * TODO : Connexion au serveur
	 * TODO : Réception des messages OU erreur mot de passe
	 * TODO : Saisir une réponse (200 char maximum)
	 * TODO : Transmettre la réponse au serveur
	 * TODO : Déconnexion
	 * 
	 * */
	
	/*
	 *  Application client
	 */	
	public static void main(String[] args) throws Exception {
		
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
			// Entrée du numéro de port		
			System.out.print("Enter port: ");
			serverPort = sc.nextInt();

			// Vérification du port du serveur
			if (serverPort >= PORT_RANGE_MIN && serverPort <= PORT_RANGE_MAX) {
				portIsValid = true;
			} else {
				System.err.println("Invalid port! Please try again...");
			}			
		} while (!portIsValid);

		// entrée nom d'utilisateur
		// validation ou création nouvel utilisateur dans la base de donnée
		// entrée mot de passe
		// validation ou message d'erreur
		
		// Fermeture du Scanner
		sc.close();
		
		
		/* Extrait de code des notes de cours
		 * 
		 * // Création d'une nouvelle connexion avec le serveur socket = new
		 * Socket(serverAddress, port);
		 * 
		 * System.out.format("Client - The server is running on %s:%d%n", serverAddress,
		 * port);
		 * 
		 * // Création d'un canal entrant pour recevoir les messages envoyés par le
		 * serveur DataInputStream in = new DataInputStream(socket.getInputStream());
		 * 
		 * // Attente de la réception d'un message envoyé par le serveur sur le canal
		 * String helloMesssageFromServer = in.readUTF(); System.out.println("Client - "
		 * + helloMesssageFromServer);
		 * 
		 * // Fermeture de la connexion avec le serveur socket.close();
		 */
			
    }    
}
