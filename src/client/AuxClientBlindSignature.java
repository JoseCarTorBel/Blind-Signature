package client;

import comun.MyStreamSocket;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class AuxClientBlindSignature {

    private MyStreamSocket mySocket;
    private InetAddress serverHost;
    private int serverPort;

    AuxClientBlindSignature(String hostName, String portNum) {
        try{
            serverHost = InetAddress.getByName(hostName);
            serverPort = Integer.parseInt(portNum);
            mySocket = new MyStreamSocket(serverHost,serverPort);

            System.out.println("Conexión establecida");

        }catch(SocketException e) {
            System.out.println("ERROR: Error en la creación de socket");
        }catch(UnknownHostException e) {
            System.out.println("ERROR: Host no reconocido");
        }catch(IOException e) {
            System.out.println("ERROR: Error en la comunicación");
        }finally {
            System.exit(1);
        }
    }

    public void pideE(){

    }

    public void enviaFicheros(byte[][] ficheros){

        for(int i =0; i<ficheros.length; i++) {
            try {
                mySocket.sendMessage(ficheros[i], 0, ficheros[i].length);

            } catch (IOException e) {
                System.out.println("[ ERROR ]\tError enviando ficheros" + e);

                System.exit(1);
            }
        }
    }

    public int recibePeticionFichero() {

        byte[] respuesta = new byte[0];
        try {
            respuesta = mySocket.receiveMessage();
        } catch (IOException e) {
            System.out.println("[ ERROR ]\tRebiendo mensaje. "+e);
            System.exit(1);
        }
        BigInteger resp = new BigInteger(respuesta);
        return resp.intValue();
    }

    public void enviaFichero(byte[] fichero){
        try {
            mySocket.sendMessage(fichero,0,fichero.length);
        } catch (IOException e) {
            System.out.println("[ ERROR ]\tEnviando mensaje. "+e);
            System.exit(1);
        }
    }




}
