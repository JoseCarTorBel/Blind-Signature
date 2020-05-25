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

    private static final long serialVersionUID = 1L;

    private JLabel texto,inicio,resultado,resultado2,conecText,conecText2;

    ButtonGroup grupo;
    private final JButton btnBuscar,btnPedirFirma;
    private JTextField txt;
    String archivo="";

    private File fileName=null;

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


        btnPedirFirma = new JButton("Solicitu tu firma");
        btnPedirFirma.addActionListener(this);
        btnPedirFirma.setBounds(50,440,300,40);
        btnPedirFirma.setFont(new Font("Verdana", Font.PLAIN, 14));
        add(btnPedirFirma);

        resultado=new JLabel("");
        resultado.setBounds(50,480,320,60);
        resultado.setFont(new Font("Verdana", Font.PLAIN, 14));
        add(resultado);

        resultado2=new JLabel("");
        resultado2.setBounds(50,500,320,60);
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

                fileName = fileChooser.getSelectedFile();

                if ((fileName == null) || (fileName.getName().equals(""))) {
                    txt.setText("...");
                } else {
                    txt.setText(fileName.getAbsolutePath());
                    client.pathFile(fileName.toPath());
                    try {
                        Reader targetReader = new FileReader(fileName);
                        BufferedReader fich = new BufferedReader(targetReader);
                        String cadena;
                        while ((cadena = fich.readLine()) != null) {
                            archivo+=cadena;
                        }
                        fich.close();
                    }catch (Exception ex){}

                }
            }
        }

        if(e.getSource() == btnPedirFirma) {
            /*if(client.blindProcess(archivo)){
                resultado.setText("Documento firmado. Guardado en: ");
                resultado2.setText(client.getPathFile());
            }else{
                resultado.setText("No se ha podido firmar.");
            }
            */

            if(client.blindProcess(fileName)){
                resultado.setText("Documento firmado. Guardado en: ");
                resultado2.setText(client.getPathFile());
            }else{
                resultado.setText("No se ha podido firmar.");
            }
        }
    }
}