package client;

import firma.RSA;

import javax.swing.plaf.synth.SynthTextAreaUI;
import java.io.IOException;
import java.math.BigInteger;
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

        System.out.println("Fichero a firmar");
        String fichero =teclado.next();

        generateOpacityFactorK();

        BigInteger ficheroHash = creaHashFichero(fichero);
        BigInteger eServer = recibeE();

        BigInteger x = generateX(ficheroHash,eServer);
        // TODO Hacer envío de la X
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

    private byte[] enviaFichero(byte[] fichero){
        byte[] ficheroFirmado = new byte[0];
        try{
            ficheroFirmado=socket.enviaFichero(fichero);
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
            System.out.println(e);
        } catch (IOException ex) {
           // System.out.println("[ ERROR ]\tRecibiendo e. "+ex);
            System.exit(1);
        }
        BigInteger eInt =  new BigInteger(e);
        System.out.println("[CLIENTE]\tRecibe e del servidor.");
        return eInt;
    }



    private void generateOpacityFactorK(){
        System.out.println("[CLIENTE]\tGenera el factor K. ");
        int r = (2^(new Random(200).nextInt()));
        if(r<0){
            r=r*-1;
        }
        this.k=new BigInteger(new byte[r]);
        //return k;
    }

    private BigInteger creaHashFichero(String fichero){
        System.out.println("[CLIENTE]\tRealiza hash.");
        BigInteger inte = stringToBigInteger(fichero);
        return rsaAlgorithm.decrypt(inte);
    }


    private BigInteger generateX(BigInteger hmsg, BigInteger e){
        System.out.println("[CLIENTE]\tGenera X.");
        BigInteger x = hmsg.multiply(k.modPow(e,rsaAlgorithm.getn())).mod(rsaAlgorithm.getn());
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






