package iciciBankWCOnline_Stmt3_0_Implement;

import static java.lang.Integer.toHexString;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.codec.digest.DigestUtils;
import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.util.encoders.Hex;

public class enc_dec_Impl
{
	
	// Below methods are common for all API Calls
	
	 // --------------------------Create SHA256 Method-------------------------------
	
    public static String SHA256(String a) throws NoSuchAlgorithmException 
    {
        String payloadHash = a;
        String result = DigestUtils.sha256Hex(payloadHash);
        return result;
    }

    //-----------------------RSA Encryption-----------------------------------------------
    public static String encrypt(String raw, PrivateKey  k, PublicKey k2) throws InvalidKeyException, SignatureException, UnsupportedEncodingException, NoSuchAlgorithmException 
    {

         Signature privateSignature = Signature.getInstance(iciciBankWCOnline_initiateTransaction_API.ENCRYPTION_ALGO);
    
            privateSignature.initSign(k);

            privateSignature.update(raw.getBytes("UTF-8"));
            
            byte[] signature = privateSignature.sign();

            byte[] encoded = Hex.encode(signature);

            String str = new String(encoded);

            //Verify Signature using PublicKey

            privateSignature.initVerify(k2);

            privateSignature.update(raw.getBytes("UTF-8"));

            if(privateSignature.verify(signature))
            {
                System.out.println("Verified");
            }
            else
            {
                System.out.println("Something is wrong");
            }

            return str;
    }

//  -----------------------------buildPrivateKey Method--------------------------------

    public static PrivateKey buildPrivateKey(String privateKeySerialized) 
    {
        StringReader reader = new StringReader(privateKeySerialized);
        PrivateKey pKey = null;
        try 
        {
            PEMReader pemReader = new PEMReader(reader);
            KeyPair keyPair = (KeyPair) pemReader.readObject();
            pKey = keyPair.getPrivate();
            pemReader.close();
        }

        catch (IOException i) 
        {
            i.printStackTrace();
        }
        return pKey;
    }
    
    //-------------------------------buildPublicKey--------------------------

    public static PublicKey buildPublicKey(String privateKeySerialized)
	  {
		  StringReader reader = new StringReader(privateKeySerialized);
		  PublicKey pKey = null;
		
		  try 
			  {
			   PEMReader pemReader = new PEMReader(reader);
			   KeyPair keyPair = (KeyPair) pemReader.readObject();
			   pKey = keyPair.getPublic();
			   pemReader.close();
			  }
			catch (IOException i) 
			 {
				i.printStackTrace();
			 }
		  return pKey;
	  }
    
    //------------------------------Create 'YYYYMMDD’T’HHMMSS’Z’‘ format Date----------------------------------------

    public static String Date() 
    {    
     DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss" );  

     LocalDateTime now = LocalDateTime.now();  

     String time=dtf.format(now);

     String Date=time.subSequence(0,8)+"T"+time.subSequence(8,14)+"Z";

     System.out.println("Date:"+Date+"\n");  

     return Date;

    } 
    
    //  ---------------------------encodeURIComponent Method------------------------------

    public static String uriEncode(final CharSequence input)
    {
        final StringBuilder result = new StringBuilder();

        for (int i = 0; i < input.length(); i++) 
        {
            final char ch = input.charAt(i);
            if ((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z') || (ch >= '0' && ch <= '9') || ch == '_' || ch == '-' || ch == '~' || ch == '.' || ch == '/')
            {
                result.append(ch);
            }
            else 
            {
             result.append(toHexString(ch));
            }
        }

        return result.toString();

    }
    
}
