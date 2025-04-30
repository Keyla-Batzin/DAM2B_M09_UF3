import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ServidorXat {
    private static final int PORT = 9999;
    private static final String HOST = "localhost";
    public static final String MSG_SORTIR = "sortir";

    private ServerSocket serverSocket;

    // Método para iniciar el servidor
    public void iniciarServidor() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Servidor iniciat a " + HOST + ":" + PORT);
        } catch (IOException e) {
            System.err.println("Error al iniciar el servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Método para detener el servidor
    public void pararServidor() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                System.out.println("Servidor aturat.");
            }
        } catch (IOException e) {
            System.err.println("Error al aturar el servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Método para obtener el nombre del cliente
    public String getNom(ObjectInputStream in, ObjectOutputStream out) {
        String nom = "";
        try {
            // Enviar solicitud de nombre al cliente
            out.writeObject("Escriu el teu nom: ");
            out.flush();

            // Esperar a recibir el nombre
            nom = (String) in.readObject();
            System.out.println("Nom rebut: " + nom);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error al obtenir el nom: " + e.getMessage());
            e.printStackTrace();
        }
        return nom;
    }

   
    public static void main(String[] args) {
        ServidorXat servidor = new ServidorXat();
        Socket clientSocket = null;

        try {
            // Iniciar el servidor
            servidor.iniciarServidor();

            // Aceptar conexión
            clientSocket = servidor.serverSocket.accept();
            System.out.println("Client connectat: " + clientSocket.getInetAddress());

            // Crear streams
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            out.flush(); // Importante para evitar bloqueos
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());

            // Obtener nombre del cliente
            String nomClient = servidor.getNom(in, out);

            // Crear y comenzar el hilo para recibir mensajes
            FilServidorXat filServidor = new FilServidorXat(in, nomClient);
            System.out.println("Fil de xat creat.");
            filServidor.start();
            System.out.println("Fil de " + nomClient + " iniciat");

            // Enviar mensajes desde la consola hasta recibir MSG_SORTIR
            Scanner scanner = new Scanner(System.in);
            String missatge;

            do {
                System.out.print("Missatge ('" + MSG_SORTIR + "' per tancar): ");
                missatge = scanner.nextLine();

                // Enviar el mensaje al cliente
                out.writeObject(missatge);
                out.flush();

            } while (!missatge.equalsIgnoreCase(MSG_SORTIR));

            scanner.close();

            // Esperar a que el hilo termine
            filServidor.join();

            // Cerrar socket del cliente
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }

        } catch (IOException e) {
            System.err.println("Error en la comunicació: " + e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.err.println("Fil interromput: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Parar el servidor
            servidor.pararServidor();
        }
    }
}