package iciciBankWCOnline_Stmt3_0_Implement;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.SignatureException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class iciciBankWCOnline_File_Upload_API 
{

	  // configurable parameters
	  static String vendor= iciciBankWCOnline_initiateTransaction_API.vendor;
	  static String Server= iciciBankWCOnline_initiateTransaction_API.Server;
	  static String Host = iciciBankWCOnline_initiateTransaction_API.Host;
	  static final String ENCRYPTION_ALGO = iciciBankWCOnline_initiateTransaction_API.ENCRYPTION_ALGO;
	  static String privateKey = iciciBankWCOnline_initiateTransaction_API.privateKey;
	 
	  // Need to update run time
	  static String PerfiosTransactionId="M9LN1593665872434";
	  

	 //static String FilePath="D:\\eStatement oct - dec-2019.pdf";	
	 static String FilePath="D:\\eStatement-Jan-march-2020.pdf";	
	  
  //--------Main method-----------------

	  public static void main(String[] args) throws IOException, InvalidKeyException, SignatureException, NoSuchAlgorithmException 
	  {
		  
	    Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
	    
	    Upload_API( FilePath);   
	    
	  }

	  public static void Upload_API(String FilePath) throws InvalidKeyException, NoSuchAlgorithmException, SignatureException, IOException
	  {
		  String xPerfiosDate= "20191125T123214Z";
		  String fileId=UploadStatement(FilePath,PerfiosTransactionId,xPerfiosDate);
		  
		   if(fileId.length()==40) 
		    {
			   System.out.println("Now calling the process API");
			   System.out.println("fileId : "+fileId);
		    }

		    else
		    {
		     System.out.println("Error Uploading the statement");
		    }
		  
	  }
	  
	 

  //----------Request creator-------------------------------

	  public static String SignatureCreator(String URL, String Payload,String Date, String PerfiosTransactionId) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, UnsupportedEncodingException
	  {

	   String Method="POST";
	   
	   String uriEncodedQuery="";

	    String sha256Payload=enc_dec_Impl.SHA256(Payload);

	    System.out.println("sha256Payload= "+sha256Payload+"\n");

	    //RequestDate in ‘YYYYMMDD’T’HHMMSS’Z’‘ format

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

	   //Create a checksum using String to sign

	   String Checksum=enc_dec_Impl.SHA256(StringToSign);

	   System.out.println("Checksum : "+Checksum);

	   //Encryption the String to sign using RSA private key

	   String Signature=enc_dec_Impl.encrypt(Checksum, enc_dec_Impl.buildPrivateKey(privateKey),enc_dec_Impl.buildPublicKey(privateKey));

	   System.out.println("Signature= "+Signature+"\n");

	   return Signature;

	  }
 //-------------------Upload STatement---------

	  

	  public static String UploadStatement(String FilePath,String perfiosTransactionId,String xPerfiosDate) throws IOException, InvalidKeyException, NoSuchAlgorithmException, SignatureException 
	  {

	   File file= new File(FilePath);

	   OkHttpClient client = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).writeTimeout(180, TimeUnit.SECONDS).readTimeout(180, TimeUnit.SECONDS).build();

	      RequestBody body = new MultipartBody.Builder()

	        .setType(MultipartBody.FORM)

	              .addFormDataPart("file", file.getName(),RequestBody.create(MediaType.parse("application/pdf"),file))
	            //  .addFormDataPart("organisationName", vendor)
	            //  .addFormDataPart("perfiosTransactionId", perfiosTransactionId) 

	              .build();

	      Request request = new Request.Builder()

	                  .url(Server+"/KuberaVault/api/v3/organisations/"+vendor+"/transactions/"+perfiosTransactionId+"/files")

	                  .addHeader("content-type", "multipart/form-data")

	                 .addHeader("Host", Host)

	                 .addHeader("X-Perfios-Algorithm", "PERFIOS-RSA-SHA256")

	                 .addHeader("X-Perfios-Content-Sha256", enc_dec_Impl.SHA256(""))

	                 .addHeader("X-Perfios-Date", xPerfiosDate)

	                 .addHeader("X-Perfios-Signature", SignatureCreator("/KuberaVault/api/v3/organisations/"+vendor+"/transactions/"+perfiosTransactionId+"/files","",xPerfiosDate,""))

	                 .addHeader("X-Perfios-Signed-Headers", "host;x-perfios-content-sha256;x-perfios-date")

	                 .addHeader("cache-control", "no-cache")

	                  .post(body)

	                  .build();

	      Response response = client.newCall(request).execute();

	      String result = response.body().string();

	      String Response=result.substring(69,109);

	      System.out.println(result+"\n");

	      System.out.println("result sub string File Id : Response "+result.substring(69,109)+"\n");

	      return Response.toString();

	  }
	  

}
