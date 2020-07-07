package iciciBankWCOnline_Stmt3_0_Implement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.SignatureException;

public class iciciBankWCOnline_institutions_API
{

	  static String vendor= iciciBankWCOnline_initiateTransaction_API.vendor;
	  static String Server= iciciBankWCOnline_initiateTransaction_API.Server;
	  static String Host = iciciBankWCOnline_initiateTransaction_API.Host;
	  static final String ENCRYPTION_ALGO = iciciBankWCOnline_initiateTransaction_API.ENCRYPTION_ALGO;
	  static String privateKey = iciciBankWCOnline_initiateTransaction_API.privateKey;

	  private static final String institutions_URL = Server+"/KuberaVault/api/v3/organisations/"+vendor+"/institutions";

	  //--------Main method-----------------

	  public static void main(String[] args) throws IOException, InvalidKeyException, SignatureException, NoSuchAlgorithmException 
	  {

	    Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

	    institutions_List_API();   
	  }

	  //-------------------institutions_List_API Calls------------------------------

	  public static void institutions_List_API() throws InvalidKeyException, NoSuchAlgorithmException, SignatureException, IOException
	  {
	   String xPerfiosDate= "20200105T123214Z"; // Date(); // This method returns the current date and time in YYYYMMDD’T’HHMMSS’Z’ format
	 
	   String signature= SignatureCreator("/KuberaVault/api/v3/organisations/"+vendor+"/institutions","",xPerfiosDate,"");
	   
	   String institutions_List_resp=Make_Request(institutions_URL,signature,"",xPerfiosDate);

       System.out.println("institutions_List_resp : \n"+institutions_List_resp);
	  }

	                  
	  //----------Request creator-------------------------------

	  public static String SignatureCreator(String URL, String Payload,String Date, String PerfiosTransactionId) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, UnsupportedEncodingException
	  {
		  
	   String Method="GET";

	   String uriEncodedQuery="";

	    String sha256Payload=enc_dec_Impl.SHA256(Payload);

	    System.out.println("sha256Payload= "+sha256Payload+"\n");

	    String xPerfiosDate=Date;

	    String CanonicalRequest=Method+"\n"

	      + enc_dec_Impl.uriEncode(URL)+"\n"

	      + uriEncodedQuery+"\n"

	      + "host:"+Host+"\n" 

	      + "x-perfios-content-sha256:"+sha256Payload+"\n"

	      + "x-perfios-date:"+xPerfiosDate+ "\n"

	      + "host;x-perfios-content-sha256;x-perfios-date" + "\n"

	      + sha256Payload;

	    System.out.println("CanonicalRequest:"+"\n"+CanonicalRequest+"\n");
	    
	    System.out.println("CanonicalRequest256 : "+enc_dec_Impl.SHA256(CanonicalRequest));

	    //Creating a String to sign using Conanical Request

	   String StringToSign ="PERFIOS-RSA-SHA256" + "\n"

	     + xPerfiosDate + "\n"

	     + enc_dec_Impl.SHA256(CanonicalRequest);
	  

	   System.out.println("StringToSign:"+"\n"+StringToSign+"\n");


	   //Create a checksum using String to sign

	   String Checksum=enc_dec_Impl.SHA256(StringToSign);

	   System.out.println("Checksum : "+Checksum);

	   //Encryption the String to sign using RSA private key

	   String Signature=enc_dec_Impl.encrypt(Checksum, enc_dec_Impl.buildPrivateKey(privateKey),enc_dec_Impl.buildPublicKey(privateKey));

	   System.out.println("Signature= "+Signature+"\n");

	   return Signature;

	  }

	  
	  //---------------------------------Make_Request method----------------------

	  public static String Make_Request(String URL, String Signature, String payload,String Date) throws IOException, NoSuchAlgorithmException 
	  {

	   System.out.println(URL);

	   String Method="GET";

	   URL obj = new URL(URL);

	   HttpURLConnection con = (HttpURLConnection) obj.openConnection();

	   con.setRequestMethod(Method);
	  System.out.println("Method : "+Method);

	  System.out.println("Below are the headers values : ");
	  
	   con.addRequestProperty("content-type", "application/xml");
	   
	   con.setRequestProperty("X-Perfios-Date", Date);
	   
	   con.setRequestProperty("Host", Host);
	   
	   con.setRequestProperty("X-Perfios-Algorithm", "PERFIOS-RSA-SHA256");
	   
	   con.setRequestProperty("X-Perfios-Content-Sha256", enc_dec_Impl.SHA256(""));
	   
	   con.setRequestProperty("X-Perfios-Signature",Signature);
	   
	   con.setRequestProperty("X-Perfios-Signed-Headers", "host;x-perfios-content-sha256;x-perfios-date");

	   con.addRequestProperty("Accept", "application/xml");
	   
	   con.addRequestProperty("cache-control", "no-cache");
	   
	   con.setDoOutput(true);

	//   There is no body part  
	   
	/*   OutputStream os = con.getOutputStream();
	   System.out.println("body : "+payload);
	   os.write(payload.getBytes());
	   os.flush();
	   os.close();
    */
	  
	   int responseCode = con.getResponseCode();

	   System.out.println("Response Code :: " + responseCode);

	   if (responseCode == HttpURLConnection.HTTP_OK) 
	   {
		   // success
		    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
	
		    String inputLine;
	
		    StringBuffer response = new StringBuffer();
	
		    while ((inputLine = in.readLine()) != null) 
		    {
		     response.append(inputLine+"\n");
		    }	
		    in.close();
	
		    return response.toString();
	   } 

	   else 
	   {
		    System.out.println("Request failed");
	
		    BufferedReader in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
		    String inputLine;
	
		    StringBuffer response = new StringBuffer();
	
		    while ((inputLine = in.readLine()) != null) 
		    {
		     response.append(inputLine+"\n");
		    }
	
		    in.close();
	
		    return response.toString();
	   }
	   
	  }
	  
}
