package client;

import firma.RSA;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
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
        vistaCliente.setBounds(700,400,600,400);
        vistaCliente.setDefaultCloseOperation(EXIT_ON_CLOSE);
        vistaCliente.setVisible(true);
        vistaCliente.setResizable(false);
    }

    private static AuxClientBlindSignature socket;


    private RSA rsaAlgorithm;
    private Scanner teclado = new Scanner(System.in);
    private BigInteger k;

//    public static void main(String[] args) {
//        ClientBlindSignature client = new ClientBlindSignature();
//        socket = new AuxClientBlindSignature("localhost", "1099");
//        client.clientExec();
//    }

    void clientExec1() {

        System.out.println("###################################");
        System.out.println("##### PETICIÓN DE FIRMA ###########");
        System.out.println("###################################");
        System.out.println("Conectando...");
        System.out.println("...");
        System.out.println("CONECTADO!");
        System.out.println("\n");
    }
    void clientExec2() {
        System.out.println("[CLIENTE]\tCrea RSA.");
        rsaAlgorithm = new RSA();
        System.out.println(rsaAlgorithm.toString());
    }
    void clientExec3(byte[] fichero){
        // Pide fichero
//        System.out.println("Fichero a firmar");
//        String fichero = teclado.next();
//        teclado.close();

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

        finaliza();
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
        System.out.println(fichero);
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

class VistaCliente extends JFrame implements ActionListener {
    private JLabel texto;
    private JButton btnBuscar;
    private JButton btnConectar;
    private JButton btnGenRSA;
    private JTextField txt;

    public VistaCliente() {
        super("Firma tu fichero");
        texto=new JLabel("Sube aquí tu fichero.");
        texto.setBounds(50,50,220,40);
        add(texto);
        setLayout(new FlowLayout());

        txt = new JTextField(30);
        add(txt);

        btnBuscar = new JButton("Buscar...");
        btnBuscar.addActionListener(this);
        add(btnBuscar);

        btnConectar = new JButton("Conectar");
        btnConectar.addActionListener(this);
        add(btnConectar);

        btnGenRSA = new JButton("Generar RSA");
        btnGenRSA.addActionListener(this);
        add(btnGenRSA);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //PROGRAMA PRINCIPAL
        AuxClientBlindSignature socket;
        ClientBlindSignature client = new ClientBlindSignature();
        socket = new AuxClientBlindSignature("localhost", "1099");

        if (e.getSource() == btnConectar) {
            client.clientExec1();
        }
        if (e.getSource() == btnGenRSA) {
            client.clientExec2();
        }

        if (e.getSource() == btnBuscar) {
            byte[] fichero = new byte[0];
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

            FileNameExtensionFilter imgFilter = new FileNameExtensionFilter("JPG & GIF Images", "jpg", "gif");
            fileChooser.setFileFilter(imgFilter);

            int result = fileChooser.showOpenDialog(this);

            if (result != JFileChooser.CANCEL_OPTION) {

                File fileName = fileChooser.getSelectedFile();

                try {
                    fichero = Files.readAllBytes(Paths.get(fileName.toPath().toString()));
                    client.clientExec3(fichero);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                if ((fileName == null) || (fileName.getName().equals(""))) {
                    txt.setText("...");
                } else {
                    txt.setText(fileName.getAbsolutePath());
                }
            }
        }


    }
}





