import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.DataOutputStream;
import java.io.IOException;
/*
 * Created by JFormDesigner on Wed Apr 10 00:43:21 IST 2013
 */



/**
 * @author Jalatif
 */
public class LoginChat extends JFrame{
    private DataOutputStream dout;
    private String userName = "";

    public LoginChat(DataOutputStream dout) {
        this.dout = dout;
        initComponents();
    }

    public void LabelChange(String message){
        label3.setText(message);
    }

    protected void clearFields(){
        textField1.setText("");
        passwordField1.setText("");
    }

    protected String getUser(){
        return userName;
    }

    private boolean addUser(){
        boolean added = false;
        String username = textField1.getText();
        String password = passwordField1.getText();
        if (username.length() > 20 || password.length() > 20 || username.contains("&") || username.equals("") || password.equals("")){
            label3.setText("Register with valid information");
            return false;
        }
        try {
            dout.writeUTF( username + "RegsU@*@~" + password );
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return added;
    }
    private void authenticate(){
        label3.setText("Waiting for Authentication");
        String username = textField1.getText();
        userName = username;
        String password = passwordField1.getText();
        try {
            dout.writeUTF( username + "AuthU@*@~" + password );
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }
    private void button1MousePressed(MouseEvent e) {
        // TODO add your code here
        authenticate();
    }

    private void button2MousePressed(MouseEvent e) {
        // TODO add your code here
        addUser();
    }

    private void button1KeyPressed(KeyEvent e) {
        // TODO add your code here
        if (e.getKeyCode() == KeyEvent.VK_SPACE){
            authenticate();
        }
    }

    private void button2KeyPressed(KeyEvent e) {
        // TODO add your code here
        if (e.getKeyCode() == KeyEvent.VK_SPACE)
            addUser();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        label1 = new JLabel();
        textField1 = new JTextField();
        label2 = new JLabel();
        passwordField1 = new JPasswordField();
        hSpacer1 = new JPanel(null);
        button2 = new JButton();
        label3 = new JLabel();
        button1 = new JButton();
        vSpacer1 = new JPanel(null);

        //======== this ========
        setTitle("Login");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Container contentPane = getContentPane();
        contentPane.setLayout(new FormLayout(
            "16dlu, 2*($lcgap, default), $lcgap, 99dlu, 2*($lcgap, default), $lcgap, 26dlu",
            "25dlu, 3*($lgap, default), $lgap, 33dlu"));

        //---- label1 ----
        label1.setText("Username");
        contentPane.add(label1, CC.xy(5, 3));
        contentPane.add(textField1, CC.xy(7, 3));

        //---- label2 ----
        label2.setText("Password");
        contentPane.add(label2, CC.xy(5, 5));

        //---- passwordField1 ----
        passwordField1.setDragEnabled(true);
        passwordField1.setToolTipText("Enter Password");
        contentPane.add(passwordField1, CC.xy(7, 5));
        contentPane.add(hSpacer1, CC.xy(13, 5));

        //---- button2 ----
        button2.setText("Register");
        button2.setToolTipText("Register with entered Username and Password");
        button2.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                button2MousePressed(e);
            }
        });
        button2.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                button2KeyPressed(e);
            }
        });
        contentPane.add(button2, CC.xy(3, 7));

        //---- label3 ----
        label3.setForeground(Color.red);
        contentPane.add(label3, CC.xywh(7, 6, 1, 2));

        //---- button1 ----
        button1.setText("Login");
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
        contentPane.add(button1, CC.xy(11, 7));
        contentPane.add(vSpacer1, CC.xywh(7, 8, 1, 2));
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel label1;
    private JTextField textField1;
    private JLabel label2;
    private JPasswordField passwordField1;
    private JPanel hSpacer1;
    private JButton button2;
    private JLabel label3;
    private JButton button1;
    private JPanel vSpacer1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables


}
