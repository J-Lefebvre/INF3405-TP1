import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.time.format.DateTimeFormatter;  
import java.time.LocalDateTime;    

public class Server {
	private static ServerSocket listener;

	// Constantes
	private static int ADDRESS_LENGTH = 4;
	private static int PORT_RANGE_MIN = 5000;
	private static int PORT_RANGE_MAX = 5050;
	private static String QUIT_COMMAND = "quit()";
	private static int NUM_RECENT_MESSAGES = 15;
	private static String CLIENT_DISCONNECTION = "*DISCONNECTED*";

	// Création du scanner pour lire les entrées de l'utilisateur
	private static Scanner sc = new Scanner(System.in);

	// Adresse et port du serveur
	private static String serverAddress;
	private static int serverPort;

	// Correspondances nom d'utilisateur - mot de passe
	private static HashMap<String, String> passwords = new HashMap<String, String>();
	
	// Conteneur des threads pour chaque client
	private static ArrayList<ClientHandler> clients = new ArrayList<>();
	
	/*
	 * Requis du serveur:
	 * 
	 * TODONE: Saisie des paramètres du serveur (adresse IP, port d’écoute entre 5000 et 5050) 
	 * TODONE: Pouvoir connecter des utilisateurs avec leurs mots de passe 
	 * TODONE: Pouvoir envoyer un historique des sessions de clavardage 
	 * TODONE: Pouvoir fermer le serveur puis le rouvrir, et avoir tous les profils usagers et messages disponibles (écriture sur disque et pas uniquement sur RAM)
	 * TODONE: Recevoir les messages des clients 
	 * TODONE: Tenir un historique de toutes les messages.
	 * TODONE: Tenir une base de données des usagers et leurs mots de passe
	 * TODONE: Afficher en temps réel les messages 
	 * TODONE: Effectuer correctement la vérification nom d’utilisateur/ mot de passe
	 * TODONE: Créer les comptes automatiquement s’ils n’existent pas
	 */

	/*
	 * Application serveur
	 */
	public static void main(String[] args) throws Exception {

		// Compteur incrémenté à chaque connexion d'un client au serveur
		int clientNumber = 0;
		
		validateAddress();
		
		createLogTxt();
		
		createPasswordsTxt();
		
		importPasswords();
			
		// Création de la connexion pour communiquer avec les clients
		listener = new ServerSocket();
		listener.setReuseAddress(true);
		InetAddress serverIP = InetAddress.getByName(serverAddress);

		// Association de l'adresse et du port à la connexion
		listener.bind(new InetSocketAddress(serverIP, serverPort));

		System.out.format("The server is running on %s:%d%n", serverAddress, serverPort);

		try {
			/*
			 * À chaque fois qu'un nouveau client se connecte, on exécute la fonction run()
			 * de l'objet ClientHandler
			 */
			while (true) {
				// Important : la fonction accept() est bloquante : attend qu'un prochain client
				// se connecte
				// Une nouvelle connection : on incrémente le compter clientNumber
				//		new ClientHandler(listener.accept(), clientNumber++).start();
				
				
				ClientHandler clientThread = new ClientHandler(listener.accept(), clientNumber++);
				clients.add(clientThread);
				clientThread.start();				
			}
		} finally {
			// Fermeture de la connexion
			listener.close();
		}
	}

	/*
	 * Une thread qui se charge de traiter la demande de chaque client sur un socket
	 * particulier
	 */
	private static class ClientHandler extends Thread {
		private Socket socket;
		private int clientNumber;
		private DataInputStream in;
		private DataOutputStream out;
		private boolean connection;

		public ClientHandler(Socket socket, int clientNumber) throws IOException {
			this.socket = socket;
			this.clientNumber = clientNumber;
			// Création d'un canal entrant pour recevoir les messages du client
			in = new DataInputStream(socket.getInputStream());
			// Création d'un canal sortant pour envoyer des messages au client
			out = new DataOutputStream(socket.getOutputStream());
			
			System.out.println("New connection with client #" + clientNumber + " at " + socket.getLocalAddress() + ":"
					+ socket.getLocalPort());
			connection = true;
		}

