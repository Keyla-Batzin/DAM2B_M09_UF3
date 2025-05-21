import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class ClientXat implements Runnable {
    // Constantes
    private static final int PORT = 9999;
    private static final String HOST = "localhost";
    
    // Atributs
    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private boolean sortir;
    
    // Constructor
    public ClientXat() {
        this.sortir = false;
    }
    
    // Método para conectar con el servidor
    public void connecta() {
        try {
            // Abrir el socket al servidor
            socket = new Socket(HOST, PORT);
            System.out.println("Client connectat a " + HOST + ":" + PORT);
            
            // Inicializar streams
            oos = new ObjectOutputStream(socket.getOutputStream());
            System.out.println("Flux d'entrada i sortida creat.");
        } catch (IOException e) {
            System.err.println("Error al connectar amb el servidor: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    // Método para enviar mensaje
    public void enviarMissatge(String missatge) {
        try {
            if (oos != null) {
                System.out.println("Enviant missatge: " + missatge);
                oos.writeObject(missatge);
                oos.flush();
            } else {
                System.out.println("oos null. Sortint...");
                sortir = true;
            }
        } catch (IOException e) {
            System.err.println("Error al enviar missatge: " + e.getMessage());
            sortir = true;
        }
    }
    
    // Método para cerrar cliente
    public void tancarClient() {
        System.out.println("Tancant client...");
        try {
            if (ois != null) {
                ois.close();
                System.out.println("Flux d'entrada tancat.");
            }
            if (oos != null) {
                oos.close();
                System.out.println("Flux de sortida tancat.");
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Error al tancar el client: " + e.getMessage());
        }
    }
    
    // Método de ejecución como hilo para recibir mensajes
    @Override
    public void run() {
        try {
            // Inicializar el ObjectInputStream
            ois = new ObjectInputStream(socket.getInputStream());
            System.out.println("DEBUG: Iniciant rebuda de missatges...");
            
            while (!sortir) {
                // Recibir mensaje
                String missatgeCru = (String) ois.readObject();
                
                // Extraer código
                String codi = Missatge.getCodiMissatge(missatgeCru);
                
                if (codi == null) {
                    continue;
                }
                
                // Obtener partes del mensaje
                String[] parts = Missatge.getPartsMissatge(missatgeCru);
                
                switch (codi) {
                    case Missatge.CODI_SORTIR_TOTS:
                        // Activar flag de salida y salir del programa
                        sortir = true;
                        System.out.println("El servidor ha tancat el xat. Sortint...");
                        tancarClient();
                        System.exit(0);
                        break;
                        
                    case Missatge.CODI_MSG_PERSONAL:
                        // Mostrar mensaje personal
                        if (parts.length > 2) {
                            String remitent = parts[1];
                            String missatge = parts[2];
                            System.out.println("Missatge de (" + remitent + "): " + missatge);
                        }
                        break;
                        
                    case Missatge.CODI_MSG_GRUP:
                        // Mostrar mensaje de grupo
                        if (parts.length > 1) {
                            System.out.println("Missatge de grup: " + parts[1]);
                        }
                        break;
                        
                    default:
                        System.out.println("Error: codi de missatge desconegut: " + codi);
                        break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error rebent missatge. Sortint...");
        } finally {
            tancarClient();
        }
    }
    
    // Método para mostrar ayuda
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
    
    // Método para leer línea de consola
    public String getLinea(Scanner scanner, String missatge, boolean obligatori) {
        String linea = "";
        
        do {
            System.out.print(missatge);
            linea = scanner.nextLine().trim();
            
            if (linea.isEmpty() && obligatori) {
                System.out.println("Aquest camp és obligatori.");
            } else {
                break;
            }
        } while (obligatori);
        
        return linea;
    }
    
    public static void main(String[] args) {
        ClientXat clientXat = new ClientXat();
        
        // Conectar al servidor
        clientXat.connecta();
        
        // Iniciar hilo para leer mensajes
        Thread thread = new Thread(clientXat);
        thread.start();
        
        // Mostrar ayuda
        clientXat.ajuda();
        
        Scanner scanner = new Scanner(System.in);
        
        while (!clientXat.sortir) {
            String linea = scanner.nextLine().trim();
            
            if (linea.isEmpty()) {
                clientXat.sortir = true;
                clientXat.enviarMissatge(Missatge.getMissatgeSortirClient("Adéu"));
                continue;
            }
            
            try {
                int opcio = Integer.parseInt(linea);
                
                switch (opcio) {
                    case 1:
                        // Conectar al servidor
                        String nom = clientXat.getLinea(scanner, "Introdueix el nom: ", true);
                        clientXat.enviarMissatge(Missatge.getMissatgeConectar(nom));
                        break;
                        
                    case 2:
                        // Enviar mensaje personal
                        String destinatari = clientXat.getLinea(scanner, "Destinatari:: ", true);
                        String missatgePersonal = clientXat.getLinea(scanner, "Missatge a enviar: ", true);
                        clientXat.enviarMissatge(Missatge.getMissatgePersonal(destinatari, missatgePersonal));
                        break;
                        
                    case 3:
                        // Enviar mensaje al grupo
                        String missatgeGrup = clientXat.getLinea(scanner, "Missatge a enviar: ", true);
                        clientXat.enviarMissatge(Missatge.getMissatgeGrup(missatgeGrup));
                        break;
                        
                    case 4:
                        // Salir del cliente
                        clientXat.sortir = true;
                        clientXat.enviarMissatge(Missatge.getMissatgeSortirClient("Adéu"));
                        break;
                        
                    case 5:
                        // Finalizar todos
                        clientXat.sortir = true;
                        clientXat.enviarMissatge(Missatge.getMissatgeSortirTots("Adéu"));
                        // Salir inmediatamente tras enviar el mensaje
                        System.exit(0);
                        break;
                        
                    default:
                        System.out.println("Opció no vàlida.");
                        break;
                }
                
                // Mostrar ayuda después de cada comando
                if (!clientXat.sortir) {
                    clientXat.ajuda();
                }
                
            } catch (NumberFormatException e) {
                System.out.println("Format incorrecte. Introdueix un número.");
            }
        }
        
        // Cerrar scanner
        scanner.close();
        
        // Esperar a que termine el hilo de lectura
        try {
            thread.join();
        } catch (InterruptedException e) {
            System.err.println("Error esperant fil de lectura: " + e.getMessage());
        }
        
        // Limpiar y salir
        System.exit(0);
    }
}