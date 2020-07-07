package iciciBankWCOnline;

import java.io.BufferedReader;

import java.io.File;

import java.io.IOException;

import java.io.InputStreamReader;

import java.io.OutputStream;

import java.io.StringReader;

import java.io.UnsupportedEncodingException;

import java.net.HttpURLConnection;

import java.net.URL;

import java.security.InvalidKeyException;

import java.security.KeyPair;

import java.security.NoSuchAlgorithmException;

import java.security.PrivateKey;

import java.security.PublicKey;

import java.security.Security;

import java.security.Signature;

import java.security.SignatureException;

import java.time.LocalDateTime;

import java.time.format.DateTimeFormatter;

import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.digest.DigestUtils;

import static java.lang.Integer.toHexString;

import org.bouncycastle.openssl.PEMReader;

import org.bouncycastle.util.encoders.Hex;

import okhttp3.MediaType;

import okhttp3.MultipartBody;

import okhttp3.OkHttpClient;

import okhttp3.Request;

import okhttp3.RequestBody;

import okhttp3.Response;

import okhttp3.ResponseBody;

import okio.Buffer;

import okio.BufferedSink;

import okio.BufferedSource;

import okio.Okio;

//Multibanking 

public class Statement_Upload 
{

 //Update the Organization name as Vendor (Provided by Perfios)

  static String vendor="iciciBankWCOnline"; //Update vendor ID here

  static String FilePath="C:\\Users\\Kalyan Chakravarthy\\Downloads\\BSA Statement\\BSA\\multiBankingEPDF JAN20 to MAR20\\"; // Please provide Folder path (/home/Sample_Statement(v3.0))

  static String Password="";//If the file is password protected then Password of the PDF file to be updated here

  static String txnID="clientTransactionID";// Transaction ID to be passed by client for mapping/reference

 
  //The Host and server

  static String Server="https://demo48.perfios.com"; 

  static String Host="demo.perfios.com"; 

  

  static String ReportFormat="xlsx,xml";

  

  static String DownloadReportAtLocation="C:\\Users\\Kalyan Chakravarthy\\Desktop\\Reports\\";//Please make sure if the location path exists

  

  //Initiate Transaction URL 

  private static final String Initiate_Transaction_URL = Server+"/KuberaVault/api/v3/organisations/"+vendor+"/transactions";

  //The padding and algorithm used for Signature generation

  static final String ENCRYPTION_ALGO = "SHA256withRSA/PSS";

  //update the payload request for Initiate Transaction API

  static String InitiateTarnsactionPayload=

              "<payload>"

            + "<txnId>"+txnID+"</txnId>"

      + "<loanAmount>20000000</loanAmount>"

      + "<processingType>STATEMENT</processingType>"

      + "<loanType>SME Loan</loanType>"

      + "<yearMonthFrom>2020-01</yearMonthFrom>"
//
     + "<yearMonthTo>2020-03</yearMonthTo>"

      + "<acceptancePolicy>atLeastOneTransactionInRange</acceptancePolicy>"

      + "<uploadingScannedStatements>false</uploadingScannedStatements>"

      + "<transactionCompleteCallbackUrl>https://www.example.com</transactionCompleteCallbackUrl>"

      + "<loanDuration>6</loanDuration>"
      +"<sourceType>GSTOD</sourceType>"

            + "</payload>";

  

  //update the 4096 bit Private key for Signature Generation

  // To create a Private key in Linux use the command: openssl genrsa -out ./private_key 4096

