package client;

import comun.MyStreamSocket;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Base64;

public class AuxClientBlindSignature {

    private MyStreamSocket mySocket;
    private InetAddress serverHost;
    private int serverPort;

    private String PEDIR_E="0",ENVIA_FICHERO="1",FINALIZA="2";

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

    /**
     * Envía el fichero y el servidor responde con el fichero firmado
     * @param fichero
     * @return Fichero firmado
     * @throws IOException
     */
    public byte[] enviaFichero(byte[] fichero) throws IOException {
        enviaPeticion(ENVIA_FICHERO);
        mySocket.sendMessage(fichero,0,fichero.length);
        return mySocket.receiveMessage();
    }

    public void finaliza() throws IOException {
        enviaPeticion(FINALIZA);
        mySocket.close();
    }





    // TODO this:
    //***************** FIRMA PARCIALMENTE CIEGA, NO IMPLEMENTADO TODAVÁIA: ********
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


    public void enviaFicheros(byte[][] ficheros) throws IOException {

        for(int i =0; i<ficheros.length; i++) {
            mySocket.sendMessage(ficheros[i], 0, ficheros[i].length);
        }
    }

}
