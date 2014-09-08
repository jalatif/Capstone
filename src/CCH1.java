import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;
/*
 * Created by JFormDesigner on Tue Apr 09 00:47:21 IST 2013
 */


/**
 * @author Jalatif
 */
public class CCH1 implements Runnable{
    private String to = "";
    private String myMsg = "";
    private int login = 0;
    protected static String toUser = "";
    private String userN = "";
    private Socket socket;
    private String checking = "";
    private DataOutputStream dout;
    private DataInputStream din;
    protected Hashtable getChatWindow = new Hashtable();

    public CCH1(String host, int port) {
        try {

            socket = new Socket( host, port );
            //setTitle("Chat Window for " + socket.getLocalPort());
            System.out.println( "connected to "+socket );

            din = new DataInputStream( socket.getInputStream() );
            dout = new DataOutputStream( socket.getOutputStream() );

            Thread signin = new Thread(this, "Login");
            checking = "Login";

            signin.start();
            try {
                signin.join();
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            //this.setVisible(true);

            Timer timer = new Timer();
            int seconds = 1;
            timer.schedule(new statusCheck(), 0, seconds*1000);

            Thread chat = new Thread( this, "Chat" );
            checking = "Chat";
            chat.start();
        } catch( IOException ie ) { System.out.println( ie ); }
    }

    private class statusCheck extends TimerTask{
        public void run(){
            try {
                //textField1.setText(toUser);
                dout.writeUTF("Status@*@~");
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
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
                lc.LabelChange("Successfull Login");
                Thread.sleep(1000);
            }
            catch (Exception e){
                e.printStackTrace();
            }
            userN = lc.getUser();
            //setTitle("Chat Window for " + userN);
            lc.setVisible(false);
            ChatWindow cw = new ChatWindow(userN, to, socket);
            getChatWindow.put(to, cw);
        }

        if (checking.equals("Chat")){
            try {
                AvailableUsers au = new AvailableUsers();
                au.setVisible(true);
                au.setTitle(userN + "Available Users");
                while (true) {
                    //dout.writeUTF("Status@*@~");
                    String msp = din.readUTF();
                    String message = "";
                    if (msp.startsWith("MsgRx~*~@")){
                        int loc = msp.indexOf("MsgRx~*~@");
                        String from = msp.substring(0, loc);
                        message = msp.substring(loc + 9, msp.length());
                        ChatWindow cW;
                        try{
                            cW = (ChatWindow)getChatWindow.get(from);
                        }
                        catch (NullPointerException npe){
                            cW = new ChatWindow(userN, from, socket);
                            getChatWindow.put(from, cW);
                        }
                        cW.processInMessage(message);
                    }
                    if (msp.startsWith("StsOf~*~@")){
                        message = msp.substring(9, msp.length());
                        String membs[] = message.split("&");
                        au.setUsers(membs);

                    }

                }
            } catch( IOException ie ) { System.out.println( ie ); }
        }
    }



    public static void main(String args[]){

        String host = args[0];
        int port = 1234;
        if (args.length == 2)
            port = Integer.parseInt( args[1] );
        CCH1 c = new CCH1(host, port);

    }
}
