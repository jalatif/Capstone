import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
/*
 * Created by JFormDesigner on Thu Apr 11 00:32:46 IST 2013
 */



/**
 * @author Jalatif
 */
public class AvailableUsers extends JFrame {
    Container contentPane;
    private String toUser = "";

    //JButton bts[] = new JButton[20];
    public AvailableUsers() {
        initComponents();
    }

    protected void setUsers(String[] users){
        list1.setListData(users);
    }

    protected String getToUser(){
        return toUser;
    }
    private void list1MouseClicked(MouseEvent e) {
        // TODO add your code here
        toUser = (String)list1.getSelectedValue();
        CCH1.toUser = this.toUser;
    }

    /*public void AddButton(int row, String text){
        bts[row - 1] = new JButton(text);
        contentPane.add(bts[row - 1], CC.xy(1, row));
        pack();
        setLocationRelativeTo(getOwner());
    }*/

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        scrollPane1 = new JScrollPane();
        list1 = new JList();

        //======== this ========
        setTitle("Available Users");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Container contentPane = getContentPane();
        contentPane.setLayout(new FormLayout(
            "115dlu",
            "default"));

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
        contentPane.add(scrollPane1, CC.xy(1, 1));
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JScrollPane scrollPane1;
    private JList list1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    public static void main(String args[]){
        AvailableUsers au = new AvailableUsers();
        au.setVisible(true);
        au.list1.setListData(new String[0]);
        String msg = "RofM~*~@jalatif&2013-04-11 17:14:28.0&Asdads asd RofM~*~@jalatif&2013-04-11 17:14:29.0&Asdads asd RofM~*~@jalatif&2013-04-11 17:14:30.0&Asdads asd RofM~*~@jalatif&2013-04-11 17:14:30.0&Asdads asd";
        String[] msp = msg.split("RofM[~][*][~][@]");
        for (String c : msp)
            System.out.println(c);
        //System.out.println(au.getClass().getClassLoader().getResource("res/notification.wav").toString());
        //File soundFile = new File(AvailableUsers.class.getResource("res/notification.wav"));
        Clip clip = null;
        try {
            clip = AudioSystem.getClip();
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(au.getClass().getClassLoader().getResourceAsStream("res/notification.wav"));//AudioSystem.getAudioInputStream(soundFile);
            clip.open(inputStream);
            clip.stop();
            clip.setFramePosition(0);
            clip.start();
        } catch (LineUnavailableException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        String uiMsg = "jalatif said : iithissgggcry FC";
        int loco = uiMsg.indexOf(':') + 1;
        String process = uiMsg.substring(loco + 1, loco + 2).toUpperCase();
        System.out.println("Processing = " + process + "b");
        String cmd = "asd asd ad /n";

    }
}
