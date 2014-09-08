/**
 * Created with IntelliJ IDEA.
 * User: jalatif
 * Date: 8/4/13
 * Time: 9:22 PM
 * To change this template use File | Settings | File Templates.
 */
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.Enumeration;
import java.util.Hashtable;

public class Server
{

    private ServerSocket ss;

    private Hashtable outputStreams = new Hashtable();
    private Hashtable socketMap = new Hashtable();

    protected static Hashtable nameMap = new Hashtable();

    protected static Hashtable portMap = new Hashtable();

    private DBConnect dbc;

    public Server( String ip, int port ) throws IOException {
        dbc = new DBConnect();
        listen( ip, port );
    }

    private void showInterfaces() throws Exception
    {
        System.out.println("Host addr: " + InetAddress.getLocalHost().getHostAddress());
        Enumeration<NetworkInterface> n = NetworkInterface.getNetworkInterfaces();
        for (; n.hasMoreElements();)
        {
            NetworkInterface e = n.nextElement();
            System.out.println("Interface: " + e.getName());
            Enumeration<InetAddress> a = e.getInetAddresses();
            for (; a.hasMoreElements();)
            {
                InetAddress addr = a.nextElement();
                System.out.println("  " + addr.getHostAddress());
            }
        }
    }

    private void listen( String ip, int port ) throws IOException {

        String data = "Pyaas JaltheBand";
        try {
            InetAddress locIP;
            locIP = InetAddress.getByName(ip);

            showInterfaces();
            System.out.println(Inet4Address.getLocalHost().getHostAddress());

            ss = new ServerSocket( port, 0, locIP );

            System.out.println( "Listening on "+ss );

            while (true) {

                Socket s = ss.accept();

                System.out.println( "Connection from "+s );


                DataOutputStream dout = new DataOutputStream( s.getOutputStream() );

                outputStreams.put( s, dout );
                socketMap.put(s.getPort(), dout);

                new ServerThread( this, s, dbc);
            }
        }
        catch (Exception e){
            System.out.print("Whoops! It didn't work!\n" + e);
        }

    }


    Enumeration getOutputStreams() {
        return outputStreams.elements();
    }

    Enumeration getSockets(){
        return outputStreams.keys();
    }

    void sendToAll( String message ) {



        synchronized( outputStreams ) {

            for (Enumeration e = getOutputStreams(); e.hasMoreElements(); ) {

                DataOutputStream dout = (DataOutputStream)e.nextElement();

                try {
                    dout.writeUTF( message );
                } catch( IOException ie ) { System.out.println( ie ); }
            }
        }
    }

    protected DataOutputStream getUserOutputStream (String user){
        synchronized( outputStreams ) {
            DataOutputStream dout;
            if (portMap.get(user) == null)
                return null;
            int localport = (Integer) portMap.get(user);
            //int localport = Integer.parseInt(lPort);
            System.out.println("Given " + localport);
            try{
                dout = (DataOutputStream) socketMap.get(localport);
                return dout;
            }
            catch(NullPointerException npe){
                npe.printStackTrace();
                return null;
            }
        }
    }
    void sendTo( String user, String message ) {

        synchronized( outputStreams ) {
            DataOutputStream dout;
            if (portMap.get(user) == null)
                return;
            int localport = (Integer) portMap.get(user);
            //int localport = Integer.parseInt(lPort);
            System.out.println("Given " + localport);
            try{
                dout = (DataOutputStream) socketMap.get(localport);
                dout.writeUTF(message);
                System.out.println("Message Written to " + user + " : " + message );
            }
            catch(NullPointerException npe){
                npe.printStackTrace();
            } catch (SocketException se){
                System.out.println("Socket removed");
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            /*Socket s;
            for (Enumeration e = getSockets(); e.hasMoreElements(); ) {

                s = (Socket)e.nextElement();
                try {
                    System.out.println("Searching in " + s.getPort());
                if (s.getPort() == localport){
                    System.out.println("Ids Matched");
                    dout = (DataOutputStream) outputStreams.get(s);
                    dout.writeUTF( message );
                    }
                }
                catch( Exception ie ) { System.out.println( ie ); }

            }
            */

        }
    }



    void removeConnection( Socket s ) {


        synchronized( outputStreams ) {
            if (s == null)
                return;
            System.out.println( "Removing connection to "+s );
            try {
                portMap.remove(nameMap.get(s.getPort()));
                nameMap.remove(s.getPort());
                socketMap.remove(s.getPort());
                outputStreams.remove( s );
            }
            catch (NullPointerException npe){

            }
            try {
                s.close();
            } catch( IOException ie ) {
                System.out.println( "Error closing "+s );
                ie.printStackTrace();
            }
            catch (NullPointerException npe){

            }
        }
    }

    public int[] checkIpgiven(){

        return new int[]{1, 2};
    }
    static public void main( String args[] ) throws Exception {

        String ip = args[0];
        int port = 1234;
        //int port = 9019;
        if (args.length == 2)
             port = Integer.parseInt( args[1] );

        new Server(ip, port);
    }
}
