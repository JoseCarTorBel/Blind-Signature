package server;
import comun.MyStreamSocket;
import firma.RSA;

import java.util.ArrayList;
import java.util.Arrays;

import java.math.BigInteger;
import java.util.Base64;
import java.util.List;

public class ThreadServerBlindSignature implements Runnable {
    MyStreamSocket myDataSocket;

    private String PIDE_E = "0", RECIBE_FICHERO = "1", RECIBE_FICHEROS = "2",PIDE_N="3";

    /**
     * Número de bytes que varían en los ficheros, es aleatorio.
     */
    private int N=10;
    private int RETO = 10;


    private RSA rsaAlgorithm;

    ThreadServerBlindSignature(MyStreamSocket myDataSocket, RSA rsaAlgorithm) {
        this.myDataSocket = myDataSocket;
        this.rsaAlgorithm=rsaAlgorithm;
    }


    public void run() {

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
                    byte[] ficheroFirmado = realizaFirma(fichero,true);

                    myDataSocket.sendMessage(ficheroFirmado, 0, ficheroFirmado.length);

                }else if(opcion.equals(PIDE_N)) {
                    BigInteger n = rsaAlgorithm.getn();
                    byte[] nByte = n.toByteArray();
                    myDataSocket.sendMessage(nByte, 0, nByte.length);

                }else if(opcion.equals(RECIBE_FICHEROS)){
                    int j  = pidej();
                    byte[] jB = {(byte) j};
                    myDataSocket.sendMessage(jB,0,jB.length);

                    List<byte[]> ficheros=new ArrayList<byte[]>();
                    List<byte[]> factores = new ArrayList<byte[]>();

                    for(int f=0; f<N-1;f++){
                        ficheros.add(myDataSocket.receiveMessage());
                        factores.add(myDataSocket.receiveMessage());
                    }
                    // Envía 0 si es False, Envía 1 si es True.
                    if(validaFicheros(ficheros,factores, N)) {
                        myDataSocket.sendMessage(new byte[]{(byte) 0}, 0, jB.length);
                    }else {
                        myDataSocket.sendMessage(new byte[]{(byte) 1}, 0, jB.length);
                    }

                } else {    /** Termina operación */
                    System.out.println("[SERVER]\tProceso terminado.\n\tEXIT");
                    myDataSocket.close();
                    done = true;
                }
            }
        } catch (Exception ex) {
            System.out.println("[ERROR]\tServer failed." + ex);
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






//_____________________________________________________________________________________

    /**
     * Genera el J que le pide al cliente
     * @return int  Devulve la J
     */
    private int pidej(){
        return (int) Math.random()* RETO +1;
    }


    /**
     * Valida que los ficheros sean iguales.
     *
     * @param ficheros  Ficheros a validar
     * @param factores  Factores K a validar
     * @param lengthCompRandom  Longitud de la parte random.
     * @return  Si se han podido validar o no.
     */
    private boolean validaFicheros(List<byte[]> ficheros,List<byte[]> factores, int lengthCompRandom) {
        for(int i=1;i<ficheros.size();i++){
            if(     !   Arrays.equals(  Arrays.copyOf(ficheros.get(i-1),ficheros.get(i-1).length-lengthCompRandom),
                        Arrays.copyOf(ficheros.get(i),ficheros.get(i).length-lengthCompRandom))
                        &&
                    !   factores.get(i-1).equals(factores.get(i))                               ) {

                    return false;
            }
        }

        return true;
    }
}