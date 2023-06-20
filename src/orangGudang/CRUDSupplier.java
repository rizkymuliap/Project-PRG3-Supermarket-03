package orangGudang;

import connection.DBConnect;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CRUDSupplier extends JFrame {
    private JPanel JPSupplier;
    private JTextField txtNamaSupplier;
    private JTextField txtNotelp;
    private JButton btnSave;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnCancel;
    private JTextField txtSearch;
    private JButton btnSearch;
    private JTable tblSupplier;
    private JTextArea txtAlamat;
    private JTextField txtEmail;
    private JButton btnRefresh;
    private final int MAX_CHARACTERS = 50;

    private DefaultTableModel model;

    //variabel yang akan digunakan di kodingan
    String namaSupplier, email, alamat, notelp, id_supplier;

    public void FrameConfig(){ //Con
        add(this.JPSupplier);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    public CRUDSupplier() {
        FrameConfig(); //Menggunakan konfigurasi panel

        model = new DefaultTableModel();
        tblSupplier.setModel(model);

        //Menetapkan beberapa button di nonaktifkan
        btnDelete.setEnabled(false);
        btnUpdate.setEnabled(false);

        addColumn();
        loadData();

        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //mengambil nilai variabel dari textbox
                namaSupplier = txtNamaSupplier.getText();
                email = txtEmail.getText();
                notelp = txtNotelp.getText();
                alamat = txtAlamat.getText();


                //Validasi agar tidak ada nama jenis barang yang sama
                boolean found = false; //inisiasi awal kalau nama yang di input tidak sama

                //Mengambil jumlah baris pada table
                int baris = tblSupplier.getModel().getRowCount();

                for(int awal = 0; awal < baris; awal++) //Mengulang pengecekan dari awal sampai jumlah baris
                {
                    //Mengecek apakah nama jenis yang dimasukkan sama dengan nama pada kolom tertentu
                    if(namaSupplier.toLowerCase().equals(model.getValueAt(awal, 1).toString().toLowerCase()))
                    {
                        found = true; //Menemukan data yang sama
                    }
                }

                if(found) //Jika menemukan data yang sama pada tabel
                {
                    JOptionPane.showMessageDialog(null, "Data Supplier sudah ada!", "Information!",
                            JOptionPane.INFORMATION_MESSAGE); //Menampilkan pesan

                }else{
                    if (txtNamaSupplier.getText().equals("") || txtAlamat.getText().equals("") || txtEmail.getText().equals("") || txtNotelp.getText().equals("")) //Mengecek apakah txtbox kosong agar tidak ada data kosong
                    {
                        JOptionPane.showMessageDialog(null, "Tolong, isikan semua data!", "Warning!"
                                , JOptionPane.WARNING_MESSAGE); //Jika kosong maka akan menampilkan pesan data tidak boleh kosong

                    } else {
                        try {

                            Boolean valid_no_telp = validateInput(notelp);

                            if(!valid_no_telp){  //Melakukan cek apakah inputan no_telp valid
                                JOptionPane.showMessageDialog(null, "Nomor telepon harus dalam format 628xxx"
                                        , "Warning!", JOptionPane.WARNING_MESSAGE);
                                txtNotelp.setText("");
                                txtNotelp.requestFocus();
                                return;
                            }

                            Boolean valid_email = validateEmail(email);

                            if(!valid_email){  //Melakukan cek apakah inputan email valid
                                JOptionPane.showMessageDialog(null, "Inputan email tidak sesuai dengan format"
                                        , "Warning!", JOptionPane.WARNING_MESSAGE);
                                txtEmail.setText("");
                                txtEmail.requestFocus();
                                return;
                            }

                            DBConnect connection = new DBConnect();
                            String sql = "EXEC sp_InserttblSupplier @id_supplier=?, @nama_supplier=?, @alamat=?, @no_telp=?, @email=? ,@status=1";

                            connection.pstat = connection.conn.prepareStatement(sql);

                            connection.pstat.setString(1, generateNextSupplierID()); //generate id supplier sebagai PK
                            connection.pstat.setString(2, namaSupplier); //namasupplier sebagai parameter kedua
                            connection.pstat.setString(3, alamat); //alamat sebagai parameter ketiga
                            connection.pstat.setString(4, notelp); //notelp sebagai parameter keempat
                            connection.pstat.setString(5, email); //email sebagai parameter kelima

                            connection.pstat.executeUpdate();
                            connection.pstat.close();

                            clear(); //Mengosongkan semua textbox

                            JOptionPane.showMessageDialog(null, "Data Supplier berhasil disimpan!", "Informasi",
                                    JOptionPane.INFORMATION_MESSAGE); //Menampilkan pesan berhasil input data Supplier
                            loadData();
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                }
            }
        });


        txtNamaSupplier.addKeyListener(new KeyAdapter() { //Beertujuan untuk cek inputan pada textbox nama supplier
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                if (txtNamaSupplier.getText().length() > MAX_CHARACTERS) { //Jika inputan lebih dari max char > 50
                    e.consume(); // Mengkonsumsi event jika jumlah karakter lebih dari 50
                }

                char c = e.getKeyChar(); //Mengambil huruf yang saat ini diinput
                if (Character.isDigit(c)) { //cek apakah inputan angka
                    e.consume(); // Jika iya Mengkonsumsi event jika karakter yang diketik adalah angka
                }
            }
        });

        txtAlamat.addKeyListener(new KeyAdapter() { //Melakukan cek inputan Alamat supplier
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                if (txtAlamat.getText().length() > 100) { //Jika inputan lebih dari 100
                    e.consume(); // Mengkonsumsi event jika jumlah karakter lebih dari 100
                }
            }
        });

        txtEmail.addKeyListener(new KeyAdapter() { //Melakukan cek inputan Alamat supplier
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                if (txtAlamat.getText().length() > MAX_CHARACTERS) { //Jika inputan lebih dari Max char
                    e.consume(); // Mengkonsumsi event jika jumlah karakter lebih dari 50
                }
            }
        });

        tblSupplier.addMouseListener(new MouseAdapter() { //Untuk mengatur agar saat di klik
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int i = tblSupplier.getSelectedRow();
                if(i == -1){
                    return;
                }
                txtNamaSupplier.setText((String) model.getValueAt(i,1));
                txtAlamat.setText((String) model.getValueAt(i,2));
                txtNotelp.setText((String) model.getValueAt(i,3));
                txtEmail.setText((String) model.getValueAt(i,4));

                //Mengatur agar beberapa button diaktifkan
                btnSave.setEnabled(false); //dimatikan
                btnUpdate.setEnabled(true); //diaktifkan
                btnDelete.setEnabled(true); //diaktifkan
            }
        });
        btnCancel.addActionListener(new ActionListener() { //Event saat mengklik btncancel
            @Override
            public void actionPerformed(ActionEvent e) {
                loadData(); //melakukan load data
                clear(); //Melakukan Clear kepada seluruh textbox

                //Mengatur agar beberapa button diaktifkan
                btnSave.setEnabled(true);
                btnUpdate.setEnabled(false);
                btnDelete.setEnabled(false);
            }
        });

        btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean found = false;
                // validasi tidak boleh sama
                Object[] obj = new Object[1];
                obj[0] = txtNamaSupplier.getText();


                if(found) {
                    JOptionPane.showMessageDialog(null, "Data supplier sudah ada!", "Information"
                            , JOptionPane.INFORMATION_MESSAGE); //Jika Sudah diinput
                } else{
                    try {
                        if (txtNamaSupplier.getText().equals("") || txtAlamat.getText().equals("") || txtEmail.getText().equals("") || txtNotelp.getText().equals(""))
                            JOptionPane.showMessageDialog(null, "Tolong, isikan semua data!", "Warning!",
                                    JOptionPane.WARNING_MESSAGE);
                        else {
                            try {
                                int i = tblSupplier.getSelectedRow();
                                if (i == -1) return;
                                id_supplier = String.valueOf(model.getValueAt(i, 0));
                                namaSupplier = txtNamaSupplier.getText();
                                alamat = txtAlamat.getText();
                                notelp = txtNotelp.getText();

                                //validasi inputan no telp
                                Boolean valid_no_telp = validateInput(notelp);
                                if(!valid_no_telp){  //Melakukan cek apakah inputan no_telp valid
                                    JOptionPane.showMessageDialog(null, "Nomor telepon harus dalam format 628xxx"
                                            , "Warning!", JOptionPane.WARNING_MESSAGE);
                                    txtNotelp.setText("");
                                    txtNotelp.requestFocus();
                                    return;
                                }

                                email = txtEmail.getText();

                                DBConnect connection = new DBConnect();

                                String query = "EXEC sp_UpdatetblSupplier @id_supplier=?, @nama_supplier=?, @alamat=?, @no_telp=?, @email=?";
                                connection.pstat = connection.conn.prepareStatement(query);
                                connection.pstat.setString(1, id_supplier);
                                connection.pstat.setString(2, namaSupplier);
                                connection.pstat.setString(3, alamat);
                                connection.pstat.setString(4, notelp);
                                connection.pstat.setString(5, email);

                                connection.stat.close();
                                connection.pstat.executeUpdate();
                                connection.pstat.close();

                                clear();
                                JOptionPane.showMessageDialog(null, "Data updated successfully!");
                                loadData();

                                btnUpdate.setEnabled(false);
                                btnDelete.setEnabled(false);
                                btnSave.setEnabled(true);

                            } catch (NumberFormatException nex) {
                                JOptionPane.showMessageDialog(null, "Please, enter the valid number.");
                            } catch (Exception e1) {
                                JOptionPane.showMessageDialog(null, "an error occurred while updating data into the database.\n" + e1);
                            }
                        }
                    } catch(Exception e1){

                    }
                }
            }
        });
        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Menampilkan kotak dialog konfirmasi
                int option = JOptionPane.showConfirmDialog(null, "Apakah Anda yakin ingin menghapus data?", "Konfirmasi Penghapusan Data", JOptionPane.YES_NO_OPTION);

                // Menggunakan hasil pilihan dari kotak dialog
                if (option == JOptionPane.YES_OPTION) {
                    // Proses penghapusan data
                    try {
                        DBConnect connection = new DBConnect(); //Membuat object koneksi baru

                        int i = tblSupplier.getSelectedRow(); //mengambil nilai baris yang di klik

                        if (i == -1) return; //Jika tidak ada data dari table yang dipilih
                        id_supplier = String.valueOf(model.getValueAt(i, 0)); //mengambil nilai id dari kolom pertama daribaris yang dipilih


                        String query = "EXEC sp_HapusSupplier @id_supplier=?";
                        connection.pstat = connection.conn.prepareStatement(query);
                        connection.pstat.setString(1, id_supplier); //variabel id dari

                        connection.stat.close();
                        connection.pstat.executeUpdate();
                        connection.pstat.close();

                        clear();
                        JOptionPane.showMessageDialog(null, "Hapus Supplier Berhasil!", "Informasi", JOptionPane.INFORMATION_MESSAGE);
                        loadData();

                        btnUpdate.setEnabled(false);
                        btnDelete.setEnabled(false);
                        btnSave.setEnabled(true);

                    } catch (Exception e1) {
                        JOptionPane.showMessageDialog(null, "an error occurred while updating data into the database.\n" + e1);
                    }


                } else {
                    // Tidak melakukan penghapusan data
                    JOptionPane.showMessageDialog(null, "Supplier batal dihapus!", "Informasi", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        btnRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadData();
            }
        });
    }

        public String generateNextSupplierID() {
            DBConnect connection = new DBConnect();
            try {
                connection.pstat = connection.conn.prepareStatement("SELECT TOP 1 id_supplier FROM tblSupplier ORDER BY id_supplier DESC");
                connection.result = connection.pstat.executeQuery();

                if (connection.result.next()) {
                    String lastItemID = connection.result.getString("id_supplier");
                    int lastNumber = Integer.parseInt(lastItemID.substring(3));
                    int nextNumber = lastNumber + 1;

                    String nextItemID = "SPL" + String.format("%03d", nextNumber);
                    return nextItemID;
                } else {
                    return "SPL001";
                }
            } catch (Exception e) {
                System.out.println("Terjadi error saat memeriksa id_supplier terakhir: " + e);
            } finally {
                try {
                    if (connection.result != null) {
                        connection.result.close();
                    }
                    if (connection.pstat != null) {
                        connection.pstat.close();
                    }
                    if (connection.conn != null) {
                        connection.conn.close();
                    }
                } catch (SQLException ex) {
                    System.out.println("Terjadi error saat menutup koneksi: " + ex);
                }
            }

            return null;
        }
    public void clear()
    {
        txtNamaSupplier.setText("");
        txtAlamat.setText("");
        txtEmail.setText("");
        txtNotelp.setText("");
    }

    public void addColumn(){
        model.addColumn("ID Supplier");
        model.addColumn("Nama Supplier");
        model.addColumn("Alamat");
        model.addColumn("No. Telepon");
        model.addColumn("Email");
    }

    public void loadData()
    {
        model.getDataVector().removeAllElements();
        model.fireTableDataChanged();

        try{
            DBConnect connection = new DBConnect();
            connection.stat = connection.conn.createStatement();
            String query = "EXEC sp_LoadSupplier";
            connection.result = connection.stat.executeQuery(query);

            while(connection.result.next()){
                if(connection.result.getInt("status") == 1) { //Mengecek apakah jenis barang masih tersedia
                    Object[] obj = new Object[5];
                    obj[0] = connection.result.getString("id_supplier"); //Mengambil ID
                    obj[1] = connection.result.getString("nama_supplier"); //Mengambil nama
                    obj[2] = connection.result.getString("alamat"); //Mengambil nama
                    obj[3] = connection.result.getString("no_telp"); //Mengambil nama
                    obj[4] = connection.result.getString("email"); //Mengambil nama
                    model.addRow(obj);
                }
            }
            connection.stat.close();
            connection.result.close();
        }catch (Exception ex){
            JOptionPane.showMessageDialog(null, "Eror while loading for data : "+ex);
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

    public static void main(String[] args) {
        new CRUDSupplier().setVisible(true);
    }

}
