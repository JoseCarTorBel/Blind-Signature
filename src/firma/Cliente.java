package firma;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * Acciones cliente:
 *  - Genera K -> Factor opacidad
 *  - Realiza el HASH
 *  - Calcula X
 *  - Verifica
 *  - Destruyhe K
 */
public class Cliente  {

    BigInteger k;
    RSA rsa ;

    public Cliente(){
        rsa = new RSA(20);}

    /**
     * Genera el factor de opacidad.
     * @return  random num
     */
    public BigInteger generateOpacityFactorK(){
        int r = (2^(new Random(200).nextInt()));
        if(r<0){
            r=r*-1;
        }
        this.k=new BigInteger(new byte[r]);
        return k;
    }

    /**
     * Realiza el hash rsa del mensaje
     * @param m
     * @return  hash del mensaje.
     */
    public BigInteger generateHash(byte[] m){
        BigInteger mInt = new BigInteger(m);
        return rsa.decrypt(mInt);
    }

    /**
     * @param hmsg      Hash del mensaje
     * @return Devuelve el factor X
     */
    public BigInteger generateX(BigInteger hmsg, BigInteger e){
        BigInteger x = hmsg.multiply(k.modPow(e,rsa.getn())).mod(rsa.getn());
        return x;
    }

    /**
     * Detruye k
     * @return
     */
    public byte[] descipher(BigInteger y)  {
        byte[] original = rsa.decrypt(y).toByteArray();
        return original;
    }
}
