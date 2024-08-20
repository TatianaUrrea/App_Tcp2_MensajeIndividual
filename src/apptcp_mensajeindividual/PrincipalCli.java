package apptcp_mensajeindividual;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;

public class PrincipalCli extends javax.swing.JFrame {
    private final int PORT = 12345;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private final String clienteNombre;
    private DefaultListModel<String> usuariosModel;    
    private HashMap<String, StringBuilder> conversaciones = new HashMap<>();
    
    public PrincipalCli(String clienteNombre) {
        this.clienteNombre = clienteNombre;
        initComponents();
        Estado("DESCONECTADO");
        Nombre(clienteNombre);
        usuariosModel = new DefaultListModel<>();
        usuariosList.setModel(usuariosModel);
        jScrollPane2.setViewportView(usuariosList);
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        this.setTitle("Cliente");

        bConectar = new javax.swing.JButton();
        jLabelNombre = new javax.swing.JLabel();
        jLabelEstado = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        mensajesTxt = new javax.swing.JTextArea();
        mensajeTxt = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        btEnviar = new javax.swing.JButton();
        bDesconectar = new javax.swing.JButton();
        bSalir = new javax.swing.JButton();
        usuariosList = new javax.swing.JList<>();
        jScrollPane2 = new javax.swing.JScrollPane(usuariosList);
        jLabelDestinatario = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(null);

        // BOTON CONECTAR
        bConectar.setFont(new java.awt.Font("Spectral", 1, 11));
        bConectar.setText("CONECTAR");
        bConectar.addActionListener((ActionEvent evt) -> bConectarActionPerformed(evt));
        getContentPane().add(bConectar);
        bConectar.setBounds(150, 45, 200, 30);
        bConectar.setBackground(Color.WHITE); // Fondo blanco
        bConectar.setForeground(Color.BLACK); // Texto negro

        // TITULO CLIENTE CON ESTADO
        jLabelNombre.setFont(new java.awt.Font("Spectral", 2, 10));
        getContentPane().add(jLabelNombre);
        jLabelNombre.setBounds(45, 10, 300, 17);
        jLabelEstado.setFont(new java.awt.Font("Spectral", 2, 10));
        getContentPane().add(jLabelEstado);
        jLabelEstado.setBounds(350, 10, 300, 17);
        
        // LISTA DE USUARIOS CONECTADOS
        usuariosList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        usuariosList.addListSelectionListener((ListSelectionEvent evt) -> usuariosListValueChanged(evt));
        jScrollPane2.setViewportView(usuariosList);
        getContentPane().add(jScrollPane2);
        jScrollPane2.setBounds(20, 90, 150, 170);

        // NOMBRE DEL DESTINATARIO
        jLabelDestinatario.setFont(new java.awt.Font("Spectral", 1, 11));
        jLabelDestinatario.setText("Destinatario: ");
        getContentPane().add(jLabelDestinatario);
        jLabelDestinatario.setBounds(20, 65, 200, 30);

        // MENSAJE RECIBIDO
        mensajesTxt.setColumns(20);
        mensajesTxt.setRows(5);
        mensajesTxt.setEnabled(false);
        jScrollPane1.setViewportView(mensajesTxt);
        getContentPane().add(jScrollPane1);
        jScrollPane1.setBounds(180, 90, 270, 170);

        // MENSAJE A ENVIAR
        jLabel2.setFont(new java.awt.Font("Spectral", 1, 11));
        jLabel2.setText("Mensaje:");
        getContentPane().add(jLabel2);
        jLabel2.setBounds(20, 270, 120, 30);

        mensajeTxt.setFont(new java.awt.Font("Spectral", 0, 11));
        getContentPane().add(mensajeTxt);
        mensajeTxt.setBounds(20, 300, 320, 30);

        // BOTON ENVIAR
        btEnviar.setFont(new java.awt.Font("Spectral", 1, 11));
        btEnviar.setText("ENVIAR");
        btEnviar.addActionListener((ActionEvent evt) -> btEnviarActionPerformed(evt));
        getContentPane().add(btEnviar);
        btEnviar.setBounds(350, 300, 100, 30);
        btEnviar.setBackground(Color.WHITE); // Fondo blanco
        btEnviar.setForeground(Color.BLACK); // Texto negro

        // BOTON DESCONECTAR
        bDesconectar.setFont(new java.awt.Font("Spectral", 1, 11));
        bDesconectar.setText("DESCONECTAR");
        bDesconectar.addActionListener((ActionEvent evt) -> bDesconectarActionPerformed(evt));
        getContentPane().add(bDesconectar);
        bDesconectar.setBounds(100, 350, 150, 30);
        bDesconectar.setBackground(Color.WHITE); // Fondo blanco
        bDesconectar.setForeground(Color.BLACK); // Texto negro

        // BOTON SALIR
        bSalir.setFont(new java.awt.Font("Spectral", 1, 11));
        bSalir.setText("SALIR");
        bSalir.addActionListener((ActionEvent evt) -> bSalirActionPerformed(evt));
        getContentPane().add(bSalir);
        bSalir.setBounds(260, 350, 150, 30);
        bSalir.setBackground(Color.WHITE); // Fondo blanco
        bSalir.setForeground(Color.BLACK); // Texto negro

        // DIMENSIONES VENTANA
        setSize(new java.awt.Dimension(491, 435));
        setLocationRelativeTo(null);
        this.getContentPane().setBackground(Color.WHITE);
    }

