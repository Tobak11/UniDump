package cleanssl;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.security.KeyStore;
import java.util.Scanner;
 
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
 
public class HTTPSClient {
    private String host = "127.0.0.1";
    private int port = 6666;
    
    private SSLContext sslContext;
    
    private SSLSocket sslSocket;
     
    public static void main(String[] args) throws IOException{
        HTTPSClient client = new HTTPSClient();
        client.start();
    }
     
    // Create the and initialize the SSLContext
    private SSLContext createSSLContext(){
        try{
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(new FileInputStream("mykeystore.keystore"),"123456".toCharArray());
             
            // Create trust manager
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
            trustManagerFactory.init(keyStore);
            TrustManager[] tm = trustManagerFactory.getTrustManagers();
             
            // Initialize SSLContext
            SSLContext sslContext = SSLContext.getInstance("TLSv1");
            sslContext.init(null,  tm, null);
             
            return sslContext;
        } catch (Exception ex){
            ex.printStackTrace();
        }
         
        return null;
    }
     
    // Start to run the server
    public void start() throws IOException{
        sslContext = createSSLContext();
        
        SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
        
        sslSocket = (SSLSocket) sslSocketFactory.createSocket(host, port);
        sslSocket.setEnabledCipherSuites(sslSocket.getSupportedCipherSuites());
        
        sslSocket.startHandshake();
        
        InputStream inputStream = sslSocket.getInputStream();
        OutputStream outputStream = sslSocket.getOutputStream();

        Scanner scan = new Scanner(System.in);

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(outputStream));

        String line = null;

        while(!(line = scan.nextLine()).equals("exit")){
            printWriter.println(line);
            printWriter.flush();

            System.out.println(bufferedReader.readLine());
        }

        sslSocket.close();
    }
}