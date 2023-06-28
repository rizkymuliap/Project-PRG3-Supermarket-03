package Admin;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HalamanAdmin extends JFrame {

    private JPanel Panel_Bantuan_Akun;
    private JLabel Label_Gambar_2;
    private JLabel Label_Nama;
    private JLabel Label_Jabatan;
    private JPanel Panel_Tengah;
    private JLabel Label_Salam;
    private JPanel Panel_Bantuan;
    private JButton shoesButton;
    private JButton eventButton;
    private JButton memberTypeButton;
    private JButton payMethodButton;
    private JButton BundleButton;
    private JPanel AdminMenu;
    private JPanel PanelKiri;
    private JPanel PanelKiriAtas;
    private JPanel PanelButton;
    private JPanel PanelForm;
    private JButton karyawanButton;
    private JLabel label_nama;
    private JButton employeeButton;
    private JButton btLogOut;
    private JButton empType;

    public void FrameConfig() {
        add(AdminMenu);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    public HalamanAdmin(String[] value) {
        FrameConfig();

        Label_Nama.setText(value[2]);
        Label_Jabatan.setText(value[3]);
        karyawanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                label_nama.setText("KARYAWAN MENU");
                PanelForm.removeAll();
                PanelForm.revalidate();
                PanelForm.repaint();
                CRUDKaryawan show = new CRUDKaryawan();
                show.JPKaryawan.setVisible(true);
                PanelForm.revalidate();
                PanelForm.setLayout(new java.awt.BorderLayout());
                PanelForm.add(show.JPKaryawan);

            }
        });
        eventButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                label_nama.setText("EVENT MENU");
                PanelForm.removeAll();
                PanelForm.revalidate();
                PanelForm.repaint();
                CRUDBundle show = new CRUDBundle();
                show.JPBundle.setVisible(true);
                PanelForm.revalidate();
                PanelForm.setLayout(new java.awt.BorderLayout());
                PanelForm.add(show.JPBundle);
            }
        });
        memberTypeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        });
        payMethodButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                label_nama.setText("PAYMENT METHOD MENU");
            }
        });
        BundleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                label_nama.setText("BUNDLE MENU");
                PanelForm.removeAll();
                PanelForm.revalidate();
                PanelForm.repaint();
                CRUDBundle show = new CRUDBundle();
                show.JPBundle.setVisible(true);
                PanelForm.revalidate();
                PanelForm.setLayout(new java.awt.BorderLayout());
                PanelForm.add(show.JPBundle);
            }
        });
        shoesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                label_nama.setText("SHOES MENU");
            }
        });
        employeeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                label_nama.setText("EMPLOYEE MENU");
                PanelForm.removeAll();
                PanelForm.revalidate();
                PanelForm.repaint();
                PanelForm.revalidate();
                PanelForm.setLayout(new java.awt.BorderLayout());
            }
        });
        btLogOut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();

            }
        });
        empType.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                label_nama.setText("EMPLOYEE TYPE MENU");
                PanelForm.removeAll();
                PanelForm.revalidate();
                PanelForm.repaint();

                PanelForm.revalidate();
                PanelForm.setLayout(new java.awt.BorderLayout());
                //PanelForm.add(show.JPJabatan);
            }
        });
    }
    public static void main(String[] args) {
        String[] data = {"123", "123", "123", "123", "123"};
        HalamanAdmin halamanAdmin = new HalamanAdmin(data);
        halamanAdmin.setVisible(true); // Menampilkan jendela HalamanAdmin
    }
}

