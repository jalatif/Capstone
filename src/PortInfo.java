import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import javax.swing.*;
import javax.swing.UIManager.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
/*
 * Created by JFormDesigner on Thu Apr 11 17:33:31 IST 2013
 */



/**
 * @author Jalatif
 */
public class PortInfo extends JFrame implements Runnable {
    //private String ip = "192.168.1.3";
    //private String ip = "jalatif.no-ip.biz";
    //private String ip = "jalatif.read-books.org";
    private String ip = "jalatif.unusualperson.com";
    private int port = 1234;
    public PortInfo() {
        initComponents();
        setVisible(true);
        textField1.setText(ip);
        textField2.setText(String.valueOf(port));
    }

    private void checkParams(){
        try{
            ip = textField1.getText();
            port = Integer.parseInt(textField2.getText());
            Thread a = new Thread(this);
            a.start();
            //ClientChatHandler cch = new ClientChatHandler(ip, port);
        }
        catch(NumberFormatException nex){
            label3.setText("Port Should be a number");
        }
    }

    private void button1MousePressed(MouseEvent e) {
        // TODO add your code here
        checkParams();
    }

    @Override
    public void run() {
        //To change body of implemented methods use File | Settings | File Templates.
        try{
            //ClientChatHandler cch = new ClientChatHandler(ip, port);
            Socket socket = new Socket( ip, port );
            this.setVisible(false);
            ClientChatHandler cch = new ClientChatHandler(ip, port, socket);

        }
        catch(UnknownHostException uhe){
            label3.setText("Unknow host : " + ip);
        }
        catch (ConnectException ce){
            label3.setText("Wrong Port Number");
        }
        catch (IOException e) {
            label3.setText("Invalid Host Name");
            //e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        catch(IllegalArgumentException iae){
            label3.setText("Port out of range");
        }

    }

    private void button1KeyPressed(KeyEvent e) {
        // TODO add your code here
        if (e.getKeyCode() == KeyEvent.VK_SPACE)
            checkParams();
    }



    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        vSpacer1 = new JPanel(null);
        hSpacer1 = new JPanel(null);
        label1 = new JLabel();
        textField1 = new JTextField();
        label2 = new JLabel();
        textField2 = new JTextField();
        hSpacer2 = new JPanel(null);
        label3 = new JLabel();
        button1 = new JButton();
        vSpacer2 = new JPanel(null);

        //======== this ========
        setTitle("Port Info");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Container contentPane = getContentPane();
        contentPane.setLayout(new FormLayout(
            "2*(default, $lcgap), 72dlu, $lcgap, 12dlu",
            "12dlu, 4*($lgap, default), $lgap, 18dlu"));
        contentPane.add(vSpacer1, CC.xy(3, 1));
        contentPane.add(hSpacer1, CC.xy(1, 3));

        //---- label1 ----
        label1.setText("Enter IP Address of Server");
        contentPane.add(label1, CC.xy(3, 3));

        //---- textField1 ----
        textField1.setToolTipText("IP Addres of server");
        contentPane.add(textField1, CC.xy(5, 3));

        //---- label2 ----
        label2.setText("Enter Port Number of Server");
        contentPane.add(label2, CC.xy(3, 5));

        //---- textField2 ----
        textField2.setToolTipText("Port Number (if not given take 1234)");
        contentPane.add(textField2, CC.xy(5, 5));
        contentPane.add(hSpacer2, CC.xy(7, 5));

        //---- label3 ----
        label3.setForeground(Color.red);
        contentPane.add(label3, CC.xy(3, 7));

        //---- button1 ----
        button1.setText("Enter Chat");
        button1.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                button1MousePressed(e);
            }
        });
        button1.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                button1KeyPressed(e);
            }
        });
        contentPane.add(button1, CC.xy(5, 9));
        contentPane.add(vSpacer2, CC.xy(3, 11));
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel vSpacer1;
    private JPanel hSpacer1;
    private JLabel label1;
    private JTextField textField1;
    private JLabel label2;
    private JTextField textField2;
    private JPanel hSpacer2;
    private JLabel label3;
    private JButton button1;
    private JPanel vSpacer2;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    public static void main(String args[]){

        try {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus is not available, you can set the GUI to another look and feel.
        }
        new PortInfo();
    }


}
