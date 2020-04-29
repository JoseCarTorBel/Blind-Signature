package comun;

import java.net.*;
import java.io.*;

public class MyStreamSocket extends Socket {
   private Socket  socket;
   private BufferedReader input;
   private PrintWriter output;

   public MyStreamSocket(InetAddress acceptorHost,
                  int acceptorPort ) throws SocketException, IOException{
      socket = new Socket(acceptorHost, acceptorPort );
      setStreams( );
   }

   public MyStreamSocket(Socket socket)  throws IOException {
      this.socket = socket;
      String host = socket.getInetAddress().getHostName();
      System.out.println("Creado socket con host: " + host);
      setStreams();
   }

   private void setStreams( ) throws IOException{

      InputStream inStream = socket.getInputStream();
      input = new BufferedReader(new InputStreamReader(inStream));
      OutputStream outStream = socket.getOutputStream();
      output = new PrintWriter(new OutputStreamWriter(outStream));
   }

   public void sendMessage(byte[] messageArray, int start, int len) throws IOException {

      if(len<0) {
         throw new IllegalArgumentException("Negative length");
      }
      if(start<0 || start>=messageArray.length){
         throw new IndexOutOfBoundsException("Out of bounds: "+start);
      }

      OutputStream out = socket.getOutputStream();
      DataOutputStream dataOut =new DataOutputStream(out);

      dataOut.writeInt(len);
      if(len>0) {
         dataOut.write(messageArray, start, len);
      }
   }

   public byte[] receiveMessage( ) throws IOException {
      InputStream in = socket.getInputStream();
      DataInputStream dataIn = new DataInputStream(in);

      int len = dataIn.readInt();
      byte[] data = new byte[len];

      if(len>0){
         dataIn.readFully(data);
      }
      return data;
   }


   public void close( ) throws IOException {
      socket.close( );
   }
} 