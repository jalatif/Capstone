/**
 * Created with IntelliJ IDEA.
 * User: jalatif
 * Date: 8/4/13
 * Time: 9:35 PM
 * To change this template use File | Settings | File Templates.
 */
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
public class Client extends Panel implements Runnable
{
    private String myMsg = "";
    private String mySMsg = "";

    private TextField tf = new TextField();
    private TextField tf1 = new TextField();
    private TextArea ta = new TextArea();

    private Socket socket;


    private DataOutputStream dout;
    private DataInputStream din;

    public Client( String host, int port ) {

        setLayout( new BorderLayout() );
        add( "North", tf );
        add("North", tf1);
        add( "Center", ta );
        ta.setEditable(false);



        tf.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                processMessage( e.getActionCommand() );
            }
        } );

        try {

            socket = new Socket( host, port );

            System.out.println( "connected to "+socket );


            din = new DataInputStream( socket.getInputStream() );
            dout = new DataOutputStream( socket.getOutputStream() );

            new Thread( this ).start();
        } catch( IOException ie ) { System.out.println( ie ); }
    }

    private void processMessage( String message ) {
        try {

            mySMsg = message;
            message = socket.getLocalPort() + " said: " + message;
            myMsg = message;
            dout.writeUTF(message);

            tf.setText( "" );
        } catch( IOException ie ) { System.out.println( ie ); }
    }

    public void run() {
        try {

            while (true) {

                String message = din.readUTF();


                ta.append( message+"\n" );

            }
        } catch( IOException ie ) { System.out.println( ie ); }
    }

    public static void main(String args[]){
        int port = Integer.parseInt(args[1]);

        String host = args[0];
        Client c = new Client(host, port);
    }

}

