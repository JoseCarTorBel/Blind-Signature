package firma;
import java.math.BigInteger;
import java.util.Base64;


public class Firma {

    public static void main(String[] args) {

        String originalInput = "test input";

        System.out.println("String -> "+originalInput);
        byte[] encodedString = Base64.getEncoder().encodeToString(originalInput.getBytes()).getBytes();
        System.out.println("Byte -> "+encodedString);

        BigInteger inte = new BigInteger(encodedString);
        System.out.println("BigInteger -> "+inte);

        byte[] mensajeByte=inte.toByteArray();
        byte[] decodedBytes = Base64.getDecoder().decode(mensajeByte);


        String decodedString = new String(decodedBytes);
        System.out.println("String -> "+decodedString);






    }
}
