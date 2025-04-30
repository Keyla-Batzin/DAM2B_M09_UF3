import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;

public class FilLectorCX extends Thread {
    private ObjectInputStream in;
    
    public FilLectorCX(ObjectInputStream in) {
        this.in = in;
    }
    

    @Override
    public void run() {
        System.out.println("Fil de lectura iniciat");
        
        try {
            String missatge;
            // Recibir mensajes hasta que se cierre la conexión
            while (true) {
                // Leer mensaje del servidor
                missatge = (String) in.readObject();
                
                // Mostrar el mensaje recibido
                System.out.println("Rebut: " + missatge);
            }
        } catch (EOFException e) {
            System.out.println("El servidor ha tancat la connexió.");
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