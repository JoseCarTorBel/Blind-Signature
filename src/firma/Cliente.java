package firma;
// https://www.novixys.com/blog/how-to-generate-rsa-keys-java/
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.math.BigInteger;
import java.security.*;
import java.util.*;

public class Cliente  {

    KeyPair kp;

    public Cliente(){}

    /**
     * Genera el factor de opacidad.
     * @return  random num
     */
    public BigInteger generateOpacityFactor(){
        int r = (2^(new Random(200).nextInt()));
        if(r<0){
            r=r*-1;
        }
        return new BigInteger(new byte[r]);
    }

    /**
     * Inicializa el creador de llaves.
     * @throws NoSuchAlgorithmException
     */
    public void initKeyGenerator() throws NoSuchAlgorithmException {
        KeyPairGenerator key =  KeyPairGenerator.getInstance("RSA");
        key.initialize(2048);
        kp = key.generateKeyPair();
    }

    public Key getPublicKey(){
        return kp.getPublic();
    }

    public Key getPrivate() {
        return kp.getPrivate();
    }

    public byte[] generateHashRSA(String m, Key publicK) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
         byte[] h;

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicK);
        h=cipher.doFinal(m.getBytes());

        return h;
    }

    /**
     * @param k     Factor de opacidad
     * @param e     Exponente público del que firma
     * @param h     Hash del mensaje
     */


    public void generateX(Byte[] k, byte[] e, byte[] h){
        byte[] x;
       // return (k^e)*h;

    }



}
