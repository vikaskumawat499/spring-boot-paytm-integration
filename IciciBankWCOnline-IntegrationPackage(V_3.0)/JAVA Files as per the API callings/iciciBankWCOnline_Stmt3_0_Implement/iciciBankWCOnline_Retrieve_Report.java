package iciciBankWCOnline_Stmt3_0_Implement;

import static java.lang.Integer.toHexString;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.SignatureException;

import org.apache.commons.codec.digest.DigestUtils;
import org.bouncycastle.openssl.PEMReader;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

public class iciciBankWCOnline_Retrieve_Report
{
	
	 static String vendor= iciciBankWCOnline_initiateTransaction_API.vendor;
	  static String Server= iciciBankWCOnline_initiateTransaction_API.Server;
	  static String Host = iciciBankWCOnline_initiateTransaction_API.Host;
	  static final String ENCRYPTION_ALGO = iciciBankWCOnline_initiateTransaction_API.ENCRYPTION_ALGO;
	  static String privateKey = iciciBankWCOnline_initiateTransaction_API.privateKey;
	  
	  // Need to update run time 
	  static String PerfiosTransactionId=  iciciBankWCOnline_File_Upload_API.PerfiosTransactionId;  //
	  static String ReportFormat="xlsx,xml";
	  
	  static String DownloadReportAtLocation="C:\\Users\\Kalyan Chakravarthy\\Desktop\\Reports\\";	//Please make sure if the location path exists
	  
	  public static void main(String[] args) throws IOException, InvalidKeyException, SignatureException, NoSuchAlgorithmException 
	  {
	    Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
	    RetrieveReport_API(); 
	  }
	  
	  
	  public static void RetrieveReport_API() throws InvalidKeyException, NoSuchAlgorithmException, SignatureException, IOException
	  {
		  String xPerfiosDate= "20191125T123214Z";
		  
		   //Process Statement call made here 

		   String RetrieveReport_Payload="";

	       String RetrieveReport_URL=Server+"/KuberaVault/api/v3/organisations/"+vendor+"/transactions/"+PerfiosTransactionId+"/reports?types="+ReportFormat;

	       String RetrieveReport_Signature= SignatureCreator("/KuberaVault/api/v3/organisations/"+vendor+"/transactions/"+PerfiosTransactionId+"/reports?types="+ReportFormat,RetrieveReport_Payload,xPerfiosDate,PerfiosTransactionId);

	        if(ReportFormat.contains(","))
	        {
	         ReportFormat="zip";
	        }
	       RetrieveReport(RetrieveReport_URL,RetrieveReport_Signature,xPerfiosDate,DownloadReportAtLocation+"/"+PerfiosTransactionId+"."+ReportFormat);
	       
	       System.out.println("\nReport for Perfios Transaction("+PerfiosTransactionId+")Downloaded and saved at location: "+ DownloadReportAtLocation+"\n");
			
	       System.out.println("\n--------------------------------------------------\n");
	  }
	  
	  public static String SignatureCreator(String URL, String Payload,String Date, String PerfiosTransactionId) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, UnsupportedEncodingException {

		   String Method="POST";

		   String QueryParam="";

		   String QueryParamValue="";

		   String uriEncodedQuery="";

		   //If the Query parameters are present in the URL

		   if(URL.contains("?"))

		   {

		    Method = "GET";

		    QueryParam="types";

		    QueryParamValue=ReportFormat;

		    uriEncodedQuery=uriEncode(QueryParam)+"="+uriEncode(QueryParamValue);

		    URL="/KuberaVault/api/v3/organisations/"+vendor+"/transactions/"+PerfiosTransactionId+"/reports";

		   }

		   

		   //Lowercase(Hex(Sha256(Payload))) creation

		    String sha256Payload=SHA256(Payload);
		    String xPerfiosDate=Date;

		    //Creating a Conanical Request

		    String CanonicalRequest=Method+"\n"

		      + uriEncode(URL)+"\n"

		      + uriEncodedQuery+"\n"

		      + "host:"+Host+"\n" 

		      + "x-perfios-content-sha256:"+sha256Payload+"\n"

		      + "x-perfios-date:"+xPerfiosDate+ "\n"

		      + "host;x-perfios-content-sha256;x-perfios-date" + "\n"

		      + sha256Payload;

		  //  System.out.println("CanonicalRequest:"+"\n"+CanonicalRequest+"\n");

		    //Creating a String to sign using Conanical Request

		   String StringToSign ="PERFIOS-RSA-SHA256" + "\n"

		     + xPerfiosDate + "\n"

		     + SHA256(CanonicalRequest);

    		 //  System.out.println("StringToSign:"+"\n"+StringToSign+"\n");


		   //Create a checksum using String to sign

		   String Checksum=SHA256(StringToSign);

		   //Encryption the String to sign using RSA private key

		   String Signature=enc_dec_Impl.encrypt(Checksum, buildPrivateKey(privateKey),enc_dec_Impl.buildPublicKey(privateKey));

		  // System.out.println("Signature= "+Signature+"\n");

		   return Signature;

		  }

	  

