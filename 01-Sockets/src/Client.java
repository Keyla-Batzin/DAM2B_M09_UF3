import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    // Constantes
    private static final int PORT = 7777;
    private static final String HOST = "localhost";
    
    // Socket y PrintWriter
    private Socket socket;
    private PrintWriter out;
    
    // Método para conectar con el servidor
    public void connecta() {
        try {
            // Abrir el socket al servidor
            socket = new Socket(HOST, PORT);
            // Crear el PrintWriter para enviar datos al servidor
            out = new PrintWriter(socket.getOutputStream(), true);
            System.out.println("Connectat a servidor en " + HOST + ": " + PORT);
        } catch (IOException e) {
            System.err.println("Error al connectar amb el servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Método para enviar un mensaje al servidor
    public void envia(String missatge) {
        if (out != null) {
            out.println(missatge);
            System.out.println("Enviat al servidor: " + missatge);
        }
    }
    
    // Método para cerrar las conexiones
    public void tanca() {
        try {
            if (out != null) {
                out.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            System.out.println("Client tancat");
        } catch (IOException e) {
            System.err.println("Error al tancar el client: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
   
    public static void main(String[] args) {
        Client client = new Client();
        client.connecta();
        client.envia("Prova d'enviament 1");
        client.envia("Prova d'enviament 2");
        client.envia("Adéu!");
        
        // Esperar hasta que el usuario pulse ENTER
        System.out.println("Prem Enter per tancar el client...");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        scanner.close();
        
        client.tanca();
    }
}