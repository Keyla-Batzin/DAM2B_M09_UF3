import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Fitxer {
    private final String nom;
    private byte[] contingut;

    public Fitxer(String nom) {
        this.nom = nom;
    }

    public byte[] getContingut() {
        if (contingut != null) return contingut;
        try {
            File fitxer = new File(nom);
            if (!fitxer.exists() || !fitxer.isFile() || !fitxer.canRead()) return null;
            contingut = Files.readAllBytes(Paths.get(nom));
            return contingut;
        } catch (IOException e) {
            return null;
        }
    }
}
