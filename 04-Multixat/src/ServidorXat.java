import java.util.Hashtable;

public class ServidorXat {
    private static final int PORT = 9999;
    private static final String HOST = "localhost";
    public static final String MSG_SORTIR = "sortir";
    private static Hashtable<String, GestorClients> clients;
    private static boolean sortir;

    // Mètode que escolta per el port i host anteriors
    public static void servidorAEscoltar() {

    }

    // Mètode que aturi el serverSocket
    public static void pararServidor() {

    }

    // Mètode finalitza el xat
    public static void finalitzaXat() {

    }

    // Afegeig al client al Hashtable
    public static void afegirClient(GestorClients client) {

    }

    // Elimina el client de la Hashtable
    public static void eliminarClient(String nomClient) {

    }

    // Envia missatge a tots
    public void enviarMissatgeGrup(String missatge) {

    }

    // Envia missatge personal
    public void enviarMissatgePersonal(String destinatari, String remitent, String missatge) {

    }

    public static void main(String[] args) {
        // fica el servidorXat a escoltar
        // accepta peticions
        // Crea un GestorClients amb el clientsocket i el servidorXat
        // Inicia el GestorClients
        // ho fa mentre sortir == false
        // 
    }
}