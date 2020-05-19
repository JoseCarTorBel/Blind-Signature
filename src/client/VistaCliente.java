package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.nio.file.Files;
import java.util.Base64;


//**********************************************************
//******************** VISTA CLIENTE ***********************
//**********************************************************


/**
 * Vista del cliente
 */
class VistaCliente extends JFrame implements ActionListener {
    private JLabel texto,inicio,resultado,resultado2,conecText,conecText2;

    private JRadioButton radio,radio2;
    ButtonGroup grupo;
    private final JButton btnBuscar,btnBuscarE,btnBuscarD,btnGenRSA,btnGenRSA2,btnPedirFirma;
    private JTextField txt,claveE,claveN,claveD;
    String archivo="";

    private String keyE="", keyD="";
    private ClientBlindSignature client;

    /**
     * Definición objetos de la vista
     */
    public VistaCliente(ClientBlindSignature cliente) {
        super("Solicitud de firma");

        client = cliente;

        inicio=new JLabel("Solicitud de firma ciega");
        inicio.setBounds(50,0,300,40);
        inicio.setFont(new Font("Verdana", Font.BOLD, 17));
        grupo = new ButtonGroup();
        add(inicio);
        setLayout(null);

        conecText=new JLabel("   Conexion establecida con:");
        conecText.setBounds(50,15,320,60);
        conecText.setFont(new Font("Verdana", Font.PLAIN, 14));
        add(conecText);

        conecText2=new JLabel("   " + client.getHostAndPort());
        conecText2.setBounds(50,35,340,60);
        conecText2.setFont(new Font("Verdana", Font.PLAIN, 14));
        add(conecText2);

        texto=new JLabel("Sube aquí tu fichero.");
        texto.setBounds(50,70,220,40);
        texto.setFont(new Font("Verdana", Font.PLAIN, 14));
        grupo = new ButtonGroup();
        add(texto);

        txt = new JTextField(30);
        txt.setBounds(50,110,300,25);
        add(txt);

        btnBuscar = new JButton("Buscar...");
        btnBuscar.addActionListener(this);
        btnBuscar.setBounds(50,140,100,25);
        btnBuscar.setFont(new Font("Verdana", Font.PLAIN, 14));
        add(btnBuscar);

        radio = new JRadioButton();
        radio.setText("Generar llaves");
        radio.setBounds(50,170,220,40);
        radio.setFont(new Font("Verdana", Font.PLAIN, 14));
        add(radio);
        grupo.add(radio);

        btnGenRSA = new JButton("Generar llaves");

        btnGenRSA.addActionListener(this);
        btnGenRSA.setBounds(50,210,220,30);
        btnGenRSA.setFont(new Font("Verdana", Font.PLAIN, 14));
        add(btnGenRSA);
        //btnGenRSA.setEnabled(false);

        radio2 = new JRadioButton();
        radio2.setText("Importar llaves");
        radio2.setBounds(50,260,300,30);
        radio2.setFont(new Font("Verdana", Font.PLAIN, 14));
        add(radio2);
        grupo.add(radio2);

        claveE = new JTextField(30);
        claveE.setBounds(50,300,200,25);
        add(claveE);

        // Encripta con la E
        btnBuscarE = new JButton("Clave privada");
        btnBuscarE.addActionListener(this);
        btnBuscarE.setBounds(255,300,125,25);
        btnBuscarE.setFont(new Font("Verdana", Font.PLAIN, 13));
        add(btnBuscarE);

        claveD = new JTextField(30);
        claveD.setBounds(50,340,200,25);
        add(claveD);

        // Desencripta con la n
        btnBuscarD = new JButton("Clave pública");
        btnBuscarD.addActionListener(this);
        btnBuscarD.setBounds(255,340,125,25);
        btnBuscarD.setFont(new Font("Verdana", Font.PLAIN, 13));
        add(btnBuscarD);

        btnGenRSA2 = new JButton("Importar llaves");
        btnGenRSA2.addActionListener(this);
        btnGenRSA2.setBounds(50,380,220,30);
        btnGenRSA2.setFont(new Font("Verdana", Font.PLAIN, 14));
        add(btnGenRSA2);

        btnPedirFirma = new JButton("Cifrar y solicitar firma");
        btnPedirFirma.addActionListener(this);
        btnPedirFirma.setBounds(50,460,300,40);
        btnPedirFirma.setFont(new Font("Verdana", Font.PLAIN, 14));
        add(btnPedirFirma);

        resultado=new JLabel("");
        resultado.setBounds(50,500,320,60);
        resultado.setFont(new Font("Verdana", Font.PLAIN, 14));
        add(resultado);

        resultado2=new JLabel("");
        resultado2.setBounds(50,530,320,60);
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
                    try {
                        Reader targetReader = new FileReader(fileName);
                        BufferedReader fich = new BufferedReader(targetReader);
                        String cadena;
                        int i = 0;
                        while ((cadena = fich.readLine()) != null) {
                           archivo+=cadena;
                        }
                    }catch (Exception ex){}


                //    System.out.println(txt.toString());
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
                            String privateKeyContent = new String(Files.readAllBytes(fileName.toPath()));
                            privateKeyContent = privateKeyContent.replaceAll("\\n", "").replace("-----BEGIN RSA PRIVATE KEY-----", "").replace("-----END RSA PRIVATE KEY-----", "");
                            keyE=privateKeyContent;
                            System.out.println(keyE);


                            claveE.setText(fileName.getAbsolutePath());
//                            fileName.createNewFile();
//                            Reader targetReader = new FileReader(fileName);
//                            BufferedReader b = new BufferedReader(targetReader);
//                            ultima = b.lines().count();
//                            targetReader=new FileReader(fileName);
//                            b = new BufferedReader(targetReader);
//                            int i=0;
//                            while ((cadena = b.readLine()) != null) {
//                                if (i>0 && i< ultima-1)
//                                    keyE+=cadena;
//                                i++;
//                            }
//                            b.close();
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
