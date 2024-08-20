package apptcp_mensajeindividual;

import java.awt.*;
import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PrincipalSrv extends javax.swing.JFrame {
    private final int PORT = 12345;
    private ServerSocket serverSocket;
    private ExecutorService threadPool;
    private HashMap<PrintWriter, String> clientWriters = new HashMap<>();


    public PrincipalSrv() {
        initComponents();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        this.setTitle("Servidor ..."); 

        bIniciar = new javax.swing.JButton(); 
        bSalir = new javax.swing.JButton();         
        bDesconectar = new javax.swing.JButton(); // Nuevo botón para desconectar el servidor
        jLabel1 = new javax.swing.JLabel(); 
        mensajesTxt = new JTextArea(); 
        jScrollPane1 = new javax.swing.JScrollPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE); // Configura la operación de cierre de la ventana.
        getContentPane().setLayout(null); // Establece el diseño del contenedor en nulo.

        //BOTON INICIAR SERVIDOR
        bIniciar.setFont(new java.awt.Font("Spectral", 1, 11)); 
        bIniciar.setText("INICIAR SERVIDOR"); 
        bIniciar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bIniciarActionPerformed(evt); 
            }
        });
        getContentPane().add(bIniciar); 
        bIniciar.setBounds(20, 50, 410, 40); 
        bIniciar.setBackground(Color.WHITE); 
        bIniciar.setForeground(Color.BLACK); 
        
        
        //BOTON SALIR
        bSalir.setFont(new java.awt.Font("Spectral", 1, 11)); 
        bSalir.setText("SALIR"); 
        bSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bSalirActionPerformed(evt); 
            }
        });
        getContentPane().add(bSalir); 
        bSalir.setBounds(20, 220, 410, 40); 
        bSalir.setBackground(Color.WHITE); 
        bSalir.setForeground(Color.BLACK); 
        
        //TITULO SERVIDOR
        jLabel1.setFont(new java.awt.Font("Spectral", 1, 12)); 
        jLabel1.setText("SERVIDOR TCP"); 
        getContentPane().add(jLabel1); 
        jLabel1.setBounds(200, 10, 160, 17);

        // ENTRADA DE MENSAJES
        mensajesTxt.setColumns(25); 
        mensajesTxt.setRows(5); 

        jScrollPane1.setViewportView(mensajesTxt); // Configura el scroll pane para mostrar el área de texto.
        getContentPane().add(jScrollPane1); 
        jScrollPane1.setBounds(20, 110, 410, 100); 

        // CONFIGURACIONES VENTANA
        setSize(new java.awt.Dimension(465, 320)); 
        this.getContentPane().setBackground(Color.WHITE); // 
        setLocationRelativeTo(null); // Centra la ventana en la pantalla.
    }
    
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PrincipalSrv().setVisible(true);
            }
        });
    }

    private void bIniciarActionPerformed(java.awt.event.ActionEvent evt) {
        iniciarServidor(); // Llama al método para iniciar el servidor.
    }
    
    private void bSalirActionPerformed(java.awt.event.ActionEvent evt) {
        
        System.exit(0); // Sale de la aplicación
    }

    private void iniciarServidor() {
        // Inicia el servidor en un nuevo hilo
        threadPool = Executors.newCachedThreadPool(); // Crea un pool de hilos para manejar conexiones concurrentes
        new Thread(new Runnable() {
            public void run() {
                try {
                    InetAddress addr = InetAddress.getLocalHost(); // Obtiene la dirección IP local del servidor
                    serverSocket = new ServerSocket(PORT); // Crea un ServerSocket para escuchar conexiones en el puerto especificado
                    mensajesTxt.append("Servidor TCP en ejecución: " + addr + " ,Puerto " + serverSocket.getLocalPort() + "\n");

                    while (true) {
                        // Acepta una conexión entrante de un cliente
                        Socket clientSocket = serverSocket.accept();
                        // Asigna el manejo del cliente a un hilo del pool
                        threadPool.execute(new ClientHandler(clientSocket));
                    }
                } catch (IOException ex) {
                    ex.printStackTrace(); // Imprime la traza del error en caso de excepción
                    InetAddress addr;
                    try {
                        addr = InetAddress.getLocalHost(); // Obtiene la dirección IP local del servidor
                        mensajesTxt.append("Servidor TCP desconectado: " + addr + " ,Puerto " + serverSocket.getLocalPort() + "\n"); // Muestra mensaje de error en la interfaz
                
                    } catch (UnknownHostException ex1) {
                        ex.printStackTrace(); // Imprime la traza del error en caso de excepción
                    }
                    
                }
            }
        }).start(); // Inicia el hilo
    }

    private class ClientHandler implements Runnable {
        private final Socket clientSocket; // Socket del cliente
        private BufferedReader in; // BufferedReader para recibir mensajes
        private PrintWriter out; // PrintWriter para enviar mensajes
        private String clientName; // Nombre del cliente

        public ClientHandler(Socket socket) {
            this.clientSocket = socket; // Inicializa el socket del cliente
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); // Inicializa el BufferedReader
                out = new PrintWriter(clientSocket.getOutputStream(), true); // Inicializa el PrintWriter

                clientName = in.readLine(); // Lee el nombre del cliente (primer mensaje recibido)

                synchronized (clientWriters) {
                    clientWriters.put(out, clientName); // Asocia el PrintWriter con el nombre del cliente
                    enviarListaUsuarios(); // Envia la lista de usuarios a todos los clientes
                }

                mensajesTxt.append(clientName + " se ha unido.\n"); // Notifica que un nuevo cliente se ha unido

                String line;
                while ((line = in.readLine()) != null) { // Lee mensajes del cliente
                    line = line.trim(); // Elimina espacios innecesarios
                    mensajesTxt.append(clientName + ": " + line + "\n"); // Muestra el mensaje en la interfaz

                    if (line.startsWith("TO:")) {
                        // Maneja los mensajes privados
                        String[] parts = line.split(":", 3);
                        if (parts.length == 3) {
                            String destinatario = parts[1].trim();
                            String mensaje = parts[2].trim();
                            enviarMensajePrivado(clientName, destinatario, mensaje); // Envía el mensaje privado
                        }
                    }
                }

            } catch (IOException ex) {
                ex.printStackTrace(); // Imprime la traza del error en caso de excepción
                mensajesTxt.append("Error de cliente: " + ex.getMessage() + "\n"); // Muestra mensaje de error en la interfaz

            } finally {
                try {
                    clientSocket.close(); // Cierra el socket del cliente
                } catch (IOException e) {
                    e.printStackTrace(); // Imprime la traza del error en caso de excepción
                }
                synchronized (clientWriters) {
                    clientWriters.remove(out); // Elimina el cliente desconectado del registro
                    if (clientName != null) {
                        mensajesTxt.append(clientName + " se ha desconectado.\n"); // Notifica que el cliente se ha desconectado
                        enviarListaUsuarios(); // Actualiza la lista de usuarios conectados
                    }
                }
            }
        }

        private void enviarListaUsuarios() {
            // Envía la lista de usuarios a todos los clientes conectados
            StringBuilder userList = new StringBuilder("USERLIST:");
            for (String user : clientWriters.values()) {
                userList.append(user).append(","); // Agrega cada usuario a la lista
            }
            // Enviar la lista a todos los clientes
            for (PrintWriter writer : clientWriters.keySet()) {
                writer.println(userList.toString()); // Envía la lista de usuarios a cada cliente
            }
        }

        private void enviarMensajePrivado(String remitente, String destinatario, String mensaje) {
            // Envía un mensaje privado al destinatario especificado
            synchronized (clientWriters) {
                for (HashMap.Entry<PrintWriter, String> entry : clientWriters.entrySet()) {
                    if (entry.getValue().equals(destinatario)) {
                        entry.getKey().println("FROM:" + remitente + ":" + mensaje); // Envía el mensaje al destinatario
                        break; // Salir del bucle después de enviar el mensaje
                    }
                }
            }
            mensajesTxt.append(remitente + " a " + destinatario + ": " + mensaje + "\n"); // Muestra el mensaje enviado en la interfaz
        }
    }


    private javax.swing.JButton bIniciar;
    private javax.swing.JButton bSalir;
    private javax.swing.JButton bDesconectar; 
    private javax.swing.JLabel jLabel1;
    private javax.swing.JTextArea mensajesTxt;
    private javax.swing.JScrollPane jScrollPane1;
}
