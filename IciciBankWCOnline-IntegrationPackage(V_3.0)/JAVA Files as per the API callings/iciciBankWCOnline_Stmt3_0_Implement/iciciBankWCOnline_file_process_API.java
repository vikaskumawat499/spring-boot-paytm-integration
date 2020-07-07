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

public class iciciBankWCOnline_file_process_API 
{
	  // configurable parameters
	  static String vendor= iciciBankWCOnline_initiateTransaction_API.vendor;
	  static String Server= iciciBankWCOnline_initiateTransaction_API.Server;
	  static String Host = iciciBankWCOnline_initiateTransaction_API.Host;
	  static final String ENCRYPTION_ALGO = iciciBankWCOnline_initiateTransaction_API.ENCRYPTION_ALGO;
	  static String privateKey = iciciBankWCOnline_initiateTransaction_API.privateKey;
	  
	  // Need to update run time 
	  static String institutionId="20";  // for scanned transaction
	  
	  static String PerfiosTransactionId=iciciBankWCOnline_File_Upload_API.PerfiosTransactionId;  // 
	  
	  static String fileId = "da9a379734da19c0681605c7034e67a573992024";
	  
	  static String Password="";//If the file is password protected then Password of the PDF file to be updated here
	  
	  //--------Main method-----------------

	  public static void main(String[] args) throws IOException, InvalidKeyException, SignatureException, NoSuchAlgorithmException 
	  {
	    Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
	    Process_API(fileId);
	  }

	  public static void Process_API(String fileId) throws InvalidKeyException, NoSuchAlgorithmException, SignatureException, IOException
	  {
		  String xPerfiosDate= "20191125T123214Z";
		  
		    if(fileId.length()==40) 
		    {
		     //Process Statement call made here 
		     String ProcessStatement_Payload="";
		     if (Password.equals(""))       //Payload Without password
		     {
		      // ProcessStatement_Payload="<payload><fileId>"+fileId+"</fileId></payload>";
		    	 ProcessStatement_Payload="<payload><fileId>"+fileId+"</fileId></payload>"; // scanned
		     }

		     else  //Payload with password
		     {
		         ProcessStatement_Payload="<payload><fileId>"+fileId+"</fileId><password>"+Password+"</password></payload>";
		    	//  ProcessStatement_Payload="<payload><fileId>"+fileId+"</fileId><password>"+Password+"</password><institutionId>"+institutionId+"</institutionId></payload>";  // when scanned and password both
		     }
		     
		     
		     String ProcessStatement_Signature=SignatureCreator("/KuberaVault/api/v3/organisations/"+vendor+"/transactions/"+PerfiosTransactionId+"/bank-statements",ProcessStatement_Payload,xPerfiosDate,"");

		     String ProcessStatement_URL=Server+"/KuberaVault/api/v3/organisations/"+vendor+"/transactions/"+PerfiosTransactionId+"/bank-statements";

		     String ProcessResponse=Make_Request(ProcessStatement_URL,ProcessStatement_Signature,ProcessStatement_Payload,xPerfiosDate);

		     System.out.println("ProcessResponse : \n"+ProcessResponse);
	  }
}
	  
	//----------Request creator-------------------------------

	  public static String SignatureCreator(String URL, String Payload,String Date, String PerfiosTransactionId) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, UnsupportedEncodingException
	  {

	   String Method="POST";
	   
	   String uriEncodedQuery="";

	    String sha256Payload=enc_dec_Impl.SHA256(Payload);

	    System.out.println("sha256Payload= "+sha256Payload+"\n");

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


	    //Creating a String to sign using Conanical Request

	   String StringToSign ="PERFIOS-RSA-SHA256" + "\n"

	     + xPerfiosDate + "\n"

	     + enc_dec_Impl.SHA256(CanonicalRequest);

	   System.out.println("StringToSign:"+"\n"+StringToSign+"\n");

	   //  Create a checksum using String to sign

	   String Checksum=enc_dec_Impl.SHA256(StringToSign);

	   System.out.println("Checksum : "+Checksum);

	   //Encryption the String to sign using RSA private key

	   String Signature=enc_dec_Impl.encrypt(Checksum, enc_dec_Impl.buildPrivateKey(privateKey),enc_dec_Impl.buildPublicKey(privateKey));

	   System.out.println("Signature= "+Signature+"\n");

	   return Signature;

	  }
	  
	  
	  
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
	   
	   System.out.println("content-type      application/xml");

	   con.addRequestProperty("Accept", "application/xml");
	   
	   System.out.println("Accept      application/xml");

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

	   if (responseCode == HttpURLConnection.HTTP_OK || responseCode == 202)
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
	     response.append(inputLine);
	    }

	    in.close();
	    // print result
	    System.out.println(response.toString());

	    return response.toString();
	   }   
	  }  
    
}
