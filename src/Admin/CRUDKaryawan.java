package Admin;

import connection.DBConnect;
import sun.security.pkcs11.Secmod;

import javax.jws.soap.SOAPBinding;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CRUDKaryawan {
    private JPanel JPKaryawan;
    private JTextField txtTelp;
    private JTextField txtEmail;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JTextField txtJenisKelamin;
    private JTextField txtAlamat;
    private JButton btnSave;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnClear;
    private JTextField txtSearch;
    private JTable TabelData;
    private JTextField txtNama;
    private JButton btnRefresh;
    private JComboBox comboBox1;
    private final int MAX_CHARACTERS = 50;


    private DefaultTableModel Model;

    DBConnect connect = new DBConnect();

    String Nama;
    String JenisKelamin;
    String Notelp;
    String Alamat;
    String Email;
    String Username;
    String Password;
    String id_karyawan;

    public static void main(String[] args) {
        JFrame frame = new JFrame("CRUDKaryawan");
        frame.setContentPane(new CRUDKaryawan().JPKaryawan);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setSize(500, 500);
        frame.setVisible(true);
    }

    public CRUDKaryawan()
    {
        Model = new DefaultTableModel();
        TabelData.setModel(Model);
        addColomn();
        loadData();

        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Objects.equals(txtNama.getText(), "") || txtJenisKelamin.getText().equals("") || (txtTelp.getText().equals("")) || (txtAlamat.getText().equals("")) || (txtEmail.getText().equals("")) || Objects.equals(txtUsername.getText(), "") || Objects.equals(txtTelp.getText(), "") || Objects.equals(txtPassword.getText(), "")){
                    JOptionPane.showMessageDialog(null, "Please, fill in all data!");
                }
                else
                {
                    Nama = txtNama.getText();
                    JenisKelamin = txtJenisKelamin.getText();
                    Notelp = txtTelp.getText();
                    boolean valid = validateInput(Notelp);
                    if (!valid)
                    {
                        JOptionPane.showMessageDialog(null, "No telp harus 628XXX");
                        txtTelp.setText("");
                        txtTelp.requestFocus();
                        return;
                    }
                    Alamat = txtAlamat.getText();
                    Email = txtEmail.getText();
                    boolean valid2 = validateEmail(Email);
                    if(!valid2)
                    {
                        JOptionPane.showMessageDialog(null, "No Email Harus Menggunakan a@b.c");
                        txtEmail.setText("");
                        txtEmail.requestFocus();
                        return;
                    }
                    Username = txtUsername.getText();
                    Password = txtPassword.getText();

                    try
                    {
                        String sql = "INSERT tblKaryawan VALUE (?,?,?,?,?,?,?,?)";
                        connect.pstat = connect.conn.prepareStatement(sql);
                        connect.pstat.setString(0, generateNextSupplierID());
                        connect.pstat.setString(1, Nama);
                        connect.pstat.setString(2, JenisKelamin);
                        connect.pstat.setString(3, Notelp);
                        connect.pstat.setString(4, Alamat);
                        connect.pstat.setString(5, Email);
                        connect.pstat.setString(6, Username);
                        connect.pstat.setString(7, Password);

                        connect.stat.close();
                        connect.pstat.executeUpdate();
                        connect.pstat.close();

                        JOptionPane.showMessageDialog(null, "Data Berhasil ditambahkan!!");
                        loadData();
                    }
                    catch (Exception ex)
                    {
                        JOptionPane.showMessageDialog(null, "Eror saat Menyimpan kedalam database.\n" + ex);
                    }
                }
            }
        });
        txtNama.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                char c = e.getKeyChar();
                if (((c < 'a') || (c > 'z')) && ((c < 'A') || (c > 'Z')) && (c != KeyEvent.VK_BACK_SPACE)
                        && (c != KeyEvent.VK_SPACE) && (c != KeyEvent.VK_PERIOD)) {
                    e.consume();
                }
            }
        });
        btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Objects.equals(txtNama.getText(), "") || txtJenisKelamin.getText().equals("") || (txtTelp.getText().equals("")) || (txtAlamat.getText().equals("")) || (txtEmail.getText().equals("")) || Objects.equals(txtUsername.getText(), "") || Objects.equals(txtTelp.getText(), "") || Objects.equals(txtPassword.getText(), "")){
                    JOptionPane.showMessageDialog(null, "Please, fill in all data!");
                }
                else
                {
                    Nama = txtNama.getText();
                    JenisKelamin = txtJenisKelamin.getText();
                    Notelp = txtTelp.getText();
                    boolean valid = validateInput(Notelp);
                    if (!valid)
                    {
                        JOptionPane.showMessageDialog(null, "No telp harus 628XXX");
                        txtTelp.setText("");
                        txtTelp.requestFocus();
                        return;
                    }
                    Alamat = txtAlamat.getText();
                    Email = txtEmail.getText();
                    boolean valid2 = validateEmail(Email);
                    if(!valid2)
                    {
                        JOptionPane.showMessageDialog(null, "No Email Harus Menggunakan a@b.c");
                        txtEmail.setText("");
                        txtEmail.requestFocus();
                        return;
                    }
                    Username = txtUsername.getText();
                    Password = txtPassword.getText();

                    try
                    {
                        String sql1 ="UPDATE tblKaryawan SET Nama = ?, JenisKelamin = ?, Notelp = ?, Alamat = ?," +
                                "Email = ?, Username = ?, Password = ? WHERE id_karyawan = ?";
                        connect.result = connect.stat.executeQuery(sql1);
                        connect.pstat.setString(1, Nama);
                        connect.pstat.setString(2, JenisKelamin);
                        connect.pstat.setString(3, Notelp);
                        connect.pstat.setString(4, Alamat);
                        connect.pstat.setString(5, Email);
                        connect.pstat.setString(6, Username);
                        connect.pstat.setString(7, Password);
                        connect.pstat.setString(8, id_karyawan);


                        connect.pstat.executeUpdate();
                        connect.pstat.close();
                    }
                    catch (Exception ex)
                    {
                        System.out.println("Error saat Update Karyawan : " + ex);
                    }

                    JOptionPane.showMessageDialog(null, "Update Data Karyawan berhasil !!");
                }
            }
        });
        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int opsi;
                if (Objects.equals(txtNama.getText(), "") || txtJenisKelamin.getText().equals("") || (txtTelp.getText().equals("")) || (txtAlamat.getText().equals("")) || (txtEmail.getText().equals("")) || Objects.equals(txtUsername.getText(), "") || Objects.equals(txtTelp.getText(), "") || Objects.equals(txtPassword.getText(), "")){
                    JOptionPane.showMessageDialog(null, "Please, fill in all data!");
                }else{
                    try {
                        int kode = TabelData.getSelectedRow();
                        opsi = JOptionPane.showConfirmDialog(null, "Are you sure delete this data?",
                                "Confirmation", JOptionPane.YES_OPTION, JOptionPane.WARNING_MESSAGE);
                        if (opsi != 0) {
                            JOptionPane.showMessageDialog(null, "Data failed to delete");
                        } else {
                            id_karyawan = String.valueOf(Model.getValueAt(kode, 0));
                            String query = "EXEC sp_DeleteKaryawan @id_karyawan=?";
                            connect.pstat = connect.conn.prepareStatement(query);
                            connect.pstat.setString(1, id_karyawan);
                            connect.pstat.executeUpdate();
                            connect.pstat.close();
                        }
                    } catch (NumberFormatException nex){
                        JOptionPane.showMessageDialog(null, "Please, enter the valid number ."+nex.getMessage());
                    } catch (Exception e1){
                        JOptionPane.showMessageDialog(null, "an error occurred while deleting data into the database.\n" + e1);
                    }

                    JOptionPane.showMessageDialog(null, "Data deleted successfully!");
                    loadData();
                }
            }
        });
        txtNama.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                if (txtNama.getText().length() > MAX_CHARACTERS) { //Jika inputan lebih dari max char > 50
                    e.consume(); // Mengkonsumsi event jika jumlah karakter lebih dari 50
                }

                char c = e.getKeyChar(); //Mengambil huruf yang saat ini diinput
                if (Character.isDigit(c)) { //cek apakah inputan angka
                    e.consume(); // Jika iya Mengkonsumsi event jika karakter yang diketik adalah angka
                }
            }
        });

        txtTelp.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                char c = e.getKeyChar(); //Mengambil huruf yang saat ini diinput
                if (!Character.isDigit(c)) { //cek apakah inputan angka
                    e.consume(); // Jika iya Mengkonsumsi event jika karakter yang diketik adalah angka
                }
            }
        });
        txtEmail.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                if (txtEmail.getText().length() > MAX_CHARACTERS) { //Jika inputan lebih dari Max char
                    e.consume(); // Mengkonsumsi event jika jumlah karakter lebih dari 50
                }
            }
        });
    }

    public void addColomn(){
        Model.addColumn("ID Karyawan");
        Model.addColumn("Nama Karyawan");
        Model.addColumn("No Telp");
        Model.addColumn("Alamat");
        Model.addColumn("Email");
        Model.addColumn("Username");
        Model.addColumn("Password");
    }

    public String generateNextSupplierID() {
        DBConnect connection = new DBConnect();
        try {
            connection.pstat = connection.conn.prepareStatement("SELECT TOP 1 id_karyawan FROM tblKaryawan ORDER BY id_karyawan DESC");
            connection.result = connection.pstat.executeQuery();

            if (connection.result.next()) {
                String lastItemID = connection.result.getString("id_karyawan");
                int lastNumber = Integer.parseInt(lastItemID.substring(3));
                int nextNumber = lastNumber + 1;

                String nextItemID = "KRW" + String.format("%03d", nextNumber);
                return nextItemID;
            } else {
                return "KRW001";
            }
        } catch (Exception e) {
            System.out.println("Terjadi error saat memeriksa id_supplier terakhir: " + e);
        } finally {
            try {
                if (connect.result != null) {
                    connect.result.close();
                }
                if (connect.pstat != null) {
                    connect.pstat.close();
                }
                if (connect.conn != null) {
                    connect.conn.close();
                }
            } catch (SQLException ex) {
                System.out.println("Terjadi error saat menutup koneksi: " + ex);
            }
        }

        return null;
    }

    public void loadData(){
        Model.getDataVector().removeAllElements();
        Model.fireTableDataChanged();

        try{
            connect.stat = connect.conn.createStatement();
            String query = "EXEC sp_LoadKaryawan";
            connect.result = connect.stat.executeQuery(query);

            while(connect.result.next()){
                String temp = connect.result.getString("password");

                Object[] obj = new Object[8];
                obj[0] = connect.result.getString("id_karyawan");
                obj[1] = connect.result.getString("nama_karyawan");
                obj[2] = connect.result.getString("jenis_kelamin");
                obj[3] = connect.result.getString("no_telp");
                obj[4] = connect.result.getString("email");
                obj[5] = connect.result.getString("alamat");
                obj[6] = connect.result.getString("username");
                obj[7] = connect.result.getString("password");

                Model.addRow(obj);
            }
            connect.stat.close();
            connect.result.close();
            btnSave.setEnabled(true);
            btnUpdate.setEnabled(false);
            btnDelete.setEnabled(false);
        }
        catch(Exception e){
            JOptionPane.showMessageDialog(null, "an error occurred while loading data.\n" + e);
        }
    }

    public static boolean validateInput(String input) { //Digunakan untuk validasi inputan agar berformat 628
        // Regex pattern untuk memvalidasi format input
        String regexPattern = "^628\\d{1,14}$";

        // Membuat objek Pattern dari regex pattern
        Pattern pattern = Pattern.compile(regexPattern);

        // Mencocokkan input dengan pattern menggunakan Matcher
        Matcher matcher = pattern.matcher(input);

        // Mengembalikan true jika input cocok dengan pattern, false jika tidak cocok
        return matcher.matches();
    }

    public static boolean validateEmail(String email) {
        // Regex pattern untuk validasi email
        String regexPattern = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

        // Membuat objek Pattern dari regex pattern
        Pattern pattern = Pattern.compile(regexPattern);

        // Mencocokkan email dengan pattern menggunakan Matcher
        Matcher matcher = pattern.matcher(email);

        // Mengembalikan true jika email cocok dengan pattern, false jika tidak cocok
        return matcher.matches();
    }
}