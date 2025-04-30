import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;

public class FilServidorXat extends Thread {
    private ObjectInputStream in;
    private String nomClient;
    
    public FilServidorXat(ObjectInputStream in, String nomClient) {
        this.in = in;
        this.nomClient = nomClient;
    }
    
    @Override
    public void run() {
        try {
            String missatge;
            // Recibir mensajes hasta recibir MSG_SORTIR
            while (true) {
                // Leer el mensaje del cliente
                missatge = (String) in.readObject();
                
                // Comprobar si es el mensaje de salida
                if (missatge.equalsIgnoreCase(ServidorXat.MSG_SORTIR)) {
                    System.out.println("Fil de xat finalitzat.");
                    break;
                }
                
                // Mostrar el mensaje recibido
                System.out.println("Rebut: " + missatge);
            }
        } catch (EOFException e) {
            System.out.println("El client ha tancat la connexió.");
        } catch (IOException e) {
            // Solo mostrar mensaje de error si no se está cerrando la aplicación
            if (!Thread.currentThread().isInterrupted()) {
                System.err.println("Error de lectura: " + e.getMessage());
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Tipus d'objecte desconegut: " + e.getMessage());
        }
    }
}