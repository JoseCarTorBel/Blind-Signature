package server;

import comun.MyStreamSocket;

public class ThreadServerBlindSignature implements Runnable{
	MyStreamSocket myDataSocket;
	//private Firma firma;

	ThreadServerBlindSignature(MyStreamSocket myDataSocket) {
		this.myDataSocket = myDataSocket;
//		firma = new Firma();

	}


	public void run() { //Aqui debemos gestionar una sesion con un cliente

		boolean done = false;

		while(!done){

		}




	}

}