  static String privateKey ="-----BEGIN RSA PRIVATE KEY-----\n" + 
  		"MIIEowIBAAKCAQEAg0yNCwQVeV7ZSC3AhmhyaYg47jsySY/O8czbUj8qTh5g40ad\n" + 
  		"+LhRRW+53etrpEqbPGaeXL+W4I1mAUUxOsPpBoQcZvJ9hSg9JPwa1QvQpCQkGSTL\n" + 
  		"VxMfBzDHjP/LNY/vfM3V/7fyfIZv5Ll4cQYDb25I/lg9OPY2Jo38pUc0O0XSF+8b\n" + 
  		"sSnp29cDI6i6KYgG9K3P02YtTTNP518shqLqxOkhWLZfSYnGM6i9FPEEWO14Y3q7\n" + 
  		"mbECM1EjSJa0Kzsr7I/u2YYd69JyrjqDsd/qWbHgQTH2qisF3Wg7pUuppZN82FfG\n" + 
  		"6wm1fSK4pNMkUdyaR2c687G50+wOm6FX6ujRjQIDAQABAoIBABvUZvTVycNvAjkV\n" + 
  		"UYBrtQx/TinYrEEBVUjVNguJzeq+76RcvHESiE9ibhAGrUsmcR7tVVWtWkqATgWo\n" + 
  		"59Vf3w19hROhBE/sBoHHWdycY33XFzupG9x59hKtGS7erPDOcGGQs3I/SAty51ag\n" + 
  		"pVG3h5Tf2GLoyxk+vp2PbOSPYhfJ1u9oTmopFjRaeyGJkftBmDJ/Zxxj/rqLKHJv\n" + 
  		"CQz5RQsW4T199jepkl1r7e3f5eMnD4zU5m4zj5DbuvnjwJDrGzTZlvod7VYd13c4\n" + 
  		"QDO11UEL0Fbo6SEpuB3T26SrsDgGo6bRpLyv5+xv9G/P04ggqAVUawbKZTWc8STb\n" + 
  		"3mhRfjUCgYEA2aRRjo5UvStG49lgyV5NGPop4A3iktj3U5W1A8RBjPW98VUXVZvB\n" + 
  		"eZaVOwbMTCod9KzDjwmpJAfBtUJZgMJa+BISCmB4mJHO+gL/oxdqStx+h3ufJP1X\n" + 
  		"7VRgIh6B0iDRJ81LFCfWXCgmC4PQ5EcR7ID9G2ouWNku26Ns5htfb0cCgYEAmnCR\n" + 
  		"9m6RRlEM3Ey4HAzmt95mWLYRhj7m/YZAsss4b1Qsc/4bcLR54e1UdujA93MJVRb/\n" + 
  		"8jbCheeuGVlliLAJdpYficUOfUKpFXaerp/ENKz3whAYuZF/M3NEaOjmPkj6Nyk2\n" + 
  		"d4/TmRc27uzsS26n9JlF+hqGWI6hbkdEEU/caosCgYEAsh6tMMKDz54PkoJhl+w5\n" + 
  		"pOt4QgqbGBvBrwKi9sYz761fGpfNVR74JQ58a6aQpPUDNy/9jJ8Xcol2m2YF4j8u\n" + 
  		"PEBty1hQ2mzau/qgRVXwg/wZAcDG6nr87xtS4hmdnd+FzdFSVNiVsy6YlGv5Fhqk\n" + 
  		"LK8Uds9bxxNYKiGCCoD0hhkCgYAf5nqv3VzSmnE+m/Y3jQ6nEiAv2MO76AhMe8un\n" + 
  		"YsSwckGQX/+JAy5d1Wjp+t/I/REDa3HItTju8dELWWTYnAwIW6/+BB8fDoLgdRUH\n" + 
  		"2uZrPwMaKbsBsaUFmpyZ0umkGezdMf0XKzU43QuyxGt2lU/J/VLv+0SezC9aGQxd\n" + 
  		"SxKrSwKBgH5G7pe+D9ILHJmoqHz7RwgZVOnMXaq9Qd1mdiZ5UirkLsrZYEonyjFO\n" + 
  		"NVMKOrvvO18LPypLWQM2X4JgzO8bBx/RDebwFVO8M9JeS8nhcpBQ1ryLVd5t19zo\n" + 
  		"9hDqH67ovSANhMpUr+dvbrDA1X5vfil4sD7hxpwD5+wbvM1kdvJ7\n" + 
  		"-----END RSA PRIVATE KEY-----\n";