    private void Estado(String status) {
        // Actualiza el estado del cliente en la interfaz gráfica
        jLabelEstado.setText(status);
    }

    private void Nombre(String clienteNombre) {
        // Actualiza el nombre del cliente en la interfaz gráfica
        jLabelNombre.setText("CLIENTE: " + clienteNombre);
    }

    private void bConectarActionPerformed(java.awt.event.ActionEvent evt) {
        // Acción cuando se presiona el botón "Conectar"
        if (clienteNombre.isEmpty()) { // Verificar si el nombre del cliente está vacío
            JOptionPane.showMessageDialog(this, "Por favor, ingrese su nombre."); // Mostrar mensaje de advertencia
            return;
        }
        conectar(); // Llamar al método para conectar al servidor
    }

    private void btEnviarActionPerformed(java.awt.event.ActionEvent evt) {
        // Acción cuando se presiona el botón "Enviar"
        enviarMensaje(); // Llamar al método para enviar un mensaje
    }

    private void bDesconectarActionPerformed(java.awt.event.ActionEvent evt) {
        closeConnection();
        Estado("DESCONECTADO"); 
        bConectar.setEnabled(true);
        bDesconectar.setEnabled(false);
        mensajeTxt.setEnabled(false);
        btEnviar.setEnabled(false);
        JOptionPane.showMessageDialog(this, "Desconectado del servidor.");
    }

    private void bSalirActionPerformed(java.awt.event.ActionEvent evt) {
        // Acción cuando se presiona el botón "Salir"
        closeConnection(); // Cerrar la conexión si está abierta
        System.exit(0); // Salir de la aplicación
    }

    private void usuariosListValueChanged(javax.swing.event.ListSelectionEvent evt) {
        // Acción cuando cambia la selección en la lista de usuarios
        if (!evt.getValueIsAdjusting()) { // Verificar si la selección ha cambiado completamente
            String selectedUser = usuariosList.getSelectedValue(); // Obtener el usuario seleccionado
            if (selectedUser != null) {
                jLabelDestinatario.setText("Destinatario: " + selectedUser); // Actualizar el destinatario en la interfaz

                // Cargar la conversación previa con el usuario seleccionado
                StringBuilder conversacion = conversaciones.get(selectedUser);
                if (conversacion != null) {
                    mensajesTxt.setText(conversacion.toString()); // Mostrar conversación previa
                } else {
                    mensajesTxt.setText(""); // Limpiar el área de mensajes si no hay conversación previa
                }
            }
        }
    }

    private void conectar() {
        // Método para conectar al servidor
        try {
            if (socket == null || socket.isClosed()) { // Verificar si el socket es nulo o está cerrado
                // Crear una nueva conexión al servidor
                socket = new Socket("localhost", PORT);
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out.println(clienteNombre); // Enviar el nombre del cliente al servidor
                
                // Hilo para escuchar mensajes del servidor
                new Thread(() -> {
                    try {
                        String fromServer;
                        while ((fromServer = in.readLine()) != null) { // Leer mensajes del servidor
                            if (fromServer.startsWith("USERLIST:")) {
                                actualizarListaUsuarios(fromServer); // Actualizar la lista de usuarios
                            } else {
                                final String mensajeFinal = fromServer;
                                SwingUtilities.invokeLater(() -> procesarMensajeEntrante(mensajeFinal)); // Procesar mensaje entrante
                            }
                        }

                        // Actualizar el estado de la interfaz después de conectar
                        Estado("CONECTADO");
                        jScrollPane2.setEnabled(true); // Habilitar el scroll pane

                    } catch (IOException ex) {
                        reconectar();  // Intentar reconexión si no se pudo conectar
                    }
                }).start();

                Estado("CONECTADO"); // Actualizar el estado a conectado
                bConectar.setEnabled(false);
                bDesconectar.setEnabled(true);
                mensajeTxt.setEnabled(true);
                btEnviar.setEnabled(true);
                usuariosList.setEnabled(true);
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "No se pudo conectar al servidor. Verifique la conexión e intente nuevamente.");
            reconectar(); // Intentar reconexión si no se pudo conectar
        }
    }

    private void actualizarListaUsuarios(String userListMessage) {
        // Actualizar la lista de usuarios conectados
        SwingUtilities.invokeLater(() -> {
            usuariosModel.clear(); // Limpiar la lista de usuarios
            String[] users = userListMessage.substring(9).split(","); // Obtener los usuarios de la cadena
            for (String user : users) {
                if (!user.equals(clienteNombre) && !user.isEmpty()) {
                    usuariosModel.addElement(user); // Agregar usuarios a la lista
                }
            }
        });
    }

