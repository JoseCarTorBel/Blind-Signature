package server;

import comun.MyStreamSocket;
import firma.RSA;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Base64;

public class ThreadServerBlindSignature implements Runnable {
    MyStreamSocket myDataSocket;

    private String PIDE_E = "0", RECIBE_FICHERO = "1", RECIBE_FICHEROS = "2",PIDE_N="3";

    private int N=10;

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
                    byte[] ficheroFirmado = realizaFirma(fichero,true);

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


    private byte[] realizaFirma(byte[] fichero, boolean esTotal) {
        System.out.println("[SERVER]\nRealiza firma.");
        if(esTotal){
            BigInteger x = new BigInteger(fichero);
            BigInteger y = x.modPow(rsaAlgorithm.getd(), rsaAlgorithm.getn());
            //byte[] ySigned = Base64.getDecoder().decode(y.toByteArray());
            // System.out.println("y= "+y);
            return y.toByteArray();
        }

        BigInteger x = new BigInteger(Arrays.copyOf(fichero,fichero.length-N));
        BigInteger y = x.modPow(rsaAlgorithm.getd(), rsaAlgorithm.getn());
        //byte[] ySigned = Base64.getDecoder().decode(y.toByteArray());
        // System.out.println("y= "+y);
        return y.toByteArray();
    }



    // TODO implementar la firma parcialmente ciega

    /**
     * Genera el J que le pide al cliente
     * @param N
     * @return int
     */
    private int pidej(int N){
        return (int) Math.random()*N+1;
    }

    private boolean validaFicheros(byte[][] ficheros,int lengthCompRandom) {
        for(int i=1;i<ficheros.length;i++){

            if(!Arrays.equals(  Arrays.copyOf(ficheros[i-1],ficheros[i-1].length-lengthCompRandom),
                    Arrays.copyOf(ficheros[i],ficheros[i-1].length-lengthCompRandom))) {
                return false;
            }
        }
        return true;
    }
}

