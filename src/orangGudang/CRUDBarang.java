package orangGudang;

import com.toedter.calendar.JDateChooser;
import connection.DBConnect;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.Date;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class CRUDBarang extends JFrame{
    private JPanel PanelBarang;
    private JPanel Panel_Template;
    private JPanel Pembatas_Kiri;
    private JPanel Pembatas_Kanan;
    private JPanel Pembatas_Atas;
    private JPanel Pembatas_Bawah;
    private JPanel Panel_Konten;
    private JPanel Panel_Kontrol;
    private JPanel Panel_Form;
    private JPanel Panel_Gambar;
    private JPanel Pembatas_Gambar_Atas;
    private JPanel Pembatas_Gambar_Bawah;
    private JButton Tombol_Browse;
    private JPanel Pembatas_Gambar_Kanan;
    private JPanel Pembatas_Gambar_Kiri;
    private JPanel Panel_Konten_Gambar;
    private JLabel gambar_barang;
    private JTextField txtFileName;
    private JPanel Panel_Attribut;
    private JTextField txtlayer;
    private JPanel Panel_Pembatas_Bantuan;
    private JTextField txtHargaBeli;
    private JTextField txtHargaJual;
    private JTextField txtNama;
    private JComboBox cmbJenis;
    private JComboBox cmbRak;
    private JPanel Panel_Tabel;
    private JTable tbBarang;
    private JTextField Textbox_Pencarian;
    private JButton btnSave;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnCari;
    private JButton btRefresh;
    private JTextField txtStockBarang;
    private JComboBox cmbSatuan;
    private JButton btnCancel;
    private JPanel jpkadaluarsa;
    private JDateChooser chooser;

    DefaultTableModel model = new DefaultTableModel(){
        @Override
        public boolean isCellEditable(int row, int column){
            return false;
        }
    };
    DecimalFormat decimalFormat = new DecimalFormat("#,###");

    //Variabel
    String selectedImagePath = "";
    private File selectedImageFile;
    byte[] imageBytes;
    int i;
    String huruflama;

    public void FrameConfigure()
    {
        add(this.PanelBarang);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    public CRUDBarang() {
        FrameConfigure();
        tbBarang.setModel(model);

        chooser = new JDateChooser();
        jpkadaluarsa.add(chooser);

    Thread tampilJenisBarangThread = new Thread(new Runnable() {
        @Override
        public void run() {
            tampilJenisBarang();
        }
    });

    Thread tampilRakThread = new Thread(new Runnable() {
        @Override
        public void run() {
            tampilRak();
        }
    });

    Thread addColumnThread = new Thread(new Runnable() {
        @Override
        public void run() {
            addColumn();
        }
    });

    Thread loadDataThread = new Thread(new Runnable() {
        @Override
        public void run() {
            loadData();
        }
    });

    try {
        loadGambarawal();
    } catch (IOException e) {
        throw new RuntimeException(e);
    }

    tampilJenisBarangThread.start();
    tampilRakThread.start();
    addColumnThread.start();
    loadDataThread.start();




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
    btnSave.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            //Validasi agar tidak ada nama jenis barang yang sama
            boolean found = false; //inisiasi awal kalau nama yang di input tidak sama

            //Mengambil jumlah baris pada table
            int baris = tbBarang.getModel().getRowCount();

            for(int awal = 0; awal < baris; awal++) //Mengulang pengecekan dari awal sampai jumlah baris
            {
                //Mengecek apakah nama jenis yang dimasukkan sama dengan nama pada kolom tertentu
                if(txtNama.getText().toLowerCase().equals(model.getValueAt(awal, 1).toString().toLowerCase()))
                {
                    found = true; //Menemukan data yang sama
                }
            }

            if(found) //Jika menemukan data yang sama pada tabel
            {
                JOptionPane.showMessageDialog(null, "Data Barang sudah ada!", "Informasi",
                        JOptionPane.INFORMATION_MESSAGE); //Menampilkan pesan

            }

            else {
                if(cmbJenis.getSelectedIndex() == 0){
                    JOptionPane.showMessageDialog(null, "Pilih salah satu jenis barang!", "Information!",
                            JOptionPane.INFORMATION_MESSAGE); //Menampilkan pesan
                }
                else if(cmbRak.getSelectedIndex() == 0){
                    JOptionPane.showMessageDialog(null, "Pilih salah satu rak!", "Information!",
                            JOptionPane.INFORMATION_MESSAGE); //Menampilkan pesan
                }else if(cmbJenis.getSelectedIndex() == 0) {
                    JOptionPane.showMessageDialog(null, "Pilih satuan barang!", "Information!",
                            JOptionPane.INFORMATION_MESSAGE); //Menampilkan pesan
                }else{
                    if (txtNama.getText().equals("") || txtlayer.getText().equals("") ||  txtStockBarang.getText().equals("") || txtHargaBeli.getText().equals("") || txtHargaJual.getText().equals("")) //Mengecek apakah txtbox kosong agar tidak ada data kosong
                    {
                        JOptionPane.showMessageDialog(null, "Data tidak boleh kosong!", "Peringatan!",
                                JOptionPane.WARNING_MESSAGE); //Jika kosong maka akan menampilkan pesan data tidak boleh kosong
                    }
                    else{

                        try {

                            //Konvert Image to Byte


                            DBConnect connection = new DBConnect();
                            String sql = "EXEC sp_InserttblBarang @id_barang=?, @id_jenis_barang=?, @id_rak=?, @layer=? ,@nama_barang =?, @stock=?, @satuan=?, @gambar_barang=?, @harga_beli=?, @harga_jual=?, @kadaluarsa=?, @status=1";

                            connection.pstat = connection.conn.prepareStatement(sql);
                            connection.pstat.setString(1,generateNextBarangID());

                            //Mencari ID Jenis Barang
                            DBConnect connection2 = new DBConnect();
                            try {
                                connection2.stat = connection2.conn.createStatement();
                                String sql2 = "SELECT * FROM tblJenisBarang WHERE nama_jenis = ?";
                                connection2.pstat = connection2.conn.prepareStatement(sql2);
                                connection2.pstat.setString(1, cmbJenis.getSelectedItem().toString());
                                connection2.result = connection2.pstat.executeQuery();

                                while (connection2.result.next()) {
                                    connection.pstat.setInt(2,connection2.result.getInt("id_jenis_barang") );
                                }

                                connection2.pstat.close();
                                connection2.result.close();
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(null, "Error while loading data: " + ex);
                            }

                            //Mencari ID Rak
                            DBConnect connection3 = new DBConnect();
                            try {
                                connection3.stat = connection3.conn.createStatement();
                                String sql3 = "SELECT id_rak, huruf, status FROM tblRak WHERE huruf = ?";
                                connection3.pstat = connection3.conn.prepareStatement(sql3);
                                connection3.pstat.setString(1, cmbRak.getSelectedItem().toString());
                                connection3.result = connection3.pstat.executeQuery();

                                while (connection3.result.next()) {

                                    if(connection3.result.getString("status").equals("1"))
                                        connection.pstat.setString(3,connection3.result.getString("id_rak"));
                                }

                                connection3.pstat.close();
                                connection3.result.close();
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(null, "Error while loading data: " + ex);
                            }

                            //Update jumlah barang dalam rak
                            DBConnect connection4 = new DBConnect();
                            try {
                                connection4.stat = connection4.conn.createStatement();
                                String sql4 = "UPDATE tblRak SET jumlah  = jumlah +1 WHERE huruf = ?";
                                connection4.pstat = connection4.conn.prepareStatement(sql4);
                                connection4.pstat.setString(1, cmbRak.getSelectedItem().toString());
                                connection4.result = connection4.pstat.executeQuery();

                                connection4.pstat.close();
                                connection4.result.close();
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(null, "Error while loading data: " + ex);
                            }


                            connection.pstat.setInt(4, Integer.parseInt(txtlayer.getText()));
                            connection.pstat.setString(5, txtNama.getText());
                            connection.pstat.setInt(6, Integer.parseInt(txtStockBarang.getText()));
                            connection.pstat.setString(7, cmbSatuan.getSelectedItem().toString());
                            connection.pstat.setBytes(8, imageBytes);
                            connection.pstat.setInt(9, Integer.parseInt(txtHargaBeli.getText()));
                            connection.pstat.setInt(10, Integer.parseInt(txtHargaJual.getText()));
                            java.util.Date utilDate = chooser.getDate();
                            java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());

                            connection.pstat.setDate(11, sqlDate);



                            connection.pstat.executeUpdate();
                            connection.pstat.close();

                            clear(); //Mengosongkan semua textbox

                            JOptionPane.showMessageDialog(null, "Data Berhasil ditambahkan!",
                                    "Informasi" ,JOptionPane.INFORMATION_MESSAGE);  //Menampilkan pesan berhasil input data barang
                            loadData();
                            tampilRak();
                            loadGambarawal();
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }

                    }

                }

            }

        }
    });
    btnUpdate.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            boolean found = false;

            //Mengambil jumlah baris pada table
            int baris = tbBarang.getModel().getRowCount();

            for(int awal = 0; awal < baris; awal++) //Mengulang pengecekan dari awal sampai jumlah baris
            {
                //Mengecek apakah nama jenis yang dimasukkan sama dengan nama pada kolom tertentu
                if(txtNama.getText().toLowerCase().equals(model.getValueAt(awal, 1).toString().toLowerCase()) && i == baris)
                {
                    found = true; //Menemukan data yang sama
                }
            }

            if (found) {
                JOptionPane.showMessageDialog(null, "Data Barang sudah ada!", "Informasi",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                if (cmbJenis.getSelectedIndex() == 0) {
                    JOptionPane.showMessageDialog(null, "Pilih salah satu jenis barang!", "Peringatan!",
                            JOptionPane.WARNING_MESSAGE);
                } else if (cmbRak.getSelectedIndex() == 0) {
                    JOptionPane.showMessageDialog(null, "Pilih salah satu rak!", "Peringatan!",
                            JOptionPane.WARNING_MESSAGE);
                } else if (cmbJenis.getSelectedIndex() == 0) {
                    JOptionPane.showMessageDialog(null, "Pilih satuan barang!", "Peringatan!",
                            JOptionPane.WARNING_MESSAGE);
                } else {
                    if (txtNama.getText().equals("") || txtlayer.getText().equals("") || txtStockBarang.getText().equals("") || txtHargaBeli.getText().equals("") || txtHargaJual.getText().equals("")) {
                        JOptionPane.showMessageDialog(null, "Tolong, isikan semua data!", "Peringatan!", JOptionPane.WARNING_MESSAGE);
                    } else {
                        try {
                            //Konvert Image to Byte
                            //imageBytes = convertImageToByteArray(selectedImageFile);
                            int row = tbBarang.getSelectedRow();

                            DBConnect connection = new DBConnect();
                            String sql = "EXEC sp_UpdatetblBarang @id_barang=?, @id_jenis_barang=?, @id_rak=?, @layer=? ,@nama_barang =?, @stock=?, @satuan=?, @gambar_barang=?, @harga_beli=?, @harga_jual=?, @kadaluarsa= ?";

                            connection.pstat = connection.conn.prepareStatement(sql);
                            connection.pstat.setString(1, model.getValueAt(row, 0).toString());

                            //Mencari ID Jenis Barang
                            DBConnect connection2 = new DBConnect();
                            try {
                                connection2.stat = connection2.conn.createStatement();
                                String sql2 = "SELECT * FROM tblJenisBarang WHERE nama_jenis = ?";
                                connection2.pstat = connection2.conn.prepareStatement(sql2);
                                connection2.pstat.setString(1, cmbJenis.getSelectedItem().toString());
                                connection2.result = connection2.pstat.executeQuery();

                                while (connection2.result.next()) {
                                    connection.pstat.setInt(2,connection2.result.getInt("id_jenis_barang") );
                                }

                                connection2.pstat.close();
                                connection2.result.close();
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(null, "Error while loading data: " + ex);
                            }
                            JOptionPane.showMessageDialog(null, cmbRak.getSelectedItem().toString());
                            if (!huruflama.equals(cmbRak.getSelectedItem().toString())) {
                                // Update jumlah barang dalam rak lama
                                DBConnect connection4 = new DBConnect();
                                try  {
                                    String sql4 = "UPDATE tblRak SET jumlah = jumlah - 1 WHERE huruf = ?";
                                    connection4.pstat = connection4.conn.prepareStatement(sql4);
                                    connection4.pstat.setString(1, huruflama);
                                    connection4.pstat.executeUpdate();
                                } catch (Exception ex) {
                                    JOptionPane.showMessageDialog(null, "Error while updating data: " + ex);
                                }

                                // Update jumlah barang dalam rak baru
                                DBConnect connection5 = new DBConnect();
                                try  {
                                    String sql5 = "UPDATE tblRak SET jumlah = jumlah + 1 WHERE huruf = ?";
                                    connection5.pstat = connection5.conn.prepareStatement(sql5);
                                    connection5.pstat.setString(1, cmbRak.getSelectedItem().toString());
                                    connection5.pstat.executeUpdate();
                                } catch (Exception ex) {
                                    JOptionPane.showMessageDialog(null, "Error while updating data: " + ex);
                                }
                            }


                            //Mencari ID Rak
                            DBConnect connection3 = new DBConnect();
                            try {
                                connection3.stat = connection3.conn.createStatement();
                                String sql3 = "SELECT id_rak, huruf, status FROM tblRak WHERE huruf = ?";
                                connection3.pstat = connection3.conn.prepareStatement(sql3);
                                connection3.pstat.setString(1, cmbRak.getSelectedItem().toString());
                                connection3.result = connection3.pstat.executeQuery();

                                while (connection3.result.next()) {

                                    if(connection3.result.getString("status").equals("1"))
                                        connection.pstat.setString(3,connection3.result.getString("id_rak"));
                                }

                                connection3.pstat.close();
                                connection3.result.close();
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(null, "Error while loading data: " + ex);
                            }



                            connection.pstat.setInt(4, Integer.parseInt(txtlayer.getText()));
                            connection.pstat.setString(5, txtNama.getText());
                            connection.pstat.setInt(6, Integer.parseInt(txtStockBarang.getText()));
                            connection.pstat.setString(7, cmbSatuan.getSelectedItem().toString());
                            connection.pstat.setBytes(8, imageBytes);
                            connection.pstat.setInt(9, Integer.parseInt(txtHargaBeli.getText().replaceAll(",", "")));
                            connection.pstat.setInt(10, Integer.parseInt(txtHargaJual.getText().replaceAll(",","")));
                            java.util.Date utilDate = chooser.getDate();
                            java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());

                            connection.pstat.setDate(11, sqlDate);



                            connection.pstat.executeUpdate();
                            connection.pstat.close();
                            JOptionPane.showMessageDialog(null, "Data Berhasil Di-Update!",
                                    "Informasi", JOptionPane.INFORMATION_MESSAGE);
                            clear(); //Mengosongkan semua textbox
                            loadData(); //Melakukan load data paling baru
                            tampilRak();
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                }
            }
        }
    });

    tbBarang.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
            long value1, value2;
            // Disable buttons
            btnDelete.setEnabled(false);
            btnUpdate.setEnabled(false);
            btnSave.setEnabled(false);

            i = tbBarang.getSelectedRow();
            if (i == -1) {
                return;
            }

            // Retrieve data from table model
            String nama = (String) model.getValueAt(i, 1);
            String jenis = model.getValueAt(i, 2).toString();
            String rak = model.getValueAt(i, 3).toString();
            huruflama = rak;
            String layer = model.getValueAt(i, 4).toString();
            String stock = model.getValueAt(i, 5).toString();

            //Mengambil nilai rupiah tanpa Format mata uang

            //a. Harga Beli
            // Menghapus semua karakter non-digit
            String nilaiStr = model.getValueAt(i, 7).toString();
            String angkaStr = nilaiStr.substring(nilaiStr.indexOf(" ") + 1).replaceAll("[^\\d]", "");
            angkaStr = angkaStr.substring(0, angkaStr.length() - 2);
            // Mengonversi string menjadi integer
            int hargaBeliInt = Integer.parseInt(angkaStr);
            String hargaBeli = String.valueOf(decimalFormat.format(hargaBeliInt));

            //b. Harga Jual
            // Menghapus semua karakter non-digit
            nilaiStr = model.getValueAt(i, 8).toString();
            angkaStr = nilaiStr.substring(nilaiStr.indexOf(" ") + 1).replaceAll("[^\\d]+", "");
            angkaStr = angkaStr.substring(0, angkaStr.length() - 2);
            // Mengonversi string menjadi integer
            int hargaJualInt = Integer.parseInt(angkaStr);
            String hargaJual = String.valueOf(decimalFormat.format(hargaJualInt));




            String satuan = model.getValueAt(i, 6).toString();

            // Execute the SwingWorker in the background
            String finalHargaBeli = hargaBeli;
            String finalHargaJual = hargaJual;

            SwingWorker<ImageIcon, Void> worker = new SwingWorker<ImageIcon, Void>() {
                @Override
                protected ImageIcon doInBackground() throws Exception {
                    // Retrieve the image from the database
                    return retrieveImageFromDatabase(model.getValueAt(i, 0).toString());
                }

                @Override
                protected void done() {
                    try {
                        // Get the image result from doInBackground()
                        ImageIcon imageIcon = get();

                        // Update the UI with the retrieved data
                        txtNama.setText(nama);
                        cmbJenis.setSelectedItem(jenis);

                        boolean rakDitemukan = false;
                        for (int i = 0; i < cmbRak.getItemCount(); i++) {
                            String hurufRak = cmbRak.getItemAt(i).toString();
                            if (hurufRak.equals(rak)) {
                                rakDitemukan = true;
                                break;
                            }
                        }

                        if (!rakDitemukan) {
                            cmbRak.addItem(rak);
                            cmbRak.setSelectedItem(rak);
                        }else{
                            cmbRak.setSelectedItem(rak);
                        }
                        
                        txtlayer.setText(layer);
                        txtStockBarang.setText(stock);
                        txtHargaBeli.setText(finalHargaBeli);
                        txtHargaJual.setText(finalHargaJual);
                        cmbSatuan.setSelectedItem(satuan);
                        chooser.setDate((java.sql.Date) model.getValueAt(i, 9));

                        // Resize image to fit JLabel
                        Image image = imageIcon.getImage().getScaledInstance(gambar_barang.getWidth(), gambar_barang.getHeight(), Image.SCALE_SMOOTH);
                        gambar_barang.setIcon(new ImageIcon(image));

                        // Enable buttons
                        btnSave.setEnabled(false);
                        btnUpdate.setEnabled(true);
                        btnDelete.setEnabled(true);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            };

            worker.execute(); // Start the SwingWorker
        }
    });
    btnDelete.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Menampilkan kotak dialog konfirmasi
            int option  = JOptionPane.showConfirmDialog(null, "Apakah Anda yakin ingin menghapus data ini?",
                    "Konfirmasi", JOptionPane.YES_OPTION, JOptionPane.INFORMATION_MESSAGE);
            // Menggunakan hasil pilihan dari kotak dialog
            if (option == JOptionPane.YES_OPTION) {
                // Proses penghapusan data
                try {
                    DBConnect connection = new DBConnect(); //Membuat object koneksi baru

                    int i = tbBarang.getSelectedRow(); //mengambil nilai baris yang di klik

                    if (i == -1) return; //Jika tidak ada data dari table yang dipilih
                    String id_barang = String.valueOf(model.getValueAt(i, 0)); //mengambil nilai id dari kolom pertama daribaris yang dipilih


                    String query = "EXEC sp_HapusBarang @id_barang=?";
                    connection.pstat = connection.conn.prepareStatement(query);
                    connection.pstat.setString(1, id_barang); //variabel id dari

                    // Update jumlah barang dalam rak lama
                    DBConnect connection4 = new DBConnect();
                    try  {
                        String sql4 = "UPDATE tblRak SET jumlah = jumlah - 1 WHERE huruf = ?";
                        connection4.pstat = connection4.conn.prepareStatement(sql4);
                        connection4.pstat.setString(1, String.valueOf(model.getValueAt(i, 3)));
                        connection4.pstat.executeUpdate();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Error while updating data: " + ex);
                    }

                    connection.stat.close();
                    connection.pstat.executeUpdate();
                    connection.pstat.close();

                    clear();
                    JOptionPane.showMessageDialog(null, "Data berhasil dihapus!",
                            "Informasi!", JOptionPane.INFORMATION_MESSAGE);
                    loadGambarawal();
                    loadData();
                    tampilRak();

                    btnUpdate.setEnabled(false);
                    btnDelete.setEnabled(false);
                    btnSave.setEnabled(true);

                } catch (Exception e1) {
                    JOptionPane.showMessageDialog(null, "an error occurred while updating data into the database.\n" + e1);
                }


            } else {
                // Tidak melakukan penghapusan data
                JOptionPane.showMessageDialog(null, "Data batal dihapus!",
                        "Informasi!", JOptionPane.INFORMATION_MESSAGE);            }
        }
    });
    btnCari.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            showByNama(model);
            Textbox_Pencarian.setText("");
        }
    });
    btRefresh.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            clear();
            loadData();
            tampilJenisBarang();
            tampilRak();
        }
    });
    btnCancel.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                loadGambarawal();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            clear();
            btnUpdate.setEnabled(false);
            btnDelete.setEnabled(false);
            btnSave.setEnabled(true);
        }
    });
    txtHargaBeli.addKeyListener(new KeyAdapter() {
        @Override
        public void keyReleased(KeyEvent e) {
            super.keyReleased(e);

            char c = e.getKeyChar();
            if (!Character.isDigit(c)) {
                // Mengabaikan karakter yang bukan angka
                txtHargaBeli.setText(txtHargaBeli.getText().replaceAll("[^\\d]", ""));
            }

            String input = txtHargaBeli.getText().trim().replaceAll(",", "");
            try {
                // Mengubah input menjadi angka
                long value = Long.parseLong(input);
                // Format angka dengan penambahan titik otomatis dan tampilkan dalam textbox
                txtHargaBeli.setText(decimalFormat.format(value));
            } catch (NumberFormatException ex) {
                // Jika input tidak valid, tidak melakukan apa-apa
            }
        }
    });
    txtHargaJual.addKeyListener(new KeyAdapter() {
        @Override
        public void keyReleased(KeyEvent e) {
            super.keyReleased(e);

            char c = e.getKeyChar();
            if (!Character.isDigit(c)) {
                // Mengabaikan karakter yang bukan angka
                txtHargaJual.setText(txtHargaJual.getText().replaceAll("[^\\d]", ""));
            }

            String input = txtHargaJual.getText().trim().replaceAll(",", "");
            try {
                // Mengubah input menjadi angka
                long value = Long.parseLong(input);
                // Format angka dengan penambahan titik otomatis dan tampilkan dalam textbox
                txtHargaJual.setText(decimalFormat.format(value));
            } catch (NumberFormatException ex) {
                // Jika input tidak valid, tidak melakukan apa-apa
            }
        }
    });

    }

    public void loadGambarawal() throws IOException {
        ImageIcon imageIcon = new ImageIcon(getClass().getResource("/Gambar/defaultbarang.jpg")); // Ganti dengan path gambar Anda
        Image image = imageIcon.getImage();

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



    public void showByNama(DefaultTableModel model) {
        model.getDataVector().removeAllElements();
        model.fireTableDataChanged();

        DBConnect connection = new DBConnect();
        try {
            connection.stat = connection.conn.createStatement();
            String query = "SELECT * FROM tblBarang WHERE nama_barang LIKE '%" + Textbox_Pencarian.getText() + "%'";
            connection.result = connection.stat.executeQuery(query);

            boolean found = false;

            while (connection.result.next()) {
                found = true;
                if (connection.result.getInt("status") == 1) {
                    Object[] obj = new Object[10];
                    obj[0] = connection.result.getString("id_barang");
                    obj[1] = connection.result.getString("nama_barang");

                    DBConnect connection2 = new DBConnect();
                    try {
                        connection2.pstat = connection2.conn.prepareStatement("SELECT id_jenis_barang, nama_jenis FROM tblJenisBarang WHERE id_jenis_barang = ?");
                        connection2.pstat.setString(1, connection.result.getString("id_jenis_barang"));
                        connection2.result = connection2.pstat.executeQuery();

                        while (connection2.result.next()) {
                            obj[2] = connection2.result.getString("nama_jenis");
                        }

                        connection2.pstat.close();
                        connection2.result.close();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Error while loading data: " + ex);
                    }

                    DBConnect connection3 = new DBConnect();
                    try {
                        connection3.pstat = connection3.conn.prepareStatement("SELECT id_rak, huruf FROM tblRak WHERE id_rak = ?");
                        connection3.pstat.setString(1, connection.result.getString("id_rak"));
                        connection3.result = connection3.pstat.executeQuery();

                        while (connection3.result.next()) {
                            obj[3] = connection3.result.getString("huruf");
                        }

                        connection3.pstat.close();
                        connection3.result.close();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Error while loading data: " + ex);
                    }

                    obj[4] = connection.result.getString("layer");
                    obj[5] = connection.result.getString("stock");
                    obj[6] = connection.result.getString("satuan");

                    String harga_beli = connection.result.getString("harga_beli");
                    int harga_beli_int = (int) Double.parseDouble(harga_beli);
                    obj[7] = "Rp. " + String.valueOf(harga_beli_int);

                    String harga_jual = connection.result.getString("harga_jual");
                    int harga_jual_int = (int) Double.parseDouble(harga_jual);
                    obj[8] = "Rp. " + String.valueOf(harga_jual_int);
                    obj[9] = connection.result.getDate("kadaluarsa");

                    model.addRow(obj);
                }
            }

            if(!found)
            {
                JOptionPane.showMessageDialog(null, "Nama Tidak Ada!!");
            }

            connection.result.close();
            connection.stat.close();

        } catch (Exception e) {
            System.out.println("Terjadi error saat load data item: " + e);
        }
    }

public void clear()
{
    try {
        loadGambarawal();
    } catch (IOException e) {
        throw new RuntimeException(e);
    }
    txtNama.setText("");
    cmbRak.setSelectedIndex(0);
    cmbJenis.setSelectedIndex(0);
    cmbSatuan.setSelectedIndex(0);
    txtlayer.setText("");
    txtStockBarang.setText("");
    txtHargaBeli.setText("");
    txtHargaJual.setText("");
    chooser.setDate(null);
}

    public void tampilJenisBarang() {
        Thread tampilJenisBarangThread = new Thread(new Runnable() {
            @Override
            public void run() {
                cmbJenis.removeAllItems();
                DBConnect connection = new DBConnect();
                try {
                    connection.stat = connection.conn.createStatement();
                    String sql = "SELECT * FROM tblJenisBarang";
                    connection.result = connection.stat.executeQuery(sql);

                    while (connection.result.next()) {
                        if (connection.result.getInt("status") == 1) {
                            cmbJenis.addItem(connection.result.getString("nama_jenis"));
                        }
                    }

                    connection.stat.close();
                    connection.result.close();
                } catch (Exception ex) {
                    System.out.println("Terjadi error saat load data jenis Kendaraan " + ex);
                }
                cmbJenis.insertItemAt("-- Pilih Jenis Barang --", 0);
                cmbJenis.setSelectedIndex(0);
            }
        });

        tampilJenisBarangThread.start();
    }


    public void tampilRak() {
        Thread tampilRakThread = new Thread(new Runnable() {
            @Override
            public void run() {
                cmbRak.removeAllItems();
                DBConnect connection = new DBConnect();
                try {
                    connection.stat = connection.conn.createStatement();
                    String sql = "SELECT huruf, status, id_karyawan, jumlah FROM tblRak";
                    connection.result = connection.stat.executeQuery(sql);

                    while (connection.result.next()) {
                        if (connection.result.getString("status").equals("1") && connection.result.getInt("jumlah") < 10) {
                            cmbRak.addItem(connection.result.getString("huruf"));
                        }
                    }

                    connection.stat.close();
                    connection.result.close();
                } catch (Exception ex) {
                    System.out.println("Terjadi error saat load data rak " + ex);
                }
                cmbRak.insertItemAt("-- Pilih Rak --", 0);
                cmbRak.setSelectedIndex(0);
            }
        });

        tampilRakThread.start();
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

    public void addColumn(){
        model.addColumn("ID Barang");
        model.addColumn("Nama Barang");
        model.addColumn("Jenis Barang");
        model.addColumn("Kode Rak");
        model.addColumn("Layer");
        model.addColumn("Stock");
        model.addColumn("Satuan");
        model.addColumn("Harga Beli");
        model.addColumn("Harga Jual");
        model.addColumn("Kadaluarsa");
    }

    public void loadData() {
        Thread loadDataThread = new Thread(new Runnable() {
            @Override
            public void run() {
                model.getDataVector().removeAllElements();
                model.fireTableDataChanged();

                try {
                    DBConnect connection = new DBConnect();
                    connection.stat = connection.conn.createStatement();
                    String query = "EXEC sp_LoadBarang";
                    connection.result = connection.stat.executeQuery(query);

                    while (connection.result.next()) {

                            Object[] obj = new Object[10];
                            obj[0] = connection.result.getString("id_barang");
                            obj[1] = connection.result.getString("nama_barang");

                            DBConnect connection2 = new DBConnect();
                            try {
                                connection2.pstat = connection2.conn.prepareStatement("SELECT id_jenis_barang, nama_jenis FROM tblJenisBarang WHERE id_jenis_barang = ?");
                                connection2.pstat.setString(1, connection.result.getString("id_jenis_barang"));
                                connection2.result = connection2.pstat.executeQuery();

                                while (connection2.result.next()) {
                                    obj[2] = connection2.result.getString("nama_jenis");
                                }

                                connection2.pstat.close();
                                connection2.result.close();
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(null, "Error while loading data: " + ex);
                            }

                            DBConnect connection3 = new DBConnect();
                            try {
                                connection3.pstat = connection3.conn.prepareStatement("SELECT id_rak, huruf FROM tblRak WHERE id_rak = ?");
                                connection3.pstat.setString(1, connection.result.getString("id_rak"));
                                connection3.result = connection3.pstat.executeQuery();

                                while (connection3.result.next()) {
                                    obj[3] = connection3.result.getString("huruf");
                                }

                                connection3.pstat.close();
                                connection3.result.close();
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(null, "Error while loading data: " + ex);
                            }

                            obj[4] = connection.result.getString("layer");
                            obj[5] = connection.result.getString("stock");
                            obj[6] = connection.result.getString("satuan");

                            String harga_beli = connection.result.getString("harga_beli");
                            int harga_beli_int = (int) Double.parseDouble(harga_beli);

                            NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
                            String harga_beli_str = formatRupiah.format(harga_beli_int);

                            // Menambahkan jarak satu spasi antara "Rp" dan angka
                            harga_beli_str = harga_beli_str.replace("Rp", "Rp ");


                            obj[7] = harga_beli_str;

                            String harga_jual = connection.result.getString("harga_jual");
                            int harga_jual_int = (int) Double.parseDouble(harga_jual);

                            String harga_jual_str = formatRupiah.format(harga_jual_int);

                            harga_jual_str = harga_jual_str.replace("Rp", "Rp ");

                            obj[8] = harga_jual_str;
                            obj[9] = connection.result.getDate("kadaluarsa");;

                            model.addRow(obj);

                    }
                    connection.stat.close();
                    connection.result.close();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error while loading data: " + ex);
                }
            }
        });

        loadDataThread.start();
    }


    public String generateNextBarangID() {
        DBConnect connection = new DBConnect();
        try {
            connection.pstat = connection.conn.prepareStatement("SELECT TOP 1 id_barang FROM tblBarang ORDER BY id_barang DESC");
            connection.result = connection.pstat.executeQuery();

            if (connection.result.next()) {
                String lastItemID = connection.result.getString("id_barang");
                int lastNumber = Integer.parseInt(lastItemID.substring(3));
                int nextNumber = lastNumber + 1;

                String nextItemID = "BRG" + String.format("%03d", nextNumber);
                return nextItemID;
            } else {
                return "BRG001";
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

    public ImageIcon retrieveImageFromDatabase(String id_barang) {
        ImageIcon imageIcon = null;

        try {
            // Establish database connection
            DBConnect connection = new DBConnect();

            // Prepare SQL statement
            String sq2 = "SELECT gambar_barang FROM tblBarang WHERE id_barang = ?";
            connection.pstat = connection.conn.prepareStatement(sq2);


            // Set the image ID parameter
            connection.pstat.setString(1, id_barang);

            connection.result = connection.pstat.executeQuery();


            if (connection.result.next()) {
                // Retrieve the image data as an input stream
                InputStream inputStream = connection.result.getBinaryStream("gambar_barang");

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


    public static void main(String[] args) {
        new CRUDBarang().setVisible(true);
    }
}

