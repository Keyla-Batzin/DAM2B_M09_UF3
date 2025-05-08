import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Client {
    // Constantes
    private static final int PORT = 9999;
    private static final String HOST = "localhost";
    private static final String DIR_ARRIBADA = "/tmp";
    
    // Socket y streams
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    
    /**
     * Método para conectar con el servidor
     */
    public void connectar() {
        try {
            // Abrir el socket al servidor
            socket = new Socket(HOST, PORT);
            System.out.println("Connectat a servidor en " + HOST + ":" + PORT);
            
            // Crear los streams de entrada y salida
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            
        } catch (IOException e) {
            System.err.println("Error al connectar amb el servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Método para recibir ficheros del servidor
     */
    public void rebreFitxers() {
        try {
            // Leer de consola el nombre del fichero a recibir
            Scanner scanner = new Scanner(System.in);
            System.out.print("Introdueix la ruta completa del fitxer a rebre: ");
            String rutaFitxer = scanner.nextLine();
            
            // Enviar el nombre del fichero al servidor
            out.writeObject(rutaFitxer);
            System.out.println("Sol·licitud enviada per a: " + rutaFitxer);
            
            // Recibir el contenido del fichero
            byte[] contingut = (byte[]) in.readObject();
            
            // Comprobar si el contenido es válido
            if (contingut.length > 0) {
                // Extraer solo el nombre del fichero de la ruta completa
                String nomFitxer = new File(rutaFitxer).getName();
                // Crear la ruta de destino
                String rutaDestino = DIR_ARRIBADA + File.separator + nomFitxer;
                
                // Guardar el fichero en el directorio de llegada
                Path outputPath = Paths.get(rutaDestino);
                Files.write(outputPath, contingut);
                
                System.out.println("Fitxer rebut i guardat a: " + rutaDestino);
                System.out.println("Mida del fitxer: " + contingut.length + " bytes");
            } else {
                System.out.println("Error: El fitxer sol·licitat no existeix al servidor o està buit");
            }
            
            scanner.close();
            
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error al rebre el fitxer: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Método para cerrar la conexión
     */
    public void tancarConnexio() {
        try {
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            System.out.println("Connexió tancada amb el servidor");
        } catch (IOException e) {
            System.err.println("Error al tancar la connexió: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Método principal
     */
    public static void main(String[] args) {
        Client client = new Client();
        client.connectar();
        client.rebreFitxers();
        client.tancarConnexio();
    }
}