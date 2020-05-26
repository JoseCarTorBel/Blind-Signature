package client;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Random;


import static javax.swing.WindowConstants.EXIT_ON_CLOSE;


public class ClientBlindSignature {
	
	private int N=10;
	private int COMPRANDOM = 20;

    public static void main (String[]args){
        ClientBlindSignature client = new ClientBlindSignature();
        socket = new AuxClientBlindSignature("localhost", "1099");
        VistaCliente vistaCliente = new VistaCliente(client);
        vistaCliente.setBounds(700,400,400,600);
        vistaCliente.setDefaultCloseOperation(EXIT_ON_CLOSE);
        vistaCliente.setVisible(true);
        vistaCliente.setResizable(false);
    }

    private static AuxClientBlindSignature socket; 

    private String filePath;  private BigInteger k;

    public String getHostAndPort(){
        return socket.getHostAndPort();    }


    boolean blindProcess(File fichero) {
    	System.out.println("Fichero+ "+fichero);
    	if(fichero==null){
            return false;
        }
        BigInteger k = generateOpacityFactorK();
        
        byte[] afichero = null ;

		try {
			afichero = creaHashFichero(fichero);
			System.out.println(afichero);
		} catch (NoSuchAlgorithmException e) {	
			e.printStackTrace();
			finaliza();
		} catch (IOException e) {
			finaliza();
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		        
		
        BigInteger eServer = recibeE();
        BigInteger nServer = recibeN();

        byte[] x = generateX(afichero, eServer, nServer,k);
        
        // Envio de la X
        byte[] ficheroFirmado = enviaFichero(x);

        //Validar firma
        boolean verificado = verifySignature(x, eServer, nServer, ficheroFirmado);
        

        // Guardar firma
        if (verificado) {
            System.out.println("[CLIENTE]\tFirma realizada correctamente.");
          
            String data = calculateSign(k,nServer,ficheroFirmado);
            destruyeK();
            
            try {
            	FileWriter  ficheroSalida = new FileWriter(this.filePath + "/ficheroFirmado");              
                ficheroSalida.write(data);
                System.out.println(data);
                ficheroSalida.close();
            }catch(Exception ex){
                System.out.println("[ERROR]\tEscritura fichero.");
                ex.printStackTrace();
            }
        }else {
            System.out.println("[CLIENTE]\tFirma no realizada correctamente.");
        }
        finaliza();
        return verificado;   	
    }
    
    boolean blindProcessPartial(ArrayList<File> ficheros) {
    	if(ficheros.contains(null)) {
    		return false;
    	}
    	try {
	    	// LO DEL HASH
	    	ArrayList<byte[]> filesConComponente = new ArrayList<byte[]>();
	    	ArrayList<byte[]> files = new ArrayList<byte[]>();
	    	
	    	

	    	SecureRandom random = new SecureRandom();
	        byte bytes[] = new byte[COMPRANDOM];
	        byte[] fich;
	    	
	        for(int i=0;i<ficheros.size(); i++) {
	    		
	    		fich=creaHashFichero(ficheros.get(i));
	    		
	    		files.add(fich);
	    		
	    		byte[] destination = new byte[fich.length + COMPRANDOM];
	    		
	    		System.arraycopy(fich, 0, destination, 0, fich.length);
	    		random.nextBytes(bytes);
	    		System.arraycopy(bytes, 0, destination, fich.length, COMPRANDOM);
	    		
	    		filesConComponente.add(destination);
	    	}
	    	
	    	BigInteger eServer = recibeE();
	        BigInteger nServer = recibeN();
	        
	        ArrayList<byte[]> x = new ArrayList<byte[]>();
	        ArrayList<byte[]> k = new ArrayList<byte[]>();
	        
	        for(int i = 0; i<files.size();i++) {
	        	BigInteger kbig = generateOpacityFactorK();
	        	k.add(kbig.toByteArray());
	        	x.add(generateX(files.get(i), eServer, nServer,new BigInteger(k.get(i))));
	        }
	        
	        int j = Integer.parseInt(new String(Base64.getDecoder().decode(socket.pideJ())));
	        
	        
	        boolean verificados = enviaFicheros(files, x, k,j);
	        
	        if(!verificados)
	        	return false;
	        
	        // Envio de la X
	        byte[] ficheroFirmado = enviaFichero(x.get(j));

	        //Validar firma
	        boolean verificado = verifySignature(x.get(j), eServer, nServer, ficheroFirmado);
	        

	        // Guardar firma
	        if (verificado) {
	            System.out.println("[CLIENTE]\tFirma realizada correctamente.");
	          
	            String data = calculateSign(new BigInteger(k.get(j)),nServer,ficheroFirmado);
	            destruyeK();
	            
	            try {
	            	FileWriter  ficheroSalida = new FileWriter(this.filePath + "/ficheroFirmado");              
	                ficheroSalida.write(data);
	                System.out.println(data);
	                ficheroSalida.close();
	            }catch(Exception ex){
	                System.out.println("[ERROR]\tEscritura fichero.");
	                ex.printStackTrace();
	            }
	        }else {
	            System.out.println("[CLIENTE]\tFirma no realizada correctamente.");
	        }
	        finaliza();
	        return verificado;   	
	   
    	}catch(Exception ex) {
    		System.out.println("[ERROR]\n Error firma parcialmente ciega.");
    	}
		return false;
    	
    }
    
    
    
    
//_____________________________________________________________

    /**
     * Estraer el path sin nombre del fichero
     * @param path  path sin nombre
     */
    public void pathFile(Path path){
        Path pathNoName = path.getParent();
        this.filePath= pathNoName.toString();
    }

    public String getPathFile(){
        return this.filePath;
    }


    private BigInteger recibeE() {
        byte[] e = new byte[0];
        try {
            e = socket.pideE();
        } catch (IOException ex) {
            System.exit(1);
        }
        BigInteger eInt = new BigInteger(e);
        System.out.println("[CLIENTE]\tRecibe e del servidor.");
        return eInt;
    }

    private BigInteger recibeN() {
        byte[] n = new byte[0];
        try {
            n = socket.pideN();
            //   System.out.println(e);
        } catch (IOException ex) {
            // System.out.println("[ ERROR ]\tRecibiendo e. "+ex);
            System.exit(1);
        }
        BigInteger nInt = new BigInteger(n);
        System.out.println("[CLIENTE]\tRecibe N del servidor.");
        return nInt;
    }

    
    private BigInteger generateOpacityFactorK() {
        System.out.println("[CLIENTE]\tGenera el factor K. ");
        Random random;
        try {
            random = SecureRandom.getInstance("SHA1PRNG");
            return new BigInteger(2048, random);

        } catch (NoSuchAlgorithmException ex) {
            System.out.println("[ERROR]\tNot find algorithm " + ex);
            System.exit(1);
        }
		return null;
    }

    /**
     * Calula la firma  a partir del  y*k**-1 mod n
     * @param k
     * @param nServer
     * @param y
     * @return
     */
    private String calculateSign(BigInteger k, BigInteger nServer, byte[] y) {    	
    	
        BigInteger firma = k.modInverse(nServer).multiply(new BigInteger(y)).mod(nServer);
        return new String(firma.toByteArray(),StandardCharsets.UTF_8);       	
    }
    	
    
    private void destruyeK() {
        System.out.println("[CLIENTE]\tDestruye K.");
        this.k = null;
    }

    
    
    
    private void finaliza() {
        try {
            socket.finaliza();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   
    //_____________________________________________________________________

    /**
     * Convertir de byte a Integer
     */
    private BigInteger stringToBigInteger(String convert) {
//        byte[] encodedString = Base64.getEncoder().encodeToString(convert.getBytes(StandardCharsets.UTF_8)).getBytes();
        byte[] encodedString = convert.getBytes(StandardCharsets.UTF_8);
        BigInteger inte = new BigInteger(encodedString);
        return inte;
    }

    private String IntegerToString(BigInteger convert){

//        String converted = Base64.getEncoder().encodeToString(convert.toByteArray());
//        System.out.println(converted);
//        System.out.println(convert);
//
//        System.out.println("Convert "+convert.toByteArray());
//        System.out.println("Base64 "+Base64.getDecoder().decode(convert.toByteArray()));
//
//        String converted = new String(Base64.getDecoder().decode(convert.toByteArray()));
//
//        System.out.println("String "+converted);
        String converted = new String(convert.toByteArray());
        return converted;
    }



//___________________________________________________________________________


    /**
     * Creación del hash del fichero.
     * @param fichero
     * @return
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
	private byte[] creaHashFichero(File fichero) throws NoSuchAlgorithmException, IOException {
	    
	    int buff = 240;
	    try {
	        RandomAccessFile file = new RandomAccessFile(fichero, "r");
	     
	        MessageDigest hashSum = MessageDigest.getInstance("SHA-256");
	        byte[] buffer = new byte[buff];
	        byte[] partialHash = null;
	        long read = 0;
	        // calculate the hash of the hole file for the test
	        long offset = file.length();
	        int unitsize;
	        while (read < offset) {
	            unitsize = (int) (((offset - read) >= buff) ? buff : (offset - read));
	            file.read(buffer, 0, unitsize);
	            hashSum.update(buffer, 0, unitsize);
	            read += unitsize;
	        }
	        file.close();
	        partialHash = new byte[hashSum.getDigestLength()];
	        partialHash = hashSum.digest();
	       
	        return partialHash;
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	    }
	    return null;
	}
	
	
	  private byte[] generateX(byte[] hmsg, BigInteger e, BigInteger n, BigInteger k) {
		    
	        System.out.println("[CLIENTE]\tGenera X.");	        
	        BigInteger x =new BigInteger(hmsg).multiply(k.modPow(e, n)).mod(n);
	       
	        return x.toByteArray();
	    }
	  
	  
	
	    private boolean verifySignature(byte[] x, BigInteger eServer, BigInteger nServer,byte[] fichFirmado) {
	        System.out.println("[CLIENTE]\tVerifica firma.");

        	BigInteger firma = new BigInteger(fichFirmado).modPow(eServer, nServer);
        	return new BigInteger(x).equals(firma);
	    }
	    
	    private byte[] enviaFichero(byte[] x) {
	      
	        try {
	            byte[] fichero = socket.enviaFichero(x);
	            //System.out.println("Firmado: "+new BigInteger(ficheroFirmado));
	            return fichero;

	        } catch (IOException e) {
	            System.out.println("[ ERROR ]\tEnviando mensaje. " + e);
	            System.exit(1);
	        }
	        return null;
	    }
	    
	    
	    private boolean enviaFicheros(ArrayList<byte[]> ficheros, ArrayList<byte[]> x, ArrayList<byte[]> k, int j) {
		      byte[] verificados = null;
			try {
				verificados = socket.enviaFicheros(x,k,ficheros,j);
			} catch (IOException e) {
				System.out.print("[CLIENTE]\n Error enviando ficheros.");
				e.printStackTrace();
			}
		      
		      String ver = new String(Base64.getDecoder().decode(verificados));
		      if(ver.equals("1"))
		    	  return true;
		      return false;
	    }

}






