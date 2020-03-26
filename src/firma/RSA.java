package firma;

import java.math.BigInteger;
import java.security.SecureRandom;

public class RSA {

    private final BigInteger one = new BigInteger("1");
    private final SecureRandom random = new SecureRandom();

    private BigInteger d;
    private BigInteger e;
    private BigInteger n;

    RSA(int num){
        BigInteger p =  BigInteger.probablePrime(num/2,random);
        BigInteger q = BigInteger.probablePrime(num/2,random);
        BigInteger phi = (p.subtract(one)).multiply(q.subtract(one));

        n = p.multiply(q);
        String f = "65748595spas";
        e = new BigInteger(f.getBytes());
        d=e.modInverse(phi);
    }

    public BigInteger encrypt(BigInteger m){
        return m.modPow(e, n);
    }

    public BigInteger decrypt(BigInteger m){
        return m.modPow(d,n);
    }

    public BigInteger getd(){return d;}
    public BigInteger gete(){return e;}
    public BigInteger getn(){return n;}
}