	  public static void RetrieveReport(String URL,String Sinature, String xPerfiosDate, String filePath) throws IOException, NoSuchAlgorithmException, InvalidKeyException, SignatureException 
	  {

	   OkHttpClient client = new OkHttpClient();

	       String result="";

	   Request request = new Request.Builder()

	     .url(URL)

	     .get()

	     .addHeader("Content-Type", "application/xml")

	     .addHeader("X-Perfios-Date", xPerfiosDate)

	     .addHeader("Host", Host)

	     .addHeader("X-Perfios-Algorithm", "PERFIOS-RSA-SHA256")

	     .addHeader("X-Perfios-Content-Sha256", SHA256(""))

	     .addHeader("X-Perfios-Signature", Sinature)

	     .addHeader("X-Perfios-Signed-Headers", "host;x-perfios-content-sha256;x-perfios-date")

	     .addHeader("Accept", "application/xml")

	     .addHeader("cache-control", "no-cache")

	     .build();
	   
	   System.out.println("\nRetrieve Report : endPointURL : "+URL);
	   System.out.println("Method :       GET");

	   System.out.println("\nBelow are the headers values : \n");
	   
	    System.out.println("content-type                application/xml");
	    System.out.println("Accept      application/xml");
	    System.out.println("Host                         "+Host);
	    System.out.println("X-Perfios-Algorithm 			PERFIOS-RSA-SHA256");
	    System.out.println("X-Perfios-Content-Sha256 	    "+SHA256(""));
	    System.out.println("X-Perfios-Date 	            "+xPerfiosDate);
	    System.out.println("X-Perfios-Signature           "+Sinature);
	    System.out.println("X-Perfios-Signed-Headers      host;x-perfios-content-sha256;x-perfios-date");
	    System.out.println("cache-control                 no-cache");
	    
	    
	   Response response = client.newCall(request).execute();

	   ResponseBody body = response.body();

   	   File file= new File(filePath);

	      BufferedSource source = body.source();

	      BufferedSink sink = Okio.buffer(Okio.sink(file));

	      Buffer sinkBuffer = sink.buffer();

	      long totalBytesRead = 0;

	      int bufferSize = 8 * 1024;

	      for (long bytesRead; (bytesRead = source.read(sinkBuffer, bufferSize)) != -1; ) 
	      {
	          sink.emit();
	          totalBytesRead += bytesRead;
	      }

	      sink.flush();

	      sink.close();

	      source.close();

	   System.out.println("Total byte read:"+totalBytesRead+"\n");

	   System.out.println("Done\n");

	      //System.out.println(result);

	   }

	        // --------------------------Create SHA256 Method-------------------------------	    
	        public static String SHA256(String a) throws NoSuchAlgorithmException 
	        {
	            String payloadHash = a;
	            String result = DigestUtils.sha256Hex(payloadHash);
	           // System.out.println(result);
	            return result;
	        }

	      //  ---------------------------encodeURIComponent Method------------------------------

	        private static String uriEncode(final CharSequence input) 
	        {

	            final StringBuilder result = new StringBuilder();

	            for (int i = 0; i < input.length(); i++) {

	                final char ch = input.charAt(i);

	                if ((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z') || (ch >= '0' && ch <= '9') || ch == '_'

	                        || ch == '-' || ch == '~' || ch == '.' || ch == '/') {

	                    result.append(ch);
	                }
	                else 
	                {
	                 result.append(toHexString(ch));
	                }
	            }

	            return result.toString();

	        }

	    //  -----------------------------buildPrivateKey Method--------------------------------

	        private static PrivateKey buildPrivateKey(String privateKeySerialized) 
	        {

	            StringReader reader = new StringReader(privateKeySerialized);

	            PrivateKey pKey = null;

	            try {

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
}
