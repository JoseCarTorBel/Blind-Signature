package firma;
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
    public BigInteger generateOpacityFactorK(){
        int r = (2^(new Random(200).nextInt()));
        if(r<0){
            r=r*-1;
        }
        return new BigInteger(new byte[r]);
    }

    /**param
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

    public BigInteger generateHashRSA(byte[] m, Key publicK) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
         byte[] h;

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicK);
        h=cipher.doFinal(m);
        BigInteger hBig = new BigInteger(h);
        return hBig;
    }

    /**
     * @param k         Factor de opacidad
     * @param e         Exponente público del que firma
     * @param hmsg      Hash del mensaje
     * @param n         Factor modular
     * @return Devuelve el factor X
     *
     */
    public BigInteger generateX( BigInteger hmsg,BigInteger k, int e, BigInteger n){
        BigInteger x = k.pow(e).multiply(hmsg).mod(n);
        return x;
    }

    /**
     * Detruye k
     * @param y
     * @param k
     * @return
     */
    public byte[] descipher(BigInteger y, BigInteger k, Key privateK) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher decipher = Cipher.getInstance("RSA");
        decipher.init(Cipher.DECRYPT_MODE, privateK);
        k=null; // Destrucción de k

        return decipher.doFinal(y.toByteArray());
    }
    // BigNumber -> hay que usar esa que deja exponenciar
}
