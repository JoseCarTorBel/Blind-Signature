package server;

import comun.MyStreamSocket;
import firma.Firma;

public class ThreadServerBlindSignature implements Runnable{
	MyStreamSocket myDataSocket;
	private Firma firma;

	ThreadServerBlindSignature(MyStreamSocket myDataSocket) {
		this.myDataSocket = myDataSocket;
		firma = new Firma();

	}


	public void run() { //Aqui debemos gestionar una sesion con un cliente
		
	}

}
