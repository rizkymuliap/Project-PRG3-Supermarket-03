package login;

import Admin.HalamanAdmin;
import connection.DBConnect;
import jdk.nashorn.internal.scripts.JO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class halaman_login extends JFrame{
    private JPanel halaman_utama;
    private JPanel panel_tengah;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JCheckBox showPasswordCheckBox;
    private JButton simpanButton;
    private JButton keluarButton;

    public halaman_login() {
        add(halaman_utama);

        setExtendedState(JFrame.MAXIMIZED_BOTH);

        setUndecorated(true);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setVisible(true);
        simpanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] value = validasi();

                Boolean valid = Boolean.parseBoolean(value[0]);

                System.out.println(value[0].toString() + " " + value[1].toString() + " " + value[2].toString() + " "+value[3].toString());


                if (valid) {
                    JOptionPane.showMessageDialog(halaman_utama, "Selamat Datang!", "Information", JOptionPane.INFORMATION_MESSAGE);
                    if (value[3].equals("Admin")) {
                        dispose();
                        new HalamanAdmin(value).setVisible(true);
                    } else if (value[3].equals("kasir")) {
                        // Handle kasir
                    } else if (value[3].equals("manajer")) {
                        // Handle manajer
                    }
                }
            }
        });

        keluarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    public String[] validasi(){
        if(txtUsername.getText().isEmpty()|| txtPassword.getText().isEmpty()){
            JOptionPane.showMessageDialog(halaman_utama,"Username / Password Kosong", "Peringatan", JOptionPane.WARNING_MESSAGE);
        }else {

            try {
                String jabatan = "";
                DBConnect connection = new DBConnect();
                connection.stat = connection.conn.createStatement();
                String query = "SELECT * FROM tblKaryawan WHERE username = '" +txtUsername.getText()+"' and password = '" +txtPassword.getText()+"'";
                connection.result = connection.stat.executeQuery(query);

                if(!connection.result.next()){
                    throw  new Exception("Pengguna Tidak Ditemukan");
                }

                String id_karyawan = connection.result.getString(1);
                String nama = connection.result.getString(2);
                switch (connection.result.getString(10)){
                    case "1" : jabatan = "Manager"; break;
                    case "2" : jabatan = "Manager Keuangan"; break;
                    case "3" : jabatan = "Admin"; break;
                    case "4" : jabatan = "Kasir"; break;
                    case "5" : jabatan = "Orang Gudang"; break;
                    case "6" : jabatan = "PJR"; break;
                }


                return new String[] {"true", id_karyawan,nama,jabatan};
            }catch (Exception ex){

                System.out.println(ex.getMessage());
                JOptionPane.showMessageDialog(halaman_utama,ex.getMessage(), "Peringatan",JOptionPane.WARNING_MESSAGE);
            }
        }
        return new String[] {"false"};
    }

    public static void main(String[] args) {

        new halaman_login().setVisible(true);

        JTextField txtUsername = new JTextField();
        txtUsername.setSize(500, 100000); // Mengatur lebar: 200px, tinggi: 30px


    }
}
