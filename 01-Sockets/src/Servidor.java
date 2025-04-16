import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
    // Constantes
    private static final int PORT = 7777;
    private static final String HOST = "localhost";

    // Sockets
    private ServerSocket srvSocket;
    private Socket clientSocket;

    // Método para abrir la conexión del servidor y aceptar conexiones
    public void connecta() {
        try {
            srvSocket = new ServerSocket(PORT);
            System.out.println("Servidor en marxa a " + HOST + ":" + PORT);
            System.out.println("Esperant connexions a " + HOST + ":" + PORT);

            // Bloquea hasta que un cliente se conecte
            clientSocket = srvSocket.accept();
            System.out.println("Client connectat: " + clientSocket.getInetAddress());

        } catch (IOException e) {
            System.err.println("Error al iniciar el servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Método para recibir datos del cliente
    public void repDades() {
        try {
            // Crear un BufferedReader para leer texto del cliente
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String linia;
            while ((linia = in.readLine()) != null) {
                System.out.println("Rebut: " + linia);

                if (linia.equals("Adéu!")) {
                    break;
                }
            }

            in.close();

        } catch (IOException e) {
            System.err.println("Error al rebre dades: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Método para cerrar las conexiones
    public void tanca() {
        try {
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
            if (srvSocket != null && !srvSocket.isClosed()) {
                srvSocket.close();
            }
            System.out.println("Servidor tancat.");
        } catch (IOException e) {
            System.err.println("Error al tancar el servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Servidor servidor = new Servidor();
        servidor.connecta();
        servidor.repDades();
        servidor.tanca();
    }
}