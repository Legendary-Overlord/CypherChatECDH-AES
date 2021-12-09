package commucation;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.*;

public class AESDiffieHellmanKeyExchange {

    private PublicKey publicKey;
    KeyAgreement keyAgreement;
    byte[] sharedSecret;

    String ALGO = "AES";

    AESDiffieHellmanKeyExchange() {
        setKeyExchangeParams();
    }

    /**
     * Performs Basic Setup for KeyExchange
     */
    private void setKeyExchangeParams() {
        KeyPairGenerator kpg = null;
        try {
            //Setting up the Key Pair Generator with ALGO Elliptic Curve
            kpg = KeyPairGenerator.getInstance("EC");
            //key size 128 bits
            kpg.initialize(128);
            //Generating KeyPair
            KeyPair kp = kpg.generateKeyPair();
            //Assign publicKey
            publicKey = kp.getPublic();
            //Setting KeyAgreement to Elliptic Curve Diffie Hellman
            keyAgreement = KeyAgreement.getInstance("ECDH");
            //Initialize KeyAgreement with private key
            keyAgreement.init(kp.getPrivate());
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    /**
     * Set Receiver Public Key
     * @param publickey
     */
    public void setReceiverPublicKey(PublicKey publickey) {
        try {
            keyAgreement.doPhase(publickey, true);
            sharedSecret = keyAgreement.generateSecret();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    /**
     * Encrypts a Message
     * @param msg
     * @return
     */
    public String encrypt(String msg) {
        try {
            Key key = generateKey();
            Cipher c = Cipher.getInstance(ALGO);
            c.init(Cipher.ENCRYPT_MODE, key);
            byte[] encVal = c.doFinal(msg.getBytes());
            return new BASE64Encoder().encode(encVal);
        } catch (BadPaddingException | InvalidKeyException | NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Decrypts a Message
     * @param encryptedData
     * @return
     */
    public String decrypt(String encryptedData) {
        try {
            Key key = generateKey();
            Cipher c = Cipher.getInstance(ALGO);
            c.init(Cipher.DECRYPT_MODE, key);
            byte[] decodedValue = new BASE64Decoder().decodeBuffer(encryptedData);
            byte[] decValue = c.doFinal(decodedValue);
            return new String(decValue);
        } catch (BadPaddingException | InvalidKeyException | NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }
        return encryptedData;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    /**
     * Generates a new Key using the sharedSecret and Algorithm for encription.
     * @return
     */
    protected Key generateKey() {
        return new SecretKeySpec(sharedSecret, ALGO);
    }
}
