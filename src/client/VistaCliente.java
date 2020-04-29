package client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;


//**********************************************************
//******************** VISTA CLIENTE ***********************
//**********************************************************


/**
 * Vista del cliente
 */
class VistaCliente extends JFrame implements ActionListener {
    private JLabel texto;
    private JLabel texto2;
    private JLabel texto3;
    private JButton btnBuscar;
    private JButton btnBuscarE;
    private JButton btnBuscarN;
    private JButton btnBuscarD;
    private JButton btnGenRSA;
    private JButton btnGenRSA2;
    private JTextField txt;
    private JTextField claveE;
    private JTextField claveN;
    private JTextField claveD;
    static byte[] archivo = new byte[0];

    /**
     * Definición objetos de la vista
     */
    public VistaCliente() {
        super("Firma tu fichero");
        texto=new JLabel("Sube aquí tu fichero.");
        texto.setBounds(50,10,220,40);
        add(texto);
        setLayout(null);

        txt = new JTextField(30);
        txt.setBounds(50,50,300,25);
        add(txt);

        btnBuscar = new JButton("Buscar...");
        btnBuscar.addActionListener(this);
        btnBuscar.setBounds(50,80,100,25);
        add(btnBuscar);

        texto2=new JLabel("Genera tu firma completa.");
        texto2.setBounds(50,110,220,40);
        add(texto2);

        btnGenRSA = new JButton("Generar RSA");
        btnGenRSA.addActionListener(this);
        btnGenRSA.setBounds(50,150,220,30);
        add(btnGenRSA);

        texto3=new JLabel("Genera tu firma escogiendo tus claves.");
        texto3.setBounds(50,210,250,30);
        add(texto3);

        claveE = new JTextField(30);
        claveE.setBounds(50,250,200,25);
        add(claveE);

        btnBuscarE = new JButton("Clave 'e'");
        btnBuscarE.addActionListener(this);
        btnBuscarE.setBounds(255,250,100,25);
        add(btnBuscarE);

        claveN = new JTextField(30);
        claveN.setBounds(50,290,200,25);
        add(claveN);

        btnBuscarN = new JButton("Clave 'n'");
        btnBuscarN.addActionListener(this);
        btnBuscarN.setBounds(255,290,100,25);
        add(btnBuscarN);

        claveD = new JTextField(30);
        claveD.setBounds(50,330,200,25);
        add(claveD);

        btnBuscarD = new JButton("Clave 'd'");
        btnBuscarD.addActionListener(this);
        btnBuscarD.setBounds(255,330,100,25);
        add(btnBuscarD);

        btnGenRSA2 = new JButton("Generar RSA");
        btnGenRSA2.addActionListener(this);
        btnGenRSA2.setBounds(50,370,220,30);
        add(btnGenRSA2);

    }


    /**
     * Realiza las tareas de escuchar los objetos de la vista.
     * @param e Objetos que se pulsa
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        ClientBlindSignature client = new ClientBlindSignature();

        if (e.getSource() == btnBuscar) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

            int result = fileChooser.showOpenDialog(this);

            if (result != JFileChooser.CANCEL_OPTION) {

                File fileName = fileChooser.getSelectedFile();

                if ((fileName == null) || (fileName.getName().equals(""))) {
                    txt.setText("...");
                } else {
                    txt.setText(fileName.getAbsolutePath());
                    //TODO Esto no va
                    client.pathFile(fileName.toPath());
                    archivo = txt.toString().getBytes();
                    System.out.println(txt);
                    System.out.println(archivo.length);
                }
            }
        }

        //TODO Cambiar, cuando se pulsa que genere rsa, genera el rsa pero no lo envía todavía
        if (e.getSource() == btnGenRSA) {
            System.out.println(archivo.length);
            client.clientExec1(archivo);
        }

        if (e.getSource() == btnBuscarE) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            String cadena;

            int result = fileChooser.showOpenDialog(this);

            if (result != JFileChooser.CANCEL_OPTION) {

                File fileName = fileChooser.getSelectedFile();

                if ((fileName == null) || (fileName.getName().equals(""))) {
                    claveE.setText("...");
                } else {
                    // Con esto leemos el contenido de la clave
                    try {
                        claveE.setText(fileName.getAbsolutePath());
                        fileName.createNewFile();
                        Reader targetReader = new FileReader(fileName);
                        BufferedReader b = new BufferedReader(targetReader);
                        while ((cadena = b.readLine()) != null) {
                            System.out.println(cadena);
                        }
                        b.close();
                    } catch (Exception ex){
                        System.out.println("Algo ha ido mal con el fichero");
                    }
                }
            }
        }

        if (e.getSource() == btnBuscarN) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

            int result = fileChooser.showOpenDialog(this);

            if (result != JFileChooser.CANCEL_OPTION) {

                File fileName = fileChooser.getSelectedFile();

                if ((fileName == null) || (fileName.getName().equals(""))) {
                    claveN.setText("...");
                } else {
                    claveN.setText(fileName.getAbsolutePath());
                }
            }
        }

        if (e.getSource() == btnBuscarD) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

            int result = fileChooser.showOpenDialog(this);

            if (result != JFileChooser.CANCEL_OPTION) {

                File fileName = fileChooser.getSelectedFile();

                if ((fileName == null) || (fileName.getName().equals(""))) {
                    claveD.setText("...");
                } else {
                    claveD.setText(fileName.getAbsolutePath());
                }
            }
        }

        if (e.getSource() == btnGenRSA2) {
            System.out.println();
            //client.clientExec1(archivo);
        }
    }
}
