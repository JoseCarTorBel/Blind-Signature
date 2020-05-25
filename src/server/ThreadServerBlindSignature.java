package server;
import comun.MyStreamSocket;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Base64;

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
                    BigInteger e = rsaAlgorithm.gete();

                    byte[] eSended = e.toByteArray();
                    myDataSocket.sendMessage(eSended, 0, eSended.length);

                } else if (opcion.equals(RECIBE_FICHERO)) {
                    String hola =new String(Base64.getDecoder().decode(myDataSocket.receiveMessage()));
                    Integer numBloc = Integer.parseInt(hola);


                    System.out.println("Num bloc "+hola);

                    ArrayList<byte[]> fichero = new ArrayList<byte[]>(numBloc);
                    ArrayList<byte[]> ficheroFirmado = new ArrayList<byte[]>(numBloc);


                    for(int i =0; i< numBloc; i++) {
                        byte[] fich = myDataSocket.receiveMessage();
                        fichero.add(fich);
                        ficheroFirmado.add(realizaFirma(fich));
                    }

                    for(int i =0; i<numBloc; i++) {
                        myDataSocket.sendMessage(ficheroFirmado.get(i), 0, ficheroFirmado.get(i).length);
                    }


                }else if(opcion.equals(PIDE_N)) {
                    BigInteger n = rsaAlgorithm.getn();
                    byte[] nByte = n.toByteArray();
                    myDataSocket.sendMessage(nByte, 0, nByte.length);
                }else {
                    done=true;
                    myDataSocket.close();
                }
            }
        } catch (Exception ex) {
            System.out.println("[ERROR]\tServer failed." + ex);
            ex.printStackTrace();
        }
    }


    private byte[] realizaFirma(byte[] fichero) {
        System.out.println("[SERVER]\nRealiza firma.");
        BigInteger x = new BigInteger(fichero);

        BigInteger y = x.modPow(rsaAlgorithm.getd(), rsaAlgorithm.getn());

        return y.toByteArray();

    }

}