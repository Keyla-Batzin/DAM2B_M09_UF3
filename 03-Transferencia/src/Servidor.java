import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
    private static final int PORT = 9999;
    private static final String HOST = "localhost";
    private ServerSocket serverSocket;

    // Método connectar que abre la conexión con serverSocket y retorna un socket
    public Socket connecta() {
        Socket clientSocket = null;
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Acceptant connexions en -> " + HOST + ":" + PORT);
            System.out.println("Esperant connexio... ");

            clientSocket = serverSocket.accept();
            System.out.println("Connexio acceptada: " + clientSocket.getInetAddress());

        } catch (IOException e) {
            System.err.println("Error al iniciar el servidor: " + e.getMessage());
            e.printStackTrace();
        }
        return clientSocket;
    }

    // Método tancaConnexio que cierra el socket recibido como parámetro
    public void tancarConnexio(Socket socket) {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
                System.out.println("Connexió amb el client tancada.");
            }
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                System.out.println("Servidor aturat.");
            }
        } catch (IOException e) {
            System.err.println("Error al aturar el servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Método enviarFixers que recibe del cliente el nombre del archivo a enviar,
     * lee su contenido y lo envía como byte[]
     */
    public void enviarFixers(Socket clientSocket) {
        try {

            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());

            String nomFitxer = (String) in.readObject();
            System.out.println("El client ha sol·licitat el fitxer: " + nomFitxer);

            // Crear y cargar el archivo
            Fitxer fitxer = new Fitxer(nomFitxer);
            fitxer.getContingut();

            // Comprobar si el archivo existe
            if (fitxer.getContingut() != null) {
                // Enviar el contenido del archivo
                out.writeObject(fitxer.getContingut());
                System.out.println("Fitxer enviat: " + nomFitxer + " (" + fitxer.getContingut().length + " bytes)");
            } else {
                // Si el archivo no existe, enviar un array vacío
                out.writeObject(new byte[0]);
                System.out.println("Error: El fitxer no existeix o no es pot llegir");
            }

            out.close();
            in.close();

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error al enviar el fitxer: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Servidor servidor = new Servidor();
        Socket clientSocket = servidor.connecta();
        servidor.enviarFixers(clientSocket);
        servidor.tancarConnexio(clientSocket);
    }
}