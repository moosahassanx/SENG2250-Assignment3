import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
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
    private BigInteger signature;

    private final BigInteger publicYA;
    private final BigInteger publicYB;

    private String diffiePublic;

    // constructor
    public KeyGenerator()
    {
        this.p = new BigInteger("178011905478542266528237562450159990145232156369120674273274450314442865788737020770612695252123463079567156784778466449970650770920727857050009668388144034129745221171818506047231150039301079959358067395348717066319802262019714966524135060945913707594956514672855690606794135837542707371727429551343320695239");
        this.g = new BigInteger("174068207532402095185811980123523436538604490794561350978495831040599953488455823147851597408940950725307797094915759492368300574252438761037084473467180148876118103083043754985190983472601550494691329488083395492313850000361646482644608492304078721818959999056496097769368017749273708962006689187956744210730");
        this.publicYA = null;
        this.publicYB = null;
        this.diffiePublic = null;
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

        System.out.println("hashedIntMsg: " + hashedMsg);

        // pass it through the powmod method
        this.signature = powmod4(hashedMsg, this.d, this.N);       // creating signature
        System.out.println("\n SIGNATURE: " + signature);

        // does the hash of the message equal to s^e mod n?
        BigInteger sae = powmod4(this.signature, this.E, this.N);

        if(hashedMsg == sae)
        {
            System.out.println("\n MATCH!! ");
        }
        else
        {
            System.out.println("\n NOT MATCHING.............");

            System.out.println("\n hashIntMsg: " + hashedMsg);

            System.out.println("\n sae: " + sae);
        }

        // returning private key
        return this.privateKey;
    }

    // support methods
    public BigInteger hasher(String input) throws NoSuchAlgorithmException
    {
        MessageDigest d = MessageDigest.getInstance("SHA-256");
        d.update(diffiePublic.getBytes(), 0, diffiePublic.length());

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

    // mutators
    public void setDHPublicKey(BigInteger input)
    {
        this.diffiePublic = input.toString();
        System.out.println("\n Diffie public key is set.");
    }
}