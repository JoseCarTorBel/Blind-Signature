package client;

import firma.RSA;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.math.BigInteger;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;
import java.util.Scanner;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;


public class ClientBlindSignature {

    public static void main (String[]args){

        VistaCliente vistaCliente = new VistaCliente();
        vistaCliente.setBounds(700,400,400,600);
        vistaCliente.setDefaultCloseOperation(EXIT_ON_CLOSE);
        vistaCliente.setVisible(true);
        vistaCliente.setResizable(false);
        socket = new AuxClientBlindSignature("localhost", "1099");

    }

    private static AuxClientBlindSignature socket;

    private String filePath;
    private RSA rsaAlgorithm;
    private Scanner teclado = new Scanner(System.in);
    private BigInteger k;

//    public static void main(String[] args) {
//        ClientBlindSignature client = new ClientBlindSignature();
//        socket = new AuxClientBlindSignature("localhost", "1099");
//        client.clientExec();
//    }

    void clientExec1(byte[] fichero) {
        System.out.println("###################################");
        System.out.println("##### PETICIÓN DE FIRMA ###########");
        System.out.println("###################################");
        System.out.println("Conectando...");
        System.out.println("...");
        System.out.println("CONECTADO!");
        System.out.println("\n");
        System.out.println("[CLIENTE]\tCrea RSA.");
        rsaAlgorithm = new RSA();
        System.out.println(rsaAlgorithm.toString());
        // Pide fichero
//        System.out.println("Fichero a firmar");
//        String fichero = teclado.next();
//        teclado.close();

        System.out.println(fichero);

        generateOpacityFactorK();

        BigInteger ficheroHash = creaHashFichero(fichero);
        BigInteger eServer = recibeE();
        BigInteger nServer = recibeN();

        BigInteger x = generateX(ficheroHash, eServer, nServer);
        System.out.println("X: " + x);

        // Envio de la X
        BigInteger ficheroFirmado = enviaFichero(x.toByteArray());

        //Destruye k
        destruyeK();

        //Validar firma
        boolean verificado = verifySignature(x, eServer, nServer, ficheroFirmado);
        if (verificado)
            System.out.println("[CLIENTE]\tFirma realizada correctamente.");
        else
            System.out.println("[CLIENTE]\tFirma no realizada correctamente.");

        FileWriter fich = null;
        try{
            fich = new FileWriter(this.filePath+"/ficheroFirmado");
            fich.write(String.valueOf(ficheroFirmado));
            fich.close();
        }catch (Exception ex){
            System.out.println("[ERROR]\tEscritura fichero.");
            ex.printStackTrace();
        }
        finaliza();
    }

    public void pathFile(Path path){
        Path pathNoName = path.getParent();
        this.filePath= pathNoName.toString();
    }

    private BigInteger enviaFichero(byte[] x) {

        BigInteger ficheroFirmado = null;
        try {
            System.out.println("[CLIENTE]\tEnvía la X.");
            byte[] fichero = socket.enviaFichero(x);
            //System.out.println("Firmado: "+new BigInteger(ficheroFirmado));
            return new BigInteger(fichero);

        } catch (IOException e) {
            System.out.println("[ ERROR ]\tEnviando mensaje. " + e);
            System.exit(1);
        }
        return ficheroFirmado;
    }

    private BigInteger recibeE() {
        byte[] e = new byte[0];
        try {
            e = socket.pideE();
            //   System.out.println(e);
        } catch (IOException ex) {
            // System.out.println("[ ERROR ]\tRecibiendo e. "+ex);
            System.exit(1);
        }
        BigInteger eInt = new BigInteger(e);
        System.out.println("[CLIENTE]\tRecibe e del servidor.");
        return eInt;
    }

    private BigInteger recibeN() {
        byte[] n = new byte[0];
        try {
            n = socket.pideN();
            //   System.out.println(e);
        } catch (IOException ex) {
            // System.out.println("[ ERROR ]\tRecibiendo e. "+ex);
            System.exit(1);
        }
        BigInteger nInt = new BigInteger(n);
        System.out.println("[CLIENTE]\tRecibe N del servidor.");
        return nInt;
    }

    private void generateOpacityFactorK() {
        System.out.println("[CLIENTE]\tGenera el factor K. ");
        Random random;
        try {
            random = SecureRandom.getInstance("SHA1PRNG");
            this.k = new BigInteger(2048, random);

        } catch (NoSuchAlgorithmException ex) {
            System.out.println("[ERROR]\tNot find algorithm " + ex);
            System.exit(1);
        }
    }

    private BigInteger creaHashFichero(byte[] fichero) {
        System.out.println("[CLIENTE]\tRealiza hash.");
        //BigInteger inte = stringToBigInteger(fichero);
        //System.out.println(fichero);
        return rsaAlgorithm.decrypt(new BigInteger(fichero));
    }

    /**
     * Genera la X del cliente
     *
     * @param hmsg Mensaje con hash
     * @param e    Llave pública del servidor
     * @param n    Modulo server
     * @return
     */
    private BigInteger generateX(BigInteger hmsg, BigInteger e, BigInteger n) {
        System.out.println("[CLIENTE]\tGenera X.");
        BigInteger x = hmsg.multiply(k.modPow(e, n)).mod(n);
        System.out.println("La x: " + x);
        return x;
    }

    /**
     * Verifica que lo que se ha enviado al server es lo mismo que lo que ha recibido
     *
     * @param x           Utiliza la X para verificar-
     * @param eServer     Exponente público server
     * @param nServer     Modulo del servidor
     * @param fichFirmado Fichero firmado
     * @return Indica si la firma ha sido hecha correcta o no.
     */
    private boolean verifySignature(BigInteger x, BigInteger eServer, BigInteger nServer, BigInteger fichFirmado) {
        System.out.println("[CLIENTE]\tVerifica firma.");

        BigInteger firma = fichFirmado.modPow(eServer, nServer);

        if (x.equals(firma)) {
            return true;
        }
        return false;

    }

    public BigInteger descipher(BigInteger y) {
        BigInteger original = rsaAlgorithm.decrypt(y);
        return original;
    }

    private void destruyeK() {
        System.out.println("[CLIENTE]\tDestruye K.");
        this.k = null;
    }

    private void finaliza() {
        try {
            socket.finaliza();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Convertir de byte a Integer
     */
    private BigInteger stringToBigInteger(String convert) {
        byte[] encodedString = Base64.getEncoder().encodeToString(convert.getBytes()).getBytes();
        BigInteger inte = new BigInteger(encodedString);
        return inte;
    }

}




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





