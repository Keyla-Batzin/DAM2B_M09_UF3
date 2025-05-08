import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Fitxer {
    private String nom;
    private byte[] contingut;

    public Fitxer(String nom) {
        this.nom = nom;
        this.contingut = null;
    }

    public String getNom() {
        return nom;
    }

    /**
     * Método para obtener el contenido del fichero
     * Comprueba la existencia del fichero y carga su contenido
     */
    public byte[] getContingut() {
        if (contingut != null) {
            return contingut;
        }

        try {
            File fitxer = new File(nom);
            if (!fitxer.exists() || !fitxer.isFile() || !fitxer.canRead()) {
                System.err.println("Error: El fitxer no existeix o no es pot llegir: " + nom);
                return null;
            }

            Path path = Paths.get(nom);
            contingut = Files.readAllBytes(path);

            System.out.println("Fitxer carregat correctament: " + nom + " (" + contingut.length + " bytes)");
            return contingut;

        } catch (IOException e) {
            System.err.println("Error al llegir el fitxer: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // Método para establecer el contenido del fichero
    public void setContingut(byte[] contingut) {
        this.contingut = contingut;
    }

    // Método para guardar el contenido en un fichero
    public boolean guardarFitxer(String ruta) {
        try {

            if (contingut == null) {
                System.err.println("Error: No hi ha contingut per guardar");
                return false;
            }

            Path path = Paths.get(ruta);
            Files.write(path, contingut);

            System.out.println("Fitxer guardat correctament a: " + ruta);
            return true;

        } catch (IOException e) {
            System.err.println("Error al guardar el fitxer: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}