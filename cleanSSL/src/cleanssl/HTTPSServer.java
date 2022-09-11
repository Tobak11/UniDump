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
 
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
 
public class HTTPSServer {
    private int port = 6666;
    private boolean isServerDone = false;
    
    private SSLContext sslContext;
    
    private SSLSocket sslSocket;
     
    public static void main(String[] args) throws IOException{
        HTTPSServer server = new HTTPSServer();
        server.start();
    }
     
    // Create the and initialize the SSLContext
    private void createSSLContext(){
        try{
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(new FileInputStream("mykeystore.keystore"),"123456".toCharArray());
             
            // Create key manager
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            keyManagerFactory.init(keyStore, "123456".toCharArray());
            KeyManager[] km = keyManagerFactory.getKeyManagers();
             
            
            // Initialize SSLContext
            this.sslContext = SSLContext.getInstance("TLSv1");
            this.sslContext.init(km,  null, null);
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }
     
    // Start to run the server
    public void start() throws IOException{   
        createSSLContext();
        
        SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();
        SSLServerSocket sslServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(port); 
        System.out.println("SSL server started");
        
        sslSocket = (SSLSocket) sslServerSocket.accept();
        System.out.println("Client accepted");
        sslSocket.setEnabledCipherSuites(sslSocket.getSupportedCipherSuites());
        sslSocket.startHandshake();
                 
        // Start handling application content
        InputStream inputStream = sslSocket.getInputStream();
        OutputStream outputStream = sslSocket.getOutputStream();

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(outputStream));

        String line = null;
        while((line = bufferedReader.readLine()) != null){
            printWriter.println("Echoback: " + line);
            printWriter.flush();
            System.out.println(line);
        }

        sslSocket.close();
    }
}