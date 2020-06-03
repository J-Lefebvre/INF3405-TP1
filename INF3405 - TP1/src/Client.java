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

	// Cr�ation du scanner pour lire les entr�es de l'utilisateur
	private static Scanner sc = new Scanner(System.in);

	// Adresse et port du serveur
	private static String serverAddress;
	private static int serverPort;
	
	// Cr�ation d'un canal sortant pour envoyer des messages au serveur
	private static DataOutputStream out;
	
	// Cr�ation d'un canal entrant pour recevoir les messages envoy�s par le serveur
	private static DataInputStream in;
	
	/*
	 * Requis du client :
	 * 
	 * TODONE : Saisie et validation des param�tres serveur (adresse IP, port) 
	 * TODONE : Saisie et validation nom d'utilisateur et mot de passe 
	 * TODONE : Connexion au serveur
	 * TODO : R�ception des messages OU erreur mot de passe
	 * TODONE : Saisir une r�ponse (200 char maximum)
	 * TODONE : Transmettre la r�ponse au serveur 
	 * TODONE : D�connexion
	 */

	/*
	 * Application client
	 */
	public static void main(String[] args) throws Exception {

		validateAddress();
		boolean connection = true;
		
		// Cr�ation d'une nouvelle connexion avec le serveur
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
			
			// Le client ne peut �crire tant que le dernier message du serveur ne contient pas "> " 
			if (readFromServer.contains("> ")) {
				// �crire canal sortant (to Server)	
				writeToServer = sc.next()+sc.nextLine();
				// Message ne doit pas d�passer 200 caract�res
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
		// Entr�e et v�rification de l'adresse IP
		boolean ipIsValid = false;
		do {
			// Conditions de validit�
			boolean hasFourBytes = false;
			boolean bytesAreValid = true;

			// Entr�e de l'adresse IP du serveur
			System.out.print("> Enter IP address: ");
			serverAddress = sc.next();

			// V�rification de la longueur (nombre d'octets == 4)
			int[] ip = new int[ADDRESS_LENGTH];
			String[] parts = serverAddress.split("\\.");
			if (parts.length == ADDRESS_LENGTH) {
				hasFourBytes = true;
			}

			// V�rification de la validit� des octets (positifs et inf�rieurs � 256)
			if (hasFourBytes) {
				for (int i = 0; i < ADDRESS_LENGTH; i++) {
					ip[i] = Integer.parseInt(parts[i]);
					bytesAreValid &= ip[i] >= 0 && ip[i] < 256;
				}
			}

			// V�rification de l'adresse IP du serveur
			ipIsValid = hasFourBytes && bytesAreValid;
			if (!ipIsValid) {
				System.err.println("ERROR: Invalid IP address! Please try again...");
			}
		} while (!ipIsValid);

		// Entr�e et v�rification du port
		boolean portIsValid = false;

		do {
			// Entr�e du num�ro de port
			System.out.print("> Enter port: ");
			serverPort = sc.nextInt();

			// V�rification du port du serveur
			if (serverPort >= PORT_RANGE_MIN && serverPort <= PORT_RANGE_MAX) {
				portIsValid = true;
			} else {
				System.err.println("ERROR: Invalid port! Please try again...");
			}
		} while (!portIsValid);

	}

}
