package Admin;

import connection.DBConnect;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.SQLException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CRUDKaryawan extends JFrame {

    //Deklarasi semua komponen dalam form
    public JPanel JPKaryawan;
    private JTextField txtTelp;
    private JTextField txtEmail;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    //private JTextField cbjeniskelamin;
    private JTextField txtAlamat;
    private JButton btnSave;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnClear;
    private JTextField txtSearch;
    private JTable TabelData;
    private JTextField txtNama;
    private JButton btnRefresh;
    private JComboBox cbjk;
    private JLabel gambar_barang;
    private JButton Tombol_Browse;
    private JPanel Panel_Gambar;
    private JPanel Pembatas_Gambar_Atas;
    private JPanel Pembatas_Gambar_Bawah;
    private JPanel Pembatas_Gambar_Kanan;
    private JPanel Pembatas_Gambar_Kiri;
    private JPanel Panel_Konten_Gambar;
    private JTextField txtFileName;
    private JComboBox cmbToken;
    private JCheckBox cbPassword;
    private JPanel cbShow;

    //deklarasi maksimal character
    private final int MAX_CHARACTERS = 50;

    //deklarasi model
    private DefaultTableModel Model;

    //Variabel yang digunakan dalam program
    String selectedImagePath = "";
    private File selectedImageFile;
    byte[] imageBytes;
    int selectedRow = 0;

    //Deklarasi objec connection dari class DBConnect
    DBConnect connection = new DBConnect();

    //Deklarasi beberapa variabel yang akan menjadi penampung nilai dari data
    String Nama;
    String JenisKelamin;
    String Notelp;
    String Alamat;
    String Email;
    String Username;
    String Password;
    String id_karyawan;
    int token;

    //Configurasi frame form
    public void FrameConfigure()
    {
        add(this.JPKaryawan); //Mengatur yang akan ditampilkan adalah Jpanel JPKaryawan
        setTitle("Kelola Karyawan"); //Mengatur title dari form yang akan ditampilakan "CRUD Karyawan"
        setExtendedState(JFrame.MAXIMIZED_BOTH); //Mengatur agar frame ditampilkan semaksimal Jpanel penampung
        setUndecorated(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true); //Mengatur visibilitas dari form

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CRUDKaryawan().setVisible(true); //Menjalankan CRUDKaryawan
        });
    }

    public CRUDKaryawan()
    {
        FrameConfigure(); //Mengatur agar tampilan sesuai dengan konfigurasi yang sebelumnya

        Model = new DefaultTableModel(); //Deklarasi Model menjadi object dari class DefaultTableModel
        TabelData.setModel(Model); //Mengatur model dari table TabelData yaitu "Model"
        addColomn(); //Menambahkan header ke tabel TabelData --> Menuju prosedur addColumn()
        loadData(); //Melakukan load data --> Menuju prosedur loadData()


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

        btnUpdate.addActionListener(new ActionListener() {  //Listener saat tombol Ubah di tekan
            @Override
            public void actionPerformed(ActionEvent e) {
                //Mengecek apakah ada salah satu data yang kosong
                if (Objects.equals(txtNama.getText(), "") || cbjk.getSelectedItem().equals("") || (txtTelp.getText().equals("")) || (txtAlamat.getText().equals("")) || (txtEmail.getText().equals("")) || Objects.equals(txtUsername.getText(), "") || Objects.equals(txtTelp.getText(), "") || Objects.equals(txtPassword.getText(), "")){
                    //Menampilkan pesan berikut jika salah satu atau lebih data masih ada yang kosong
                    JOptionPane.showMessageDialog(null, "Data tidak boleh kosong!", "Peringatan!",
                            JOptionPane.WARNING_MESSAGE);
                }
                else
                {
                    //Melakukan inisialisasi nilai variabel dari textbox
                    Nama = txtNama.getText();
                    JenisKelamin = cbjk.getSelectedItem().toString();
                    Notelp = txtTelp.getText();
                    token = cmbToken.getSelectedIndex() + 1;

                    //Melakukan validasi input melakukan pengecekan terhadap inputan nomor telepon
                    boolean valid = validateInput(Notelp);

                    if (!valid) //Jika tidak valid
                    {
                        //Maka akan menampilkan pesan berikut
                        JOptionPane.showMessageDialog(null, "Nomor telepon harus 628XXX"
                                , "Kesalahan", JOptionPane.ERROR_MESSAGE);
                        txtTelp.setText(""); //Mengosongkan isi dari textbox nomor telepon
                        txtTelp.requestFocus(); //Mengatur focus pengisian input ke textbox nomor telepon
                        return;
                    }

                    //Melanjutkan inisialisasi nilai
                    Alamat = txtAlamat.getText();
                    Email = txtEmail.getText();

                    //Melakukan validasi inputan email yang diinput oleh user
                    boolean valid2 = validateEmail(Email);
                    //Jika inputan email tidak valid
                    if(!valid2)
                    {
                        //Maka akan menampilkan pesan berikut
                        JOptionPane.showMessageDialog(null, "Email Harus Menggunakan a@b.c"
                                , "Kesalahan", JOptionPane.ERROR_MESSAGE);
                        txtEmail.setText(""); //Mengosongkan isi textbox email
                        txtEmail.requestFocus(); //Mengatur focus untuk mengisi textbox email
                        return;
                    }

                    //Inisialisi variabel lanjutan
                    Username = txtUsername.getText();
                    Password = txtPassword.getText();

                    try {
                        //Menyiapkan query yang akan di eksekusi dalam variabel sql1
                        String sql1 = "EXEC sp_UpdateKaryawan ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?"; //query SP untuk update data karyawan
                        int selectedRow = TabelData.getSelectedRow(); //Mengambil baris yang di klik pada tabel

                        connection.pstat = connection.conn.prepareStatement(sql1); // Menyiapkan pernyataan SQL dengan objek PreparedStatement
                        connection.pstat.setString(1, (String) Model.getValueAt(selectedRow, 0)); // Mengatur nilai parameter pertama dengan nilai dari sel di baris terpilih dalam Model
                        connection.pstat.setString(2, Nama); // Mengatur nilai parameter kedua dengan nilai dari variabel Nama
                        connection.pstat.setString(3, JenisKelamin); // Mengatur nilai parameter ketiga dengan nilai dari variabel JenisKelamin
                        connection.pstat.setString(4, Notelp); // Mengatur nilai parameter keempat dengan nilai dari variabel Notelp
                        connection.pstat.setString(5, Email); // Mengatur nilai parameter kelima dengan nilai dari variabel Email
                        connection.pstat.setString(6, Alamat); // Mengatur nilai parameter keenam dengan nilai dari variabel Alamat
                        connection.pstat.setBytes(7, imageBytes); // Mengatur nilai parameter ketujuh dengan nilai dari array of bytes (imageBytes)
                        connection.pstat.setString(8, Username); // Mengatur nilai parameter kedelapan dengan nilai dari variabel Username
                        connection.pstat.setString(9, Password); // Mengatur nilai parameter kesembilan dengan nilai dari variabel Password
                        connection.pstat.setInt(10, token); // Mengatur nilai parameter kesepuluh dengan nilai dari variabel token
                        connection.pstat.setString(11, "1"); // Mengatur nilai parameter kesebelas dengan nilai string "1"

                        connection.pstat.executeUpdate(); // Mengeksekusi pernyataan SQL untuk melakukan pembaruan data (seperti INSERT, UPDATE, DELETE)
                        connection.pstat.close(); // Menutup objek PreparedStatement untuk membebaskan sumber daya yang digunakan
                        loadData(); // Memuat data kembali setelah pembaruan dilakukan
                        clear(); // Mengosongkan atau mereset input atau komponen lainnya setelah pembaruan dilakukan

                    } catch (Exception ex) {
                        System.out.println("Error saat Update Karyawan: " + ex); // Menampilkan pesan kesalahan jika terjadi pengecualian
                    }
                    // Menampilkan dialog informasi menggunakan JOptionPane setelah pembaruan data berhasil dilakukan
                    JOptionPane.showMessageDialog(null, "Data Berhasil Di-Update!",
                            "Informasi", JOptionPane.INFORMATION_MESSAGE);

                }
            }
        });
        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int opsi; //Deklarasi variabel opsi untuk menampung pilihan konfirmasi user
                //Melakukan pengecekan apakah masih ada data yang kosong
                if (Objects.equals(txtNama.getText(), "") || cbjk.getSelectedItem().equals("") || (txtTelp.getText().equals("")) || (txtAlamat.getText().equals("")) || (txtEmail.getText().equals("")) || Objects.equals(txtUsername.getText(), "") || Objects.equals(txtTelp.getText(), "") || Objects.equals(txtPassword.getText(), "")){
                    JOptionPane.showMessageDialog(null, "Data tidak boleh kosong!", "Peringatan!",
                            JOptionPane.WARNING_MESSAGE); //Menampilkan pesan jika ditemukan salah satu data masih kosong
                }else{ //Jika tidak ada yang kosong lagi
                    try {
                        //Mengambil nilai baris yang dipilih
                        int kode = TabelData.getSelectedRow(); //Inisialisasi variabel kode untuk menampung nilai baris
                        //Menampilkan pesan konfirmasi pakah data yang dipilih akan dihapus
                        opsi = JOptionPane.showConfirmDialog(null, "Apakah Anda yakin ingin menghapus data ini?",
                                "Konfirmasi", JOptionPane.YES_OPTION, JOptionPane.INFORMATION_MESSAGE);
                        if (opsi != 0) { //jika user memmilih tidak
                            JOptionPane.showMessageDialog(null, "Data batal dihapus!",
                                    "Informasi!", JOptionPane.INFORMATION_MESSAGE); //Menampilkan pesan data batal dihapus
                        } else {
                            //Mengambil nilai id_karyawan
                            id_karyawan = String.valueOf(Model.getValueAt(kode, 0)); //Mengambil pada kolom 0 dari Tabel
                            String query = "EXEC sp_DeleteKaryawan @id_karyawan=?"; //query SP untuk hapus data karyawan
                            connection.pstat = connection.conn.prepareStatement(query); // Menyiapkan pernyataan SQL dengan objek PreparedStatement
                            connection.pstat.setString(1, id_karyawan); // Mengatur nilai parameter pertama dengan nilai dari variabel id_karyawan
                            connection.pstat.executeUpdate(); // Mengeksekusi pernyataan SQL untuk melakukan pembaruan data (seperti INSERT, UPDATE, DELETE)
                            connection.pstat.close(); // Menutup objek PreparedStatement untuk membebaskan sumber daya yang digunakan

                            //Menampilkan pesan berikut jika hapus data berhasil
                            JOptionPane.showMessageDialog(null, "Data berhasil dihapus!",
                                    "Informasi!", JOptionPane.INFORMATION_MESSAGE);
                        }
                    } catch (NumberFormatException nex){
                        JOptionPane.showMessageDialog(null, "Tolong masukan nomor yang benar ."+nex.getMessage());
                        // Menampilkan dialog pesan kesalahan jika terjadi pengecualian NumberFormatException
                        // Pesan dialog memberi tahu pengguna untuk memasukkan nomor yang benar
                    }
                    catch (Exception e1){
                        JOptionPane.showMessageDialog(null, "an error occurred while deleting data into the database.\n" + e1);
                        // Menampilkan dialog pesan kesalahan jika terjadi pengecualian yang tidak ditangkap sebelumnya (Exception)
                        // Pesan dialog memberi tahu pengguna bahwa terjadi kesalahan saat menghapus data ke dalam database
                    }

                    // Memuat data kembali setelah pembaruan atau penghapusan dilakukan (mungkin untuk memperbarui tampilan data)
                    loadData();
                    // Mengosongkan atau mereset input atau komponen lainnya setelah pembaruan atau penghapusan dilakukan
                    clear();
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
        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Notelp = txtTelp.getText();

                boolean found = false; // inisiasi awal kalau nama yang di input tidak sama


                // Mengambil jumlah baris pada table
                int baris = TabelData.getModel().getRowCount();

                for (int awal = 0; awal < baris; awal++) { // Mengulang pengecekan dari awal sampai jumlah baris


                    // Mengecek apakah nama jenis yang dimasukkan sama dengan nama pada kolom tertentu
                    if (Notelp == Model.getValueAt(awal, 3).toString() && selectedRow != awal) {
                        found = true; // Menemukan data yang sama pada yang sebelumnya

                    }
                }

                if(found) { //Jika menemukan data yang sama
                    //akan menampilkna pesan berikut
                    JOptionPane.showMessageDialog(null, "Data karyawan sudah ada!", "Informasi"
                            , JOptionPane.INFORMATION_MESSAGE); //Jika Sudah diinput
                    txtTelp.setText(""); //Mengosongkan ini dari textbox telepon
                    txtTelp.requestFocus(); //Mengatur set focus ke textbox tersebut

                }else{
                    // Dilakukan pengecekan apakah masih ada data yang kosong atau tidak
                if (txtNama.getText().equals("") || cbjk.getSelectedItem().equals("") || txtTelp.getText().equals("") || txtAlamat.getText().equals("") || txtEmail.getText().equals("") || txtUsername.getText().equals("") || txtTelp.getText().equals("") || txtPassword.getText().equals("")) {
                    JOptionPane.showMessageDialog(null, "Data tidak boleh kosong!", "Peringatan!",
                            JOptionPane.WARNING_MESSAGE); // Jika ditemukan maka akan menampilkan pesan seperti diatas
                }
                else { //Jika semua data telah terisi

                    //Diakukan inisialisasi data pada variabel penampung nilai dati textbox yang bersangkutan
                    Nama = txtNama.getText();
                    JenisKelamin = cbjk.getSelectedItem().toString();
                    Notelp = txtTelp.getText();
                    token = cmbToken.getSelectedIndex() + 1;

                    //Dilakukan validasi apakah nomor telepon yang dimasukkan sudah sesuai
                    boolean valid = validateInput(Notelp);
                    if (!valid || !Notelp.matches("[0-9]+")) {
                        //Menampilkan pesan error berikut jika nomor telepon tidak sesuai dengan format atau semua nya tidak angka
                        JOptionPane.showMessageDialog(null, "Nomor telepon harus 628XXX / semua harus angka"
                                , "Kesalahan", JOptionPane.ERROR_MESSAGE);
                        txtTelp.setText(""); //Mengosongkan textbox nomor telepon
                        txtTelp.requestFocus(); //Mengatur set focus ke textbox nomor telepon
                        return;
                    }
                    //inisialisasi lanjutan
                    Alamat = txtAlamat.getText();
                    Email = txtEmail.getText();

                    // Memanggil metode validateEmail() untuk memvalidasi format Email. Hasil validasi disimpan dalam variabel boolean valid2.
                    boolean valid2 = validateEmail(Email);
                    if (!valid2) { //Jika inputan tidak valid
                        // Jika validasi Email tidak berhasil, menampilkan dialog pesan kesalahan dengan pesan yang menunjukkan format yang benar
                        JOptionPane.showMessageDialog(null, "Email Harus Menggunakan a@b.c"
                                , "Kesalahan", JOptionPane.ERROR_MESSAGE);
                        // Mengosongkan nilai txtEmail dan mengarahkan fokus ke komponen txtEmail.
                        txtEmail.setText("");
                        txtEmail.requestFocus();
                        return;
                        // Menghentikan eksekusi lebih lanjut dan kembali dari metode atau blok saat ini.
                    }
                    Username = txtUsername.getText();
                    Password = txtPassword.getText();

                    try {
                        DBConnect connection = new DBConnect(); // Membuat objek DBConnect untuk mengelola koneksi ke database
                        String sql = "EXEC sp_InsertKaryawan ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?"; // Mendefinisikan pernyataan SQL yang akan dieksekusi untuk memasukkan data karyawan ke database menggunakan stored procedure
                        connection.pstat = connection.conn.prepareStatement(sql); // Menyiapkan pernyataan SQL dengan menggunakan koneksi database yang dibuat sebelumnya
                        connection.pstat.setString(1, generateNextSupplierID()); // Mengatur nilai parameter pertama dengan menggunakan metode generateNextSupplierID() untuk menghasilkan ID karyawan baru
                        connection.pstat.setString(2, Nama); // Mengatur nilai parameter kedua dengan nilai dari variabel Nama
                        connection.pstat.setString(3, JenisKelamin); // Mengatur nilai parameter ketiga dengan nilai dari variabel JenisKelamin
                        connection.pstat.setString(4, Notelp); // Mengatur nilai parameter keempat dengan nilai dari variabel Notelp
                        connection.pstat.setString(5, Email); // Mengatur nilai parameter kelima dengan nilai dari variabel Email
                        connection.pstat.setString(6, Alamat); // Mengatur nilai parameter keenam dengan nilai dari variabel Alamat
                        connection.pstat.setBytes(7, imageBytes); // Mengatur nilai parameter ketujuh dengan nilai dari variabel imageBytes
                        connection.pstat.setString(8, Username); // Mengatur nilai parameter kedelapan dengan nilai dari variabel Username
                        connection.pstat.setString(9, Password); // Mengatur nilai parameter kesembilan dengan nilai dari variabel Password
                        connection.pstat.setInt(10, token); // Mengatur nilai parameter kesepuluh dengan nilai dari variabel token
                        connection.pstat.setString(11, "1"); // Mengatur nilai parameter kesebelas dengan nilai "1"

                        connection.pstat.executeUpdate(); // Mengeksekusi pernyataan SQL yang sudah disiapkan untuk memasukkan data karyawan ke dalam database

                        //Menampilkan pesan berikut jika input data berhasil
                        JOptionPane.showMessageDialog(null, "Data Berhasil ditambahkan!",
                                "Informasi", JOptionPane.INFORMATION_MESSAGE);
                        loadData(); // Memuat data kembali setelah pembaruan
                        clear(); // Mengosongkan atau mereset input
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Eror saat Menyimpan ke dalam database." + ex);
                    } finally {
                        try {
                            if (connection.pstat != null) {
                                connection.pstat.close();
                            }
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                }

                }
            }
        });

        btnRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String namaKaryawan = txtSearch.getText();
                if(!namaKaryawan.isEmpty())
                {
                    searchKaryawan(namaKaryawan);
                    clear();
                }
                else
                {
                    loadData();
                    clear();
                }
            }
        });
        txtNama.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        TabelData.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                selectedRow = TabelData.getSelectedRow();
                if (selectedRow == -1) {
                    return;
                }

                // Mendapatkan nilai dari kolom yang dipilih
                String nama = (String) Model.getValueAt(selectedRow, 1);
                String jenisKelamin = (String) Model.getValueAt(selectedRow, 2); // Menambahkan kolom jenis kelamin
                String telp = (String) Model.getValueAt(selectedRow, 3);
                String alamat = (String) Model.getValueAt(selectedRow, 4);
                String email = (String) Model.getValueAt(selectedRow, 5);
                String username = (String) Model.getValueAt(selectedRow, 6);
                String password = (String) Model.getValueAt(selectedRow, 7);

                switch (Model.getValueAt(selectedRow, 8).toString()){
                    case "Manager" : token = 1; break;
                    case "Manager Keuangan" : token = 2; break;
                    case "Admin" : token = 3; break;
                    case "Kasir" : token = 4; break;
                    case "Orang Gudang" : token = 5; break;
                    case "PJR" : token = 6; break;
                }




                // Execute the SwingWorker in the background
                SwingWorker<ImageIcon, Void> worker = new SwingWorker<ImageIcon, Void>() {
                    @Override
                    protected ImageIcon doInBackground() throws Exception {
                        // Retrieve the image from the database
                        return retrieveImageFromDatabase(Model.getValueAt(selectedRow, 0).toString());
                    }

                    @Override
                    protected void done() {
                        try {
                            // Get the image result from doInBackground()
                            ImageIcon imageIcon = get();

                            if (imageIcon != null) {
                                // Mengatur nilai teks dalam komponen
                                txtNama.setText(nama);
                                txtTelp.setText(telp);
                                txtAlamat.setText(alamat);
                                txtEmail.setText(email);
                                txtUsername.setText(username);
                                txtPassword.setText(password);

                                // Mengatur pilihan pada JComboBox
                                if (jenisKelamin.equals("Laki-Laki")) {
                                    cbjk.setSelectedIndex(1);
                                } else if (jenisKelamin.equals("Perempuan")) {
                                    cbjk.setSelectedIndex(2);
                                }

                                cmbToken.setSelectedIndex(token);

                                // Resize image to fit JLabel
                                Image image = imageIcon.getImage().getScaledInstance(gambar_barang.getWidth(), gambar_barang.getHeight(), Image.SCALE_SMOOTH);
                                gambar_barang.setIcon(new ImageIcon(image));

                                // Enable buttons
                                btnSave.setEnabled(false);
                                btnUpdate.setEnabled(true);
                                btnDelete.setEnabled(true);
                            } else {
                                // Handle the case when imageIcon is null
                                // Set default values or display an error message
                                // Example:
                                txtNama.setText("");
                                txtTelp.setText("");
                                txtAlamat.setText("");
                                txtEmail.setText("");
                                txtUsername.setText("");
                                txtPassword.setText("");
                                gambar_barang.setIcon(null);
                                btnSave.setEnabled(false);
                                btnUpdate.setEnabled(false);
                                btnDelete.setEnabled(false);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                };


                worker.execute(); // Start the SwingWorker

                // Mengatur status tombol
                btnSave.setEnabled(true);
                btnUpdate.setEnabled(true);
                btnDelete.setEnabled(true);
            }
        });



        btnClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnDelete.setEnabled(false);
                btnUpdate.setEnabled(false);
                btnSave.setEnabled(true);

                clear();
            }
        });
        Tombol_Browse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser browseImageFile = new JFileChooser();
                //Filter extensions
                FileNameExtensionFilter fnef = new FileNameExtensionFilter("IMAGES", "png", "jpg", "jpeg");
                browseImageFile.addChoosableFileFilter(fnef);
                int showOpenDialogue = browseImageFile.showOpenDialog(null);

                if (showOpenDialogue == JFileChooser.APPROVE_OPTION) {
                    selectedImageFile = browseImageFile.getSelectedFile();
                    selectedImagePath = selectedImageFile.getAbsolutePath();

                    try {
                        // Mengubah gambar menjadi byte array
                        imageBytes = convertImageToByteArray(selectedImageFile);

                        //Display image on jlable
                        ImageIcon ii = new ImageIcon(selectedImagePath);
                        //Resize image to fit jlabel
                        Image image = ii.getImage().getScaledInstance(gambar_barang.getWidth(), gambar_barang.getHeight(), Image.SCALE_SMOOTH);
                        gambar_barang.setIcon(new ImageIcon(image));

                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        gambar_barang.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                try {
                    loadGambarawal();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        cbPassword.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(cbPassword.isSelected()){
                    txtPassword.setEchoChar('\0');
                }else{
                    txtPassword.setEchoChar('•');
                }
            }
        });
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
            System.out.println("Terjadi error saat memeriksa Karyawan terakhir: " + e);
        } finally {
            try {
                if (this.connection.result != null) {
                    this.connection.result.close();
                }
                if (this.connection.pstat != null) {
                    this.connection.pstat.close();
                }
                if (this.connection.conn != null) {
                    this.connection.conn.close();
                }
            } catch (SQLException ex) {
                System.out.println("Terjadi error saat menutup koneksi: " + ex);
            }
        }

        return null;
    }

    private void searchKaryawan(String nama) {
        Model.getDataVector().removeAllElements();
        Model.fireTableDataChanged();

        try {
            String query = "EXEC sp_SearchKaryawan ?";
            connection.pstat = connection.conn.prepareStatement(query);
            connection.pstat.setString(1, nama);
            connection.result = connection.pstat.executeQuery();

            while (connection.result.next()) {
                Object[] obj = new Object[10];
                obj[0] = connection.result.getString("id_karyawan");
                obj[1] = connection.result.getString("nama_karyawan");
                obj[2] = connection.result.getString("jenis_kelamin");
                obj[3] = connection.result.getString("no_telp");
                obj[4] = connection.result.getString("alamat");
                obj[5] = connection.result.getString("email");
                obj[6] = connection.result.getString("username");
                obj[7] = connection.result.getString("password");
                obj[8] = connection.result.getString("token");
                obj[9] = connection.result.getString("status");

                Model.addRow(obj);

                btnUpdate.setEnabled(true);
                btnDelete.setEnabled(true);
                txtSearch.setText("");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "An error occurred while searching data.\n" + e);
        }
    }

    public void addColomn(){  //Berfungsi menambahkan Header
        Model.addColumn("ID Karyawan"); //Menambahkan header "ID Karyawan" ke "Model"
        Model.addColumn("Nama Karyawan"); //Menambahkan header "Nama Karyawan" ke "Model"
        Model.addColumn("Jenis Kelamin"); //Menambahkan header "Jenis Kelamin" ke "Model"
        Model.addColumn("Nomor telepon"); //Menambahkan header "Nomor telepon " ke "Model"
        Model.addColumn("Alamat"); //Menambahkan header "Alamat" ke "Model"
        Model.addColumn("Email"); //Menambahkan header "Email" ke "Model"
        Model.addColumn("Username"); //Menambahkan header "Username" ke "Model"
        Model.addColumn("Password"); //Menambahkan header "Password" ke "Model"
        Model.addColumn("Jabatan"); //Menambahkan header "Jabatan" ke "Model"
    }

    public void loadData() { //Melakukan memuat data
        DBConnect connection = null; //Deklarasi awal connection dari Class DBConnect
        try {
            Model.getDataVector().removeAllElements(); //Menghapus data yang sebelumnya sudah ada pada table
            Model.fireTableDataChanged(); //Memberi tahu table model bahwa data telah berubah sehingga akan memperbarui isi

            connection = new DBConnect(); //Mendeklarasi Obejct Connect
            connection.stat = connection.conn.createStatement(); //menjalankan pernyataan SQL. Objek Statement tersebut kemudian disimpan dalam variabel stat
            String query = "EXEC sp_LoadKaryawan"; //Mendeklarasi kan query yang akan di eksekusi yaitu sp_LoadKaryawan
            connection.result = connection.stat.executeQuery(query); // Menjalankan pernyataan SQL yang diberikan pada objek Statement dan menyimpan hasilnya dalam objek ResultSet.

            while (connection.result.next()) { //Melakukan iterasi terhadap setiap baris dalam object ResultSet

                Object[] obj = new Object[10]; //Deklarasi array object dengan nama obj ukuran 10
                obj[0] = connection.result.getString("id_karyawan"); //mengambil nilai kolom "id_karyawan" dari ResultSet dan menetapkannya ke elemen indeks ke-0 dari obj.
                obj[1] = connection.result.getString("nama_karyawan"); //mengambil nilai kolom "nama_karyawan" dari ResultSet dan menetapkannya ke elemen indeks ke-1 dari obj.
                obj[2] = connection.result.getString("jenis_kelamin"); //mengambil nilai kolom "jenis_kelamin" dari ResultSet dan menetapkannya ke elemen indeks ke-2 dari obj.
                obj[3] = connection.result.getString("no_telp"); //mengambil nilai kolom "no_telp" dari ResultSet dan menetapkannya ke elemen indeks ke-3 dari obj.
                obj[4] = connection.result.getString("alamat"); //mengambil nilai kolom "alamat" dari ResultSet dan menetapkannya ke elemen indeks ke-4 dari obj.
                obj[5] = connection.result.getString("email"); //mengambil nilai kolom "email" dari ResultSet dan menetapkannya ke elemen indeks ke-5 dari obj.
                obj[6] = connection.result.getString("username"); //mengambil nilai kolom "username" dari ResultSet dan menetapkannya ke elemen indeks ke-6 dari obj.
                obj[7] = connection.result.getString("password"); //mengambil nilai kolom "password" dari ResultSet dan menetapkannya ke elemen indeks ke-7 dari obj.

                //Mencari Nama Jabatan
                switch (connection.result.getString("token")){ //Mengambil nilai dari kolom "token"
                    case "1" : obj[8] = "Manager"; break; //jika token bernilai "1" maka menetapkan nilai obj[8] = "Manager"
                    case "2" : obj[8] = "Manager Keuangan"; break; //jika token bernilai "2" maka menetapkan nilai obj[8] = "Manager Keuangan"
                    case "3" : obj[8] = "Admin"; break; //jika token bernilai "3" maka menetapkan nilai obj[8] = "Admin"
                    case "4" : obj[8] = "Kasir"; break; //jika token bernilai "4" maka menetapkan nilai obj[8] = "Kasir"
                    case "5" : obj[8] = "Orang Gudang"; break; //jika token bernilai "5" maka menetapkan nilai obj[8] = "Orang Gudang"
                    case "6" : obj[8] = "PJR"; break; //jika token bernilai "6" maka menetapkan nilai obj[8] = "PJR"
                }
                obj[9] = connection.result.getString("status"); //Mengambil nilai dari kolom "status" dari ResultSet dan menyimpannya dalam obj index ke-9

                Model.addRow(obj); //Menambah sebuah baris pada tabel yang diisi dengan data dari obj

                //Melakukan iterasi sampai akhir data dari ResultSet
            }

            btnSave.setEnabled(true); //Menetapkan tombol save enable
            btnUpdate.setEnabled(false); //Menetapkan tombol update disable
            btnDelete.setEnabled(false); //Menetapkan tombol delete disable
        } catch (Exception e) { //Jika menangkap kesalahan / error selama melakukan load data
            JOptionPane.showMessageDialog(null, "An error occurred while loading data.\n" + e); //Menampilkan pesan error
        } finally { //Hal yang akan pasti dilakukan meski error maupun tidak error
            // Pastikan untuk selalu menutup koneksi setelah digunakan
            try {
                if (connection != null) { //Jika connection tidak null
                    if (connection.stat != null) //jika connection statement tidak null
                        connection.stat.close(); //melakukan penutupan statement
                    if (connection.result != null) //jika connection statement tidak null
                        connection.result.close(); //melakukan penutupan Result set
                    if (connection.conn != null) //jika connection statement tidak null
                        connection.conn.close(); //melakukan penutupan statement
                }
            } catch (SQLException e) { //Jika terjadi kesalahan selama melakukan pengaksesan data ke database
                e.printStackTrace(); //Menampilakn pesan error
            }
        }
    }

    public void clear()
    {
        txtNama.setText("");
        txtAlamat.setText("");
        txtEmail.setText("");
        txtTelp.setText("");
        txtPassword.setText("");
        txtUsername.setText("");
        txtSearch.setText("");
        cmbToken.setSelectedIndex(0);
        cbjk.setSelectedIndex(0);

        try {
            loadGambarawal();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadGambarawal() throws IOException {
        ImageIcon imageIcon = new ImageIcon(getClass().getResource("/Gambar/defaultuser.jpg")); // Ganti dengan path gambar Anda
        Image image = imageIcon.getImage();

        //JOptionPane.showMessageDialog(null, gambar_barang.getHeight() +" "+gambar_barang.getWidth());

        // Mengubah ukuran gambar sesuai dengan ukuran JLabel
        int labelWidth = gambar_barang.getWidth();
        int labelHeight = gambar_barang.getHeight();
        Image scaledImage = image.getScaledInstance(labelWidth, labelHeight, Image.SCALE_SMOOTH);

        // Membuat ImageIcon dari gambar yang telah diubah ukurannya
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        // Mengubah gambar menjadi byte array
        File scaledImageFile = convertImageToFile(scaledImage);

        // Mengatur ikon pada JLabel
        gambar_barang.setIcon(scaledIcon);

        // Mengubah gambar menjadi byte array
        imageBytes = convertImageToByteArray(scaledImageFile);
    }

    private File convertImageToFile(Image image) throws IOException {
        BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();

        File tempFile = File.createTempFile("temp", ".jpg");
        ImageIO.write(bufferedImage, "jpg", tempFile);
        return tempFile;
    }

    private static byte[] convertImageToByteArray(File imageFile) throws IOException {
        FileInputStream fis = new FileInputStream(imageFile);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = fis.read(buffer)) != -1) {
            baos.write(buffer, 0, bytesRead);
        }

        fis.close();
        baos.close();

        return baos.toByteArray();
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

    public ImageIcon retrieveImageFromDatabase(String id_karyawan) {
        ImageIcon imageIcon = null;

        try {
            // Establish database connection
            DBConnect connection = new DBConnect();

            // Prepare SQL statement
            String sq2 = "SELECT foto_karyawan FROM tblKaryawan WHERE id_karyawan = ?";
            connection.pstat = connection.conn.prepareStatement(sq2);


            // Set the image ID parameter
            connection.pstat.setString(1, id_karyawan);

            connection.result = connection.pstat.executeQuery();


            if (connection.result.next()) {
                // Retrieve the image data as an input stream
                InputStream inputStream = connection.result.getBinaryStream("foto_karyawan");

                // Convert the input stream to a byte array
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[4096];
                int bytesRead;

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                imageBytes = outputStream.toByteArray();

                // Create an ImageIcon from the byte array
                imageIcon = new ImageIcon(imageBytes);

                // Close streams and database connection
                inputStream.close();
                outputStream.close();
            }

            connection.pstat.close();
        } catch (SQLException | IOException ex) {
            ex.printStackTrace();
        }

        return imageIcon;
    }
}
