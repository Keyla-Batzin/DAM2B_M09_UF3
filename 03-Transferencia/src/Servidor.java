import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
    private static final int PORT = 9999;
    private static final String HOST = "localhost";
    private ServerSocket serverSocket;

    public Socket connecta() {
        Socket clientSocket = null;
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Acceptant connexions en -> " + HOST + ": " + PORT);
            System.out.println("Esperant connexio...");
            clientSocket = serverSocket.accept();
            System.out.println("Connexio acceptada: " + clientSocket.getInetAddress());
        } catch (IOException e) {
            System.err.println("Error iniciant el servidor: " + e.getMessage());
        }
        return clientSocket;
    }

    public void enviarFitxers(Socket clientSocket) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());

            while (true) {
                System.out.println("Esperant el nom del fitxer del client...");
                String nomFitxer = (String) in.readObject();

                if (nomFitxer == null || nomFitxer.trim().isEmpty()) {
                    System.out.println("Nom del fitxer buit o nul. Sortint...");
                    System.out.println("Tancant conexió amb el client: " + clientSocket.getInetAddress());
                    break;
                }

                if (nomFitxer.equalsIgnoreCase("sortir")) {
                    System.out.println("Sortint");
                    System.out.println("Tancant conexió amb el client: " + clientSocket.getInetAddress());
                    break;
                }

                System.out.println("Nomfitxer rebut: " + nomFitxer);
                Fitxer fitxer = new Fitxer(nomFitxer);
                byte[] contingut = fitxer.getContingut();

                if (contingut != null) {
                    System.out.println("Contingut del fitxer a enviar: " + contingut.length + " bytes");
                    out.writeObject(contingut);
                    System.out.println("Fitxer enviat al client: " + nomFitxer);
                } else {
                    out.writeObject(new byte[0]);
                    System.out.println("Error llegint el fitxer del client: null");
                }
            }

            out.close();
            in.close();

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error en enviarFitxers: " + e.getMessage());
        }
    }

    public void tancarConnexio(Socket socket) {
        try {
            if (socket != null && !socket.isClosed()) {
                System.out.println("Tancant connexió amb el client: " + socket.getInetAddress());
                socket.close();
            }
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error tancant connexió: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Servidor servidor = new Servidor();
        Socket clientSocket = servidor.connecta();
        servidor.enviarFitxers(clientSocket);
        servidor.tancarConnexio(clientSocket);
    }
}
