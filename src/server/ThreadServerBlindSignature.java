package server;
import comun.MyStreamSocket;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Random;

public class ThreadServerBlindSignature implements Runnable {
    MyStreamSocket myDataSocket;

    /*private String 	PIDE_E = "0",
            RECIBE_FICHERO = "1",
            RECIBE_FICHEROS = "2",
            PIDE_N="3",
            PIDE_J="4";
     */
    
    private BigInteger 	PIDE_E = BigInteger.ZERO,
            RECIBE_FICHERO = BigInteger.ONE,
            RECIBE_FICHEROS = BigInteger.valueOf(2),
            PIDE_N=BigInteger.valueOf(3),
            PIDE_J=BigInteger.valueOf(4);
    
    /**
     * Número de bytes que varían en los ficheros, es aleatorio.
     */
    private int N=10;
    private int NCOMPRANDOM=20; //20 bytes

    private RSA rsaAlgorithm;

    ThreadServerBlindSignature(MyStreamSocket myDataSocket, RSA rsaAlgorithm) {
        this.myDataSocket = myDataSocket;
        this.rsaAlgorithm=rsaAlgorithm;
    }


    public void run() {

        boolean done = false;

        BigInteger opcion;
        try {
            while (!done) {
            	opcion = new BigInteger(myDataSocket.receiveMessage());
                System.out.println("[SERVER]\tOpcion recibida " + opcion);


                if (opcion.equals(PIDE_E)) {
                    BigInteger e = rsaAlgorithm.gete();

                    byte[] eSended = e.toByteArray();
                    myDataSocket.sendMessage(eSended, 0, eSended.length);

                } else if (opcion.equals(RECIBE_FICHERO)) {
                    byte[] fich = myDataSocket.receiveMessage();
                    System.out.println(new BigInteger(fich));
                    byte[] ficheroFirmado=realizaFirma(fich);

                    myDataSocket.sendMessage(ficheroFirmado, 0, ficheroFirmado.length);

                }else if(opcion.equals(PIDE_N)) {
                    BigInteger n = rsaAlgorithm.getn();
                    byte[] nByte = n.toByteArray();
                    myDataSocket.sendMessage(nByte, 0, nByte.length);
                }else if(opcion.equals(PIDE_J)) {
                    String j = eligeJ();
                    byte[] encode=  Base64.getEncoder().encodeToString(j.getBytes()).getBytes();
                    myDataSocket.sendMessage(encode, 0, encode.length);

                }else if(opcion.equals(RECIBE_FICHEROS)) {
                    ArrayList<byte[]> files = new ArrayList<byte[]>();
                    ArrayList<byte[]> kfiles = new ArrayList<byte[]>();
                    ArrayList<byte[]> x = new ArrayList<byte[]>();

                    for(int i =0; i<N-1; i++) {
                        x.add(myDataSocket.receiveMessage());
                        files.add(myDataSocket.receiveMessage());
                        kfiles.add(myDataSocket.receiveMessage());
                    }

                    boolean verificados = checkXFiles(x, kfiles, files);
                    boolean iguales = checkFilesEquals(files);

                    byte[] ver;

                    if(verificados && iguales)
                        ver = Base64.getEncoder().encodeToString("1".getBytes()).getBytes();
                    else
                        ver = Base64.getEncoder().encodeToString("0".getBytes()).getBytes();
                    myDataSocket.sendMessage(ver, 0, ver.length);

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





    // ________________________________________________________________________
    // PARCIALMENTE CIEGA

    /**
     * Elige el fichero que quiere firmar
     * @return num aleatorio 0<= i < N
     */
    private String eligeJ() {
        return Integer.toString(new Random().nextInt()%N);
    }

    /**
     * Comprueba que Xi sean los esperados para todos los Xi donde i != j
     *
     * @param X
     * @param kArray
     * @param files
     * @return
     */
    private boolean checkXFiles(ArrayList<byte[]> X, ArrayList<byte[]> kArray, ArrayList<byte[]> files) {
        BigInteger e =  rsaAlgorithm.gete();
        BigInteger n = rsaAlgorithm.getn();

        for(int i= 0; i<X.size(); i++) {
            BigInteger x = new BigInteger(X.get(i));
            BigInteger k = new BigInteger(kArray.get(i));
            BigInteger f = new BigInteger(files.get(i));

            BigInteger esperado = f.multiply(k.modPow(e, n)).mod(n);
            if(!esperado.equals(x)) {
                return false;
            }
        }
        return true;
    }

    private boolean checkFilesEquals(ArrayList<byte[]> files) {

        byte[] ant = files.get(0);
        byte[] fileAnt = new byte[ant.length-NCOMPRANDOM];

        for(int i = 0; i< ant.length-NCOMPRANDOM;i++) {
            fileAnt[i] = ant[i];
        }

        byte[] actual = new byte[ant.length-NCOMPRANDOM];
        for(int i = 1; i<files.size(); i++) {

            for(int j = 0; j< ant.length-NCOMPRANDOM;j++) {
                actual[j] = files.get(i)[j];
            }
            if(!new BigInteger(fileAnt).equals(new BigInteger(actual))) {
                return false;
            }
            fileAnt = actual;
        }
        return true;
    }
}