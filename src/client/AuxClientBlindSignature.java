package client;

import comun.MyStreamSocket;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Base64;

/**
 * Clase auxilair conexión sockets.
 */
public class AuxClientBlindSignature {

    private MyStreamSocket mySocket;
    private InetAddress serverHost;
    private int serverPort;

    private String 	PEDIR_E="0",
            ENVIA_FICHERO="1",
            ENVIA_FICHEROS="2",
            PEDIR_N="3",
            PEDIR_J="4",
            FINALIZA="5";

    AuxClientBlindSignature(String hostName, String portNum) {
        try{
            serverHost = InetAddress.getByName(hostName);
            serverPort = Integer.parseInt(portNum);
            mySocket = new MyStreamSocket(serverHost,serverPort);

            System.out.println("[CLIENTE]\tConexión establecida con "+serverHost+" en puerto "+serverPort);

        }catch(SocketException e) {
            System.out.println("[ERROR]\tError en la creación de socket");
            System.exit(1);
        }catch(UnknownHostException e) {
            System.out.println("[ERROR]\tHost no reconocido");
            System.exit(1);
        }catch(IOException e) {
            System.out.println("[ERROR]\tError en la comunicación");
            System.exit(1);

        }
    }

    // ******** IMPLEMENTACIÓN DE LA FIRMA TOTALMENTE CIEGA *******
    private void enviaPeticion(String peticion) throws IOException {
        byte[] encodedString = Base64.getEncoder().encodeToString(peticion.getBytes()).getBytes();
        mySocket.sendMessage(encodedString,0,encodedString.length);
    }

    /**
     * @return Devuelve el la E del servidor
     * @throws IOException
     */
    public byte[] pideE() throws IOException {
        enviaPeticion(PEDIR_E);
        return mySocket.receiveMessage();
    }

    public byte[] pideN() throws IOException {
        enviaPeticion(PEDIR_N);
        return mySocket.receiveMessage();
    }

    public byte[] pideJ( ) throws IOException {
        enviaPeticion(PEDIR_J);
        return mySocket.receiveMessage();
    }


    /**
     * Envía el fichero y el servidor responde con el fichero firmado
     * @param fichero
     * @return Fichero firmado
     * @throws IOException
     */
    public byte[] enviaFichero(byte[] fichero) throws IOException {
        enviaPeticion(ENVIA_FICHERO);

        System.out.println("FICHERO ------------------ > "+new BigInteger(fichero));
        mySocket.sendMessage(fichero,0,fichero.length);
        return mySocket.receiveMessage();
    }

    public void finaliza() throws IOException {
        enviaPeticion(FINALIZA);
        mySocket.close();
    }

    public String getHostAndPort(){
        return serverHost+" en puerto "+serverPort;    }





    public byte[] enviaFicheros(ArrayList<byte[]> x, ArrayList<byte[]> k, ArrayList<byte[]> ficheros, int j) throws IOException {
        enviaPeticion(ENVIA_FICHEROS);
        for(int i=0; i<x.size();i++) {
            if(i!=j) {
                mySocket.sendMessage(x.get(i),0,x.get(i).length);
                mySocket.sendMessage(ficheros.get(i),0,ficheros.get(i).length);
                mySocket.sendMessage(k.get(i),0,k.get(i).length);
            }
        }
        return mySocket.receiveMessage();
    }

}