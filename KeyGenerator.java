import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;

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

    // constructor
    public KeyGenerator()
    {
        this.p = new BigInteger("178011905478542266528237562450159990145232156369120674273274450314442865788737020770612695252123463079567156784778466449970650770920727857050009668388144034129745221171818506047231150039301079959358067395348717066319802262019714966524135060945913707594956514672855690606794135837542707371727429551343320695239");
        this.g = new BigInteger("174068207532402095185811980123523436538604490794561350978495831040599953488455823147851597408940950725307797094915759492368300574252438761037084473467180148876118103083043754985190983472601550494691329488083395492313850000361646482644608492304078721818959999056496097769368017749273708962006689187956744210730");
    }

    // methods
    public String generateRSAPublic(String input) throws UnsupportedEncodingException, NoSuchAlgorithmException
    {
        // variables
        this.message = input;

        // message to hex-message conversion
        System.out.println("converting message (\"" + this.message + "\") to BigInteger...");
        this.hexInteger = new BigInteger(this.message.getBytes("UTF-8"));

        System.out.println("hexInteger: " + hexInteger);
        
        // calculations
        this.N = this.p.multiply(this.g);
        this.E = new BigInteger("65537");
        
        this.d = chooseD(this.E, this.hexInteger);

        // generating private key
        this.privateKey = "(" + this.p + ", " + this.g + ", " + this.d + ")";
        System.out.println("private key: " + privateKey);

        // signature generation
        // generateSignatures();

        // returner
        this.publicKey = "(" + this.N.toString() + ", " + this.E.toString() + ")";
        return this.publicKey;
    }

    public String generateRSAPrivate()
    {
        return this.privateKey;
    }

    public void generateSignatures()
    {
        // Signature Generation: s = h(m)^d mod(n)
        // learn how to hash something
        // use the powmod2() method to compute s


        // Signature Verification h(m) = s^e mod(n)


        /*
        // SIGNATURE GENERATION
        String hexIntString = this.hexInteger.toString();
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");     // hashing
        messageDigest.update(hexIntString.getBytes());
        String hashString = new String(messageDigest.digest());

        // modulus formula
        int sigGenBase = Integer.parseInt(hashString);
        int sigGenExpo = this.d.intValue();
        int sigGenN = this.N.intValue();
        
        // assigning to attribute
        int sigGenInt = powmod2(sigGenBase, sigGenExpo, sigGenN);
        this.signatureGeneration = String.valueOf(sigGenInt);

        // SIGNATURE VERIFIER
        int e = E.intValue();
        int sigVerifier = powmod2(sigGenInt, e, sigGenN);
        this.signatureVerification = String.valueOf(sigVerifier);

        System.out.print("Signature generation == verification: ");
        if(this.signatureGeneration == this.signatureVerification)
        {
            System.out.println("TRUE");
        }
        else
        {
            System.out.println("FALSE");
        }
        */

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

    public BigInteger chooseD(BigInteger e, BigInteger m)
    {
        BigInteger output = null;

        // find d, such that ed = 1 mod m
        BigInteger sigmaN = this.p.subtract(BigInteger.ONE).multiply(this.g.subtract(BigInteger.ONE));

        System.out.println("\n sigmaN: " + sigmaN);
        
        // finding D
        BigInteger D = this.E.modInverse(sigmaN);

        // System.out.println("\n D: " + D);

        output = D;

        // returner
        return output;
    }
}