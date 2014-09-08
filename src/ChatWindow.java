import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import javax.sound.sampled.Clip;
import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
/*
 * Created by JFormDesigner on Thu Apr 11 12:42:14 IST 2013
 */



/**
 * @author Jalatif
 */
public class ChatWindow extends JFrame{
    private String username = "";
    private Socket socket;
    private DataOutputStream dout;
    private ObjectOutputStream outStream;
    private String withUser = "";
    private String myMsg = "";
    private boolean status;
    private Clip clip = null;
    private ChatWindow ctx;
    private ControlWindow ctw;

    public ChatWindow(String userName, String to, Socket socket) {
        this.username = userName;
        this.socket = socket;
        try {
            dout = new DataOutputStream(socket.getOutputStream());
            //outStream = new ObjectOutputStream(socket.getOutputStream());
            //outStream.flush();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        this.withUser = to;
        initComponents();
        setTitle("Chat B/W " + username + " and " + to);
        label5.setText(withUser);
        label6.setText("Online");
        status = true;
        setVisible(true);
        ///////Control Window Support........
        ctx = this;
        //makeController();
        ////////////////////////////////////////
        DefaultCaret caret = (DefaultCaret)textArea2.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(WindowEvent winEvt) {
                ClientChatHandler.getChatWindow.remove(withUser);
            }
        });
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                toFront();
                repaint();
            }
        });
        //File soundFile = new File("/home/jalatif/programs/Capstone/src/res/notification.wav");
        //String pathToImageSortBy = "nameOfProject/resources/testDataIcons/filling.png";
        //File soundFile = new File(getClass().getClassLoader().getResource(pathToImageSortBy));
        /*try {
            clip = AudioSystem.getClip();
            //AudioInputStream inputStream = AudioSystem.getAudioInputStream(soundFile);
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(ChatWindow.class.getClassLoader().getResourceAsStream("res/notification.wav"));
            clip.open(inputStream);
        } catch (LineUnavailableException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } */

    }

    private void makeController(){
        Thread controller = new Thread(new Runnable() {
            @Override
            public void run() {
                //To change body of implemented methods use File | Settings | File Templates.
                ctw = new ControlWindow(username, ctx);
                ctw.setVisible(true);
            }
        });
        controller.start();
    }

    public String getInfo(){
        return username + "&" + withUser;
    }

    protected void changeStatus(boolean status){
        this.status = status;
        if (status)
            label6.setText("Online");
        else
            label6.setText("Offline");
    }
    protected void processInMessage(String message){
        textArea2.append(message);
        //clip.stop();
        //clip.setFramePosition(0);
        //clip.start();
    }

    protected void processControl(String action){
        if (ClientChatHandler.busy){
            textArea2.append("Busy Sending File Now");
            return;
        }
        try {
            String message = action;
            //String to = (String) comboBox1.getSelectedItem();
            String to = withUser;//textField1.getText();
            //message = socket.getLocalPort() + " said: " + message;
            myMsg = message;
            if (status){
                textArea2.append("I said : " + myMsg + "\n");
                dout.writeUTF(to+"SendTo@*@~"+message);
            }
            else{
                textArea2.append("I said : " + myMsg + " \n(" + withUser + " is offline for now.\n He'll receive ur message when he comes online.)\n");
                dout.writeUTF(to + "OfMsg@*@~"+message);
            }
        } catch( IOException ie ) { System.out.println( ie ); }
    }
    private void processOutMessage(){
        //to = textField1.getText();
        //msg = textArea1.getText();
        //textArea2.append("I said to "+to+": "+msg+"\n");
        if (ClientChatHandler.busy){
            textArea2.append("Busy Sending File Now");
            return;
        }

        try {
            String message = textArea1.getText();
            if (message.endsWith("\n"))
                message = message.substring(0, message.length() - 1);
            //String to = (String) comboBox1.getSelectedItem();
            String to = withUser;//textField1.getText();
            //message = socket.getLocalPort() + " said: " + message;
            myMsg = message;

            textArea1.setText( "" );
            if (status){
                textArea2.append("I said : " + myMsg + "\n");
                dout.writeUTF(to+"SendTo@*@~"+message);
            }
            else{
                textArea2.append("I said : " + myMsg + " \n(" + withUser + " is offline for now.\n He'll receive ur message when he comes online.)\n");
                dout.writeUTF(to + "OfMsg@*@~"+message);
            }
        } catch( IOException ie ) { System.out.println( ie ); }
    }

    private void sendFile(File file) throws IOException {
        long fileSize = file.length();
        long completed = 0;
        int step = (int) fileSize;//150000;
        if (fileSize > 26214400){
            textArea2.append("Can't Send File Greater than 25MB\n");
            return;
        }
        // creates the file stream
        FileInputStream fileStream = new FileInputStream(file);
        BufferedInputStream bis = new BufferedInputStream(fileStream);

        // sending a message before streaming the file
        System.out.println("SENDING_FILE|" + file.getName() + "|" + fileSize);
        textArea2.append("File Selected to send -> " + file.getAbsolutePath() + "\n");
        textArea2.append("Plz wait till File Sent...\n");
        dout.writeUTF("SFile@*@~" + fileSize + "&" + withUser + "&" + file.getName());
        ClientChatHandler.busy = true;
        /*byte[] buffer = new byte[step];
        while (completed <= fileSize) {
            fileStream.read(buffer);
            dout.write(buffer);
            completed += step;
        } */
        byte [] mybytearray  = new byte [(int)fileSize];
        bis.read(mybytearray,0,mybytearray.length);
        //InputStream
        dout.write(mybytearray,0,mybytearray.length);
        dout.flush();
        //dout.writeUTF("EFile@*@~" + withUser + "&" + file.getName());
        System.out.println("SEND_COMPLETE");
        bis.close();
        fileStream.close();

    }
    private void textArea1KeyReleased(KeyEvent e) {
        // TODO add your code here
        if(e.isShiftDown() && e.getKeyCode() == KeyEvent.VK_ENTER){
            textArea1.append("\n");
        }
        if (!e.isShiftDown() && e.getKeyCode() == KeyEvent.VK_ENTER){
            processOutMessage();
        }
    }

    private void button1MouseClicked(MouseEvent e) {
        // TODO add your code here
        processOutMessage();
    }

    private void bControlMouseClicked(MouseEvent e) {
        // TODO add your code here
        makeController();
    }

    private void thisWindowClosed(WindowEvent e) {
        // TODO add your code here
        if(ctw != null)
            ctw.dispose();

    }

    private void bFSMouseClicked(MouseEvent e) {
        // TODO add your code here
        JFileChooser fc = new JFileChooser();
        int returnVal = fc.showOpenDialog(ChatWindow.this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            //This is where a real application would open the file.
            System.out.println("Opening: " + file.getAbsolutePath());
            try {
                sendFile(file);
            } catch (IOException e1) {
                System.out.println("Cannot send File");
                System.out.println("Disconnected in FileSend");
                e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        } else {
            System.out.println("Open command cancelled by user.");
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        label1 = new JLabel();
        label5 = new JLabel();
        label6 = new JLabel();
        label2 = new JLabel();
        scrollPane2 = new JScrollPane();
        textArea1 = new JTextArea();
        label4 = new JLabel();
        bControl = new JButton();
        bFS = new JButton();
        button1 = new JButton();
        vSpacer2 = new JPanel(null);
        label3 = new JLabel();
        hSpacer1 = new JPanel(null);
        scrollPane1 = new JScrollPane();
        textArea2 = new JTextArea();
        hSpacer2 = new JPanel(null);
        vSpacer1 = new JPanel(null);

        //======== this ========
        setTitle("Chat Window");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                thisWindowClosed(e);
            }
        });
        Container contentPane = getContentPane();
        contentPane.setLayout(new FormLayout(
            "16dlu, $lcgap, default, $lcgap, 51dlu:grow, $lcgap, default, $lcgap, 29dlu:grow, 12dlu:grow, 45dlu",
            "16dlu, $lgap, 18dlu:grow(0.19999999999999998), 3*($lgap, default), $lgap, 99dlu:grow(0.8), $lgap, 23dlu"));

        //---- label1 ----
        label1.setText("Chatting With");
        contentPane.add(label1, CC.xy(3, 1));

        //---- label5 ----
        label5.setHorizontalAlignment(SwingConstants.CENTER);
        contentPane.add(label5, CC.xywh(5, 1, 3, 1));

        //---- label6 ----
        label6.setHorizontalAlignment(SwingConstants.CENTER);
        contentPane.add(label6, CC.xy(9, 1));

        //---- label2 ----
        label2.setText("Message");
        contentPane.add(label2, CC.xy(3, 3));

        //======== scrollPane2 ========
        {

            //---- textArea1 ----
            textArea1.setRows(2);
            textArea1.setToolTipText("Message for that user");
            textArea1.setTabSize(4);
            textArea1.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    textArea1KeyReleased(e);
                }
            });
            scrollPane2.setViewportView(textArea1);
        }
        contentPane.add(scrollPane2, CC.xywh(5, 3, 6, 2));
        contentPane.add(label4, CC.xy(3, 5));

        //---- bControl ----
        bControl.setText("Controls");
        bControl.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                bControlMouseClicked(e);
            }
        });
        contentPane.add(bControl, CC.xy(5, 5));

        //---- bFS ----
        bFS.setText("FileSend");
        bFS.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                bFSMouseClicked(e);
            }
        });
        contentPane.add(bFS, CC.xy(7, 5));

        //---- button1 ----
        button1.setText("Send");
        button1.setToolTipText("Press this to send the message to the user");
        button1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                button1MouseClicked(e);
            }
        });
        contentPane.add(button1, CC.xywh(9, 5, 2, 1));
        contentPane.add(vSpacer2, CC.xy(3, 7));

        //---- label3 ----
        label3.setText("Chat");
        label3.setHorizontalAlignment(SwingConstants.CENTER);
        contentPane.add(label3, CC.xywh(3, 9, 7, 1));
        contentPane.add(hSpacer1, CC.xy(1, 11));

        //======== scrollPane1 ========
        {

            //---- textArea2 ----
            textArea2.setRows(10);
            textArea2.setToolTipText("Chat with that user");
            textArea2.setEditable(false);
            scrollPane1.setViewportView(textArea2);
        }
        contentPane.add(scrollPane1, CC.xywh(3, 11, 8, 2));
        contentPane.add(hSpacer2, CC.xy(11, 11));
        contentPane.add(vSpacer1, CC.xy(5, 13));
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel label1;
    private JLabel label5;
    private JLabel label6;
    private JLabel label2;
    private JScrollPane scrollPane2;
    private JTextArea textArea1;
    private JLabel label4;
    private JButton bControl;
    private JButton bFS;
    private JButton button1;
    private JPanel vSpacer2;
    private JLabel label3;
    private JPanel hSpacer1;
    private JScrollPane scrollPane1;
    private JTextArea textArea2;
    private JPanel hSpacer2;
    private JPanel vSpacer1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
