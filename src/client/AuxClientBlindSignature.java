package client;

import comun.MyStreamSocket;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class AuxClientBlindSignature {

    private MyStreamSocket mySocket;
    private InetAddress serverHost;
    private int serverPort;

    AuxClientBlindSignature(String hostName, String portNum)
            throws SocketException, UnknownHostException, IOException {

        serverHost = InetAddress.getByName(hostName);
        serverPort = Integer.parseInt(portNum);
        mySocket = new MyStreamSocket(serverHost,serverPort);

        //Metodo para crear nueva firma

    }


}
