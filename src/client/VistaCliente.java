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
    private JLabel texto,inicio,resultado,resultado2;

    private JRadioButton radio,radio2;
    ButtonGroup grupo;
    private final JButton btnBuscar,btnBuscarE,btnBuscarD,btnGenRSA,btnGenRSA2,btnPedirFirma;
    private JTextField txt,claveE,claveN,claveD;
    static byte[] archivo = new byte[0];

    private String keyE="", keyD="",keyN="";
    private ClientBlindSignature client;

    /**
     * Definición objetos de la vista
     */
    public VistaCliente(ClientBlindSignature cliente) {
        super("Solicitud de firma");

        client = cliente;

        inicio=new JLabel("Solicitud de firma ciega");
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
        radio.setText("Generar llaves");
        radio.setBounds(50,140,220,40);
        radio.setFont(new Font("Verdana", Font.PLAIN, 14));
        add(radio);
        grupo.add(radio);

        btnGenRSA = new JButton("Generar llaves");

        btnGenRSA.addActionListener(this);
        btnGenRSA.setBounds(50,180,220,30);
        btnGenRSA.setFont(new Font("Verdana", Font.PLAIN, 14));
        add(btnGenRSA);
        //btnGenRSA.setEnabled(false);

        radio2 = new JRadioButton();
        radio2.setText("Importar llaves");
        radio2.setBounds(50,240,300,30);
        radio2.setFont(new Font("Verdana", Font.PLAIN, 14));
        add(radio2);
        grupo.add(radio2);

        claveE = new JTextField(30);
        claveE.setBounds(50,280,200,25);
        add(claveE);
        // Encripta con la E
        btnBuscarE = new JButton("Clave privada");
        btnBuscarE.addActionListener(this);
        btnBuscarE.setBounds(255,280,100,25);
        btnBuscarE.setFont(new Font("Verdana", Font.PLAIN, 14));
        add(btnBuscarE);

        claveN = new JTextField(30);
        claveN.setBounds(50,320,200,25);
        add(claveN);

//        btnBuscarN = new JButton("Clave 'n'");
//        btnBuscarN.addActionListener(this);
//        btnBuscarN.setBounds(255,320,100,25);
//        btnBuscarN.setFont(new Font("Verdana", Font.PLAIN, 14));
//        add(btnBuscarN);

        claveD = new JTextField(30);
        claveD.setBounds(50,360,200,25);
        add(claveD);
        // Desencripta con la n
        btnBuscarD = new JButton("Clave pública");
        btnBuscarD.addActionListener(this);
        btnBuscarD.setBounds(255,360,100,25);
        btnBuscarD.setFont(new Font("Verdana", Font.PLAIN, 14));
        add(btnBuscarD);

        btnGenRSA2 = new JButton("Importar llaves");
        btnGenRSA2.addActionListener(this);
        btnGenRSA2.setBounds(50,400,220,30);
        btnGenRSA2.setFont(new Font("Verdana", Font.PLAIN, 14));
        add(btnGenRSA2);

        btnPedirFirma = new JButton("Cifrar y solicitar firma");
        btnPedirFirma.addActionListener(this);
        btnPedirFirma.setBounds(50,450,300,40);
        btnPedirFirma.setFont(new Font("Verdana", Font.PLAIN, 14));
        add(btnPedirFirma);

        resultado=new JLabel("");
        resultado.setBounds(50,480,320,60);
        resultado.setFont(new Font("Verdana", Font.PLAIN, 14));
        add(resultado);

        resultado2=new JLabel("");
        resultado2.setBounds(50,510,320,60);
        resultado2.setFont(new Font("Verdana", Font.PLAIN, 14));
        add(resultado2);

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

        if (radio.isSelected()) {
            radio2.setSelected(false);
         //   btnGenRSA.setEnabled(true);

            btnGenRSA2.setEnabled(false);

            if (e.getSource() == btnGenRSA) {
                client.initialRSA();
            }
        }

        if (radio2.isSelected()) {
            radio.setSelected(false);

            if (e.getSource() == btnBuscarE) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                String cadena;
                long ultima;

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
                            ultima = b.lines().count();
                            targetReader=new FileReader(fileName);
                            b = new BufferedReader(targetReader);
                            int i=0;
                            while ((cadena = b.readLine()) != null) {
                                if (i>0 && i< ultima-1)
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

            if (e.getSource() == btnBuscarD) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                String cadena;
                long ultima;

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
                            ultima = b.lines().count();
                            targetReader=new FileReader(fileName);
                            b = new BufferedReader(targetReader);
                            int i=0;
                            while ((cadena = b.readLine()) != null) {
                                if (i>0 && i< ultima-1)
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
                client.initialRSA(keyE, keyD);
            }

        }

        if(e.getSource() == btnPedirFirma) {
            if(client.blindProcess(archivo)){
                resultado.setText("Documento firmado. Guardado en: ");
                resultado2.setText(client.getPathFile());
            }else{
                resultado.setText("No se ha podido firmar.");
            }
        }
    }
}