		public void run() {
			try {
				while (connection) {		
					String username;
					String password;
					Boolean passwordIsCorrect = false;
				
					do {
						// Réception du nom d'utilisateur du client
						out.writeUTF("> Enter username: ");					
						username = in.readUTF();
						
						// Recherche du nom d'utilisateur dans la HashMap passwords
						Boolean usernameExists = passwords.containsKey(username);
						
						// Réception du mot de passe du client
						out.writeUTF("> Enter password: ");
						password = in.readUTF();
						
						if (usernameExists == false) { // Ajouter nom d'utilisateur et mot de passe à la HashMap passwords et à Passwords.txt
							if (!username.equals(QUIT_COMMAND) || !username.equals(QUIT_COMMAND)) {
								out.writeUTF("Username non-existent! Saving new account...");
								passwords.put(username, password);
								addPassword(username, password);
							} else {
								connection = false;
							}
						}
						else { // Vérifier que le mot de passe fourni correspond au nom d'utilisateur	
							out.writeUTF("Authentification...");
							passwordIsCorrect = password.equals(passwords.get(username));										
						}	
						
						// Répéter la boucle tant que la combinaison username-password est invalide					
						if (!passwordIsCorrect && usernameExists) {
							out.writeUTF("ERROR: Wrong password!");
						}
					} while (!passwordIsCorrect);
					
					// Combinaison username-password valide
					out.writeUTF("Access granted! Welcome " + username + "!");
										
					// Historique des 15 derniers messages					
					LinkedList<String> lastMessages = lastMessages();
					if (lastMessages.size() != 0) {
						if (lastMessages.size() == 1) {
							out.writeUTF("***The following is the last message***");
						}
						else {
							out.writeUTF("***The following are the last " + lastMessages.size() + " messages***");
						}
						for (String line : lastMessages) {
							out.writeUTF(line);
						}
						out.writeUTF("*******************************************");
					}
					// Clavardage...
					out.writeUTF("You can start chatting now.");
					out.writeUTF("Type \"" + QUIT_COMMAND + "\"to leave the server!");
					String message;
					String timestamp;
					String loggedMessage;
					while(true) {
						out.writeUTF("> ");
						message = in.readUTF(); 
						timestamp = getTimestamp();						
						if (message.equals(QUIT_COMMAND)) {
							out.writeUTF(CLIENT_DISCONNECTION);			
							connection = false;
						} else if (message.isEmpty()) {
							
						} else {
							loggedMessage = "[" + username + " - " + this.socket.getInetAddress().getHostAddress() + ":" + this.socket.getPort() + " - " + timestamp + "]: " + message;		
							System.out.println(loggedMessage);
							
							for (ClientHandler ch : clients) {
								ch.out.writeUTF(loggedMessage);
							}
							saveToLog(loggedMessage);
						}													
					}

				}
				// Fermeture des canaux
				in.close();
				out.close();		
			} catch (IOException e) {
				System.out.println("Error handling client#" + clientNumber + ": " + e);
			} finally {
				try {
					// Fermeture de la connexion avec le client
					socket.close();					
				} catch (IOException e) {
					System.out.println("Couldn't close a socket, what's going on?");
				}
				System.out.println("Connection with Client# " + clientNumber + " closed");
			}
		}
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
			// Entrée de l'adresse IP du serveur
			System.out.print("> Enter port: ");
			serverPort = sc.nextInt();

			// Vérification du port du serveur
			if (serverPort >= PORT_RANGE_MIN && serverPort <= PORT_RANGE_MAX) {
				portIsValid = true;
			} else {
				System.err.println("ERROR: Invalid port! Please try again...");
			}
		} while (!portIsValid);

		// Fermeture du Scanner
		sc.close();
	}
	
	private static void createPasswordsTxt() {
		// Crée le fichier Passwords.txt s'il n'existe pas déjà
		try {
			File passwords = new File("Passwords.txt");
			passwords.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void importPasswords() {
		FileReader fr = null;
		BufferedReader br = null;
		try {
			File fdPasswords = new File("Passwords.txt");
			String[] splitTxtLine = null;
			fr = new FileReader(fdPasswords);
			br = new BufferedReader(fr);
			String txtLine;
			
			while((txtLine = br.readLine()) != null) {
				splitTxtLine = txtLine.split(" ");
				passwords.put(splitTxtLine[0], splitTxtLine[1]);
			}
		}
		catch (IOException e) {
			System.err.println("IOException was caught.");
			e.printStackTrace();
		} finally {
			try {
				br.close();
			}
			catch (IOException e) {
				System.err.println("IOException was caught.");
				e.printStackTrace();
			}
		}
	}
	
	private static void addPassword(String username, String password) {
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			fw = new FileWriter("Passwords.txt", true);
			bw = new BufferedWriter(fw);
			
			bw.write(username);
			bw.write(" ");
			bw.write(password);
			bw.newLine();
		}
		catch (IOException e) {
			System.err.println("IOException was caught.");
			e.printStackTrace();
		} finally {
			try {
				bw.close();
			}
			catch (IOException e) {
				System.err.println("IOException was caught.");
				e.printStackTrace();
			}
		}
	}
	
	private static String getTimestamp() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd@HH:mm:ss");
		LocalDateTime ts = LocalDateTime.now();  
		return formatter.format(ts);
	}

	private static void createLogTxt() {
		try {
			File log = new File("Log.txt");
			log.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void saveToLog(String message) {
		FileWriter fw = null;
		BufferedWriter bw = null;
		PrintWriter pw = null;
		try {
			fw = new FileWriter("Log.txt", true);
			bw = new BufferedWriter(fw);
			pw = new PrintWriter(bw);
			
			pw.println(message);
		}
		catch (IOException e) {
			System.err.println("IOException was caught.");
			e.printStackTrace();
		} finally {
			try {
				bw.close();
			}
			catch (IOException e) {
				System.err.println("IOException was caught.");
				e.printStackTrace();
			}
		}
	}
	
	private static LinkedList<String> lastMessages() {
		FileReader fr = null;
		BufferedReader br = null;
		
		LinkedList<String> logBuffer = new LinkedList<String>();
		LinkedList<String> lastMessages = new LinkedList<String>();
		String lineBuffer;
		
		try {
			fr = new FileReader("Log.txt");
			br = new BufferedReader(fr);
			
			while((lineBuffer = br.readLine()) != null) {
				logBuffer.push(lineBuffer);
			}
			
			for (int i = 0; (i < NUM_RECENT_MESSAGES) && (!logBuffer.isEmpty()); i++) {
				lastMessages.add(logBuffer.pop());
			}		
			lastMessages = reverseList(lastMessages);
		}
		catch (IOException e) {
			System.err.println("IOException was caught.");
			e.printStackTrace();
		}
		finally
		{
			try {
				br.close();
			}
			catch (IOException e) {
				System.err.println("IOException was caught.");
				e.printStackTrace();
			}
		}
		return lastMessages;
	}
	
	private static LinkedList<String> reverseList(LinkedList<String> list) {
		LinkedList<String> newList = new LinkedList<String>();
		for (String line : list) {
			newList.push(line);
		}	
		return newList;
	}	
}
