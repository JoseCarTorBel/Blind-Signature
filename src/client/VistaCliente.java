package client;

import javax.swing.*;
import java.awt.*;
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
    private JLabel inicio;
    private JRadioButton radio;
    private JRadioButton radio2;
    ButtonGroup grupo;
    private JButton btnBuscar;
    private JButton btnBuscarE;
    private JButton btnBuscarN;
    private JButton btnBuscarD;
    private JButton btnGenRSA;
    private JButton btnGenRSA2;
    private JButton btnPedirFirma;
    private JTextField txt;
    private JTextField claveE;
    private JTextField claveN;
    private JTextField claveD;
    static byte[] archivo = new byte[0];
    private String keyE="";
    private String keyD="";
    private String keyN="";

    private ClientBlindSignature client;


    /**
     * Definición objetos de la vista
     */
    public VistaCliente(ClientBlindSignature cliente) {
        super("Firma tu fichero");

        client = cliente;

        inicio=new JLabel("Sistema de solicitud de firma ciega");
        inicio.setBounds(50,10,300,40);
        inicio.setFont(new Font("Verdana", Font.BOLD, 16));
        grupo = new ButtonGroup();
        add(inicio);
        setLayout(null);

        texto=new JLabel("Sube aquí tu fichero.");
        texto.setBounds(50,40,220,40);
        texto.setFont(new Font("Verdana", Font.PLAIN, 14));
        grupo = new ButtonGroup();
        add(texto);

        txt = new JTextField(30);
        txt.setBounds(50,80,300,25);
        add(txt);

        btnBuscar = new JButton("Buscar...");
        btnBuscar.addActionListener(this);
        btnBuscar.setBounds(50,110,100,25);
        btnBuscar.setFont(new Font("Verdana", Font.PLAIN, 14));
        add(btnBuscar);

        radio = new JRadioButton();
        radio.setText("Genera tu firma completa.");
        radio.setBounds(50,140,220,40);
        radio.setFont(new Font("Verdana", Font.PLAIN, 14));
        add(radio);
        grupo.add(radio);

        btnGenRSA = new JButton("Generar RSA");
        btnGenRSA.addActionListener(this);
        btnGenRSA.setBounds(50,180,220,30);
        btnGenRSA.setFont(new Font("Verdana", Font.PLAIN, 14));
        add(btnGenRSA);
        //btnGenRSA.setEnabled(false);

        radio2 = new JRadioButton();
        radio2.setText("Genera tu firma escogiendo tus claves.");
        radio2.setBounds(50,240,300,30);
        radio2.setFont(new Font("Verdana", Font.PLAIN, 14));
        add(radio2);
        grupo.add(radio2);

        claveE = new JTextField(30);
        claveE.setBounds(50,280,200,25);
        add(claveE);

        btnBuscarE = new JButton("Clave 'e'");
        btnBuscarE.addActionListener(this);
        btnBuscarE.setBounds(255,280,100,25);
        btnBuscarE.setFont(new Font("Verdana", Font.PLAIN, 14));
        add(btnBuscarE);

        claveN = new JTextField(30);
        claveN.setBounds(50,320,200,25);
        add(claveN);

        btnBuscarN = new JButton("Clave 'n'");
        btnBuscarN.addActionListener(this);
        btnBuscarN.setBounds(255,320,100,25);
        btnBuscarN.setFont(new Font("Verdana", Font.PLAIN, 14));
        add(btnBuscarN);

        claveD = new JTextField(30);
        claveD.setBounds(50,360,200,25);
        add(claveD);

        btnBuscarD = new JButton("Clave 'd'");
        btnBuscarD.addActionListener(this);
        btnBuscarD.setBounds(255,360,100,25);
        btnBuscarD.setFont(new Font("Verdana", Font.PLAIN, 14));
        add(btnBuscarD);

        btnGenRSA2 = new JButton("Generar RSA");
        btnGenRSA2.addActionListener(this);
        btnGenRSA2.setBounds(50,400,220,30);
        btnGenRSA2.setFont(new Font("Verdana", Font.PLAIN, 14));
        add(btnGenRSA2);

        btnPedirFirma = new JButton("SOLICITAR FIRMA");
        btnPedirFirma.addActionListener(this);
        btnPedirFirma.setBounds(50,450,300,60);
        btnPedirFirma.setFont(new Font("Verdana", Font.PLAIN, 14));
        add(btnPedirFirma);

    }


    /**
     * Realiza las tareas de escuchar los objetos de la vista.
     * @param e Objetos que se pulsa
     */
    @Override
    public void actionPerformed(ActionEvent e) {

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
                    client.pathFile(fileName.toPath());
                    archivo = txt.toString().getBytes();
                    //System.out.println(txt);
                    //System.out.println(archivo.length);
                }
            }
        }

        if (radio.isSelected() == true) {
            radio2.setSelected(false);
            //btnGenRSA.setEnabled(true);
            if (e.getSource() == btnGenRSA) {
                client.initialRSA(archivo);
            }
        }
        if (radio2.isSelected() == true) {
            radio.setSelected(false);

            //TODO Cambiar, cuando se pulsa que genere rsa, genera el rsa pero no lo envía todavía

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
                            int i=0;
                            while ((cadena = b.readLine()) != null) {
                                if (i>0 && i<27)
                                    keyE+=cadena;
                                i++;
                            }
                            b.close();
                        } catch (Exception ex) {
                            System.out.println("Algo ha ido mal con el fichero");
                        }
                    }
                }
            }

            if (e.getSource() == btnBuscarN) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                String cadena;

                int result = fileChooser.showOpenDialog(this);

                if (result != JFileChooser.CANCEL_OPTION) {

                    File fileName = fileChooser.getSelectedFile();

                    if ((fileName == null) || (fileName.getName().equals(""))) {
                        claveN.setText("...");
                    } else {
                        // Con esto leemos el contenido de la clave
                        try {
                            claveN.setText(fileName.getAbsolutePath());
                            fileName.createNewFile();
                            Reader targetReader = new FileReader(fileName);
                            BufferedReader b = new BufferedReader(targetReader);
                            int i=0;
                            while ((cadena = b.readLine()) != null) {
                                if (i>0 && i<27)
                                    keyN+=cadena;
                                i++;
                            }
                            b.close();
                        } catch (Exception ex) {
                            System.out.println("Algo ha ido mal con el fichero");
                        }
                    }
                }
            }

            if (e.getSource() == btnBuscarD) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                String cadena;

                int result = fileChooser.showOpenDialog(this);

                if (result != JFileChooser.CANCEL_OPTION) {

                    File fileName = fileChooser.getSelectedFile();

                    if ((fileName == null) || (fileName.getName().equals(""))) {
                        claveD.setText("...");
                    } else {
                        // Con esto leemos el contenido de la clave
                        try {
                            claveD.setText(fileName.getAbsolutePath());
                            fileName.createNewFile();
                            Reader targetReader = new FileReader(fileName);
                            BufferedReader b = new BufferedReader(targetReader);
                            int i=0;
                            while ((cadena = b.readLine()) != null) {
                                if (i>0 && i<27)
                                    keyD+=cadena;
                                i++;
                            }
                            b.close();
                        } catch (Exception ex) {
                            System.out.println("Algo ha ido mal con el fichero");
                        }

                    }
                }
            }

            if (e.getSource() == btnGenRSA2) {
                client.initialRSA(archivo, keyE, keyN, keyD);
            }

        }

        if(e.getSource() == btnPedirFirma) {
            client.blindProcess(archivo);

        }
    }
}
