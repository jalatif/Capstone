import java.sql.*;

/**
 * Created with IntelliJ IDEA.
 * User: jalatif
 * Date: 4/10/13
 * Time: 3:14 AM
 * To change this template use File | Settings | File Templates.
 */
public class DBConnect {
    Connection con = null;
    PreparedStatement p;
    private boolean connected = false;
    public DBConnect(){
        connect();
        /*try {
            boolean b = addUser("jalatif", "poptfs");
            System.out.println("User added "+b);
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            System.out.println("User not added");
        }
        try {
            System.out.println(checkUser("jalatif"));
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        */
    }

    protected boolean isConnected(){
        return connected;
    }

    protected void storeMessage(String to, String from, String message) throws SQLException{
            p = con.prepareCall("insert into offline_store (ForUser, FromUser, Message, MsgTime) values (\""+to+"\", \""+from+"\", \""+message+"\" , NOW());");
            p.executeUpdate();
            System.out.println("Message Stored");
    }

    protected String getOfflineMessages(String forUser) throws SQLException {
        p = con.prepareCall("select * from offline_store where ForUser=\"" + forUser + "\" order by msgtime");
        ResultSet rs = p.executeQuery();
        String send = "";
        while(rs.next()){
            String from = rs.getString(2);
            String msg = rs.getString(3);
            //Date dt = rs.getDate(4);
            Timestamp dt = rs.getTimestamp(4);
            send += "RofM~*~@" + from + "&" + dt.toString() + "&" + msg;
        }
        if(!send.equals("")){
            p = con.prepareCall("delete from offline_store where ForUser=\"" + forUser + "\"");
            p.executeUpdate();
        }
        return send;
    }
    protected String getUsers(String username) throws SQLException{
        String users = "";
        p = con.prepareCall("select username from login_passwords");
        ResultSet r = p.executeQuery();
        String res = "";
        while(r.next()){
            res = r.getString(1);
            if (!res.equals(username))
                users += res + "&";
        }
        return users;
    }

    protected boolean addUser(String username, String password) throws SQLException {
        if (checkUser(username))
            return false;
        p = con.prepareCall("insert into login_passwords values(?,?)");
        p.setString(1, username);
        p.setString(2, password);
        p.executeUpdate();
        return true;
    }

    private boolean checkUser(String username) throws SQLException{
        p = con.prepareCall("select * from login_passwords where username=\""+username+"\";");
        ResultSet r = p.executeQuery();
        if(!r.next())
            return false;
        return true;
    }

    public boolean authenticate(String username, String password) throws SQLException{
        p = con.prepareCall("select * from login_passwords where username=\""+username+"\";");
        ResultSet r = p.executeQuery();
        if(!r.next())
            return false;
        if (username.equalsIgnoreCase(r.getString(1)) && password.equals(r.getString(2))){
            return true;
        }
        return false;
    }

    public void connect(){
        try
        {String url = "jdbc:mysql://localhost:5131/";
            String dbName = "Chat_Users";
            String driver = "com.mysql.jdbc.Driver";
            String userName = "root";
            String password = "poptfs";
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + dbName, userName, password);
            System.out.println("Driver Loaded");
            connected = true;
        }
        catch (Exception e)
        {   System.out.println(e.getClass().getCanonicalName());
            System.out.println(e);
            connected = false;
        }
    }

    public static void main(String args[]){
        DBConnect dbc = new DBConnect();
        //dbc.storeMessage("jalatif", "naman", "asdasd");
        try {
            System.out.println(dbc.getOfflineMessages("naman"));
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

}
