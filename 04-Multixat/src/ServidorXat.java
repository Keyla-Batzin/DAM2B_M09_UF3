import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

public class ServidorXat {
    // Constantes
    private static final int PORT = 9999;
    private static final String HOST = "localhost";
    private static final String MSG_SORTIR = "sortir";
    
    // Atributs
    private Hashtable<String, GestorClients> clients;
    private boolean sortir;
    private ServerSocket serverSocket;
    
    public ServidorXat() {
        this.clients = new Hashtable<>();
        this.sortir = false;
    }
    
    // Método para abrir la conexión del servidor y escuchar
    public void servidorAEscoltar() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Servidor iniciat a " + HOST + ":" + PORT);
        } catch (IOException e) {
            System.err.println("Error al iniciar el servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Método para parar el servidor
    public void pararServidor() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error al tancar el servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Método para finalizar el chat completamente
    public void finalitzarXat() {
        // Enviar mensaje de grupo con MSG_SORTIR
        enviarMissatgeGrup(Missatge.getMissatgeSortirTots(MSG_SORTIR));
        System.out.println("Tancant tots els clients.");
        System.out.println("DEBUG: multicast sortir");
        
        // Vaciar hashtable
        clients.clear();
        
        // Salir del programa
        sortir = true;
        
        // Cerrar el servidor para que el hilo principal también termine
        pararServidor();
        
        // Salir del programa completamente
        System.exit(0);
    }
    
    // Método para añadir un cliente
    public void afegirClient(GestorClients gestorClients) {
        // Añadir cliente a la hashtable
        clients.put(gestorClients.getNom(), gestorClients);
        
        // Enviar mensaje de grupo indicando que entra
        enviarMissatgeGrup(Missatge.getMissatgeGrup("Entra: " + gestorClients.getNom()));
        System.out.println(gestorClients.getNom() + " connectat.");
        System.out.println("DEBUG: multicast Entra: " + gestorClients.getNom());
    }
    
    // Método para eliminar un cliente
    public void eliminarClient(String nomClient) {
        if (nomClient != null && clients.containsKey(nomClient)) {
            clients.remove(nomClient);
        }
    }
    
    // Método para enviar mensaje a todos los clientes
    public void enviarMissatgeGrup(String missatge) {
        // Enviar a todos los clientes
        for (GestorClients client : clients.values()) {
            client.enviarMissatge(missatge);
        }
    }
    
    // Método para enviar mensaje personal
    public void enviarMissatgePersonal(String destinatari, String remitent, String missatge) {
        if (clients.containsKey(destinatari)) {
            clients.get(destinatari).enviarMissatge(Missatge.getMissatgePersonal(remitent, missatge));
            System.out.println("Missatge personal per (" + destinatari + ") de (" + remitent + "): " + missatge);
        }
    }
    
    public static void main(String[] args) {
        ServidorXat servidorXat = new ServidorXat();
        
        // Iniciar servidor
        servidorXat.servidorAEscoltar();
        
        // Aceptar peticiones mientras no se deba salir
        while (!servidorXat.sortir) {
            try {
                // Aceptar cliente
                Socket clientSocket = servidorXat.serverSocket.accept();
                System.out.println("Client connectat: " + clientSocket.getInetAddress());
                
                // Crear gestor e iniciarlo
                GestorClients gestorClients = new GestorClients(clientSocket, servidorXat);
                Thread thread = new Thread(gestorClients);
                thread.start();
                
            } catch (IOException e) {
                if (!servidorXat.sortir) {
                    System.err.println("Error al acceptar client: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        
        // Parar el servidor
        servidorXat.pararServidor();
    }
}