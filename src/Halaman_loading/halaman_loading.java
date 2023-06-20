package Halaman_loading;

import javax.swing.*;

public class halaman_loading extends JFrame {
    private JProgressBar progressBar1;
    private JLabel load;
    private JPanel loadpan;
    private JLabel tulisanload;

    public halaman_loading() {
        add(loadpan);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);

        loading();
    }

    public void loading(){
        int a = 0;

        while(a <= 100) {
            try {
                Thread.sleep(40);
                progressBar1.setValue(a);
                tulisanload.setText("LOADING......(" + (a) + "%)");
                a++;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        dispose();
    }

    public static void main(String[] args) {
        new halaman_loading();
        new login.halaman_login().setVisible(true);
    }

}
