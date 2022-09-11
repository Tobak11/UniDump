/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package koliappdbservice;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author tobak11
 */
public class Client {
    private static DataOutputStream out;
    private static DataInputStream in;
    
    private static Socket socket;
    
    public static void main(String[] args) throws IOException {
        socket = new Socket("10.1.9.130 ", 50000); 
        out = new DataOutputStream(socket.getOutputStream()); 
        in = new DataInputStream(socket.getInputStream());

        out.writeUTF("YOSSUP");
        System.out.println(in.readUTF());
    }
}
