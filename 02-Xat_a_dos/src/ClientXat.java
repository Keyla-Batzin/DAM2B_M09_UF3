import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class ClientXat {
    // Constantes
    private static final int PORT = 9999;
    private static final String HOST = "localhost";
    private static final String MSG_SORTIR = "sortir";
    
    // Socket y streams
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    
    // Método para conectar con el servidor
    public void connecta() {
        try {
            // Abrir el socket al servidor
            socket = new Socket(HOST, PORT);
            System.out.println("Client connectat a " + HOST + ":" + PORT);
            
            // Crear los streams de entrada y salida
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush(); // Para evitar bloqueos
            in = new ObjectInputStream(socket.getInputStream());
            
            System.out.println("Flux d'entrada i sortida creat.");
            
        } catch (IOException e) {
            System.err.println("Error al connectar amb el servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Método para enviar un mensaje
     */
    public void enviarMissatge(String missatge) {
        try {
            if (out != null) {
                out.writeObject(missatge);
                out.flush();
                System.out.println("Enviant missatge: " + missatge);
            }
        } catch (IOException e) {
            System.err.println("Error al enviar missatge: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Método para cerrar el cliente
    public void tancarClient() {
        try {
            System.out.println("Tancant client...");
            
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            
            System.out.println("Client tancat.");
            
        } catch (IOException e) {
            System.err.println("Error al tancar el client: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Método principal
    public static void main(String[] args) {
        ClientXat client = new ClientXat();
        
        try {
            // Conectar al servidor
            client.connecta();
            
            // Crear e iniciar el hilo para recibir mensajes del servidor
            FilLectorCX filLector = new FilLectorCX(client.in);
            filLector.start();
            
            // Esperar un momento para que el hilo comience a recibir mensajes
            Thread.sleep(100);
            
            // Preparar scanner para leer de la consola
            Scanner scanner = new Scanner(System.in);
            String missatge;
            
            // Enviar mensajes desde la consola
            do {
                // Leer mensaje de la consola
                missatge = scanner.nextLine();
                
                // Enviar mensaje al servidor
                client.enviarMissatge(missatge);
                
            } while (!missatge.equalsIgnoreCase(MSG_SORTIR));
            
            // Cerrar el scanner
            scanner.close();
            
            // Esperar a que finalice el hilo de lectura
            filLector.join();
            
            // Cerrar el cliente
            client.tancarClient();
            
        } catch (InterruptedException e) {
            System.err.println("Fil interromput: " + e.getMessage());
            e.printStackTrace();
        }
    }
}