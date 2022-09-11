package filetest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileTest {

    public static void main(String[] args) {
        readEDFHeaderNIOMapped();
    }
    
    public static void createBinaryFile(){
        try(FileOutputStream out = new FileOutputStream("binary.dat")) {
            for(int i=0;i<5;i++){
                for(int j=0;j<30;j++){
                    out.write(i);
                }
            }
        } catch(IOException e){
            System.out.println("Ohoh");
        }
    }
    
    public static void validateBinaryFile(){
        try(FileInputStream in = new FileInputStream("binary.dat")) {
            int count = 0;
            int value = 0;
            
            while((value = in.read()) != -1){
                if(count != 0 && count % 30 == 0) {
                    System.out.println("");
                }
                System.out.print(value + " ");
                count++;
            }
            System.out.println("");
        } catch(IOException e){
            System.out.println("Ohoh");
        }        
    }
    
    public static void validateBinaryFileNIO(){
        try(FileChannel in = new FileInputStream("binary.dat").getChannel()) {
            int count = 0;
            int value = 0;
            
            ByteBuffer dst = ByteBuffer.allocate(150);
            in.read(dst);
            
//            System.out.println("Buffer attributes at start");
//            System.out.println("capacity: " + dst.capacity() + ", limit: " + dst.limit() + ", position: " + dst.position());
            
            dst.flip();
            
//            System.out.println("Buffer attributes after flip");
//            System.out.println("capacity: " + dst.capacity() + ", limit: " + dst.limit() + ", position: " + dst.position());
            
            for (int i = 0; i < dst.limit(); i++) {
                if(count != 0 && count % 30 == 0) {
                    System.out.println("");
                }
                System.out.print(dst.get() + " ");
                count++;
            }
            
            System.out.println("");
        } catch(IOException e){
            System.out.println("Ohoh");
        }        
    }
    
    public static void randomAccessRead(){
        try(RandomAccessFile in = new RandomAccessFile("binary.dat", "r")){
            int count = 0;
            int value = 0;
            
//            while((value = in.read()) != -1){
//                if(count != 0 && count % 30 == 0) {
//                    System.out.println("");
//                }
//                System.out.print(value + " ");
//                count++;
//            }
//            System.out.println("");
            
            in.seek(4*30);
            for (int i = 0; i < 30; i++) {
                System.out.print(in.read() + " ");
            }
            System.out.println("");
            
            in.seek(2*30);
            for (int i = 0; i < 30; i++) {
                System.out.print(in.read() + " ");
            }
            System.out.println("");
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch(IOException ex2){
            Logger.getLogger(FileTest.class.getName()).log(Level.SEVERE, null, ex2);
        }
    }
    
    public static void readEDFHeaderSeq(){
        char[] header = new char[256];
        
        try(FileInputStream in = new FileInputStream("test_generator.edf")) {
            int count = 0;
            
            while(count < 255){
                header[count] = (char)in.read();
                count++;
            }
            
            String id = new String(header,0,8);
            String subjectInfo = new String(header,8,80);
            String recordingInfo = new String(header,88,80);
            
            System.out.println(id.replaceAll("\\s+","") + " " + subjectInfo.replaceAll("\\s+","") + " " + recordingInfo.replaceAll("\\s+",""));
        } catch(IOException e){
            System.out.println("Ohoh");
        }     
    }

    public static void readEDFHeaderNIO(){       
        try(FileChannel in = new FileInputStream("test_generator.edf").getChannel()) {
            
            ByteBuffer buffer = ByteBuffer.allocate(256);
            in.read(buffer);
            
            String id = new String(buffer.array(),0,8);
            String subjectInfo = new String(buffer.array(),8,80);
            String recordingInfo = new String(buffer.array(),88,80);
            
            System.out.println(id.replaceAll("\\s+","") + " " + subjectInfo.replaceAll("\\s+","") + " " + recordingInfo.replaceAll("\\s+",""));
            
        } catch(IOException e){
            System.out.println("Ohoh");
        }     
    }
    
    public static void readEDFHeaderNIOMapped(){       
        try(FileChannel in = new FileInputStream("test_generator.edf").getChannel()) {
            
            MappedByteBuffer map = in.map(FileChannel.MapMode.READ_ONLY, 0, 256);
            Charset cs = Charset.forName("ASCII");
            CharBuffer chBuffer = cs.decode(map);
            
            String id = new String(chBuffer.subSequence(0,8).toString());
            String subjectInfo = new String(chBuffer.subSequence(8,88).toString());
            String recordingInfo = new String(chBuffer.subSequence(88,168).toString());
            
            System.out.println(id.replaceAll("\\s+","") + " " + subjectInfo.replaceAll("\\s+","") + " " + recordingInfo.replaceAll("\\s+",""));
            
        } catch(IOException e){
            System.out.println("Ohoh");
        }     
    }
}
