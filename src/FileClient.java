/**
 * Created with IntelliJ IDEA.
 * User: jalatif
 * Date: 4/11/13
 * Time: 9:37 PM
 * To change this template use File | Settings | File Templates.
 */

import java.net.*;
import java.io.*;

public class FileClient{
    public static void main (String [] args ) throws IOException {
        int filesize=6022386; // filesize temporary hardcoded

        long start = System.currentTimeMillis();
        int bytesRead;
        int current = 0;
        // localhost for testing
        Socket sock = new Socket("127.0.0.1",13267);
        System.out.println("Connecting...");

        // receive file
        byte [] mybytearray  = new byte [filesize];
        InputStream is = sock.getInputStream();
        FileOutputStream fos = new FileOutputStream("/home/jalatif/source-copy.txt");
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        bytesRead = is.read(mybytearray,0,mybytearray.length);
        current = bytesRead;
        System.out.println(bytesRead);
        System.out.println(current);

        do {
            System.out.println(bytesRead);
            System.out.println(current);
            bytesRead =
                    is.read(mybytearray, current, (mybytearray.length-current));

            if(bytesRead >= 0) current += bytesRead;
        } while(bytesRead > -1);
        System.out.println(bytesRead);
        System.out.println(current);
        bos.write(mybytearray, 0 , current);
        bos.flush();
        long end = System.currentTimeMillis();
        System.out.println(end-start);
        bos.close();
        sock.close();
    }
}
