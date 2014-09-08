/**
 * Created with IntelliJ IDEA.
 * User: jalatif
 * Date: 4/11/13
 * Time: 9:36 PM
 * To change this template use File | Settings | File Templates.
 */

import java.net.*;
import java.io.*;

public class FileServer {
    public static void main (String [] args ) throws IOException {
        // create socket
        ServerSocket servsock = new ServerSocket(13267);
        while (true) {
            System.out.println("Waiting...");

            Socket sock = servsock.accept();
            System.out.println("Accepted connection : " + sock);

            // sendfile
            File myFile = new File ("/home/jalatif/Documents/Chroot_Readme.txt");
            byte [] mybytearray  = new byte [(int)myFile.length()];
            FileInputStream fis = new FileInputStream(myFile);
            BufferedInputStream bis = new BufferedInputStream(fis);
            bis.read(mybytearray,0,mybytearray.length);
            OutputStream os = sock.getOutputStream();
            //InputStream
            System.out.println("Sending...");
            os.write(mybytearray,0,mybytearray.length);
            os.flush();
            sock.close();
        }
    }
}

