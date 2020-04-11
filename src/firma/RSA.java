package firma;

import java.math.BigInteger;
import java.security.SecureRandom;

public class RSA {
    int BIT_LENGTH=2048;

    private final BigInteger one = new BigInteger("1");
    private final SecureRandom random;

    private BigInteger d;
    private BigInteger e;
    private BigInteger n;

    /**
     * 1. Generate Random primes
     * 2. Calculate products
     * 3. Generate public and private exponent.
     */
    public RSA(){

        random = new SecureRandom();
        BigInteger p =  BigInteger.probablePrime(BIT_LENGTH/2, this.random);
        BigInteger q = BigInteger.probablePrime(BIT_LENGTH/2, this.random);

        n = p.multiply(q);
        BigInteger phi = (p.subtract(one)).multiply(q.subtract(one));
        System.out.println(phi);

        do e=new BigInteger(phi.bitLength(),random);
        while(e.compareTo(one)<=0 ||  e.compareTo(phi) >= 0 || !e.gcd(phi).equals(BigInteger.ONE));
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

    @Override
    public String toString() {
        return "RSA:\nd: "+d+"\ne: "+e+"\nn: "+n;
    }
}
