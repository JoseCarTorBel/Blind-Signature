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
        ClientBlindSignature client = new ClientBlindSignature();
        socket = new AuxClientBlindSignature("localhost", "1099");
        VistaCliente vistaCliente = new VistaCliente(client);
        vistaCliente.setBounds(700,400,400,600);
        vistaCliente.setDefaultCloseOperation(EXIT_ON_CLOSE);
        vistaCliente.setVisible(true);
        vistaCliente.setResizable(false);


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


    /**
     * Crea RSA con el par de llaves desde 0.
     * @param fichero   Fichero que debe ser firmado
     */
    void initialRSA(byte[] fichero){
        rsaAlgorithm = new RSA();
        System.out.println("Holaaaaaaaaaaaaaaaaa -> "+this.filePath);
        //blindProcess(fichero);
    }


    /**
     * Crea RSA a partir de las llaves dadas
     * @param fichero   Fichero a firmar
     * @param e Llave e
     * @param n Módulo
     * @param d Llave d
     */
    void initialRSA(byte[] fichero, String e, String n, String d) {

        BigInteger keyE = stringToBigInteger(e);
        BigInteger keyN = stringToBigInteger(n);
        BigInteger keyD = stringToBigInteger(d);

        rsaAlgorithm = new RSA(keyD, keyE, keyN);
        //blindProcess(fichero);

    }

    /**
     * Genera el factor de opacidad.
     * Recibe del servidor la E y N; Realiza el Hash del fichero
     * Genera la X del fichero.
     * Envía y recibe el fichero firmado
     * Lo verifica a ver si es correcto.
     *
     * @param fichero Fichero que debe ser firmado.
     */
    void blindProcess(byte[] fichero){

        generateOpacityFactorK();

        BigInteger ficheroHash = creaHashFichero(fichero);
        BigInteger eServer = recibeE();
        BigInteger nServer = recibeN();

        BigInteger x = generateX(ficheroHash, eServer, nServer);

        // Envio de la X
        BigInteger ficheroFirmado = enviaFichero(x.toByteArray());

        //Destruye k
        destruyeK();

        //Validar firma
        boolean verificado = verifySignature(x, eServer, nServer, ficheroFirmado);
        if (verificado) {
            System.out.println("[CLIENTE]\tFirma realizada correctamente.");
            FileWriter fich = null;
            try {
                System.out.println(this.filePath);
                fich = new FileWriter(this.filePath + "/ficheroFirmado");
                fich.write(IntegerToString(ficheroFirmado));
                fich.close();
            } catch (Exception ex) {
                System.out.println("[ERROR]\tEscritura fichero.");
                ex.printStackTrace();
            }
        }else {
            System.out.println("[CLIENTE]\tFirma no realizada correctamente.");
        }
        finaliza();
    }




//_____________________________________________________________

    /**
     * Estraer el path sin nombre del fichero
     * @param path  path sin nombre
     */
    public void pathFile(Path path){
        Path pathNoName = path.getParent();
        this.filePath= pathNoName.toString();
        System.out.println("QUE ESTOY AQUI -> "+this.filePath);
    }

    private BigInteger enviaFichero(byte[] x) {

        BigInteger ficheroFirmado = null;
        try {
           // System.out.println("[CLIENTE]\tEnvía la X.");
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

    private String IntegerToString(BigInteger convert){

        String  converted = Base64.getEncoder().encodeToString(convert.toByteArray());
        return converted;
    }
}





