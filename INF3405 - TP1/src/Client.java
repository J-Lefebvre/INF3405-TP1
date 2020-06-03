import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {

	private static Socket socket;

	// Constantes
	private static int ADDRESS_LENGTH = 4;
	private static int PORT_RANGE_MIN = 5000;
	private static int PORT_RANGE_MAX = 5050;
	private static int MAX_MESSAGE_LENGTH = 200;
	private static String CLIENT_DISCONNECTION = "*DISCONNECTED*";

	// Création du scanner pour lire les entrées de l'utilisateur
	private static Scanner sc = new Scanner(System.in);

	// Adresse et port du serveur
	private static String serverAddress;
	private static int serverPort;
	
	// Création d'un canal sortant pour envoyer des messages au serveur
	private static DataOutputStream out;
	
	// Création d'un canal entrant pour recevoir les messages envoyés par le serveur
	private static DataInputStream in;
	
	/*
	 * Requis du client :
	 * 
	 * TODONE : Saisie et validation des paramètres serveur (adresse IP, port) 
	 * TODONE : Saisie et validation nom d'utilisateur et mot de passe 
	 * TODONE : Connexion au serveur
	 * TODO : Réception des messages OU erreur mot de passe
	 * TODONE : Saisir une réponse (200 char maximum)
	 * TODONE : Transmettre la réponse au serveur 
	 * TODONE : Déconnexion
	 */

	/*
	 * Application client
	 */
	public static void main(String[] args) throws Exception {

		validateAddress();
		boolean connection = true;
		
		// Création d'une nouvelle connexion avec le serveur
		socket = new Socket(serverAddress, serverPort);

		System.out.format("Client - The server is running on %s:%d%n", serverAddress, serverPort);

		// Ouverture des canaux
		out = new DataOutputStream(socket.getOutputStream());
		in = new DataInputStream(socket.getInputStream());
	
		String writeToServer = null;	
		String readFromServer = null;

		do {
			// Lire canal entrant (from Server)
			readFromServer = in.readUTF();			
			if (readFromServer.contains("ERROR:")) {
				// Si le dernier message du serveur contient "ERROR:", affiche sur la chaine en tant qu'erreur
				System.err.println(readFromServer);
			} else if (readFromServer.contains(CLIENT_DISCONNECTION)) {
				connection = false;
			} else {
				System.out.println(readFromServer);
			}					
			
			// Le client ne peut écrire tant que le dernier message du serveur ne contient pas "> " 
			if (readFromServer.contains("> ")) {
				// Écrire canal sortant (to Server)	
				writeToServer = sc.next()+sc.nextLine();
				// Message ne doit pas dépasser 200 caractères
				if (writeToServer.length() <= MAX_MESSAGE_LENGTH) {
					out.writeUTF(writeToServer);
				} else {
					System.err.println("ERROR: Message too long! Maximum " + MAX_MESSAGE_LENGTH + " characters.");
				}	
			}
		} while (connection);

		// Fermeture du Scanner
		sc.close();
		
		// Fermeture des canaux
		in.close();
		out.close();

		// Fermeture de la connexion avec le serveur
		socket.close();
	}

	private static void validateAddress() {
		// Entrée et vérification de l'adresse IP
		boolean ipIsValid = false;
		do {
			// Conditions de validité
			boolean hasFourBytes = false;
			boolean bytesAreValid = true;

			// Entrée de l'adresse IP du serveur
			System.out.print("> Enter IP address: ");
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
				System.err.println("ERROR: Invalid IP address! Please try again...");
			}
		} while (!ipIsValid);

		// Entrée et vérification du port
		boolean portIsValid = false;

		do {
			// Entrée du numéro de port
			System.out.print("> Enter port: ");
			serverPort = sc.nextInt();

			// Vérification du port du serveur
			if (serverPort >= PORT_RANGE_MIN && serverPort <= PORT_RANGE_MAX) {
				portIsValid = true;
			} else {
				System.err.println("ERROR: Invalid port! Please try again...");
			}
		} while (!portIsValid);

	}

}
