import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;
/*
 * Created by JFormDesigner on Thu Apr 11 13:22:57 IST 2013
 */



/**
 * @author Jalatif
 */
public class ClientChatHandler extends JFrame implements Runnable{
    private String to = "";
    private String myMsg = "";
    private int login = 0;
    protected static String toUser = "";
    private String userN = "";
    private Socket socket;
    private String checking = "";
    private DataOutputStream dout;
    private DataInputStream din;
    protected static Hashtable getChatWindow = new Hashtable();
    private Set<String> buddy;
    private Clip clip = null;
    protected static boolean busy = false;
    protected static boolean downloading = false;
    protected static int saveFile = 0;

    public ClientChatHandler(String host, int port) {
        try {

            socket = new Socket( host, port );
            //setTitle("Chat Window for " + socket.getLocalPort());
            System.out.println( "connected to "+socket );

            din = new DataInputStream( socket.getInputStream() );
            dout = new DataOutputStream( socket.getOutputStream() );

            Thread signin = new Thread(this, "Login");
            checking = "Login";

            signin.start();

            signin.join();

            initComponents();
            this.setTitle(userN + "'s Available Users");
            this.setVisible(true);

            Thread offline = new Thread(this, "Offline");
            checking = "Offline";
            dout.writeUTF("GofM@*@~");
            offline.start();

            offline.join();

            java.util.Timer timer = new java.util.Timer();
            int seconds = 1;
            timer.schedule(new statusCheck(), 0, seconds*1000);


            Thread chat = new Thread( this, "Chat" );
            checking = "Chat";
            chat.start();
            Clip clip = null;
            try {
                clip = AudioSystem.getClip();
                AudioInputStream inputStream = AudioSystem.getAudioInputStream(ClientChatHandler.class.getClassLoader().getResourceAsStream("res/notification.wav"));//AudioSystem.getAudioInputStream(soundFile);
                clip.open(inputStream);
            } catch (LineUnavailableException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (UnsupportedAudioFileException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        } catch( IOException ie ) { System.out.println( ie ); } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
    public ClientChatHandler(String host, int port, Socket s) {
        try {
            /*
            try{
                socket = new Socket( host, port );
            }
            catch(UnknownHostException uhe){
                PortInfo.label3.setText("Unknow host : " + host);
                return;
            }
            catch (ConnectException ce){
                PortInfo.label3.setText("Wron Port Number");
                return;
            }   */
            //setTitle("Chat Window for " + socket.getLocalPort());
            this.socket = s;
            System.out.println( "connected to "+socket );

            din = new DataInputStream( socket.getInputStream() );
            dout = new DataOutputStream( socket.getOutputStream() );

            Thread signin = new Thread(this, "Login");
            checking = "Login";

            signin.start();

            signin.join();

            initComponents();
            this.setTitle(userN + "'s Available Users");
            this.setVisible(true);

            Thread offline = new Thread(this, "Offline");
            checking = "Offline";
            dout.writeUTF("GofM@*@~");
            offline.start();

            offline.join();

            java.util.Timer timer = new java.util.Timer();
            int seconds = 1;
            timer.schedule(new statusCheck(), 0, seconds*1000);


            Thread chat = new Thread( this, "Chat" );
            checking = "Chat";
            chat.start();
            initSound();
        } catch( IOException ie ) { System.out.println( ie ); } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private class statusCheck extends TimerTask{
        public void run(){
            if (busy || downloading)
                return;
            try {
                //textField1.setText(toUser);
                dout.writeUTF("AlUsr@*@~");
                dout.writeUTF("Status@*@~");
            } catch (IOException e) {
                System.out.println("Disconnected");
                //e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }
    public void run() {
        if (checking.equals("Login")){
            LoginChat lc = new LoginChat(dout);
            lc.setVisible(true);
            try{
                while (true){
                    String auth = din.readUTF();
                    System.out.println(auth);
                    if (auth.contains("Heart~*~@")){
                        dout.writeUTF("Heart@*@~");
                        continue;
                    }
                    if (auth.contains("RegsR~*~@")){
                        auth = auth.substring(9, auth.length());
                        lc.LabelChange(auth);
                        lc.clearFields();
                        continue;
                    }
                    if (!auth.contains("AuthR~*~@"))
                        continue;
                    auth = auth.substring(9, auth.length());
                    if (auth.equals("true"))
                        break;
                    else
                        lc.LabelChange("Invalid Username or Password");

                }
                lc.LabelChange("Successful Login");
                Thread.sleep(1000);
            }
            catch (Exception e){
                e.printStackTrace();
            }
            userN = lc.getUser();
            //setTitle("Chat Window for " + userN);
            lc.setVisible(false);
        }

        if (checking.equals("Offline")){
            String from;
            String time;
            String off_message;
            Set<String> offline_call = new HashSet<String>();
            try {   String msp = "";
                    while(true){
                        msp = din.readUTF();
                        System.out.println("Offline Message : " + msp);
                        if (msp.contains("Heart~*~@")){
                            dout.writeUTF("Heart@*@~");
                        }
                        else if (msp.contains("RofM~*~@")){
                            break;
                        }
                        else {
                            return;
                        }
                    }
                    ChatWindow cw;
                    String[] msg_set = msp.split("RofM[~][*][~][@]");
                    for (String msg : msg_set){
                        try{
                            if (msg.equals(""))
                                continue;
                            System.out.println("Message : " + msg);
                            int loc = msg.indexOf('&');
                            from = msg.substring(0, loc);
                            int loc2 = msg.indexOf('&', loc + 1);
                            time = msg.substring(loc + 1, loc2);
                            off_message = msg.substring(loc2 + 1, msg.length());
                            if (!offline_call.contains(from)){
                                cw = new ChatWindow(userN, from, socket);
                                offline_call.add(from);
                                getChatWindow.put(from, cw);
                                cw.processInMessage("Offline Message from " + from + " at " + time + "\n" + off_message + "\n");
                                playSound();
                            }else{
                                try{
                                    cw = (ChatWindow) getChatWindow.get(from);
                                    cw.processInMessage("Offline Message at " + time + "\n" + off_message + "\n");
                                    playSound();
                                }
                                catch (NullPointerException npe){
                                    System.out.println("Got Npe");
                                }
                            }
                        }
                        catch(NullPointerException npe){
                            System.out.println("Got NPE");
                        }
                    }
            }
            catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        if (checking.equals("Chat")){
            try {
                //AvailableUsers au = new AvailableUsers();
                //au.setVisible(true);
                //au.setTitle(userN + "Available Users");
                while (true) {
                    //dout.writeUTF("Status@*@~");
                    String msp = din.readUTF();
                    String message = "";
                    if (msp.contains("Heart~*~@")){
                        dout.writeUTF("Heart@*@~");
                        continue;
                    }
                    if (msp.contains("MsgRx~*~@")){
                        int loc = msp.indexOf("MsgRx~*~@");
                        String from = msp.substring(0, loc);
                        message = msp.substring(loc + 9, msp.length());
                        ChatWindow cW;
                        try{
                            cW = (ChatWindow)getChatWindow.get(from);
                            System.out.println("Got " + message + " from " + from);
                            //System.out.println(cW.getInfo());
                            cW.processInMessage(message);
                            playSound();
                        }
                        catch (NullPointerException npe){
                            System.out.println("Got an npe ");
                            //npe.printStackTrace();
                            cW = new ChatWindow(userN, from, socket);
                            getChatWindow.put(from, cW);
                            System.out.println("Got " + message + " from " + from);
                            //System.out.println(cW.getInfo());
                            cW.processInMessage(message);
                            playSound();
                        }
                    }
                    if (msp.startsWith("AlUsr~*~@")){
                        message = msp.substring(9, msp.length());
                        String all[] = message.split("&");
                        buddy = new HashSet<String>(Arrays.asList(all));
                    }
                    if (msp.startsWith("StsOf~*~@")){
                        message = msp.substring(9, msp.length());
                        String membs[] = message.split("&");
                        this.setUsers(membs);

                    }
                    if (msp.startsWith("RFile~*~@")){
                        busy = false;
                        int loc = msp.indexOf("&");
                        String withUser = msp.substring(9, loc);
                        String fileName = msp.substring(loc + 1, msp.length());
                        System.out.println("Server received file successfully");
                        dout.writeUTF("EFile@*@~" + withUser + "&" + fileName);
                        ChatWindow cw = (ChatWindow)getChatWindow.get(withUser);
                        cw.processInMessage("File Received at Server.\n");
                    }

                    if (msp.startsWith("NFile~*~@")){
                        busy = false;
                        int loc = msp.indexOf("&");
                        String withUser = msp.substring(9, loc);
                        String fileName = msp.substring(loc + 1, msp.length());
                        System.out.println("Error in Server side reception");
                        ChatWindow cw = (ChatWindow)getChatWindow.get(withUser);
                        cw.processInMessage("File Not Received at other end\n");
                    }

                    if (msp.startsWith("EFile~*~@")){
                        int loc = msp.indexOf("&");
                        String withUser = msp.substring(9, loc);
                        String fileName = msp.substring(loc + 1, msp.length());
                        dout.writeUTF("FFile@*@~" + withUser + "&" + fileName);
                    }

                    if (msp.startsWith("SFile~*~@")){
                        receiveFile(msp);
                    }


                }
            } catch( IOException ie ) { System.out.println( ie ); }
        }
    }
    protected void receiveFile(String message){
        int loc = message.indexOf("&");
        int loc2 = message.indexOf("&", loc + 1);
        int fileSize = Integer.parseInt(message.substring(9, loc));
        final String withUser = message.substring(loc + 1, loc2);
        final String fileName = message.substring(loc2 + 1, message.length());

        ChatWindow cW;
        try{
            cW = (ChatWindow) getChatWindow.get(withUser);
            playSound();
        } catch (NullPointerException npe){
            System.out.println("Got an npe ");
            //npe.printStackTrace();
            cW = new ChatWindow(userN, withUser, socket);
            getChatWindow.put(withUser, cW);
            System.out.println("Got " + message + " from " + withUser);
            //System.out.println(cW.getInfo());
            playSound();
        }

        ////////
        Thread alert = new Thread(new Runnable() {
            @Override
            public void run() {
                //To change body of implemented methods use File | Settings | File Templates.
                SaveFile sf = new SaveFile(withUser, fileName, userN);
                sf.setVisible(true);
            }
        });
        alert.start();

        Thread checkSave = new Thread(new Runnable() {
            @Override
            public void run() {
                //To change body of implemented methods use File | Settings | File Templates.
                while(saveFile == 0)
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
            }
        });
        checkSave.start();
        try {
            checkSave.join();
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            cW.processInMessage("Save File Interrupted. Dropping");
            saveFile = 0;
            return;
        }
        String filePath = "";
        if (saveFile == 1){
            System.out.println("File Reception Accepted");
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("File Save Location");
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnVal = fc.showOpenDialog(ClientChatHandler.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                System.out.println("Saving in : " + file.getAbsolutePath());
                filePath = file.getAbsolutePath();
            } else {
                System.out.println("Open command cancelled by user.");
            }
        }
        else if (saveFile == -1){
            cW.processInMessage("You rejected File Reception");
            saveFile = 0;
        }
        else{
            saveFile = 0;
        }

        cW.processInMessage("Receiving file from " + withUser + "\n");
        boolean success = false;
        if (saveFile == 1){
            downloading = true; busy = true;
            success = download(fileSize, fileName, filePath);
            downloading = false; busy = false;
            saveFile = 0;
        }
        try {
            if (success){
                dout.writeUTF("RFile@*@~");
                cW.processInMessage("Successfully received file\n");
            }
            else{
                dout.writeUTF("NFile@*@~");
                cW.processInMessage("File Reception Unsuccessfull\n");
            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    private boolean download(int fileSize, String fileName, String place){
        FileOutputStream outStream = null;
        try {
            outStream = new FileOutputStream(place + "/" + fileName);
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
        File checkFile = new File(place + "/" + fileName);
        int newFileSize = (int) checkFile.length();
        if ( newFileSize < fileSize){
            System.out.println("File Downloading Failed. Difference in size = " + (fileSize - newFileSize));
            return false;
        }
        else{
            System.out.println("Downloaded Successfully!");
            return true;
        }

    }

    protected void setUsers(String[] users){
        list1.setListData(users);
        Set<String> online = new HashSet<String>(Arrays.asList(users));
        buddy.removeAll(online);
        list2.setListData(buddy.toArray());

        Enumeration<String> winds = getChatWindow.keys();
        String window = "";
        ChatWindow cw;
        while(winds.hasMoreElements()){
            window = winds.nextElement();
            cw = (ChatWindow)getChatWindow.get(window);
            if (online.contains(window)){
                cw.changeStatus(true);
            }
            else{
                cw.changeStatus(false);
            }
        }
    }

    private void list1MouseClicked(MouseEvent e) {
        // TODO add your code here
        to = (String)list1.getSelectedValue();
        try{
            if (!getChatWindow.containsKey(to)){
                ChatWindow cw = new ChatWindow(userN, to, socket);
                getChatWindow.put(to, cw);
            }
        }
        catch (NullPointerException npe){
            System.out.print("Got Npe");
        }
    }

    private void list2MouseClicked(MouseEvent e) {
        // TODO add your code here
        to = (String)list2.getSelectedValue();
        try{
            if (!getChatWindow.containsKey(to)){
                ChatWindow cw = new ChatWindow(userN, to, socket);
                getChatWindow.put(to, cw);
            }
        }
        catch (NullPointerException npe){
            System.out.print("Got Npe");
        }

    }

    protected void playSound(){
        try{
            clip.stop();
            clip.setFramePosition(0);
            clip.start();
        }
        catch (NullPointerException npe){System.out.println("Can't Load Notification sound file");}
    }

    protected void initSound(){
        try {
            clip = AudioSystem.getClip();
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(ClientChatHandler.class.getClassLoader().getResourceAsStream("res/notification.wav")));//AudioSystem.getAudioInputStream(soundFile);
            clip.open(inputStream);
        } catch (LineUnavailableException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        vSpacer2 = new JPanel(null);
        label2 = new JLabel();
        hSpacer1 = new JPanel(null);
        scrollPane1 = new JScrollPane();
        list1 = new JList();
        hSpacer2 = new JPanel(null);
        label1 = new JLabel();
        scrollPane2 = new JScrollPane();
        list2 = new JList();
        vSpacer1 = new JPanel(null);

        //======== this ========
        setTitle("Available Users");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Container contentPane = getContentPane();
        contentPane.setLayout(new FormLayout(
            "14dlu, 138dlu, $lcgap, 16dlu",
            "10dlu, $lgap, default, $lgap, 133dlu, $lgap, default, $lgap, 103dlu, $lgap, 18dlu"));
        contentPane.add(vSpacer2, CC.xy(2, 1));

        //---- label2 ----
        label2.setText("Online Users");
        label2.setHorizontalAlignment(SwingConstants.CENTER);
        contentPane.add(label2, CC.xy(2, 3));
        contentPane.add(hSpacer1, CC.xy(1, 5));

        //======== scrollPane1 ========
        {

            //---- list1 ----
            list1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            list1.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    list1MouseClicked(e);
                }
            });
            scrollPane1.setViewportView(list1);
        }
        contentPane.add(scrollPane1, CC.xywh(2, 4, 1, 2));
        contentPane.add(hSpacer2, CC.xy(4, 5));

        //---- label1 ----
        label1.setText("Offline Users");
        label1.setHorizontalAlignment(SwingConstants.CENTER);
        contentPane.add(label1, CC.xy(2, 7));

        //======== scrollPane2 ========
        {

            //---- list2 ----
            list2.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    list2MouseClicked(e);
                }
            });
            scrollPane2.setViewportView(list2);
        }
        contentPane.add(scrollPane2, CC.xywh(2, 8, 1, 2));
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel vSpacer2;
    private JLabel label2;
    private JPanel hSpacer1;
    private JScrollPane scrollPane1;
    private JList list1;
    private JPanel hSpacer2;
    private JLabel label1;
    private JScrollPane scrollPane2;
    private JList list2;
    private JPanel vSpacer1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    public static void main(String args[]){

        String host = args[0];
        int port = 1234;
        if (args.length == 2)
            port = Integer.parseInt( args[1] );
        ClientChatHandler c = new ClientChatHandler(host, port);
    }
}
