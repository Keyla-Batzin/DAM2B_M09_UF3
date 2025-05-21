import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class GestorClients implements Runnable {
    // Atributs
    private Socket client;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private ServidorXat servidorXat;
    private String nom;
    private boolean sortir;
    
    // Constructor con Socket y ServidorXat
    public GestorClients(Socket client, ServidorXat servidorXat) {
        this.client = client;
        this.servidorXat = servidorXat;
        this.sortir = false;
        
        // Inicializar streams
        try {
            this.oos = new ObjectOutputStream(client.getOutputStream());
            this.ois = new ObjectInputStream(client.getInputStream());
        } catch (IOException e) {
            System.err.println("Error al inicialitzar els streams: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Getter para el nombre
    public String getNom() {
        return nom;
    }
    
    // Método de ejecución como hilo
    @Override
    public void run() {
        try {
            while (!sortir) {
                // Recibir mensaje
                String missatge = (String) ois.readObject();
                
                // Procesar mensaje
                processaMissatge(missatge);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error en la comunicació amb el client: " + e.getMessage());
        } finally {
            // Cerrar socket
            try {
                if (client != null && !client.isClosed()) {
                    client.close();
                }
            } catch (IOException e) {
                System.err.println("Error al tancar el socket del client: " + e.getMessage());
            }
        }
    }
    
    // Método para enviar mensaje
    public void enviarMissatge(String missatge) {
        try {
            if (oos != null) {
                oos.writeObject(missatge);
                oos.flush();
            }
        } catch (IOException e) {
            System.err.println("Error al enviar missatge: " + e.getMessage());
        }
    }
    
    // Método para procesar mensaje
    public void processaMissatge(String missatgeCru) {
        // Extraer código
        String codi = Missatge.getCodiMissatge(missatgeCru);
        
        if (codi == null) {
            return;
        }
        
        String[] parts = Missatge.getPartsMissatge(missatgeCru);
        
        switch (codi) {
            case Missatge.CODI_CONECTAR:
                // Añadir cliente
                if (parts.length > 1) {
                    this.nom = parts[1];
                    servidorXat.afegirClient(this);
                }
                break;
                
            case Missatge.CODI_SORTIR_CLIENT:
                // Eliminar cliente y activar flag de salida
                if (nom != null) {
                    servidorXat.eliminarClient(nom);
                    sortir = true;
                }
                break;
                
            case Missatge.CODI_SORTIR_TOTS:
                // Activar flag de salida y finalizar chat
                sortir = true;
                servidorXat.finalitzarXat();
                break;
                
            case Missatge.CODI_MSG_PERSONAL:
                // Obtener destinatario y mensaje y enviar
                if (parts.length > 2) {
                    String destinatari = parts[1];
                    String missatge = parts[2];
                    servidorXat.enviarMissatgePersonal(destinatari, nom, missatge);
                }
                break;
                
            case Missatge.CODI_MSG_GRUP:
                // Enviar mensaje de grupo
                if (parts.length > 1) {
                    String missatge = parts[1];
                    servidorXat.enviarMissatgeGrup(Missatge.getMissatgeGrup("(" + nom + "): " + missatge));
                }
                break;
                
            default:
                System.out.println("Error: codi de missatge desconegut: " + codi);
                break;
        }
    }
}