  //--------Main method-----------------

  

  public static void main(String[] args) throws IOException, InvalidKeyException, SignatureException, NoSuchAlgorithmException {

    Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

    

   

    APICalls( FilePath);   

                  

         }



  

  

  

  //-------------------All API Calls------------------------------

  

  public static void APICalls(String FilePath) throws InvalidKeyException, NoSuchAlgorithmException, SignatureException, IOException

  {

   String xPerfiosDate=Date(); // This method returns the current date and time in YYYYMMDD’T’HHMMSS’Z’ format



   //Initiate transaction call made here----

   String signature= SignatureCreator("/KuberaVault/api/v3/organisations/"+vendor+"/transactions",InitiateTarnsactionPayload,xPerfiosDate,"");

   String PerfiosTransactionId=Make_Request(Initiate_Transaction_URL,signature,InitiateTarnsactionPayload,xPerfiosDate);



   if (PerfiosTransactionId.equals("ERROR"))

   {

    System.out.println("Error initiating Transaction"); 



   }

   else

   {
	   File folder = new File(FilePath);
	   File[] listOfFiles = folder.listFiles();
	   
	   for (int i = 0; i < listOfFiles.length; i++) 
	   {
			  if (listOfFiles[i].isFile()) 
			  {

    //Upload statement call made here if the initiate call gives a success response

    String fileId=UploadStatement(FilePath+"/"+listOfFiles[i].getName(),PerfiosTransactionId,xPerfiosDate);

    

    if(fileId.length()==40) {





     //Process Statement call made here 

     String ProcessStatement_Payload="";

     

     if (Password.equals("")) {

      //Payload Without password

      ProcessStatement_Payload="<payload><fileId>"+fileId+"</fileId></payload>";

      

     }

     else 

     {

      //Payload with password

      

      ProcessStatement_Payload="<payload><fileId>"+fileId+"</fileId><password>"+Password+"</password></payload>";

     }



     String ProcessStatement_Signature=SignatureCreator("/KuberaVault/api/v3/organisations/"+vendor+"/transactions/"+PerfiosTransactionId+"/bank-statements",ProcessStatement_Payload,xPerfiosDate,"");

     String ProcessStatement_URL=Server+"/KuberaVault/api/v3/organisations/"+vendor+"/transactions/"+PerfiosTransactionId+"/bank-statements";

     String ProcessResponse=Make_Request(ProcessStatement_URL,ProcessStatement_Signature,ProcessStatement_Payload,xPerfiosDate);

     
    }
			  }
			  }
     //If re-process is required:

     

    // String reProcessStatement_Payload="<payload><password>"+Password+"</password></payload>";

    // String reProcessStatement_URL=Server+"/KuberaVault/api/v3/organisations/"+vendor+"/transactions/"+PerfiosTransactionId+"/bank-statements/"+fileId;

    // String reProcessStatement_Signature=SignatureCreator("/KuberaVault/api/v3/organisations/"+vendor+"/transactions/"+PerfiosTransactionId+"/bank-statements/"+fileId,reProcessStatement_Payload,xPerfiosDate,"");

    // String reProcessResponse=Make_Request(reProcessStatement_URL,reProcessStatement_Signature,reProcessStatement_Payload,xPerfiosDate);     

     


      //ReportGeneration API call made here if Process Statement API gives a success response

      String ReportGeneration_Payload="";

      String reportGeneration_URL=Server+"/KuberaVault/api/v3/organisations/"+vendor+"/transactions/"+PerfiosTransactionId+"/reports";

      String reportGeneration_Signature=SignatureCreator("/KuberaVault/api/v3/organisations/"+vendor+"/transactions/"+PerfiosTransactionId+"/reports",ReportGeneration_Payload,xPerfiosDate,PerfiosTransactionId);

      String ReportGenerationResponse=Make_Request(reportGeneration_URL,reportGeneration_Signature,ReportGeneration_Payload,xPerfiosDate);

      if(ReportGenerationResponse.equals("Done"))

      {

       //RetrieveReport call done here if the Processing of the reports are generated successfully

       //Use transaction Complete callback Mechanism in order to receive the status of the transaction



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

      else 

      {

       System.out.println("Error Generating Report");

      }

    
			  

   }
   }                  



  

  

  

  

  //----------Request creator-------------------------------

  

  

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

    System.out.println("sha256Payload= "+sha256Payload+"\n");

    

    

    //RequestDate in ‘YYYYMMDD’T’HHMMSS’Z’‘ format

    String xPerfiosDate=Date;

    System.out.println("Encoded Quaery Parameter:"+uriEncode(QueryParam) + "\n");

    

    //Creating a Conanical Request

    String CanonicalRequest=Method+"\n"

      + uriEncode(URL)+"\n"

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

     + SHA256(CanonicalRequest);

   System.out.println("StringToSign:"+"\n"+StringToSign+"\n");

   

   

   //Create a checksum using String to sign

   String Checksum=SHA256(StringToSign);

   

   

   //Encryption the String to sign using RSA private key

   String Signature=encrypt(Checksum, buildPrivateKey(privateKey),buildPublicKey(privateKey));

   System.out.println("Signature= "+Signature+"\n");

   return Signature;

   

  }

  

  

  

  

  

  

