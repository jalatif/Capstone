/**
 * Created with IntelliJ IDEA.
 * User: jalatif
 * Date: 8/4/13
 * Time: 9:36 PM
 * To change this template use File | Settings | File Templates.
 */
import java.awt.*;

public class ClientChat extends javax.swing.JFrame
{

    private String host;
    private int port;

    public void init() {
//String host = getParameter( "host" );
//int port = Integer.parseInt( getParameter( "port" ) );
//String host = "192.168.1.3";
//int port = 1234;
        setLayout( new BorderLayout() );
        setSize(500,500);
        setTitle("Chat Window");
        add( "Center", new Client( host, port ) );
        setVisible(true);
    }

    public static void main(String args[]){
        ClientChat ca = new ClientChat();
        ca.port = Integer.parseInt(args[1]);
        ca.host = args[0];
        ca.init();
    }

}


