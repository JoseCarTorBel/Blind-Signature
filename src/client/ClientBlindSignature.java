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


/**
 * Peticiones:
 *  0 -> Pide la E al server
 *  1 -> Indica que envía el fichero.
 */

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

        System.out.println("Fichero a firmar");
        String fichero =teclado.next();

        generateOpacityFactorK();

        BigInteger ficheroHash = creaHashFichero(fichero);
        BigInteger eServer = recibeE();

        BigInteger x = generateX(ficheroHash,eServer);

        // Envio de la X
        enviaFichero(x.toByteArray());


        //TODO validar firma.
        /* Destruir K y verificar */
        this.k=null;

    teclado.close();
        try {
            socket.finaliza();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private byte[] enviaFichero(byte[] x){

        byte[] ficheroFirmado = new byte[0];
        try{
            System.out.println("[CLIENTE]\tEnvía la X.");
            ficheroFirmado=socket.enviaFichero(x);
            System.out.println("Firmado: "+new BigInteger(ficheroFirmado));
            return ficheroFirmado;

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

    private BigInteger generateX(BigInteger hmsg, BigInteger e){
        System.out.println("[CLIENTE]\tGenera X.");
        System.out.println("h: "+hmsg+"\ne: "+e+"\nk: "+k);

        BigInteger x = hmsg.multiply(k.modPow(e,rsaAlgorithm.getn())).mod(rsaAlgorithm.getn());
        System.out.println("La x: "+x);
        return x;
    }

    public byte[] descipher(BigInteger y)  {
        byte[] original = Base64.getDecoder().decode(rsaAlgorithm.decrypt(y).toByteArray());
        return original;
    }

//    public boolean checkSignature(){
//
//    }


    /** Convertir de byte a Integer
     *
     */
    private BigInteger stringToBigInteger(String convert){
        byte[] encodedString = Base64.getEncoder().encodeToString(convert.getBytes()).getBytes();
        BigInteger inte = new BigInteger(encodedString);
        return inte;
    }
}






