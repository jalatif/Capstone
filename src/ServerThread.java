/**
 * Created with IntelliJ IDEA.
 * User: jalatif
 * Date: 8/4/13
 * Time: 9:30 PM
 * To change this template use File | Settings | File Templates.
 */

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.TimerTask;

public class ServerThread extends Thread
{

    private Server server;

    private Socket socket;

    public String userName = "";

    DataInputStream din;
    DataOutputStream dout;
    ObjectInputStream inStream;
    DBConnect dbc;

    private int timeout    = 2000;
    private int maxTimeout = 8000;
    java.util.Timer timer;
    private long lastReadTime;
    private boolean downloading = false;
    private boolean uploading = false;

    public ServerThread( Server server, Socket socket, DBConnect dbc ) {

        this.server = server;
        this.socket = socket;
        this.dbc = dbc;
        try {
            din = new DataInputStream( socket.getInputStream() );
            dout = new DataOutputStream( socket.getOutputStream() );
            //inStream = new ObjectInputStream( socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        //dbc = new DBConnect();
        start();

        lastReadTime = System.currentTimeMillis();

        timer = new java.util.Timer();
        timer.schedule(new clientStatusCheck(), 0, timeout);

    }
    private void endServerThread(){
        System.out.println("Connection TimedOut");
        timer.cancel();
        server.removeConnection(socket);
        socket = null;
        dout = null;
        din = null;
    }

    private class clientStatusCheck extends TimerTask {
        public void run(){
            if (downloading || uploading)
                return;
            if (!isConnectionAlive())
                endServerThread();
            try {
                //textField1.setText(toUser);
                dout.writeUTF("Heart~*~@");
            } catch (IOException e) {
                //e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                System.out.println("Ending in statusCheck by exception catch");
                server.removeConnection(socket);
                socket = null;
            }
            catch (NullPointerException npe){
            }
        }
    }

    private boolean isConnectionAlive(){
        return System.currentTimeMillis() - lastReadTime < maxTimeout;
    }

    private void updateConnectionAliveStatus(){
        lastReadTime = System.currentTimeMillis();
    }

    public String execute(String cmd)
    {
        //Build command
        List<String> commands = new ArrayList<String>();
        commands.add("zsh");
        //Add arguments
        commands.add("-c");
        commands.add(cmd);
        System.out.println(commands);

        //Run macro on target
        ProcessBuilder pb = new ProcessBuilder(commands);
        pb.directory(new File("/home/jalatif/programs/practice/java_practice/"));
        pb.redirectErrorStream(true);
        Process process = null;
        try {
            process = pb.start();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        //Read output
        StringBuilder out = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = null, previous = null, result = "";
        try {
            while ((line = br.readLine()) != null)
                if (!line.equals(previous)) {
                    previous = line;
                    out.append(line).append('\n');
                    System.out.println(line);
                    result += line + "\n";
                }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        result += "#####End of Command#####";

        //Check result
        try {
            if (process.waitFor() == 0)
                System.out.println("Success!");
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return result;
        //System.exit(0);

        //Abnormal termination: Log command parameters and output and throw ExecutionException
        //System.err.println(commands);
        //System.err.println(out.toString());


        /*Runtime run = Runtime.getRuntime();
        String result = "";
        try{
            System.out.println( "Server Executing command = " + cmd);
            Process pr = run.exec(new String[]{"zsh", "-c", cmd});
            //Process pr = run.exec("/bin/zsh -c \"" + cmd + "\"") ;
            //pr.waitFor() ;
            BufferedReader buf = new BufferedReader( new InputStreamReader( pr.getInputStream() ) ) ;
            String line;
            while ((line = buf.readLine())!=null)
            {
                //System.out.println(line);
                result = line + "\n";
            }
        }
        catch(Exception e){
            System.out.println("Invalid Command or its Parameters");
        }
        return result;
        */
    }

    private String serverExecute(String command){
        return execute(command);
    }

    public void transferMessage(String message){
        try{
        //String arr[] = message.split("@");
            int loc = message.indexOf("SendTo@*@~");
            System.out.println(message);
            String to = message.substring(0, loc);
            message = message.substring(loc + 10, message.length());
            if (message.endsWith("\n"))
                message = message.substring(0, message.length() - 1);
            System.out.println( "Sending "+message + " to " + to);
            if (to.equals("Server")){
                String result = serverExecute(message);
                String from = to;
                server.sendTo( userName, from + "MsgRx~*~@" + from + " said : " + result + "\n" );
            }
            else if (to.equals("")){
                server.sendToAll( String.valueOf(socket.getPort()) + " said : " + message + "\n" );
            }
            else{
                //server.sendTo( to, "MsgRx~*~@" + String.valueOf(socket.getPort()) + " said : " + message + "\n" );
                String from = (String)Server.nameMap.get(socket.getPort());
                server.sendTo( to, from + "MsgRx~*~@" + from + " said : " + message + "\n" );
            }
            //server.sendTo(String.valueOf(socket.getPort()), "MsgRx~*~@I said : " + message + "\n");
            //dout.writeUTF("MsgRx~*~@I said : " + message + "\n");
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public String checkAll(){
        Enumeration<Socket> e = server.getSockets();
        Socket s1;
        String s = "Server&";
        String unm = "";

        while (e.hasMoreElements()){
            s1 = e.nextElement();
            try{
                unm = (String)Server.nameMap.get(s1.getPort());
                if (!unm.equals(userName))
                    s += unm + "&";
            }
            catch (NullPointerException npe){
                //System.out.println("NPE : "+userName);
                //System.out.println("NPE : "+unm);
                unm = "";
            }

        }
        //System.out.println(s);
        return s;
    }

    public String checkSingle(String message){
        String user = message.substring(9, message.length());
        if (Server.portMap.containsKey(user))
            return "true";
        else
            return "false";
    }

    private boolean authUser(String message){
        System.out.println(message);
        int loc = message.indexOf("AuthU@*@~");
        String username = message.substring(0, loc);
        String password = message.substring(loc + 9, message.length());
        if (!dbc.isConnected())
            return false;
        try {
            return dbc.authenticate(username, password);
        } catch (SQLException e) {
            System.out.println("Something not working");
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return false;
        }

    }

    private String createUser(String message){
        String res = "";
        System.out.println(message);
        int loc = message.indexOf("RegsU@*@~");
        String username = message.substring(0, loc);
        String password = message.substring(loc + 9, message.length());

        try {
            boolean added = dbc.addUser(username, password);
            if (!added)
                res = "Username exists or wrong username";
            else
                res = "User Added. Now try to login";
        } catch (SQLException e) {
            System.out.println("Something not working");
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            res = "Some problem occured while adding user";
        }
        return res;
    }

    protected String getUsers(){
        try {
            return dbc.getUsers(userName);
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return "";
    }

    private boolean download(int fileSize, String fileName){
        FileOutputStream outStream = null;
        try {
            outStream = new FileOutputStream(fileName);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
            System.out.println("Downloading Failed; File can't be made check permissions");
            return false;
        }
        byte[] buffer = new byte[1024];//[200000];
        int bytesRead = 0, counter = 0;

        BufferedOutputStream bos = new BufferedOutputStream(outStream);
            /*while (true){
                bytesRead = din.read(buffer, 0, buffer.length);
                lastReadTime = System.currentTimeMillis();
                if (fileSize - counter <= buffer.length){
                    //bos.write(buffer, 0, fileSize - counter);
                    System.out.println("Bytes Read = " + (fileSize - counter) + " Total = " + fileSize + " fileSize = " + fileSize + " BufferLength = " + buffer.length);
                    break;
                }
                else{
                    System.out.println("Bytes Read = " + buffer.length + " Total = " + (counter + bytesRead) + " fileSize = " + fileSize + " BufferLength = " + buffer.length);
                    //bos.write(buffer, 0, buffer.length);
                }
                counter += bytesRead;
            }
            bos.write(buffer, 0, fileSize);
            bos.flush();
            bos.close();*/

        int filesize=27000000;//6022386;
        int current = 0;
        long start = System.currentTimeMillis();
        byte [] mybytearray  = new byte [filesize];
        try {
            bytesRead = din.read(mybytearray,0,mybytearray.length);
        } catch (IOException e1) {
            e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            System.out.println("Downloading Failed; Reading stream error");
            return false;

        }
        current = bytesRead;
        System.out.println(bytesRead);
        System.out.println(current);
        int timeout = 0;
        try {
            timeout = socket.getSoTimeout();
            socket.setSoTimeout(1000);
        } catch (SocketException e1) {
            e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            System.out.println("Downloading Failed; Socket problem");
            return false;

        }
        do {
            System.out.println(bytesRead);
            System.out.println(current);
            try{
                lastReadTime = System.currentTimeMillis();
                bytesRead = din.read(mybytearray, current, (mybytearray.length-current));
            }
            catch (IOException ie){
                System.out.println("No more data to be read");
                break;
            }
            if(bytesRead >= 0) current += bytesRead;
        } while(bytesRead > -1);
        try {
            socket.setSoTimeout(timeout);
        } catch (SocketException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            System.out.println("Downloading Failed; Socket problem");
            return false;
        }
        System.out.println(bytesRead);
        System.out.println(current);
        try {
            bos.write(mybytearray, 0 , current);
            bos.flush();
            long end = System.currentTimeMillis();
            System.out.println(end-start);
            bos.close();
            outStream.close();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            System.out.println("Downloading Failed; Writing to File Problem or Stream Busy");
            return false;
        }
        File checkFile = new File(fileName);
        int newFileSize = (int) checkFile.length();
        if ( newFileSize != fileSize){
            System.out.println("File Downloading Failed. Difference in size = " + (fileSize - newFileSize));
            lastReadTime = System.currentTimeMillis();
            return false;
        }
        else{
            System.out.println("Downloaded Successfully!");
            lastReadTime = System.currentTimeMillis();
            return true;
        }

    }



    private void receiveFile(String message){
        int loc = message.indexOf("&");
        int loc2 = message.indexOf("&", loc + 1);
        int fileSize = Integer.parseInt(message.substring(9, loc));
        String withUser = message.substring(loc + 1, loc2);
        String fileName = message.substring(loc2 + 1, message.length());
        downloading = true;
        boolean success = download(fileSize, fileName);
        lastReadTime = System.currentTimeMillis();
        downloading = false;
        try {
            if (success)
                dout.writeUTF("RFile~*~@" + withUser + "&" + fileName);
            else
                dout.writeUTF("NFile~*~@" + withUser + "&" + fileName);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private boolean sendFile(String message) throws IOException{
        int loc = message.indexOf("&");
        String withUser = message.substring(9, loc);
        String fileName = message.substring(loc + 1, message.length());
        File file = new File(fileName);
        int fileSize = (int) file.length();
        // creates the file stream
        FileInputStream fileStream = new FileInputStream(file);
        BufferedInputStream bis = new BufferedInputStream(fileStream);

        // sending a message before streaming the file
        System.out.println("SENDING_FILE to |" + withUser + " | " + file.getName() + "|" + fileSize);

        dout.writeUTF("SFile~*~@" + fileSize + "&" + withUser + "&" + file.getName());
        uploading = true;

        byte [] mybytearray  = new byte [fileSize];
        bis.read(mybytearray,0,mybytearray.length);
        //InputStream
        dout.write(mybytearray,0,mybytearray.length);
        dout.flush();
        //dout.writeUTF("EFile~*~@" + withUser + "&" + file.getName());
        System.out.println("SEND_COMPLETE");
        bis.close();
        fileStream.close();
        lastReadTime = System.currentTimeMillis();
        return true;
    }

    private void informOtherUser(String message){
        int loc = message.indexOf("&");
        String to = message.substring(9, loc);
        String fileName = message.substring(loc + 1, message.length());
        if (to.equals("Server")){
            ;
        }
        else{
            server.sendTo( to, "EFile~*~@" + userName + "&" + fileName);
        }

    }

    public void run() {
        try{
        while (true) {
                String message = din.readUTF();
                if (uploading){
                    if (message.contains("RFile@*@~")){
                        lastReadTime = System.currentTimeMillis();
                        uploading = false;
                        System.out.println("File Uploaded");
                    }
                    else if (message.contains("NFile@*@~")){
                        lastReadTime = System.currentTimeMillis();
                        uploading = false;
                        System.out.println("Error in uploading file");
                    }
                    else
                        continue;
                }
                if (message.contains("Heart@*@~"))
                    updateConnectionAliveStatus();
                if (message.contains("SendTo@*@~"))
                    transferMessage(message);
                if (message.contains("StsAt@*@~"))
                    dout.writeUTF("StsAt~*~@" + checkSingle(message));
                if (message.contains("Status@*@~"))
                    dout.writeUTF("StsOf~*~@"+checkAll());
                //server.sendTo(String.valueOf(socket.getPort()), "StsOf~*~@"+checkAll());
                if (message.contains("AlUsr@*@~"))
                    dout.writeUTF("AlUsr~*~@" + getUsers());
                if (message.contains("RegsU@*@~")){
                    dout.writeUTF("RegsR~*~@"+createUser(message));
                }
                if (message.contains("SFile@*@~")){
                    receiveFile(message);
                }
                if (message.contains("EFile@*@~")){
                    informOtherUser(message);
                }
                if (message.contains("FFile@*@~")){
                    sendFile(message);
                }
                if (message.contains("AuthU@*@~")){
                    boolean b = authUser(message);
                    if (b){
                        int loc = message.indexOf("AuthU@*@~");
                        String username = message.substring(0, loc);
                        userName = username;
                        Server.nameMap.put(socket.getPort(), username);
                        Server.portMap.put(username, socket.getPort());
                    }
                    System.out.println("AuthR~*~@" + b);
                    dout.writeUTF("AuthR~*~@" + b);
                }

                if (message.contains("OfMsg@*@~")){
                    int loc = message.indexOf("OfMsg@*@~");
                    String to = message.substring(0, loc);
                    String msg = message.substring(loc + 9, message.length());
                    try {
                        dbc.storeMessage(to, userName, msg);
                    } catch (SQLException e) {
                        System.out.println("Message not stored");
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }

                if (message.equals("GofM@*@~")){
                    System.out.println("Getting Offline Messages");
                    try {
                        dout.writeUTF(dbc.getOfflineMessages(userName));
                    } catch (SQLException e) {
                        dout.writeUTF("");
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }

                }
            } catch( EOFException ie ) {

            } catch(SocketException se){
                System.out.println("Finally Block by exception catch");
                //se.printStackTrace();
                server.removeConnection( socket );
                socket = null;
            }
            catch (UTFDataFormatException udfe){
                System.out.println("Finally Block by exception catch");
                System.out.println("Data Format Problem");
            }
            catch( IOException ie ) {
                System.out.println("Finally Block by exception catch");
                //ie.printStackTrace();
                server.removeConnection( socket );
                socket = null;

            }
            catch (NullPointerException npe){
                System.out.println("Finally Block by exception catch");
                //npe.printStackTrace();
            }


    }
}

