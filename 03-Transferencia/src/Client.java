import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class Client {
    private static final int PORT = 9999;
    private static final String HOST = "localhost";
    private static final String DIR_ARRIBADA = "/tmp";

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public void connectar() {
        try {
            socket = new Socket(HOST, PORT);
            System.out.println("Connectant a -> " + HOST + ":" + PORT);
            System.out.println("Connexio acceptada: " + socket.getInetAddress());
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.err.println("Error connectant: " + e.getMessage());
        }
    }

    public void rebreFitxers() {
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.print("Nom del fitxer a rebre ('sortir' per sortir): ");
                String nomFitxer = scanner.nextLine();

                out.writeObject(nomFitxer);

                if (nomFitxer.equalsIgnoreCase("sortir")) {
                    System.out.println("Sortint...");
                    System.out.println("Conexio tancada.");
                    break;
                }

                byte[] contingut = (byte[]) in.readObject();

                if (contingut.length > 0) {
                    String nom = new File(nomFitxer).getName();
                    String desti = DIR_ARRIBADA + File.separator + nom;
                    Files.write(Paths.get(desti), contingut);
                    System.out.println("Nom del fitxer a guardar: " + desti);
                    System.out.println("Fitxer rebut i guardat com: " + desti);
                } else {
                    System.out.println("Error: Fitxer buit o no trobat al servidor");
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error rebent fitxer: " + e.getMessage());
        }
    }

    public void tancarConnexio() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null && !socket.isClosed()) socket.close();
            System.out.println("Connexio tancada.");
        } catch (IOException e) {
            System.err.println("Error tancant connexio: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.connectar();
        client.rebreFitxers();
        client.tancarConnexio();
    }
}