  //---------------------------------Initiate_Transaction_URL method----------------------



  

  public static String Make_Request(String URL, String Signature, String payload,String Date) throws IOException, NoSuchAlgorithmException {

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

   con.addRequestProperty("content-type", "application/xml");

   con.addRequestProperty("Accept", "application/xml");

   con.setRequestProperty("Host", Host);

   con.setRequestProperty("X-Perfios-Algorithm", "PERFIOS-RSA-SHA256");

   con.setRequestProperty("X-Perfios-Content-Sha256", SHA256(payload));

   con.setRequestProperty("X-Perfios-Date", Date);

   con.setRequestProperty("X-Perfios-Signature",Signature);

   con.setRequestProperty("X-Perfios-Signed-Headers", "host;x-perfios-content-sha256;x-perfios-date");

   con.setDoOutput(true);



   OutputStream os = con.getOutputStream();

   os.write(payload.getBytes());

   os.flush();

   os.close();

   

   

   int responseCode = con.getResponseCode();

   String responseMessage = con.getResponseMessage();

   System.out.println("Response Code :: " + responseCode);

   

   if (responseCode == HttpURLConnection.HTTP_OK) { // success

    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

    

    String inputLine;

    

    StringBuffer response = new StringBuffer();



    while ((inputLine = in.readLine()) != null) {

     response.append(inputLine);

    }

    in.close();



    // print result

    System.out.println(response.toString());

    if(URL.equals(Initiate_Transaction_URL))

    {

     return response.substring(90,107);

    }

    else 

    {

     return "Done";

    }

    

   } 

   else {

    System.out.println("Request failed");

             BufferedReader in = new BufferedReader(new InputStreamReader(con.getErrorStream()));

    

    String inputLine;

    

    StringBuffer response = new StringBuffer();



    while ((inputLine = in.readLine()) != null) {

     response.append(inputLine);

     

    }

    in.close();



    // print result

    System.out.println(response.toString());

    System.out.println("Request failed:"+responseMessage);

    return "ERROR";

   

   }



  }

  

  //-------------------Upload STatement---------

  

  public static String UploadStatement(String FilePath,String perfiosTransactionId,String xPerfiosDate) throws IOException, InvalidKeyException, NoSuchAlgorithmException, SignatureException 

  {

   File file= new File(FilePath);

   OkHttpClient client = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).writeTimeout(180, TimeUnit.SECONDS).readTimeout(180, TimeUnit.SECONDS).build();

      RequestBody body = new MultipartBody.Builder()

        .setType(MultipartBody.FORM)

              .addFormDataPart("file", file.getName(),RequestBody.create(MediaType.parse("application/pdf"),file))

             //.addFormDataPart("organisationName", vendor)

             //.addFormDataPart("perfiosTransactionId", perfiosTransactionId) 

