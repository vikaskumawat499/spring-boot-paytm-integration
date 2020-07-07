package iciciBankWCOnline_Stmt3_0_Implement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.SignatureException;

public class iciciBankWCOnline_initiateTransaction_API
{

	 //Update the Organization name as Vendor (Provided by Perfios)

	  static String vendor="iciciBankWCOnline"; //Update vendor ID here

	  static String txnID="UniqueTxnId";// Transaction ID to be passed by client for mapping/reference

	  //The Host and server
	  static String Server="https://demo48.perfios.com"; 

	  static String Host="demo48.perfios.com"; 

	  //Initiate Transaction URL 

	  private static final String Initiate_Transaction_URL = Server+"/KuberaVault/api/v3/organisations/"+vendor+"/transactions";

	  //The padding and algorithm used for Signature generation

	  static final String ENCRYPTION_ALGO = "SHA256withRSA/PSS";

	  //update the payload request for Initiate Transaction API
	  
	  static String InitiateTarnsactionPayload=
		       "<payload>"
		      + "<txnId>"+txnID+"</txnId>"
		      + "<loanAmount>2000</loanAmount>"
		      + "<loanDuration>6</loanDuration>"
		      + "<loanType>SME</loanType>"
		      + "<processingType>STATEMENT</processingType>"
		      + "<transactionCompleteCallbackUrl>http://122.170.7.185:8080/CallbackTest/CallbackStatus</transactionCompleteCallbackUrl>" 
		      + "<acceptancePolicy>atLeastOneTransactionPerMonthInRange</acceptancePolicy>"  // atLeastOneTransactionPerMonthInRange , atLeastOneTransactionInRange
		      + "<uploadingScannedStatements>false</uploadingScannedStatements>"   // scanned true and false
		      + "<yearMonthFrom>2019-10</yearMonthFrom>"
		      + "<yearMonthTo>2020-03</yearMonthTo>"
		      
			  +"<sourceType>GSTOD</sourceType>"
		      + "</payload>";
	  	  

	  static String privateKey ="-----BEGIN RSA PRIVATE KEY-----\r\n" + 
	  		"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\r\n" + 
	  		"-----END RSA PRIVATE KEY-----\r\n";

	  //--------Main method-----------------

	  public static void main(String[] args) throws IOException, InvalidKeyException, SignatureException, NoSuchAlgorithmException 
	  {
		  
	    Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
	    
	    InitiateAPI( );
	    
	  }

	  //-------------------All API Calls------------------------------

	  public static void InitiateAPI() throws InvalidKeyException, NoSuchAlgorithmException, SignatureException, IOException
	  {
	
	   String xPerfiosDate= "20191125T123214Z"; // Date(); // This method returns the current date and time in YYYYMMDD’T’HHMMSS’Z’ format
	   //Initiate transaction call made here----
	  
	   String signature= SignatureCreator("/KuberaVault/api/v3/organisations/"+vendor+"/transactions",InitiateTarnsactionPayload,xPerfiosDate,"");
	   
	   String PerfiosTransactionId=Make_Request(Initiate_Transaction_URL,signature,InitiateTarnsactionPayload,xPerfiosDate);

       System.out.println("PerfiosTransactionId : "+PerfiosTransactionId);
	  }

	                  
	  //----------Request creator-------------------------------
	 
	  public static String SignatureCreator(String URL, String Payload,String Date, String PerfiosTransactionId) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, UnsupportedEncodingException
	  {
	   System.out.println("InitiateTarnsactionPayload : "+Payload);
		  
	   String Method="POST";

	   String uriEncodedQuery="";

	    String sha256Payload=enc_dec_Impl.SHA256(Payload);

	    System.out.println("Print for reference ::: sha256Payload= "+sha256Payload+"\n");

	    String xPerfiosDate=Date;

	    //Creating a Conanical Request

	    String CanonicalRequest=Method+"\n"

	      + enc_dec_Impl.uriEncode(URL)+"\n"

	      + uriEncodedQuery+"\n"

	      + "host:"+Host+"\n" 

	      + "x-perfios-content-sha256:"+sha256Payload+"\n"

	      + "x-perfios-date:"+xPerfiosDate+ "\n"

	      + "host;x-perfios-content-sha256;x-perfios-date" + "\n"

	      + sha256Payload;

	    System.out.println("CanonicalRequest:"+"\n"+CanonicalRequest+"\n");
	    
	    System.out.println("Print for reference :::   CanonicalRequest256 : "+enc_dec_Impl.SHA256(CanonicalRequest)+"\n");

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

	  
	  //---------------------------------Initiate_Transaction_URL method----------------------

	  public static String Make_Request(String URL, String Signature, String payload,String Date) throws IOException, NoSuchAlgorithmException 
	  {

	   System.out.println(URL);

	   String Method="POST";

	   URL obj = new URL(URL);

	   HttpURLConnection con = (HttpURLConnection) obj.openConnection();

	   
	   if(URL.contains("types"))
	   {

	    Method = "GET";
	    System.out.println("GET Done Here");

	   }

	   con.setRequestMethod(Method);
	  System.out.println("Method : "+Method);

	  System.out.println("Below are the headers values : ");
	  
	 con.addRequestProperty("content-type", "application/xml");
	   
	 //  System.out.println("content-type      application/xml");

	  con.addRequestProperty("Accept", "application/xml");
	  
	   
	 //  System.out.println("Accept      application/xml");

	   con.setRequestProperty("Host", Host);
	   
	   System.out.println("Host      "+Host);

	   con.setRequestProperty("X-Perfios-Algorithm", "PERFIOS-RSA-SHA256");
	   System.out.println("X-Perfios-Algorithm 			PERFIOS-RSA-SHA256");
	   
	   
	   con.setRequestProperty("X-Perfios-Content-Sha256", enc_dec_Impl.SHA256(payload));
	   System.out.println("X-Perfios-Content-Sha256"+enc_dec_Impl.SHA256(payload));

	   con.setRequestProperty("X-Perfios-Date", Date);
	   System.out.println("X-Perfios-Date 	"+Date);

	  
	   con.setRequestProperty("X-Perfios-Signature",Signature);
	   System.out.println("X-Perfios-Signature  "+Signature);

	   con.setRequestProperty("X-Perfios-Signed-Headers", "host;x-perfios-content-sha256;x-perfios-date");
	   System.out.println("X-Perfios-Signed-Headers    host;x-perfios-content-sha256;x-perfios-date");
	   
	   con.setDoOutput(true);

	   OutputStream os = con.getOutputStream();
	   System.out.println("body : "+payload);
	   os.write(payload.getBytes());

	   os.flush();
	   os.close();
	  
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
	    // print result

	    System.out.println(response.toString());

	    return response.substring(90,107);    // Perfios transaction Id 

	   } 

	   else 
	   {

	    System.out.println("Request failed");

	    BufferedReader in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
	    String inputLine;

	    StringBuffer response = new StringBuffer();

	    while ((inputLine = in.readLine()) != null) 
	    {
	     response.append(inputLine);
	    }

	    in.close();

	    // print result

	    System.out.println(response.toString());

	    return response.toString();
	   }
	   
	  }  
	  
}
