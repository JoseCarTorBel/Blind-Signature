package firma;

import java.math.BigInteger;
import java.util.Random;

public class Server {

    public Server(){ }

    // De momento generamos este exponente yaván.
    public int generateE(){
        return 8;
    }

    /**
     *
     * @param x Factor a exponenciar
     * @param d
     * @param n Modulo
     * @return
     */
    public BigInteger blindSignature(BigInteger x, BigInteger d, BigInteger n){

        BigInteger y = x.modPow(d,n);
        return y;
    }




}