              .build();

      Request request = new Request.Builder()

                  .url(Server+"/KuberaVault/api/v3/organisations/"+vendor+"/transactions/"+perfiosTransactionId+"/files")

                  .addHeader("content-type", "multipart/form-data")

                 .addHeader("Host", Host)

                 .addHeader("X-Perfios-Algorithm", "PERFIOS-RSA-SHA256")

                 .addHeader("X-Perfios-Content-Sha256", SHA256(""))

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

      System.out.println(result.substring(69,109)+"\n");

      return Response;

  }



  //--------------RetrieveReport---------

  

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

 Response response = client.newCall(request).execute();

 ResponseBody body = response.body();

 File file= new File(filePath);

    BufferedSource source = body.source();



    BufferedSink sink = Okio.buffer(Okio.sink(file));

    Buffer sinkBuffer = sink.buffer();

    long totalBytesRead = 0;

    int bufferSize = 8 * 1024;

    for (long bytesRead; (bytesRead = source.read(sinkBuffer, bufferSize)) != -1; ) {

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

  



      public static String SHA256(String a) throws NoSuchAlgorithmException {

          String payloadHash = a;

          String result = DigestUtils.sha256Hex(payloadHash);

         // System.out.println(result);

          return result;



      }

      

    //  ---------------------------encodeURIComponent Method------------------------------

      private static String uriEncode(final CharSequence input) {



          final StringBuilder result = new StringBuilder();

          for (int i = 0; i < input.length(); i++) {

              final char ch = input.charAt(i);

              if ((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z') || (ch >= '0' && ch <= '9') || ch == '_'

                      || ch == '-' || ch == '~' || ch == '.' || ch == '/') {

                  result.append(ch);



              } else {

               result.append(toHexString(ch));

               

              }

          }

          return result.toString();

      }

  













  //  -----------------------------buildPrivateKey Method--------------------------------

      private static PrivateKey buildPrivateKey(String privateKeySerialized) {

          StringReader reader = new StringReader(privateKeySerialized);

          PrivateKey pKey = null;

          try {

              PEMReader pemReader = new PEMReader(reader);

              KeyPair keyPair = (KeyPair) pemReader.readObject();

              pKey = keyPair.getPrivate();

              pemReader.close();

          }

          catch (IOException i) {

              i.printStackTrace();

          }

          return pKey;

      }

      

      

      //-----------------------RSA Encryption-----------------------------------------------

      

      public static String encrypt(String raw, PrivateKey  k, PublicKey k2) throws InvalidKeyException, SignatureException, UnsupportedEncodingException, NoSuchAlgorithmException {

         

           Signature privateSignature = Signature.getInstance(ENCRYPTION_ALGO);

              privateSignature.initSign(k);

              privateSignature.update(raw.getBytes("UTF-8"));



              byte[] signature = privateSignature.sign();

              byte[] encoded = Hex.encode(signature);

              String str = new String(encoded);

              //Verify Signature using PublicKey

              privateSignature.initVerify(k2);

              privateSignature.update(raw.getBytes("UTF-8"));

              if(privateSignature.verify(signature)){

                  System.out.println("Verified");

              }else{

                  System.out.println("Something is wrong");

              }



              return str;

       

          

      }

      //------------------------------Create 'YYYYMMDD’T’HHMMSS’Z’‘ format Date----------------------------------------

      

      public static String Date() {    

       //‘YYYYMMDD’T’HHMMSS’Z’

       DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss" );  

       LocalDateTime now = LocalDateTime.now();  

       String time=dtf.format(now);

       String Date=time.subSequence(0,8)+"T"+time.subSequence(8,14)+"Z";

       System.out.println("Date:"+Date+"\n");  

       return Date;

      } 

      

      //-------------------------------buildPublicKey--------------------------

      private static PublicKey buildPublicKey(String privateKeySerialized) {

    StringReader reader = new StringReader(privateKeySerialized);

    PublicKey pKey = null;

    try {

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
}





