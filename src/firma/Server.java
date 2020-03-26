package firma;

import java.math.BigInteger;

/**
 * Envía e para la x
 */

public class Server {

    private RSA rsa;

    public Server() {
        rsa = new RSA(1000);
    }

    /**
     * @param x Factor a exponenciar
     * @return
     */
    public BigInteger blindSignature(BigInteger x){
        BigInteger y = x.modPow(rsa.getd(),rsa.getn());

        return y;
    }

    public BigInteger getE(){
        return rsa.gete();
    }
}



