package client;

import firma.RSA;

import javax.swing.plaf.synth.SynthTextAreaUI;
import java.io.IOException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;
import java.util.Scanner;


public class ClientBlindSignature {

    private static AuxClientBlindSignature socket;


    private RSA rsaAlgorithm;
    private Scanner teclado = new Scanner(System.in);
    private BigInteger k;

    public static void main(String[] args) {
        ClientBlindSignature client = new ClientBlindSignature();
        socket=new AuxClientBlindSignature("localhost","1099");
        client.clientExec();
    }

    private void clientExec(){

        System.out.println("###################################");
        System.out.println("##### PETICIÓN DE FIRMA ###########");
        System.out.println("###################################");
        System.out.println("\n");

        System.out.println("[CLIENTE]\tCrea RSA.");
        rsaAlgorithm=new RSA();
        System.out.println(rsaAlgorithm.toString());

        // Pide fichero
        System.out.println("Fichero a firmar");
        String fichero =teclado.next();
        teclado.close();

        generateOpacityFactorK();

        BigInteger ficheroHash = creaHashFichero(fichero);
        BigInteger eServer = recibeE();
        BigInteger nServer = recibeN();

        BigInteger x = generateX(ficheroHash,eServer,nServer);
        System.out.println("X: "+x);

        // Envio de la X
        BigInteger ficheroFirmado = enviaFichero(x.toByteArray());

        //Destruye k
        destruyeK();

        //Validar firma
        boolean verificado = verifySignature(x,eServer,nServer,ficheroFirmado);
        if(verificado)
            System.out.println("[CLIENTE]\tFirma realizada correctamente.");
        else
            System.out.println("[CLIENTE]\tFirma no realizada correctamente.");

        finaliza();
    }

    private BigInteger enviaFichero(byte[] x){

       BigInteger ficheroFirmado = null;
        try{
            System.out.println("[CLIENTE]\tEnvía la X.");
            byte[] fichero=socket.enviaFichero(x);
            //System.out.println("Firmado: "+new BigInteger(ficheroFirmado));
            return new BigInteger(fichero);

        } catch (IOException e) {
            System.out.println("[ ERROR ]\tEnviando mensaje. "+e);
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
        BigInteger eInt =  new BigInteger(e);
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
        BigInteger nInt =  new BigInteger(n);
        System.out.println("[CLIENTE]\tRecibe N del servidor.");
        return nInt;
    }

    private void generateOpacityFactorK()  {
        System.out.println("[CLIENTE]\tGenera el factor K. ");
        Random random;
        try {
            random = SecureRandom.getInstance("SHA1PRNG");
            this.k=new BigInteger(2048,random);

        }catch (NoSuchAlgorithmException ex){
            System.out.println("[ERROR]\tNot find algorithm "+ex);
            System.exit(1);
        }
    }

    private BigInteger creaHashFichero(String fichero){
        System.out.println("[CLIENTE]\tRealiza hash.");
        BigInteger inte = stringToBigInteger(fichero);
        return rsaAlgorithm.decrypt(inte);
    }

    /**
     * Genera la X del cliente
     * @param hmsg  Mensaje con hash
     * @param e Llave pública del servidor
     * @param n Modulo server
     * @return
     */
    private BigInteger generateX(BigInteger hmsg, BigInteger e,BigInteger n){
        System.out.println("[CLIENTE]\tGenera X.");
        BigInteger x = hmsg.multiply(k.modPow(e,n)).mod(n);
        System.out.println("La x: "+x);
        return x;
    }

    /**
     *Verifica que lo que se ha enviado al server es lo mismo que lo que ha recibido
     *
     * @param x   Utiliza la X para verificar-
     * @param eServer   Exponente público server
     * @param nServer   Modulo del servidor
     * @param fichFirmado Fichero firmado
     *
    * @return  Indica si la firma ha sido hecha correcta o no.
     */
    private boolean verifySignature(BigInteger x,   BigInteger eServer, BigInteger nServer, BigInteger fichFirmado){
        System.out.println("[CLIENTE]\tVerifica firma.");

        BigInteger firma = fichFirmado.modPow(eServer,nServer);

        if(x.equals(firma)){
            return true;
        }
        return false;

    }

    public BigInteger descipher(BigInteger y)  {
        BigInteger original = rsaAlgorithm.decrypt(y);
        return original;
    }

    private void destruyeK(){
        System.out.println("[CLIENTE]\tDestruye K.");
        this.k=null;
    }

    private void finaliza(){
        try {
            socket.finaliza();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Convertir de byte a Integer
     *
     */
    private BigInteger stringToBigInteger(String convert){
        byte[] encodedString = Base64.getEncoder().encodeToString(convert.getBytes()).getBytes();
        BigInteger inte = new BigInteger(encodedString);
        return inte;
    }



}






