package server;

import comun.MyStreamSocket;
import firma.RSA;

import java.math.BigInteger;
import java.net.ServerSocket;
import java.util.Base64;

/**
 * Al hilo que gestiona el cliente se le pasa el rsa ya que todos los documentos firmados por el banco tendrán la misma firma.
 */

public class ServerBlindSignature {

	public static void main(String[] args) {

		RSA rsaAlgorithm = new RSA();

		try (ServerSocket myConnectionSocket = new ServerSocket(1099)) {
			System.out.println("[SERVER]\tIniciado servidor.");
			while (true) {
				MyStreamSocket myDataSocket = new MyStreamSocket(myConnectionSocket.accept());
				System.out.println("connection accepted");
				Thread theThread = new Thread(new ThreadServerBlindSignature(myDataSocket,rsaAlgorithm));
				theThread.start();
			}
		} catch (Exception ex) {
			System.out.println("[ERROR]\tError conexion servidor");
			ex.printStackTrace();
		}
	}

}