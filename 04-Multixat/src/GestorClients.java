import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class GestorClients extends Thread {
    private Socket client;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private ServidorXat servidor;
    private String nom;
    private boolean sortir;

    public GestorClients(Socket socket, ServidorXat servidor) {
        client = socket;
        this.servidor = servidor;
    }

    public String getNom() {
        return null;
    }

    @Override
    public void run() {
        // bucle principal
    }

    public void enviarMissatge(String remitent, String missatge) {
        // envia missatge
    }

    public void processaMissatge(String missatgeRaw) {
        // processa el missatge rebut
    }
}
