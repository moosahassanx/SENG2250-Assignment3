import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class KeyGenerator
{
    // attributes
    private BigInteger p;
    private BigInteger g;
    private BigInteger N;
    private BigInteger E;
    private String publicKey;
    private String privateKey;
    private BigInteger d;
    private String message;
    private BigInteger hexInteger;
    private BigInteger signature;

    private final BigInteger publicYA;
    private final BigInteger publicYB;

    private String diffiePublic;

    private BigInteger HMACValue;
    String iv;

    // constructor
    public KeyGenerator() {
        this.p = new BigInteger("178011905478542266528237562450159990145232156369120674273274450314442865788737020770612695252123463079567156784778466449970650770920727857050009668388144034129745221171818506047231150039301079959358067395348717066319802262019714966524135060945913707594956514672855690606794135837542707371727429551343320695239");
        this.g = new BigInteger("174068207532402095185811980123523436538604490794561350978495831040599953488455823147851597408940950725307797094915759492368300574252438761037084473467180148876118103083043754985190983472601550494691329488083395492313850000361646482644608492304078721818959999056496097769368017749273708962006689187956744210730");
        this.publicYA = null;
        this.publicYB = null;
        this.diffiePublic = null;
        this.HMACValue = null;
        this.iv = "ayo my slime wyd";
    }

    // methods
    public String generateRSAPublic(String input) throws UnsupportedEncodingException, NoSuchAlgorithmException
    {
        // variables
        this.message = input;

        // message to hex-message conversion
        System.out.println("\n converting message (\"" + this.message + "\") to BigInteger...");
        this.hexInteger = new BigInteger(this.message.getBytes("UTF-8"));

        System.out.println("\n message in BigInteger form: " + hexInteger);
        
        // calculations
        this.N = this.p.multiply(this.g);
        this.E = new BigInteger("65537");
        
        this.d = chooseD(this.E, this.hexInteger);

        // generating private key
        this.privateKey = "(" + this.p + ", " + this.g + ", " + this.d + ")";

        // returner
        this.publicKey = "(" + this.N.toString() + ", " + this.E.toString() + ")";

        return this.publicKey;
    }

    public String generateRSAPrivate() throws NoSuchAlgorithmException, UnsupportedEncodingException
    {
        // GENERATING SIGNATURE
        System.out.println("\n pre-hash: " + this.message);         // hashing the message
        BigInteger hashedMsg = hasher(this.message);
        System.out.println("\n pos-hash: " + hashedMsg);

        // HMAC
        this.HMACValue = HMAC(this.diffiePublic, this.message);
        System.out.println("\n HMACValue: " + this.HMACValue);

        // pass it through the powmod method
        this.signature = powmod4(hashedMsg, this.d, this.N);       // creating signature
        System.out.println("\n SIGNATURE: " + signature);

        // does the hash of the message equal to s^e mod n?
        BigInteger sae = powmod4(this.signature, this.E, this.N);

        // returning private key
        return this.privateKey;
    }

    // support methods
    public static List<String> splitEqually(String text, int size)
    {
        // Give the list the right capacity to start with. You could use an array instead if you wanted.
        List<String> ret = new ArrayList<String>((text.length() + size - 1) / size);
    
        for (int start = 0; start < text.length(); start += size)
        {
            ret.add(text.substring(start, Math.min(text.length(), start + size)));
        }
        return ret;
    }

    public String encrypt(String plaintext)
    {
        try
        {
            // IV - a randomly generated thing
            byte[] ivByte = iv.getBytes();

            // hashed key is session key hashed
            byte[] hashedKey = hashing(this.diffiePublic.toString());
            
            System.out.print("\n hashedKey: ");
            for (byte b : hashedKey)
            {
                System.out.print(b);
            }
            System.out.println();

            Cipher AES = Cipher.getInstance("AES/ECB/NoPadding");
            AES.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(hashedKey, "AES"));

            // split the plaintext into 4 blocks - each block is 16 bytes
            List<String> plaintextArray = splitEqually(plaintext, 16);
            
            String concatBlock = "";
            // each 16byte block is xor-ed with each bit of IV
            for (String item : plaintextArray)
            {
                // do AES on xor-ed block, then add the result of that to biginteger
                byte[] itemByte = item.getBytes();
                byte[] xoredByte = xor(itemByte, ivByte);

                concatBlock += AES.doFinal(xoredByte);

                ivByte = AES.doFinal(xoredByte);

            }

            // for every block after that you xor each bit with the result of the previous block
            return concatBlock;

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return null;
    }

    public String decrypt(String ciphertext)
    {
        try
        {
            // IV - a randomly generated thing
            byte[] ivByte = iv.getBytes();

            // hashed key is session key hashed
            byte[] hashedKey = hashing(this.diffiePublic.toString());

            System.out.print("\n hashedKey: ");
            for (byte b : hashedKey)
            {
                System.out.print(b);
            }
            System.out.println();

            Cipher AES = Cipher.getInstance("AES/ECB/NoPadding");
            AES.init(Cipher.DECRYPT_MODE, new SecretKeySpec(hashedKey, "AES"));

            // split the ciphertext into 4 blocks - each block is 16 bytes
            List<String> plaintextArray = splitEqually(ciphertext, 16);

            String concatBlock = "";
            // each 16byte block is xor-ed with each bit of IV
            for (String item : plaintextArray)
            {
                // do AES on xor-ed block, then add the result of that to biginteger
                byte[] itemByte = item.getBytes();
                byte[] xoredByte = xor(itemByte, ivByte);

                concatBlock += AES.doFinal(xoredByte);

                ivByte = AES.doFinal(xoredByte);

            }

            // for every block after that you xor each bit with the result of the previous block
            return concatBlock;

        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return null;
    }

    public static byte[] concat(byte[] a, byte[] b)
    {
        byte[] result = new byte[a.length + b.length];

        for (int i = 0; i < a.length; ++i)
        {
            result[i] = a[i];
        }

        for (int i = 0; i < b.length; ++i)
        {
            result[i + a.length] = b[i];
        }

        return result;
    }

    public static byte[] H(byte[] x) throws Exception
    {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        return md.digest(x);
    }

    public byte[] xor(byte[] a, byte[] b)
    {
        byte[] result = new byte[Math.max(a.length, b.length)];

        for (int i = 0; i < result.length; ++i)
        {
            result[i] = (byte) (0xff & ((int) a[i] ^ (int) b[i]));
        }
        return result;
    }

    public static String toHexString(byte[] bytes) 
    {
        StringBuilder sb = new StringBuilder();

        for (byte b : bytes)
        {
            sb.append(String.format("%02X", b));
        }

        return sb.toString();
    }

    public BigInteger HMAC(String secretKey,String message) throws RuntimeException
    {
        try
        {
            BigInteger K_ba = new BigInteger(this.diffiePublic);
            
            // opad and ipad in byte array form
            byte[] opad = new byte[32];
            byte[] ipad = new byte[32];
            
            for (int i = 0; i < opad.length; ++i)
            {
                opad[i] = 0x5c;
                ipad[i] = 0x36;
            }

            byte[] m = new String(this.message).getBytes();
            byte[] k = H(K_ba.toByteArray());
            System.out.format("\n k = %s\n", toHexString(k));
            byte[] hmac = H(concat( H(xor(k, opad)), H(concat(xor(k, ipad), m))));
            System.out.format("\n hmac = %s\n", toHexString(hmac));

            BigInteger bigIntHMAC = new BigInteger(hmac);

            return bigIntHMAC;
        }
        catch(Exception e)
        {
            System.out.println("ERROR: " + e);
        }

        return E;
    }

    public byte[] hashing(String input) throws NoSuchAlgorithmException
    {
        MessageDigest d = MessageDigest.getInstance("SHA-256");
        d.update(input.getBytes(), 0, input.length());

        byte[] output = d.digest();

        return output;
    }

    public BigInteger hasher(String input) throws NoSuchAlgorithmException
    {
        MessageDigest d = MessageDigest.getInstance("SHA-256");

        d.update(this.diffiePublic.getBytes(), 0, this.diffiePublic.length());

        BigInteger hashM = new BigInteger(1, d.digest());

        return hashM;
    }

    public BigInteger powmod4(BigInteger base, BigInteger exponent, BigInteger modulo)
    {
        BigInteger x = BigInteger.ONE;
        
        while(exponent.compareTo(BigInteger.ZERO) == 1)
        {
            if(exponent.testBit(0))
            {
                x = (x.multiply(base)).mod(modulo);
            }

            exponent = exponent.shiftRight(1);
            base = (base.multiply(base)).mod(modulo);

        }

        return x.mod(modulo);
    }

    public BigInteger chooseD(BigInteger e, BigInteger m)
    {
        // find d, such that ed = 1 mod m
        BigInteger sigmaN = this.p.subtract(BigInteger.ONE).multiply(this.g.subtract(BigInteger.ONE));
        
        // finding D
        BigInteger D = this.E.modInverse(sigmaN);

        // returner
        return D;
    }

    // accessors
    public BigInteger getXA()
    {
        return publicYA;
    }

    public BigInteger getBX()
    {
        return publicYB;
    }

    public BigInteger getP()
    {
        return this.p;
    }

    public BigInteger getG()
    {
        return this.g;
    }

    public BigInteger getHMAC()
    {
        return this.HMACValue;
    }

    public String getEncryptedMessage()
    {
        return encrypt(this.message);
    }

    public String getDecryptedMessage(String input)
    {
        return decrypt(input);
    }

    // mutators
    public void setDHPublicKey(BigInteger input)
    {
        this.diffiePublic = input.toString();
        System.out.println("\n Diffie public key is set.");
    }
}