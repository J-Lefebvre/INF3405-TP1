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
	private static String QUIT_COMMAND = "quit()";

	// Création du scanner pour lire les entrées de l'utilisateur
	private static Scanner sc = new Scanner(System.in);

	// Adresse et port du serveur
	private static String serverAddress;
	private static int serverPort;

	/*
	 * Requis du client :
	 * 
	 * TODONE : Saisie et validation des paramètres serveur (adresse IP, port) 
	 * TODONE : Saisie et validation nom d'utilisateur et mot de passe 
	 * TODO : Connexion au serveur
	 * TODO : Réception des messages OU erreur mot de passe
	 * TODO : Saisir une réponse (200 char maximum)
	 * TODO : Transmettre la réponse au serveur 
	 * TODONE : Déconnexion
	 */

	/*
	 * Application client
	 */
	public static void main(String[] args) throws Exception {

		validateAddress();
		
		// Création d'une nouvelle connexion avec le serveur
		socket = new Socket(serverAddress, serverPort);

		System.out.format("Client - The server is running on %s:%d%n", serverAddress, serverPort);

		System.out.println("Type \"" + QUIT_COMMAND + "\"to close the server!");

		// Création d'un canal sortant pour envoyer des messages au serveur
		DataOutputStream out = new DataOutputStream(socket.getOutputStream());

		// Création de la chaine contenant les messages à envoyer au server
		String writeToServer = null;

		// Création d'un canal entrant pour recevoir les messages envoyés par le serveur
		DataInputStream in = new DataInputStream(socket.getInputStream());

		// Création de la chaine contenant les messages provenant du server
		String readFromServer;

		do {
			// Lire canal entrant (from Server)
			readFromServer = in.readUTF();			
			if (readFromServer.contains("ERROR:")) {
				// Si le dernier message du serveur contient "ERROR:", affiche sur la chaine en tant qu'erreur
				System.err.println(readFromServer);
			} else {
				System.out.println(readFromServer);
			}					
			
			// Le client ne peut écrire tant que le dernier message du serveur ne contient pas "> Enter" 
			if (readFromServer.contains("> Enter")) {
				// Écrire canal sortant (to Server)
				writeToServer = sc.next();
				out.writeUTF(writeToServer);
			}
			
			
			/*
			// Entrée du nom d'utilisateur
			System.out.print("Enter username: ");
			writeToServer = sc.next();
			out.writeUTF(writeToServer);
			
			// Entrée du mot de passe
			System.out.print("Enter password: ");
			writeToServer = sc.next();
			out.writeUTF(writeToServer);
			
			// Afficher l'état (sauvegarder le nouveau compte ou valider la connexion)
			readFromServer = in.readUTF();
			System.out.println(readFromServer);							
						
			// validation ou message d'erreur
			*/
			
		} while (!writeToServer.equals(QUIT_COMMAND));

		// Fermeture du Scanner
		sc.close();

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
