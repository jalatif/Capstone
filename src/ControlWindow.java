import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
/*
 * Created by JFormDesigner on Wed May 08 22:02:34 IST 2013
 */



/**
 * @author Jalatif
 */
public class ControlWindow extends JFrame {
    private ChatWindow cw;
    public ControlWindow() {
        initComponents();
        //initImages();
    }

    public ControlWindow(String user, ChatWindow cw){
        initComponents();
        //initImages();
        setTitle(user + "'s controls");
        setLocation(200, 200);
        this.cw = cw;
        //System.out.println("DeVil " + user + " " + cw.getInfo());

    }

    private void buttonMouseClicked(MouseEvent e) {
        // TODO add your code here
        Object bt = e.getSource();
        String cmd = "";
        if (bt.equals(bUp)){System.out.println("Up"); cmd = "Forward";}
        else if (bt.equals(bLeft)){System.out.println("Left"); cmd = "Left";}
        else if (bt.equals(bRight)){System.out.println("Right"); cmd = "Right";}
        else if (bt.equals(bDown)){System.out.println("Down"); cmd = "Backward";}
        else if (bt.equals(bStop)){System.out.println("Stop"); cmd = "Stop";}
        else if (bt.equals(bFire)){System.out.println("Fire"); cmd = "PumpExtenguisher";}
        else if (bt.equals(bGun)){System.out.println("Gun"); cmd = "GunFire";}
        else if (bt.equals(bClean)){System.out.println("Clean"); cmd = "Cleaner";}
        else {System.out.println("Not Recognized");}
        if (cw != null)
            cw.processControl(cmd);
    }

    private void thisWindowClosed(WindowEvent e) {
        // TODO add your code here
        this.setVisible(false);
        System.out.println("Closing off Related Things");
        //if (cw != null)
        //    cw.setVisible(false);
    }

    private void initImages(){
        bUp.setIcon(new ImageIcon(ControlWindow.class.getClassLoader().getResource("res/Up.png")));
        bDown.setIcon(new ImageIcon(ControlWindow.class.getClassLoader().getResource("res/Down.png")));
        bRight.setIcon(new ImageIcon(ControlWindow.class.getClassLoader().getResource("res/Right.png")));
        bLeft.setIcon(new ImageIcon(ControlWindow.class.getClassLoader().getResource("res/Left.png")));
        bStop.setIcon(new ImageIcon(ControlWindow.class.getClassLoader().getResource("res/Stop.png")));
        bClean.setIcon(new ImageIcon(ControlWindow.class.getClassLoader().getResource("res/Clean.png")));
        bGun.setIcon(new ImageIcon(ControlWindow.class.getClassLoader().getResource("res/Gun.png")));
        bFire.setIcon(new ImageIcon(ControlWindow.class.getClassLoader().getResource("res/Fire.png")));

    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        vSpacer2 = new JPanel(null);
        bUp = new JButton();
        hSpacer1 = new JPanel(null);
        bLeft = new JButton();
        bStop = new JButton();
        bRight = new JButton();
        hSpacer2 = new JPanel(null);
        bDown = new JButton();
        bClean = new JButton();
        bGun = new JButton();
        bFire = new JButton();
        vSpacer3 = new JPanel(null);

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                thisWindowClosed(e);
            }
        });
        Container contentPane = getContentPane();
        contentPane.setLayout(new FormLayout(
            "9*(default, $lcgap), default",
            "7*(default, $lgap), default"));
        contentPane.add(vSpacer2, CC.xywh(11, 1, 1, 3));

        //---- bUp ----
        bUp.setIcon(new ImageIcon(getClass().getResource("/res/Up.png")));
        bUp.setBorderPainted(false);
        bUp.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                buttonMouseClicked(e);
            }
        });
        contentPane.add(bUp, CC.xy(11, 5));
        contentPane.add(hSpacer1, CC.xywh(1, 7, 5, 1));

        //---- bLeft ----
        bLeft.setIcon(new ImageIcon(getClass().getResource("/res/Left.png")));
        bLeft.setBorderPainted(false);
        bLeft.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                buttonMouseClicked(e);
            }
        });
        contentPane.add(bLeft, CC.xy(9, 7));

        //---- bStop ----
        bStop.setIcon(new ImageIcon(getClass().getResource("/res/Stop.png")));
        bStop.setBorderPainted(false);
        bStop.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                buttonMouseClicked(e);
            }
        });
        contentPane.add(bStop, CC.xy(11, 7));

        //---- bRight ----
        bRight.setIcon(new ImageIcon(getClass().getResource("/res/Right.png")));
        bRight.setBorderPainted(false);
        bRight.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                buttonMouseClicked(e);
            }
        });
        contentPane.add(bRight, CC.xy(13, 7));
        contentPane.add(hSpacer2, CC.xywh(16, 7, 4, 1));

        //---- bDown ----
        bDown.setIcon(new ImageIcon(getClass().getResource("/res/Down.png")));
        bDown.setBorderPainted(false);
        bDown.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                buttonMouseClicked(e);
            }
        });
        contentPane.add(bDown, CC.xy(11, 9));

        //---- bClean ----
        bClean.setIcon(new ImageIcon(getClass().getResource("/res/Clean.png")));
        bClean.setBorderPainted(false);
        bClean.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                buttonMouseClicked(e);
            }
        });
        contentPane.add(bClean, CC.xy(9, 11));

        //---- bGun ----
        bGun.setIcon(new ImageIcon(getClass().getResource("/res/Gun.png")));
        bGun.setBorderPainted(false);
        bGun.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                buttonMouseClicked(e);
            }
        });
        contentPane.add(bGun, CC.xy(11, 11));

        //---- bFire ----
        bFire.setIcon(new ImageIcon(getClass().getResource("/res/Fire.png")));
        bFire.setBorderPainted(false);
        bFire.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                buttonMouseClicked(e);
            }
        });
        contentPane.add(bFire, CC.xy(13, 11));
        contentPane.add(vSpacer3, CC.xywh(11, 13, 1, 3));
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel vSpacer2;
    private JButton bUp;
    private JPanel hSpacer1;
    private JButton bLeft;
    private JButton bStop;
    private JButton bRight;
    private JPanel hSpacer2;
    private JButton bDown;
    private JButton bClean;
    private JButton bGun;
    private JButton bFire;
    private JPanel vSpacer3;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    public static void main(String[] args){
        ControlWindow cw = new ControlWindow();
        cw.setVisible(true);
    }
}
