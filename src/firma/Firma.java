package firma;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class Firma {

    public static void main(String[] args) {

    }

    public byte[] makeHash(String document){

        byte[] hash = new byte[0];
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            hash = digest.digest(document.getBytes(StandardCharsets.UTF_8));
        }catch (Exception ex){
            System.out.println("HASH FAILED");
        }
        return hash;
    }








}
