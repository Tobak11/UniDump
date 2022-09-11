package koliappdbservice;
//
import com.mysql.jdbc.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

public class KoliappDBService {
    private static SimpleDateFormat sdf;
    
    private static DataInputStream in;
    private static DataOutputStream out;
    
    //For DEFAULT Server
    private static Socket clientSocket; 
    private static ServerSocket serverSocket;
    
    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {   
        ServerSocket serverSocket = new ServerSocket(50000);
        System.out.println("Waitin");
        
        clientSocket = serverSocket.accept();
        System.out.println("Client connected");
        out = new DataOutputStream(clientSocket.getOutputStream()); 
        in = new DataInputStream(clientSocket.getInputStream());
        
        String msg = in.readUTF();
        
        System.out.println(msg);
//        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//        String postingDate = sdf.format(new Timestamp(System.currentTimeMillis()));
//        
//        Class.forName("com.mysql.jdbc.Driver");
//        Connection conn = DriverManager.getConnection("jdbc:mysql://remotemysql.com:3306;databaseName=yb56Tcn8g9;user=yb56Tcn8g9;password=5q3WgyIyMq");
//        Statement sm = (Statement) conn.createStatement();
//        sm.executeUpdate("insert into NEWS (id, author, title, message, postingDate, dorm) values (" + 
//                "NULL" +
//                "'TesztSzerzo'" + 
//                "'TesztCim'" + 
//                "'Ez egy rövid tesztüzenet!'" + 
//                "'"+postingDate + "'" +
//                "'Központi Kollegium'" +
//                ");");
//        conn.close();
    }
    
}
