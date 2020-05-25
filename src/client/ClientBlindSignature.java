package client;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Random;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;


public class ClientBlindSignature {

    private int TBLOQUE=240;

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

    private String filePath;  private BigInteger k;

    public String getHostAndPort(){
        return socket.getHostAndPort();    }


    boolean blindProcess(File fichero) {
        System.out.println("Fichero+ "+fichero);
        if(fichero==null){
            return false;
        }
        generateOpacityFactorK();

        ArrayList<byte[]> afichero = new ArrayList<byte[]>();

        try {
            afichero = creaHashFichero(fichero);
            //System.out.println(afichero);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            finaliza();
        }

        System.out.println("Numero de bloques:"+afichero.size());


        BigInteger eServer = recibeE();
        BigInteger nServer = recibeN();

        ArrayList<byte[]> x = generateX(afichero, eServer, nServer);

        // Envio de la X
        ArrayList<byte[]> ficheroFirmado = enviaFichero(x);

        //Validar firma
        boolean verificado = verifySignature(x, eServer, nServer, ficheroFirmado);


        // Guardar firma
        if (verificado) {
            System.out.println("[CLIENTE]\tFirma realizada correctamente.");

            String data = calculateSign(k,nServer,ficheroFirmado);
            destruyeK();

            try {
                FileWriter  ficheroSalida = new FileWriter(this.filePath + "/ficheroFirmado");
                ficheroSalida.write(data);
                System.out.println(data);
                ficheroSalida.close();
            }catch(Exception ex){
                System.out.println("[ERROR]\tEscritura fichero.");
                ex.printStackTrace();
            }
        }else {
            System.out.println("[CLIENTE]\tFirma no realizada correctamente.");
        }
        finaliza();
        return verificado;


    }


//_____________________________________________________________

    /**
     * Estraer el path sin nombre del fichero
     * @param path  path sin nombre
     */
    public void pathFile(Path path){
        Path pathNoName = path.getParent();
        this.filePath= pathNoName.toString();
    }

    public String getPathFile(){
        return this.filePath;
    }


    private BigInteger recibeE() {
        byte[] e = new byte[0];
        try {
            e = socket.pideE();
        } catch (IOException ex) {
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


    private String calculateSign(BigInteger k, BigInteger nServer, ArrayList<byte[]> y) {
        StringBuilder firmacion = new StringBuilder();
        for(int i=0; i<y.size(); i++) {
            BigInteger firma = k.modInverse(nServer).multiply(new BigInteger(y.get(i))).mod(nServer);
            //System.out.println(firma);
            firmacion.append(new String(firma.toByteArray(),StandardCharsets.UTF_8));
            //firmacion.append( Base64.getEncoder().encodeToString(firma.toByteArray()));
        }

        return firmacion.toString();
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


    //_____________________________________________________________________

    /**
     * Convertir de byte a Integer
     */
    private BigInteger stringToBigInteger(String convert) {
//        byte[] encodedString = Base64.getEncoder().encodeToString(convert.getBytes(StandardCharsets.UTF_8)).getBytes();
        byte[] encodedString = convert.getBytes(StandardCharsets.UTF_8);
        BigInteger inte = new BigInteger(encodedString);
        return inte;
    }

    private String IntegerToString(BigInteger convert){

//        String converted = Base64.getEncoder().encodeToString(convert.toByteArray());
//        System.out.println(converted);
//        System.out.println(convert);
//
//        System.out.println("Convert "+convert.toByteArray());
//        System.out.println("Base64 "+Base64.getDecoder().decode(convert.toByteArray()));
//
//        String converted = new String(Base64.getDecoder().decode(convert.toByteArray()));
//
//        System.out.println("String "+converted);
        String converted = new String(convert.toByteArray());
        return converted;
    }



//___________________________________________________________________________



    private ArrayList<byte[]> creaHashFichero(File fichero) throws NoSuchAlgorithmException {
        try{
            MessageDigest sha=MessageDigest.getInstance("SHA-256");
            byte[] b =  Files.readAllBytes(fichero.toPath());
            int totalBlock = b.length/TBLOQUE;

            ArrayList<byte[]> hash = new ArrayList<byte[]>(totalBlock);
            int resto, pos=0;
            System.out.println("long: "+b.length+"   Tbloc: "+TBLOQUE+"   Total block: "+b.length/TBLOQUE+1);


            while ((resto = b.length - pos) > 0)
            {
                byte[] block = new byte[Math.min(resto, TBLOQUE)];

                System.arraycopy(b, pos, block, 0, block.length);
                hash.add(sha.digest(block));
                pos+=block.length;
            }

            System.out.println("hash: "+hash);
            return hash;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private ArrayList<byte[]> generateX(ArrayList<byte[]> hmsg, BigInteger e, BigInteger n) {

        System.out.println("[CLIENTE]\tGenera X.");
        ArrayList<byte[]> x=new ArrayList<byte[]>();
        for(int i = 0; i<hmsg.size(); i++) {
            BigInteger xbig =new BigInteger(hmsg.get(i)).multiply(k.modPow(e, n)).mod(n);
            x.add(xbig.toByteArray());
        }
        return x;
    }



    private boolean verifySignature(ArrayList<byte[]> x, BigInteger eServer, BigInteger nServer, ArrayList<byte[]> fichFirmado) {
        System.out.println("[CLIENTE]\tVerifica firma.");

        for(int i = 0; i<x.size(); i++) {
            BigInteger firma = new BigInteger(fichFirmado.get(i)).modPow(eServer, nServer);
            if(!new BigInteger(x.get(i)).equals(firma)) {
                return false;
            }
        }

        return true;
    }


    private ArrayList<byte[]> enviaFichero(ArrayList<byte[]> x) {

        try {
            ArrayList<byte[]> fichero = socket.enviaFichero(x);
            //System.out.println("Firmado: "+new BigInteger(ficheroFirmado));
            return fichero;

        } catch (IOException e) {
            System.out.println("[ ERROR ]\tEnviando mensaje. " + e);
            System.exit(1);
        }
        return null;
    }


}