    private void enviarMensaje() {
        // Método para enviar un mensaje al servidor
        if (out != null) {
            String destinatario = usuariosList.getSelectedValue(); // Obtener el destinatario seleccionado
            if (destinatario == null || destinatario.isEmpty()) { // Verificar si se ha seleccionado un destinatario
                JOptionPane.showMessageDialog(this, "Por favor, seleccione un destinatario."); // Mostrar mensaje de advertencia
                return;
            }
            String mensaje = mensajeTxt.getText(); // Obtener el mensaje del campo de texto
            if (mensaje.isEmpty()) { // Verificar si el mensaje está vacío
                JOptionPane.showMessageDialog(this, "Por favor, ingrese un mensaje."); // Mostrar mensaje de advertencia
                return;
            }

            // Enviar mensaje al servidor con el formato "TO:DESTINATARIO:mensaje"
            out.println("TO:" + destinatario + ":" + mensaje);
            // Actualizar el área de mensajes con el mensaje enviado
            StringBuilder conversacion = conversaciones.computeIfAbsent(destinatario, k -> new StringBuilder());
            conversacion.append(clienteNombre + ": " + mensaje + "\n");
            mensajesTxt.append("Tú: " + mensaje + "\n");
            mensajeTxt.setText(""); // Limpiar el campo de texto después de enviar el mensaje
        }else {
            JOptionPane.showMessageDialog(this, "No estás conectado al servidor.");
        }
        
    }
    
    private void reconectar() {
        Estado("DESCONECTADO"); // Actualizar estado a desconectado
        bConectar.setEnabled(true);
        bDesconectar.setEnabled(false);
        mensajeTxt.setEnabled(false);
        btEnviar.setEnabled(false);
    }

    private void procesarMensajeEntrante(String mensaje) {
        // Procesar un mensaje entrante del servidor
        if (mensaje.startsWith("FROM:")) {
            String[] parts = mensaje.split(":", 3);
            if (parts.length == 3) {
                String remitente = parts[1];
                String contenido = parts[2];

                // Actualizar la conversación del remitente
                StringBuilder conversacion = conversaciones.computeIfAbsent(remitente, k -> new StringBuilder());
                conversacion.append(remitente + ": " + contenido + "\n");

                // Actualizar el área de mensajes si el remitente es el destinatario actual
                String destinatarioActual = jLabelDestinatario.getText().replace("Destinatario: ", "").trim();
                if (remitente.equals(destinatarioActual)) {
                    mensajesTxt.append(remitente + ": " + contenido + "\n");
                } else {
                    // Notificar al usuario sobre el nuevo mensaje
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this, 
                            "Nuevo mensaje de " + remitente + ":\n" + contenido, 
                            "Nuevo Mensaje", 
                            JOptionPane.INFORMATION_MESSAGE);
                    });
                }
            }
        } else if (mensaje.startsWith("SYSTEM:")) {
            // Mostrar mensajes del sistema (como notificaciones de no entrega)
            mensajesTxt.append(mensaje.substring(7) + "\n");
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this, 
                    mensaje.substring(7), 
                    "Notificación del Sistema", 
                    JOptionPane.INFORMATION_MESSAGE);
            });
        } else {
            mensajesTxt.append(mensaje + "\n"); // Mostrar mensaje genérico
        }
    }

    private void closeConnection() {
        // Método para cerrar la conexión al servidor
        try {
            if (socket != null) {
                //out.close(); // Cerrar el escritor
                //in.close(); // Cerrar el lector
                out.println(clienteNombre + " se ha desconectado.");
                socket.close(); // Cerrar el socket
                Estado("DESCONECTADO"); // Actualizar el estado a desconectado
                bConectar.setEnabled(true); // Habilitar el botón de conectar
                bDesconectar.setEnabled(false); // Deshabilitar el botón de desconectar
                mensajeTxt.setEnabled(false); // Deshabilitar el campo de texto del mensaje
                btEnviar.setEnabled(false); // Deshabilitar el botón de enviar
                usuariosList.setEnabled(false); // Deshabilitar la lista de usuarios
                SwingUtilities.invokeLater(() -> usuariosModel.clear()); // Limpiar la lista de usuarios
            }
        } catch (IOException ex) {
            ex.printStackTrace(); // Imprimir la traza de la excepción en caso de error
        }
    }

    // Componentes de la interfaz gráfica
    private javax.swing.JButton bConectar;
    private javax.swing.JButton bDesconectar;
    private javax.swing.JButton bSalir;
    private javax.swing.JButton btEnviar;
    private javax.swing.JLabel jLabelNombre;
    private javax.swing.JLabel jLabelEstado;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabelDestinatario;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea mensajesTxt;
    private javax.swing.JTextField mensajeTxt;
    private javax.swing.JList<String> usuariosList;

}