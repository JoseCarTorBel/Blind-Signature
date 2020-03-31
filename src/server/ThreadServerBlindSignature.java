package server;

import comun.MyStreamSocket;
import firma.RSA;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Base64;
import java.util.Random;

public class ThreadServerBlindSignature implements Runnable{
	MyStreamSocket myDataSocket;
	private RSA rsaAlgorithm = new RSA(new Random().nextInt(),"servidorFirmante");
	private String PIDE_E="0",RECIBE_FICHERO="1",RECIBE_FICHEROS="2";

	ThreadServerBlindSignature(MyStreamSocket myDataSocket) {
		this.myDataSocket = myDataSocket;
	}


	public void run() { //Aqui debemos gestionar una sesion con un cliente

		boolean done = false;
		byte[] mensaje;
		byte[] op;
		try {
			while (!done) {
				op= Base64.getDecoder().decode(myDataSocket.receiveMessage());
				String opcion = new String(op);
				System.out.println("[SERVER]\tOpcion recibida "+opcion);

				if(opcion.equals(PIDE_E)){
					byte[] e = rsaAlgorithm.gete().toByteArray();
					byte[] eSended = Base64.getDecoder().decode(e);

					myDataSocket.sendMessage(e,0,e.length);
				}else if (opcion.equals(RECIBE_FICHERO)){
					byte[] fichero = myDataSocket.receiveMessage();
					byte[] ficheroFirmado = realizaFirma(fichero);
					myDataSocket.sendMessage(ficheroFirmado,0,ficheroFirmado.length);

				}else {	/**Termina operación */
					myDataSocket.close();
					done=true;
				}
			}
		}catch (Exception ex){
			System.out.println("[ERROR]\tServer failed."+ex);
			System.exit(1);
		}
	}


	private byte[] realizaFirma(byte[] fichero){
		BigInteger x = new BigInteger(fichero);
		BigInteger y = x.modPow(rsaAlgorithm.getd(),rsaAlgorithm.getn());
		byte[] ySigned = Base64.getDecoder().decode(y.toByteArray());
		return ySigned;
	}

	// TODO implementar la firma totalmente ciega
	private boolean validaFicheros(byte[][] ficheros){
		return true;
	}

}
