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
    private final int MAX_CHARACTERS = 50;

    private DefaultTableModel Model;

    //Variabel
    String selectedImagePath = "";
    private File selectedImageFile;
    byte[] imageBytes;

    DBConnect connection = new DBConnect();

    String Nama;
    String JenisKelamin;
    String Notelp;
    String Alamat;
    String Email;
    String Username;
    String Password;
    String id_karyawan;
    int token;

    public void FrameConfigure()
    {
        add(this.JPKaryawan);
        setTitle("CRUDKaryawan");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CRUDKaryawan().setVisible(true);
        });
    }

    public CRUDKaryawan()
    {
        FrameConfigure();

        Model = new DefaultTableModel();
        TabelData.setModel(Model);
        addColomn();
        loadData();


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
                if (Objects.equals(txtNama.getText(), "") || cbjk.getSelectedItem().equals("") || (txtTelp.getText().equals("")) || (txtAlamat.getText().equals("")) || (txtEmail.getText().equals("")) || Objects.equals(txtUsername.getText(), "") || Objects.equals(txtTelp.getText(), "") || Objects.equals(txtPassword.getText(), "")){
                    JOptionPane.showMessageDialog(null, "Please, fill in all data!");
                }
                else
                {
                    Nama = txtNama.getText();
                    JenisKelamin = cbjk.getSelectedItem().toString();
                    Notelp = txtTelp.getText();
                    token = cmbToken.getSelectedIndex() + 1;
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

                    try {
                        String sql1 = "EXEC sp_UpdateKaryawan ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?";
                        int selectedRow = TabelData.getSelectedRow();
                        connection.pstat = connection.conn.prepareStatement(sql1);
                        connection.pstat.setString(1, (String) Model.getValueAt(selectedRow, 0));
                        connection.pstat.setString(2, Nama);
                        connection.pstat.setString(3, JenisKelamin);
                        connection.pstat.setString(4, Notelp);
                        connection.pstat.setString(5, Email);
                        connection.pstat.setString(6, Alamat);
                        connection.pstat.setBytes(7, imageBytes);
                        connection.pstat.setString(8, Username);
                        connection.pstat.setString(9, Password);
                        connection.pstat.setInt(10, token);
                        connection.pstat.setString(11, "1");

                        connection.pstat.executeUpdate();
                        connection.pstat.close();
                        loadData();
                    } catch (Exception ex) {
                        System.out.println("Error saat Update Karyawan: " + ex);
                    }
                    JOptionPane.showMessageDialog(null, "Update Data Karyawan berhasil !!");
                }
            }
        });
        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int opsi;
                if (Objects.equals(txtNama.getText(), "") || cbjk.getSelectedItem().equals("") || (txtTelp.getText().equals("")) || (txtAlamat.getText().equals("")) || (txtEmail.getText().equals("")) || Objects.equals(txtUsername.getText(), "") || Objects.equals(txtTelp.getText(), "") || Objects.equals(txtPassword.getText(), "")){
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
                            connection.pstat = connection.conn.prepareStatement(query);
                            connection.pstat.setString(1, id_karyawan);
                            connection.pstat.executeUpdate();
                            connection.pstat.close();
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
        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (txtNama.getText().equals("") || cbjk.getSelectedItem().equals("") || txtTelp.getText().equals("") || txtAlamat.getText().equals("") || txtEmail.getText().equals("") || txtUsername.getText().equals("") || txtTelp.getText().equals("") || txtPassword.getText().equals("")) {
                    JOptionPane.showMessageDialog(null, "Please fill in all data!");
                }
                else
                {
                    Nama = txtNama.getText();
                    JenisKelamin = cbjk.getSelectedItem().toString();
                    Notelp = txtTelp.getText();
                    token = cmbToken.getSelectedIndex() + 1;

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

                    try {
                        DBConnect connection = new DBConnect();
                        String sql = "EXEC sp_InsertKaryawan ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?";
                        connection.pstat = connection.conn.prepareStatement(sql);
                        connection.pstat.setString(1, generateNextSupplierID());
                        connection.pstat.setString(2, Nama);
                        connection.pstat.setString(3, JenisKelamin);
                        connection.pstat.setString(4, Notelp);
                        connection.pstat.setString(5, Email);
                        connection.pstat.setString(6, Alamat);
                        connection.pstat.setBytes(7, imageBytes);
                        connection.pstat.setString(8, Username);
                        connection.pstat.setString(9, Password);
                        connection.pstat.setInt(10, token);
                        connection.pstat.setString(11, "1");

                        connection.pstat.executeUpdate();

                        JOptionPane.showMessageDialog(null, "Data Berhasil ditambahkan!!");
                        loadData();
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
        });
        btnRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String namaKaryawan = txtSearch.getText();
                if(!namaKaryawan.isEmpty())
                {
                    searchKaryawan(namaKaryawan);
                }
                else
                {
                    loadData();
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
                int selectedRow = TabelData.getSelectedRow();
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
                                if (jenisKelamin.equals("Laki-laki")) {
                                    cbjk.setSelectedIndex(0);
                                } else if (jenisKelamin.equals("Perempuan")) {
                                    cbjk.setSelectedIndex(1);
                                }

                                cmbToken.setSelectedIndex(token-1);

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
    }

    public void addColomn(){
        Model.addColumn("ID Karyawan");
        Model.addColumn("Nama Karyawan");
        Model.addColumn("Jenis Kelamin");
        Model.addColumn("No telp");
        Model.addColumn("Alamat");
        Model.addColumn("Email");
        Model.addColumn("Username");
        Model.addColumn("Password");
        Model.addColumn("Jabatan");
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



    public void loadData() {
        DBConnect connection = null;
        try {
            Model.getDataVector().removeAllElements();
            Model.fireTableDataChanged();

            connection = new DBConnect();
            connection.stat = connection.conn.createStatement();
            String query = "EXEC sp_LoadKaryawan";
            connection.result = connection.stat.executeQuery(query);

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

                //Mencari Nama Jabatan
                switch (connection.result.getString("token")){
                    case "1" : obj[8] = "Manager"; break;
                    case "2" : obj[8] = "Manager Keuangan"; break;
                    case "3" : obj[8] = "Admin"; break;
                    case "4" : obj[8] = "Kasir"; break;
                    case "5" : obj[8] = "Orang Gudang"; break;
                    case "6" : obj[8] = "PJR"; break;
                }
                obj[9] = connection.result.getString("status");

                Model.addRow(obj);
            }

            btnSave.setEnabled(true);
            btnUpdate.setEnabled(false);
            btnDelete.setEnabled(false);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "An error occurred while loading data.\n" + e);
        } finally {
            // Pastikan untuk selalu menutup koneksi setelah digunakan
            try {
                if (connection != null) {
                    if (connection.stat != null)
                        connection.stat.close();
                    if (connection.result != null)
                        connection.result.close();
                    if (connection.conn != null)
                        connection.conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
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
