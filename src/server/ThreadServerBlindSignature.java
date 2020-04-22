package server;

import comun.MyStreamSocket;
import firma.RSA;
import java.math.BigInteger;
import java.util.Base64;

public class ThreadServerBlindSignature implements Runnable {
    MyStreamSocket myDataSocket;

    private String PIDE_E = "0", RECIBE_FICHERO = "1", RECIBE_FICHEROS = "2",PIDE_N="3";

    private RSA rsaAlgorithm;

    ThreadServerBlindSignature(MyStreamSocket myDataSocket, RSA rsaAlgorithm) {
        this.myDataSocket = myDataSocket;
        this.rsaAlgorithm=rsaAlgorithm;
    }


    public void run() { //Aqui debemos gestionar una sesion con un cliente

        boolean done = false;
        byte[] op;
        try {
            while (!done) {
                op = Base64.getDecoder().decode(myDataSocket.receiveMessage());
                String opcion = new String(op);
                System.out.println("[SERVER]\tOpcion recibida " + opcion);

                if (opcion.equals(PIDE_E)) {
                    // Pasamos a byte el big integer
                    BigInteger e = rsaAlgorithm.gete();
                   // System.out.println("E -> "+e);

                    byte[] eSended = e.toByteArray();

                    myDataSocket.sendMessage(eSended, 0, eSended.length);

                } else if (opcion.equals(RECIBE_FICHERO)) {
                    byte[] fichero = myDataSocket.receiveMessage();
               //     System.out.println("Fichero:" + new BigInteger(fichero));
                    byte[] ficheroFirmado = realizaFirma(fichero);

                    myDataSocket.sendMessage(ficheroFirmado, 0, ficheroFirmado.length);

                }else if(opcion.equals(PIDE_N)){
                    BigInteger n = rsaAlgorithm.getn();
                    byte[] nByte = n.toByteArray();
                    myDataSocket.sendMessage(nByte,0,nByte.length);
                } else {    /**Termina operación */
                    System.out.println("[SERVER]\tProceso terminado.\n\tEXIT");
                    myDataSocket.close();
                    done = true;
                }
            }
        } catch (Exception ex) {
            System.out.println("[ERROR]\tServer failed." + ex);
            System.exit(1);
        }
    }


    private byte[] realizaFirma(byte[] fichero) {
        System.out.println("[SERVER]\nRealiza firma.");
        BigInteger x = new BigInteger(fichero);
        BigInteger y = x.modPow(rsaAlgorithm.getd(), rsaAlgorithm.getn());
        //byte[] ySigned = Base64.getDecoder().decode(y.toByteArray());
        // System.out.println("y= "+y);
        return y.toByteArray();
    }


    // TODO implementar la firma parcialmente ciega
    //***************** FIRMA PARCIALMENTE CIEGA, NO IMPLEMENTADO TODAVÁIA: ********
    private boolean validaFicheros(byte[][] ficheros) {
        return true;
    }

}

