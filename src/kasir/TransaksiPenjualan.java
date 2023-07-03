package kasir;

import connection.DBConnect;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class TransaksiPenjualan {
    private JPanel JPTransaksiCustomer;
    private JPanel jptranscust;
    private JPanel Panel_Tengah;
    private JPanel Panel_Template;
    private JPanel Pembatas_Kiri;
    private JPanel Pembatas_Kanan;
    private JPanel Panel_Konten;
    private JPanel Panel_Kontrol;
    private JPanel Panel_Form;
    private JPanel Panel_Gambar;
    private JPanel Pembatas_Gambar_Atas;
    private JPanel Pembatas_Gambar_Bawah;
    private JTextField txtFileName;
    private JPanel Pembatas_Gambar_Kanan;
    private JPanel Panel_Konten_Gambar;
    private JLabel Label_Gambar;
    private JPanel Panel_Attribut;
    private JPanel Pembatas_Attribut_Atas;
    private JPanel Pembatas_Attribut_Tengah;
    private JTextField txtIDBarang;
    private JTextField txtNamaBarang;
    private JTextField txtStock;
    private JTextField txtHarga;
    private JButton Tombol_Tambah_Pesanan;
    private JButton Tombol_Batal_Pesanan;
    private JPanel Panel_Tabel;
    private JTextField Textbox_Pencarian;
    private JButton Tombol_Pencarian;
    private JButton Tombol_Refresh;
    private JComboBox cmbFilter;
    private JPanel Panel_Konten2;
    private JPanel Panel_Kontrol2;
    private JPanel Panel_Attribut2;
    private JPanel Pembatas_Tengah_Attribut2;
    private JTable tbItem;
    private JButton btCheckOut;
    private JPanel Panel_Tabel2;
    private JLabel Label_Tanggal;
    private JComboBox cmbPaymentMethod;
    private JTextField Textbox_Tunai;
    private JTextField Textbox_Kembalian;
    private JTextField Label_Total;
    private JTextField Label_TotalPay;
    private JTextField Label_MemberDisc;
    private JRadioButton rbYes;
    private JRadioButton rbNo;
    private JTextField txtSearchMemberID;
    private JButton btSearchIdMember;
    private JTextField txtIdCustomer;
    private JButton Tombol_Batalkan;
    private JButton Tombol_Bayar;
    private JScrollPane ScrollBarang;
    private JTable tbBarang;
    private DefaultTableModel model;
    private DefaultTableModel model2;


    byte[] imageBytes;
    int i;
    DecimalFormat decimalFormat = new DecimalFormat("#,###");

    public TransaksiPenjualan() {
        txtSearchMemberID.setEnabled(false);
        btSearchIdMember.setEnabled(false);

        ButtonGroup grup = new ButtonGroup();
        grup.add(rbYes);
        grup.add(rbNo);

        model = new DefaultTableModel();
        model2 = new DefaultTableModel();

        tbBarang.setModel(model);
        tbItem.setModel(model2);

        addColumn();
        addColumn2();

        loadData();



        tbBarang.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                long value1, value2;

                i = tbBarang.getSelectedRow();
                if (i == -1) {
                    return;
                }

                // Retrieve data from table model
                String nama = (String) model.getValueAt(i, 1);
                String stock = model.getValueAt(i, 5).toString();

                //Mengambil nilai rupiah tanpa Format mata uang
                // Menghapus semua karakter non-digit
                String nilaiStr = model.getValueAt(i, 7).toString();
                String angkaStr = nilaiStr.substring(nilaiStr.indexOf(" ") + 1).replaceAll("[^\\d]+", "");
                angkaStr = angkaStr.substring(0, angkaStr.length() - 2);
                // Mengonversi string menjadi integer
                int hargaJualInt = Integer.parseInt(angkaStr);
                String hargaJual = String.valueOf(decimalFormat.format(hargaJualInt));



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


                            // Resize image to fit JLabel
                            Image image = imageIcon.getImage().getScaledInstance(Label_Gambar.getWidth(), Label_Gambar.getHeight(), Image.SCALE_SMOOTH);
                            Label_Gambar.setIcon(new ImageIcon(image));
                            txtIDBarang.setText(model.getValueAt(i, 0).toString());
                            txtNamaBarang.setText(nama);
                            txtStock.setText(stock);
                            txtHarga.setText(hargaJual);

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                };

                worker.execute(); // Start the SwingWorker
            }
        });
        Tombol_Pencarian.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(cmbFilter.getSelectedItem() == "Filter"){
                    loadData();
                    Textbox_Pencarian.setText("");
                } else
                if (cmbFilter.getSelectedItem() == "Nama"){
                    showByNama(model);
                    Textbox_Pencarian.setText("");
                } else
                if(cmbFilter.getSelectedItem() == "Jenis Barang"){
                    showByJenisBarang(model);
                    Textbox_Pencarian.setText("");
                }else
                if (cmbFilter.getSelectedItem() == "Rak"){
                    showByRak(model);
                    Textbox_Pencarian.setText("");
                }
            }
        });

        Tombol_Refresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearDatabarang();
                loadData();
            }
        });
        Tombol_Batal_Pesanan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearDatabarang();
            }
        });
        Tombol_Tambah_Pesanan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean ketemu = false;
                for(int i = 0; i < model2.getRowCount() ; i++){
                    if(model2.getValueAt(i, 0).equals(txtIDBarang.getText())){
                        ketemu = true;
                        int jumlah = Integer.parseInt(model2.getValueAt(i, 2).toString());
                        jumlah++;

                        model2.setValueAt(jumlah, i, 2);
                        model2.setValueAt((jumlah * Integer.parseInt(model2.getValueAt(i, 3).toString().replaceAll(",",""))), i, 4);
                    }
                }
                if(!ketemu){
                    Object[] obj = new Object[5];
                    obj[0] = txtIDBarang.getText();
                    obj[1] = txtNamaBarang.getText();
                    obj[2] = "1";
                    obj[3] = txtHarga.getText();
                    obj[4] =  Integer.parseInt(obj[2].toString()) * Integer.parseInt(txtHarga.getText().replaceAll(",",""));
                    model2.addRow(obj);
                }
                clearDatabarang();
            }
        });

        tbItem.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                    int row = tbItem.getSelectedRow();
                    if(row == -1){
                        return;
                    }
                    if(model2.getValueAt(row, 2) == null){
                        model2.setValueAt(0, row, 4);
                    }else{
                        int jumlah = Integer.parseInt(model2.getValueAt(row, 2).toString()) * Integer.parseInt(model2.getValueAt(row, 3).toString().replaceAll(",",""));
                        model2.setValueAt(String.valueOf(jumlah), row, 4);
                    }

            }
        });
        rbYes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtSearchMemberID.setEnabled(true);
                btSearchIdMember.setEnabled(true);
            }
        });
        rbNo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtSearchMemberID.setEnabled(false);
                btSearchIdMember.setEnabled(false);
            }
        });
        btCheckOut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               int temp  = 0, total = 0;
               for(int i = 0; i < tbItem.getRowCount(); i++){
                   temp = Integer.parseInt(model2.getValueAt(i, 4).toString());
                   total = total + temp;
               }

               Label_Total.setText(String.valueOf(total));
            }
        });

    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Transaksi Penjualan");
        frame.setContentPane(new TransaksiPenjualan().JPTransaksiCustomer);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public void addColumn(){
        model.addColumn("ID Barang");
        model.addColumn("Nama Barang");
        model.addColumn("Jenis Barang");
        model.addColumn("Kode Rak");
        model.addColumn("Layer");
        model.addColumn("Stock");
        model.addColumn("Satuan");
        //model.addColumn("Harga Beli");
        model.addColumn("Harga Jual");
        //model.addColumn("Kadaluarsa");
    }

    public void addColumn2(){
        model2.addColumn("ID Barang");
        model2.addColumn("Nama Barang");
        model2.addColumn("Quantity");
        model2.addColumn("Harga");
        model2.addColumn("Total");
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

                        if(connection.result.getInt("stock") > 0 && connection.result.getInt("status") == 1) {

                            Object[] obj = new Object[8];
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

//                            String harga_beli = connection.result.getString("harga_beli");
//                            int harga_beli_int = (int) Double.parseDouble(harga_beli);

                            NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
                          //  String harga_beli_str = formatRupiah.format(harga_beli_int);

                            // Menambahkan jarak satu spasi antara "Rp" dan angka
                           // harga_beli_str = harga_beli_str.replace("Rp", "Rp ");


                            //obj[7] = harga_beli_str;

                            String harga_jual = connection.result.getString("harga_jual");
                            int harga_jual_int = (int) Double.parseDouble(harga_jual);

                            String harga_jual_str = formatRupiah.format(harga_jual_int);

                            harga_jual_str = harga_jual_str.replace("Rp", "Rp ");

                            obj[7] = harga_jual_str;
                            //obj[9] = connection.result.getDate("kadaluarsa");


                            model.addRow(obj);
                        }

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
                if (connection.result.getInt("status") == 1 && connection.result.getInt("stock") > 0) {
                    Object[] obj = new Object[8];
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

//                            String harga_beli = connection.result.getString("harga_beli");
//                            int harga_beli_int = (int) Double.parseDouble(harga_beli);

                    NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
                    //  String harga_beli_str = formatRupiah.format(harga_beli_int);

                    // Menambahkan jarak satu spasi antara "Rp" dan angka
                    // harga_beli_str = harga_beli_str.replace("Rp", "Rp ");


                    //obj[7] = harga_beli_str;

                    String harga_jual = connection.result.getString("harga_jual");
                    int harga_jual_int = (int) Double.parseDouble(harga_jual);

                    String harga_jual_str = formatRupiah.format(harga_jual_int);

                    harga_jual_str = harga_jual_str.replace("Rp", "Rp ");

                    obj[7] = harga_jual_str;
                    //obj[9] = connection.result.getDate("kadaluarsa");


                    model.addRow(obj);

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

    public void showByJenisBarang(DefaultTableModel model) {
        model.getDataVector().removeAllElements();
        model.fireTableDataChanged();

        // Membuat ArrayList untuk menampung hasil perintah
        String idJenisBarang = "";
        DBConnect connection4 = new DBConnect();
        try {
            connection4.pstat = connection4.conn.prepareStatement("SELECT id_jenis_barang FROM tblJenisBarang WHERE nama_jenis LIKE '%" + Textbox_Pencarian.getText() + "%'");
            connection4.result = connection4.pstat.executeQuery();

            while (connection4.result.next()) {
                idJenisBarang = connection4.result.getString("id_jenis_barang");

            }

            connection4.pstat.close();
            connection4.result.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error while loading data: " + ex);
        }

        //Pengambilan semua barang berdasarkan jenis

        DBConnect connection = new DBConnect();
        try {
            connection.stat = connection.conn.createStatement();
            // Membuat StringBuilder untuk membangun query
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("SELECT * FROM tblBarang WHERE ");

                queryBuilder.append("id_jenis_barang = " + idJenisBarang);

            // Mendapatkan string query yang lengkap
            String query = queryBuilder.toString();
            connection.result = connection.stat.executeQuery(query);

            boolean found = false;

            while (connection.result.next()) {
                found = true;
                if (connection.result.getInt("status") == 1 && connection.result.getInt("stock") > 0) {
                    Object[] obj = new Object[8];
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

//                            String harga_beli = connection.result.getString("harga_beli");
//                            int harga_beli_int = (int) Double.parseDouble(harga_beli);

                    NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
                    //  String harga_beli_str = formatRupiah.format(harga_beli_int);

                    // Menambahkan jarak satu spasi antara "Rp" dan angka
                    // harga_beli_str = harga_beli_str.replace("Rp", "Rp ");


                    //obj[7] = harga_beli_str;

                    String harga_jual = connection.result.getString("harga_jual");
                    int harga_jual_int = (int) Double.parseDouble(harga_jual);

                    String harga_jual_str = formatRupiah.format(harga_jual_int);

                    harga_jual_str = harga_jual_str.replace("Rp", "Rp ");

                    obj[7] = harga_jual_str;
                    //obj[9] = connection.result.getDate("kadaluarsa");


                    model.addRow(obj);

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

    public void showByRak(DefaultTableModel model) {
        model.getDataVector().removeAllElements();
        model.fireTableDataChanged();

        // Membuat ArrayList untuk menampung hasil perintah
        ArrayList<String> idRakList = new ArrayList<>();
        DBConnect connection4 = new DBConnect();
        try {
            connection4.pstat = connection4.conn.prepareStatement("SELECT id_rak FROM tblRak WHERE huruf LIKE '%" + Textbox_Pencarian.getText() + "%'");
            connection4.result = connection4.pstat.executeQuery();

            while (connection4.result.next()) {
                idRakList.add(connection4.result.getString("id_rak"));
            }

            connection4.pstat.close();
            connection4.result.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error while loading data: " + ex);
        }

        // Mengambil semua barang berdasarkan kode rak
        DBConnect connection = new DBConnect();
        try {
            connection.stat = connection.conn.createStatement();

            // Membuat query menggunakan kondisi LIKE dengan nilai-nilai id_rak
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("SELECT * FROM tblBarang WHERE id_rak IN (");

            // Menambahkan nilai-nilai id_rak ke dalam query
            for (String idRak : idRakList) {
                queryBuilder.append("'").append(idRak).append("',");
            }

            // Menghapus koma terakhir
            queryBuilder.setLength(queryBuilder.length() - 1);

            // Menutup kurung tutup query
            queryBuilder.append(")");

            // Mendapatkan string query yang lengkap
            String query = queryBuilder.toString();
            connection.result = connection.stat.executeQuery(query);

            boolean found = false;

            while (connection.result.next()) {
                found = true;
                if (connection.result.getInt("status") == 1 && connection.result.getInt("stock") > 0) {
                    Object[] obj = new Object[8];
                    obj[0] = connection.result.getString("id_barang");
                    obj[1] = connection.result.getString("nama_barang");

                    // Mengambil nama jenis barang
                    String idJenisBarang = connection.result.getString("id_jenis_barang");
                    DBConnect connection2 = new DBConnect();
                    try {
                        connection2.pstat = connection2.conn.prepareStatement("SELECT id_jenis_barang, nama_jenis FROM tblJenisBarang WHERE id_jenis_barang = ?");
                        connection2.pstat.setString(1, idJenisBarang);
                        connection2.result = connection2.pstat.executeQuery();

                        while (connection2.result.next()) {
                            obj[2] = connection2.result.getString("nama_jenis");
                        }

                        connection2.pstat.close();
                        connection2.result.close();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Error while loading data: " + ex);
                    }

                    // Mengambil huruf dari rak
                    String idRak = connection.result.getString("id_rak");
                    DBConnect connection3 = new DBConnect();
                    try {
                        connection3.pstat = connection3.conn.prepareStatement("SELECT id_rak, huruf FROM tblRak WHERE id_rak = ?");
                        connection3.pstat.setString(1, idRak);
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

                    NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

                    String harga_jual = connection.result.getString("harga_jual");
                    int harga_jual_int = (int) Double.parseDouble(harga_jual);

                    String harga_jual_str = formatRupiah.format(harga_jual_int);

                    harga_jual_str = harga_jual_str.replace("Rp", "Rp ");

                    obj[7] = harga_jual_str;

                    model.addRow(obj);
                }
            }

            if (!found) {
                JOptionPane.showMessageDialog(null, "Nama Tidak Ada!!");
            }

            connection.result.close();
            connection.stat.close();
        } catch (Exception e) {
            System.out.println("Terjadi error saat load data item: " + e);
        }
    }


    public void clearDatabarang(){
        Label_Gambar.setIcon(null);
        txtIDBarang.setText("");
        txtNamaBarang.setText("");
        txtStock.setText("");
        txtHarga.setText("");
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
}
