package server;

import comun.MyStreamSocket;

import java.net.ServerSocket;

public class ServerBlindSignature {

	public static void main(String[] args) {
		try (ServerSocket myConnectionSocket = new ServerSocket(1099)) {
			System.out.println("[SERVER]\tIniciado servidor.");
			while (true) {
				MyStreamSocket myDataSocket = new MyStreamSocket(myConnectionSocket.accept());
				System.out.println("connection accepted");
				Thread theThread = new Thread(new ThreadServerBlindSignature(myDataSocket));
				theThread.start();
			}
		} catch (Exception ex) {
			System.out.println("[ERROR]\tError conexion servidor");
			ex.printStackTrace();
		}

	}

}
