package firma;
import javax.swing.*;
import java.math.BigInteger;
import java.util.Base64;


public class Firma {

    public static void main(String[] args) {

        String originalInput = "test input";
        byte[] encodedString = Base64.getEncoder().encodeToString(originalInput.getBytes()).getBytes();
        System.out.println("Byte -> "+encodedString);

        BigInteger inte = new BigInteger(encodedString);
        System.out.println("BigInteger -> "+inte);

        encodedString=inte.toByteArray();
        byte[] decodedBytes = Base64.getDecoder().decode(encodedString);



        String decodedString = new String(decodedBytes);
        System.out.println(decodedString);





/*
        Cliente client = new Cliente();
        Server server = new Server();

        // 1. Generamos K
        BigInteger k = client.generateOpacityFactorK();

        System.out.println("Mensaje: "+documento);

        BigInteger h = null;
        try {
            // 2. Generamos el hash
            h = client.generateHash(documentoByte);
            System.out.println("Hash mensaje: "+h);
        }catch (Exception ex){
            System.out.println("Fallo creando el hash");
            System.exit(1);
        }

        // Server envía E
        BigInteger e = server.getE();

        // 3. Cliente hace X
        BigInteger x = client.generateX(h,e);

        // 4. Server firma
        BigInteger y = server.blindSignature(x);
        System.out.println("Objeto firmado: "+y);

        //5. Verificar firma
        byte[] firma = client.descipher(y);
        System.out.println("Documento firmado: "+firma.toString());



*/






    }
}
