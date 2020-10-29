import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class KeyGenerator {
    // attributes
    private BigInteger p;
    private BigInteger g;
    private BigInteger modulusN;
    private BigInteger publicExponent;
    private String publicKey;
    private String privateKey;
    private BigInteger d;
    private char[] HEX_CHARS = "0123456789abcdef".toCharArray();
    private String message;
    private BigInteger hexInteger;
    private String signatureGeneration;
    private String signatureVerification;
    private String hash;

    // constructor
    public KeyGenerator() {
        // 1. Generate a pair of large, random primes p and q.
        this.p = new BigInteger(
                "178011905478542266528237562450159990145232156369120674273274450314442865788737020770612695252123463079567156784778466449970650770920727857050009668388144034129745221171818506047231150039301079959358067395348717066319802262019714966524135060945913707594956514672855690606794135837542707371727429551343320695239");
        this.g = new BigInteger(
                "174068207532402095185811980123523436538604490794561350978495831040599953488455823147851597408940950725307797094915759492368300574252438761037084473467180148876118103083043754985190983472601550494691329488083395492313850000361646482644608492304078721818959999056496097769368017749273708962006689187956744210730");
    }

    // methods
    public String generateRSAPublic() throws UnsupportedEncodingException, NoSuchAlgorithmException
    {
        // variables
        this.message = "raris and rovers, these hoes love chief sosa, hit em widda cobra";

        // message to hex-message conversion
        String hexStringBuilder = "";
        byte arr[] = message.getBytes("UTF8");
        for (byte b : arr)
        {
            hexStringBuilder += b;
        }

        this.hexInteger = new BigInteger(hexStringBuilder);

        System.out.println("hexInteger: " + hexInteger);
        
        // calculations
        this.modulusN = this.p.multiply(this.g);                                // 2. Compute the modulus n as n = pq. 
        this.publicExponent = new BigInteger("65537");                          // 3. Select an odd public exponent e between 3 and n-1 that is relatively prime to p-1 and q-1. 
        
        // this.d = findD(messageInt);

        // generating private key
        BigInteger d = computeD();
        this.privateKey = "(" + this.p + ", " + this.g + ", " + d + ")";
        System.out.println("private key: " + privateKey);

        // signatures
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashSplit = digest.digest(this.message.getBytes(StandardCharsets.UTF_8));

        // testing
        System.out.println("hash: ");
        for (byte b : hashSplit) 
        {
            this.hash += b;
        }


        // returner
        this.publicKey = "(" + this.modulusN.toString() + ", " + this.publicExponent.toString() + ")";
        return this.publicKey;
    }

    public BigInteger computeD()
    {
        // find d, such that ed = 1 mod m

        // find 1 mod m
        int hexInt = hexInteger.intValue();
        int modm = powmod2(1, 1, hexInt);
        
        System.out.println("modm: " + modm);

        String modmString = Integer.toString(modm);
        BigInteger BigModm = new BigInteger(modmString);

        // d = 1modm / e
        BigInteger d = BigModm.divide(this.publicExponent);
        System.out.println("d: " + d);

        return d;

    }

    // support methods
    public int powmod2(int base, int exponent, int modulus) 
    {
        long x = 1;
        long y = base;

        while (exponent > 0) 
        {
            if (exponent % 2 == 1) 
            {
                x = (x * y) % modulus;
            }

            y = (y * y) % modulus; // squaring the base
            exponent /= 2;
        }

        return (int) x % modulus;
    }

    public BigInteger findD(int m) 
    {
        BigInteger output = null;

        // find d, such that ed = 1 mod m

        // 1 mod m
        int modm = powmod2(1, 1, m);
        BigInteger modMBig = BigInteger.valueOf(modm);

        // d = e / (1 mod m)
        this.d = modMBig.divide(this.publicExponent);
        output = this.d;

        // returner
        return output;
    }

    public String hexadecimal(String input, String charsetName) throws UnsupportedEncodingException 
    {
        if (input == null)
        {
            throw new NullPointerException();
        }

        return asHex(input.getBytes(charsetName));
    }

    public String asHex(byte[] buf) 
    {
        char[] chars = new char[2 * buf.length];

        for (int i = 0; i < buf.length; ++i) 
        {
            chars[2 * i] = HEX_CHARS[(buf[i] & 0xF0) >>> 4];
            chars[2 * i + 1] = HEX_CHARS[buf[i] & 0x0F];
        }
        
        return new String(chars);
    }
}