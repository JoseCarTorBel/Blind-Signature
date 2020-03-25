package server;

import comun.MyStreamSocket;

import java.net.ServerSocket;

public class ServerBlindSignature {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try (ServerSocket myConnectionSocket = new ServerSocket(7)) {
			while (true) {
				MyStreamSocket myDataSocket = new MyStreamSocket(myConnectionSocket.accept());
				System.out.println("connection accepted");
				Thread theThread = new Thread(new ThreadServerBlindSignature(myDataSocket));
				theThread.start();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

}
