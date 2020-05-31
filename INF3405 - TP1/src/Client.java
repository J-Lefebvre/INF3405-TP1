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

	// Cr�ation du scanner pour lire les entr�es de l'utilisateur
	private static Scanner sc = new Scanner(System.in);

	// Adresse et port du serveur
	private static String serverAddress;
	private static int serverPort;

	/*
	 * Requis du client :
	 * 
	 * TODONE : Saisie et validation des param�tres serveur (adresse IP, port) 
	 * TODO : Saisie et validation nom d'utilisateur et mot de passe 
	 * TODO : Connexion au serveur
	 * TODO : R�ception des messages OU erreur mot de passe
	 * TODO : Saisir une r�ponse (200 char maximum)
	 * TODO : Transmettre la r�ponse au serveur 
	 * TODO : D�connexion
	 */

	/*
	 * Application client
	 */
	public static void main(String[] args) throws Exception {

		validateAddress();
		
		// Cr�ation d'une nouvelle connexion avec le serveur
		socket = new Socket(serverAddress, serverPort);

		System.out.format("Client - The server is running on %s:%d%n", serverAddress, serverPort);

		System.out.println("Type \"quit()\" to close the server!");

		// Cr�ation d'un canal sortant pour envoyer des messages au serveur
		DataOutputStream out = new DataOutputStream(socket.getOutputStream());

		// Cr�ation de la chaine contenant les messages � envoyer au server
		String writeToServer;

		// Cr�ation d'un canal entrant pour recevoir les messages envoy�s par le serveur
		DataInputStream in = new DataInputStream(socket.getInputStream());

		// Cr�ation de la chaine contenant les messages provenant du server
		String readFromServer;

		do {
			// Entr�e du nom d'utilisateur
			System.out.print("Enter username: ");
			writeToServer = sc.next();

			// Envoi au serveur
			out.writeUTF(writeToServer);

			
			// validation ou cr�ation nouvel utilisateur dans la base de donn�e
			// entr�e mot de passe
			// validation ou message d'erreur
		} while (!writeToServer.equals("quit()"));

		// Fermeture du Scanner
		sc.close();

		/*
		 * Extrait de code des notes de cours
		 * 
		 * 
		 * // Cr�ation d'un canal entrant pour recevoir les messages envoy�s par le
		 * serveur DataInputStream in = new DataInputStream(socket.getInputStream());
		 * 
		 * // Attente de la r�ception d'un message envoy� par le serveur sur le canal
		 * String helloMesssageFromServer = in.readUTF(); System.out.println("Client - "
		 * + helloMesssageFromServer); *
		 */

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
			System.out.print("Enter IP address: ");
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
				System.err.println("Invalid IP address! Please try again...");
			}
		} while (!ipIsValid);

		// Entr�e et v�rification du port
		boolean portIsValid = false;

		do {
			// Entr�e du num�ro de port
			System.out.print("Enter port: ");
			serverPort = sc.nextInt();

			// V�rification du port du serveur
			if (serverPort >= PORT_RANGE_MIN && serverPort <= PORT_RANGE_MAX) {
				portIsValid = true;
			} else {
				System.err.println("Invalid port! Please try again...");
			}
		} while (!portIsValid);

	}

}
