package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;


//**********************************************************
//******************** VISTA CLIENTE ***********************
//**********************************************************


/**
 * Vista del cliente
 */
class VistaCliente extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;

    private JLabel texto,texto2,inicio,resultado,resultado2,conecText,conecText2;

    ButtonGroup grupo;
    private final JButton btnBuscar,btnPedirFirma,btnBuscar0,btnBuscar1,btnBuscar2,btnBuscar3,btnBuscar4;
    private final JButton btnBuscar5,btnBuscar6,btnBuscar7,btnBuscar8,btnBuscar9;
    private JTextField txt,txt0,txt1,txt2,txt3,txt4,txt5,txt6,txt7,txt8,txt9;
    String archivo="";

    private File fileName=null,fileName0=null,fileName1=null,fileName2=null,fileName3=null,fileName4=null;
    private File fileName5=null,fileName6=null,fileName7=null,fileName8=null,fileName9=null;
    private ArrayList<File> documentos;

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

        texto=new JLabel("Firma Totalmente Ciega");
        texto.setBounds(50,75,220,40);
        texto.setFont(new Font("Verdana", Font.BOLD, 14));
        grupo = new ButtonGroup();
        add(texto);

        txt = new JTextField(30);
        txt.setBounds(50,110,200,25);
        add(txt);

        btnBuscar = new JButton("Buscar...");
        btnBuscar.addActionListener(this);
        btnBuscar.setBounds(260,110,100,25);
        btnBuscar.setFont(new Font("Verdana", Font.PLAIN, 14));
        add(btnBuscar);

        texto2=new JLabel("Firma Ciega");
        texto2.setBounds(50,135,220,40);
        texto2.setFont(new Font("Verdana", Font.BOLD, 14));
        grupo = new ButtonGroup();
        add(texto2);

        txt0 = new JTextField(30);
        txt0.setBounds(50,170,200,25);
        add(txt0);

        btnBuscar0 = new JButton("Buscar...");
        btnBuscar0.addActionListener(this);
        btnBuscar0.setBounds(260,170,100,25);
        btnBuscar0.setFont(new Font("Verdana", Font.PLAIN, 14));
        add(btnBuscar0);

        txt1 = new JTextField(30);
        txt1.setBounds(50,200,200,25);
        add(txt1);

        btnBuscar1 = new JButton("Buscar...");
        btnBuscar1.addActionListener(this);
        btnBuscar1.setBounds(260,200,100,25);
        btnBuscar1.setFont(new Font("Verdana", Font.PLAIN, 14));
        add(btnBuscar1);

        txt2 = new JTextField(30);
        txt2.setBounds(50,230,200,25);
        add(txt2);

        btnBuscar2 = new JButton("Buscar...");
        btnBuscar2.addActionListener(this);
        btnBuscar2.setBounds(260,230,100,25);
        btnBuscar2.setFont(new Font("Verdana", Font.PLAIN, 14));
        add(btnBuscar2);

        txt3 = new JTextField(30);
        txt3.setBounds(50,260,200,25);
        add(txt3);

        btnBuscar3 = new JButton("Buscar...");
        btnBuscar3.addActionListener(this);
        btnBuscar3.setBounds(260,260,100,25);
        btnBuscar3.setFont(new Font("Verdana", Font.PLAIN, 14));
        add(btnBuscar3);

        txt4 = new JTextField(30);
        txt4.setBounds(50,290,200,25);
        add(txt4);

        btnBuscar4 = new JButton("Buscar...");
        btnBuscar4.addActionListener(this);
        btnBuscar4.setBounds(260,290,100,25);
        btnBuscar4.setFont(new Font("Verdana", Font.PLAIN, 14));
        add(btnBuscar4);

        txt5 = new JTextField(30);
        txt5.setBounds(50,320,200,25);
        add(txt5);

        btnBuscar5 = new JButton("Buscar...");
        btnBuscar5.addActionListener(this);
        btnBuscar5.setBounds(260,320,100,25);
        btnBuscar5.setFont(new Font("Verdana", Font.PLAIN, 14));
        add(btnBuscar5);

        txt6 = new JTextField(30);
        txt6.setBounds(50,350,200,25);
        add(txt6);

        btnBuscar6 = new JButton("Buscar...");
        btnBuscar6.addActionListener(this);
        btnBuscar6.setBounds(260,350,100,25);
        btnBuscar6.setFont(new Font("Verdana", Font.PLAIN, 14));
        add(btnBuscar6);

        txt7 = new JTextField(30);
        txt7.setBounds(50,380,200,25);
        add(txt7);

        btnBuscar7 = new JButton("Buscar...");
        btnBuscar7.addActionListener(this);
        btnBuscar7.setBounds(260,380,100,25);
        btnBuscar7.setFont(new Font("Verdana", Font.PLAIN, 14));
        add(btnBuscar7);

        txt8 = new JTextField(30);
        txt8.setBounds(50,410,200,25);
        add(txt8);

        btnBuscar8 = new JButton("Buscar...");
        btnBuscar8.addActionListener(this);
        btnBuscar8.setBounds(260,410,100,25);
        btnBuscar8.setFont(new Font("Verdana", Font.PLAIN, 14));
        add(btnBuscar8);

        txt9 = new JTextField(30);
        txt9.setBounds(50,440,200,25);
        add(txt9);

        btnBuscar9 = new JButton("Buscar...");
        btnBuscar9.addActionListener(this);
        btnBuscar9.setBounds(260,440,100,25);
        btnBuscar9.setFont(new Font("Verdana", Font.PLAIN, 14));
        add(btnBuscar9);

        btnPedirFirma = new JButton("Solicita tu firma");
        btnPedirFirma.addActionListener(this);
        btnPedirFirma.setBounds(50,480,300,40);
        btnPedirFirma.setFont(new Font("Verdana", Font.PLAIN, 14));
        add(btnPedirFirma);

        resultado=new JLabel("");
        resultado.setBounds(50,505,320,60);
        resultado.setFont(new Font("Verdana", Font.PLAIN, 14));
        add(resultado);

        resultado2=new JLabel("");
        resultado2.setBounds(50,525,320,60);
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
        if (e.getSource() == btnBuscar0) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            int result = fileChooser.showOpenDialog(this);
            if (result != JFileChooser.CANCEL_OPTION) {
                fileName0 = fileChooser.getSelectedFile();
                if ((fileName0 == null) || (fileName0.getName().equals(""))) {
                    txt0.setText("...");
                } else {
                    txt0.setText(fileName0.getAbsolutePath());
                    client.pathFile(fileName0.toPath());
                    try {
                        Reader targetReader = new FileReader(fileName0);
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
        if (e.getSource() == btnBuscar1) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            int result = fileChooser.showOpenDialog(this);
            if (result != JFileChooser.CANCEL_OPTION) {
                fileName1 = fileChooser.getSelectedFile();
                if ((fileName1 == null) || (fileName1.getName().equals(""))) {
                    txt1.setText("...");
                } else {
                    txt1.setText(fileName1.getAbsolutePath());
                    client.pathFile(fileName1.toPath());
                    try {
                        Reader targetReader = new FileReader(fileName1);
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
        if (e.getSource() == btnBuscar2) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            int result = fileChooser.showOpenDialog(this);
            if (result != JFileChooser.CANCEL_OPTION) {
                fileName2 = fileChooser.getSelectedFile();
                if ((fileName2 == null) || (fileName2.getName().equals(""))) {
                    txt2.setText("...");
                } else {
                    txt2.setText(fileName2.getAbsolutePath());
                    client.pathFile(fileName2.toPath());
                    try {
                        Reader targetReader = new FileReader(fileName2);
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
        if (e.getSource() == btnBuscar3) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            int result = fileChooser.showOpenDialog(this);
            if (result != JFileChooser.CANCEL_OPTION) {
                fileName3 = fileChooser.getSelectedFile();
                if ((fileName3 == null) || (fileName3.getName().equals(""))) {
                    txt3.setText("...");
                } else {
                    txt3.setText(fileName3.getAbsolutePath());
                    client.pathFile(fileName3.toPath());
                    try {
                        Reader targetReader = new FileReader(fileName3);
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
        if (e.getSource() == btnBuscar4) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            int result = fileChooser.showOpenDialog(this);
            if (result != JFileChooser.CANCEL_OPTION) {
                fileName4 = fileChooser.getSelectedFile();
                if ((fileName4 == null) || (fileName4.getName().equals(""))) {
                    txt4.setText("...");
                } else {
                    txt4.setText(fileName4.getAbsolutePath());
                    client.pathFile(fileName4.toPath());
                    try {
                        Reader targetReader = new FileReader(fileName4);
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
        if (e.getSource() == btnBuscar5) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            int result = fileChooser.showOpenDialog(this);
            if (result != JFileChooser.CANCEL_OPTION) {
                fileName5 = fileChooser.getSelectedFile();
                if ((fileName5 == null) || (fileName5.getName().equals(""))) {
                    txt5.setText("...");
                } else {
                    txt5.setText(fileName5.getAbsolutePath());
                    client.pathFile(fileName5.toPath());
                    try {
                        Reader targetReader = new FileReader(fileName5);
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
        if (e.getSource() == btnBuscar6) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            int result = fileChooser.showOpenDialog(this);
            if (result != JFileChooser.CANCEL_OPTION) {
                fileName6 = fileChooser.getSelectedFile();
                if ((fileName6 == null) || (fileName6.getName().equals(""))) {
                    txt6.setText("...");
                } else {
                    txt6.setText(fileName6.getAbsolutePath());
                    client.pathFile(fileName6.toPath());
                    try {
                        Reader targetReader = new FileReader(fileName6);
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
        if (e.getSource() == btnBuscar7) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            int result = fileChooser.showOpenDialog(this);
            if (result != JFileChooser.CANCEL_OPTION) {
                fileName7 = fileChooser.getSelectedFile();
                if ((fileName7 == null) || (fileName7.getName().equals(""))) {
                    txt7.setText("...");
                } else {
                    txt7.setText(fileName7.getAbsolutePath());
                    client.pathFile(fileName7.toPath());
                    try {
                        Reader targetReader = new FileReader(fileName7);
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
        if (e.getSource() == btnBuscar8) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            int result = fileChooser.showOpenDialog(this);
            if (result != JFileChooser.CANCEL_OPTION) {
                fileName8 = fileChooser.getSelectedFile();
                if ((fileName8 == null) || (fileName8.getName().equals(""))) {
                    txt8.setText("...");
                } else {
                    txt8.setText(fileName8.getAbsolutePath());
                    client.pathFile(fileName8.toPath());
                    try {
                        Reader targetReader = new FileReader(fileName8);
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
        if (e.getSource() == btnBuscar9) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            int result = fileChooser.showOpenDialog(this);
            if (result != JFileChooser.CANCEL_OPTION) {
                fileName9 = fileChooser.getSelectedFile();
                if ((fileName9 == null) || (fileName9.getName().equals(""))) {
                    txt9.setText("...");
                } else {
                    txt9.setText(fileName9.getAbsolutePath());
                    client.pathFile(fileName9.toPath());
                    try {
                        Reader targetReader = new FileReader(fileName9);
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
 
            documentos = new ArrayList<File>();

            if(fileName0!=null && fileName1!=null && fileName2!=null && fileName3!=null && fileName4!=null && fileName5!=null
                    && fileName6!=null && fileName7!=null&& fileName8!=null && fileName9!=null && fileName==null){

                documentos.add(fileName0); documentos.add(fileName1); documentos.add(fileName2); documentos.add(fileName3); documentos.add(fileName4);
                documentos.add(fileName5); documentos.add(fileName6); documentos.add(fileName7); documentos.add(fileName8); documentos.add(fileName9);
               
                if( client.blindProcessPartial(documentos)) {
                    resultado.setText("Documento firmado. Guardado en: ");
                    resultado2.setText(client.getPathFile());
                }else {
                    resultado.setText("No se ha podido firmar. Comprueba");
                }

            } else if (fileName0==null && fileName1==null && fileName2==null && fileName3==null && fileName4==null && fileName5==null
                    && fileName6==null && fileName7==null&& fileName8==null && fileName9==null && fileName!=null){
                if(client.blindProcess(fileName)) {
                    resultado.setText("Documento firmado. Guardado en: ");
                    resultado2.setText(client.getPathFile());
                }else {
                    resultado.setText("No se ha podido firmar. Comprueba");
                }
            } else {
                resultado.setText("No se ha podido firmar. Comprueba");
                resultado2.setText("que no falten o sobren documentos");
            }
        }
    }
}