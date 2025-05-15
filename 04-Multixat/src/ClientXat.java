import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class ClientXat {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean sortir = false;

    public void connecta() {
        try {
            socket = new Socket("localhost", 9999);
            out = new ObjectOutputStream(socket.getOutputStream());
            System.out.println("Client connectat a " + socket.getLocalPort());
            System.out.println("Flux d'entrada i sortida creat.");
        } catch (IOException e) {
            System.out.println("Error en la connexió.");
            sortir = true;
        }
    }

    public void enviarMissatge(String missatge) {
        try {
            if (out != null) {
                out.writeObject(missatge);
                out.flush();
                System.out.println("Enviant missatge: " + missatge);
            } else {
                System.out.println("out null. Sortint...");
                sortir = true;
            }
        } catch (IOException e) {
            System.out.println("Error enviant missatge.");
            sortir = true;
        }
    }

    public void tancarClient() {
        try {
            if (in != null) {
                in.close();
                System.out.println("Flux d'entrada tancat.");
            }
            if (out != null) {
                out.close();
                System.out.println("Flux de sortida tancat.");
            }
            if (socket != null) {
                socket.close();
                System.out.println("Tancant client...");
            }
        } catch (IOException e) {
            System.out.println("Error tancant el client.");
        }
    }

    public void executa() {
        try {
            in = new ObjectInputStream(socket.getInputStream());
            System.out.println("DEBUG: Iniciant rebuda de missatges...");
            while (!sortir) {
                String missatgeCru = (String) in.readObject();
                String codi = Missatge.getCodiMissatge(missatgeCru);
                String[] parts = Missatge.getPartsMissatge(missatgeCru);

                if (codi == null) continue;

                switch (codi) {
                    case Missatge.CODI_SORTIR_TOTS:
                        sortir = true;
                        break;
                    case Missatge.CODI_MSG_PERSONAL:
                        if (parts.length >= 3) {
                            String remitent = parts[1];
                            String msg = parts[2];
                            System.out.println("Missatge de (" + remitent + "): " + msg);
                        }
                        break;
                    case Missatge.CODI_MSG_GRUP:
                        if (parts.length >= 2) {
                            System.out.println("Grup: " + parts[1]);
                        }
                        break;
                    default:
                        System.out.println("Error: codi no reconegut.");
                }
            }
        } catch (Exception e) {
            System.out.println("Error rebent missatge. Sortint...");
        } finally {
            tancarClient();
        }
    }

    public void ajuda() {
        System.out.println("---------------------");
        System.out.println("Comandes disponibles:");
        System.out.println("1.- Conectar al servidor (primer pass obligatori)");
        System.out.println("2.- Enviar missatge personal");
        System.out.println("3.- Enviar missatge al grup");
        System.out.println("4.- (o línia en blanc)-> Sortir del client");
        System.out.println("5.- Finalitzar tothom");
        System.out.println("---------------------");
    }

    public String getLinea(Scanner sc, String missatge, boolean obligatori) {
        String linia;
        do {
            System.out.print(missatge);
            linia = sc.nextLine();
            if (!obligatori || !linia.trim().isEmpty()) {
                break;
            }
        } while (true);
        return linia.trim();
    }

    public static void main(String[] args) {
        ClientXat client = new ClientXat();
        Scanner sc = new Scanner(System.in);
        client.connecta();

        Thread t = new Thread(() -> client.executa());
        t.start();

        client.ajuda();

        while (!client.sortir) {
            String opcio = sc.nextLine().trim();

            if (opcio.isEmpty()) {
                client.enviarMissatge(Missatge.getMissatgeSortirClient("Adéu"));
                client.sortir = true;
            } else {
                switch (opcio) {
                    case "1":
                        String nom = client.getLinea(sc, "Introdueix el nom: ", true);
                        client.enviarMissatge(Missatge.getMissatgeConectar(nom));
                        break;
                    case "2":
                        String desti = client.getLinea(sc, "Destinatari:: ", true);
                        String text = client.getLinea(sc, "Missatge a enviar: ", true);
                        client.enviarMissatge(Missatge.getMissatgePersonal(desti, text));
                        break;
                    case "3":
                        String msg = client.getLinea(sc, "Missatge al grup: ", true);
                        client.enviarMissatge(Missatge.getMissatgeGrup(msg));
                        break;
                    case "4":
                        client.enviarMissatge(Missatge.getMissatgeSortirClient("Adéu"));
                        client.sortir = true;
                        break;
                    case "5":
                        client.enviarMissatge(Missatge.getMissatgeSortirTots("Adéu"));
                        client.sortir = true;
                        break;
                    default:
                        System.out.println("Opció no vàlida.");
                }
            }
        }

        sc.close();
    }
}
