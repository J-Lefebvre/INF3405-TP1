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
	 * TODO : Saisie et validation des param�tres serveur (adresse IP, port)
	 * TODO : Saisie et validation nom d'utilisateur et mot de passe
	 * TODO : Connexion au serveur
	 * TODO : R�ception des messages OU erreur mot de passe
	 * TODO : Saisir une r�ponse (200 char maximum)
	 * TODO : Transmettre la r�ponse au serveur
	 * TODO : D�connexion
	 * 
	 * */
	
	/*
	 *  Application client
	 */	
	public static void main(String[] args) throws Exception {
		
		// Cr�ation du scanner pour lire les entr�es de l'utilisateur
		Scanner sc = new Scanner(System.in);

		// Adresse et port du serveur
		String serverAddress;
		int serverPort;

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

		// entr�e nom d'utilisateur
		// validation ou cr�ation nouvel utilisateur dans la base de donn�e
		// entr�e mot de passe
		// validation ou message d'erreur
		
		// Fermeture du Scanner
		sc.close();
		
		
		/* Extrait de code des notes de cours
		 * 
		 * // Cr�ation d'une nouvelle connexion avec le serveur socket = new
		 * Socket(serverAddress, port);
		 * 
		 * System.out.format("Client - The server is running on %s:%d%n", serverAddress,
		 * port);
		 * 
		 * // Cr�ation d'un canal entrant pour recevoir les messages envoy�s par le
		 * serveur DataInputStream in = new DataInputStream(socket.getInputStream());
		 * 
		 * // Attente de la r�ception d'un message envoy� par le serveur sur le canal
		 * String helloMesssageFromServer = in.readUTF(); System.out.println("Client - "
		 * + helloMesssageFromServer);
		 * 
		 * // Fermeture de la connexion avec le serveur socket.close();
		 */
			
    }    
}
