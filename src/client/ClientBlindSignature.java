package client;

import firma.RSA;

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

        System.out.println("Semilla generación de llaves: ");
        int num = Integer.parseInt(teclado.next());

        System.out.println("Semilla f: ");
        String f = teclado.next();


        rsaAlgorithm=new RSA(num,f);

        System.out.println("Fichero a firmar");
        String fichero =teclado.next();

        generateOpacityFactorK();

        BigInteger ficheroHash = creaHashFichero(fichero);
        BigInteger eServer = recibeE();

        BigInteger x = generateX(ficheroHash,eServer);

        /* Destruir K y verificar */



    teclado.close();
        try {
            socket.finaliza();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void enviaFichero(byte[] fichero){
        try{
            socket.enviaPeticion("1");
            socket.enviaFichero(fichero);
        } catch (IOException e) {
            System.out.println("[ ERROR ]\tEnviando mensaje. "+e);
            System.exit(1);
        }
    }

    private BigInteger recibeE() {
        byte[] e = new byte[0];
        try {
            e = socket.pideE();
        } catch (IOException ex) {
            System.out.println("[ ERROR ]\tRecibiendo e. "+ex);
            System.exit(1);
        }
        return new BigInteger(e);
    }

    private void generateOpacityFactorK(){
        int r = (2^(new Random(200).nextInt()));
        if(r<0){
            r=r*-1;
        }
        this.k=new BigInteger(new byte[r]);
        //return k;
    }

    private BigInteger creaHashFichero(String fichero){
        byte[] encodedString = Base64.getEncoder().encodeToString(fichero.getBytes()).getBytes();
        BigInteger inte = new BigInteger(encodedString);
        return rsaAlgorithm.decrypt(inte);
    }


    private BigInteger generateX(BigInteger hmsg, BigInteger e){
        BigInteger x = hmsg.multiply(k.modPow(e,rsaAlgorithm.getn())).mod(rsaAlgorithm.getn());
        return x;
    }

    public byte[] descipher(BigInteger y)  {
        byte[] original = Base64.getDecoder().decode(rsaAlgorithm.decrypt(y).toByteArray());
        return original;
    }
}






