package server;

import comun.MyStreamSocket;
import firma.RSA;
import sun.awt.X11.XVisibilityEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.util.Base64;

/**
 * Al hilo que gestiona el cliente se le pasa el rsa ya que todos los documentos firmados por el banco tendrán la misma firma.
 */

public class ServerBlindSignature {

	Vista vista;

	public static void main(String[] args) {
		ServerBlindSignature server = new ServerBlindSignature();
		server.ejecuta();
	}

	public void iniciaServer(){
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

	private void ejecuta()  {
		final Vista[] ventana = new Vista[1];

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				ventana[0] =new Vista();
				ventana[0].setBounds(500,250,300,250);
				ventana[0].setVisible(true);
				ventana[0].setResizable(false);
				ventana[0].setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			}
		});
	}


	/**
	 * Vista para iniciar el servidor
	 */
	private class Vista extends JFrame {
		private JLabel texto;
		private JButton enciendeServer;
		private JButton pararServer;

		public Vista() {
			setLayout(null);

			texto=new JLabel("Presione para encender el servidor.");
			texto.setBounds(50,50,220,30);
			add(texto);

			enciendeServer=new JButton("Enciende server");
			enciendeServer.setBounds(100,100,150,30);
			enciendeServer.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent actionEvent) {
					iniciaServer();
				}
			});
			this.add(enciendeServer);

			pararServer=new JButton("Apaga server");
			pararServer.setBounds(100,140,150,30);
			pararServer.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent actionEvent) {
					System.exit(0);
				}
			});
			this.add(pararServer);

		}
	}
}