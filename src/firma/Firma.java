package firma;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class Firma {

    public static void main(String[] args) {
        String documentoStr = "0xe04fd020ea3a6910a2d808002b30309d";
        byte[] documentoByte = documentoStr.getBytes();
        BigInteger documento = new BigInteger(documentoByte);

        Cliente client = new Cliente();
        Server server = new Server();

        BigInteger n = new BigInteger("02b3030");
        BigInteger k = client.generateOpacityFactorK();
        try {
            client.initKeyGenerator();
        }catch (NoSuchAlgorithmException ex){
            System.out.println("No se ha reconocido el algoritmo");
            return;
        }

        try {
            client.generateHashRSA(documentoByte, client.getPublicKey());
        }catch (Exception ex){
            System.out.println("Error generando hash");
        }

        BigInteger k= client.generateOpacityFactorK();

        int e = server.generateE();
        BigInteger x = client.generateX(documento,k,e,n);

        //BigInteger y = server.blindSignature(x,)



    }





}
