package login;

import connection.DBConnect;
import jdk.nashorn.internal.scripts.JO;

import javax.swing.*;
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

                if (valid){
                    JOptionPane.showMessageDialog(halaman_utama, "Selamat Datang di Program Pelatihan Java", "Information", JOptionPane.INFORMATION_MESSAGE);

                    dispose();




                    if(value[3].equals("admin")){
                    }else if (value[3].equals("kasir")){
                        dispose();


                    }else if (value[3].equals("manajer")){
                        dispose();

                    }
                }
            }
        });
    }

    public String[] validasi(){
        if(txtUsername.getText().isEmpty()|| txtPassword.getText().isEmpty()){
            JOptionPane.showMessageDialog(halaman_utama,"Username / Password Kosong", "Peringatan", JOptionPane.WARNING_MESSAGE);
        }else {

            try {
                DBConnect connection = new DBConnect();

                connection.stat = connection.conn.createStatement();

                String query = "SELECT * FROM tblKaryawan WHERE username = '" +txtUsername.getText()+"' and password = '" +txtPassword.getText()+"'";

                connection.result = connection.stat.executeQuery(query);

                if(!connection.result.next()){
                    throw  new Exception("Pengguna Tidak Ditemukan");
                }
                JOptionPane.showMessageDialog(null, connection.result.getString(1));

                String id_karyawan = connection.result.getString(1);
                String password = connection.result.getString(2);
                String nama = connection.result.getString(3);
                String jabatan = connection.result.getString(4);

                return new String[] {"true", id_karyawan,password,nama,jabatan};
            }catch (Exception ex){

                System.out.println(ex.getMessage());
                JOptionPane.showMessageDialog(halaman_utama,ex.getMessage(), "Peringatan",JOptionPane.WARNING_MESSAGE);
            }
        }
        return new String[] {"false"};
    }

    public static void main(String[] args) {
        new halaman_login().setVisible(true);
    }
}
