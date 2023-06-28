package kasir;

import connection.DBConnect;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

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
    private JTextField Textbox_ID;
    private JTextField Textbox_Nama_Barang;
    private JTextField Textbox_Stock;
    private JTextField Textbox_Harga;
    private JTextField Textbox_Warranty;
    private JButton Tombol_Tambah_Pesanan;
    private JButton Tombol_Hapus_Pesanan;
    private JButton Tombol_Batal_Pesanan;
    private JTextField Textbox_Jumlah_Pesanan;
    private JPanel Panel_Tabel;
    private JTextField Textbox_Pencarian;
    private JButton Tombol_Pencarian;
    private JButton Tombol_Refresh;
    private JComboBox Combobox_Filter;
    private JPanel Panel_Konten2;
    private JPanel Panel_Kontrol2;
    private JPanel Panel_Attribut2;
    private JPanel Pembatas_Tengah_Attribut2;
    private JTable Tabel_Item;
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
    private JTextField txtCustomerName;
    private JButton Tombol_Batalkan;
    private JButton Tombol_Bayar;
    private JScrollPane ScrollBarang;
    private DefaultTableModel model;

    byte[] imageBytes;

    public TransaksiPenjualan() {
        model = new DefaultTableModel();

        addColumn();
       loadData();



       // Panel_Tabel.setLayout(new BorderLayout()); // Menentukan layout BorderLayout pada Panel_Tabel
        //Panel_Tabel.add(ScrollBarang, BorderLayout.CENTER); // Menambahkan ScrollBarang ke dalam Panel_Tabel
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
        model.addColumn("Harga Beli");
        model.addColumn("Harga Jual");
    }

    public void loadData() {
        Thread loadDataThread = new Thread(new Runnable() {
            @Override
            public void run() {

                ScrollBarang.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

                JPanel panelContent = new JPanel();
                GridLayout gridLayout = new GridLayout(0, 5);
                gridLayout.setHgap(2);
                gridLayout.setVgap(2);
                panelContent.setLayout(gridLayout);





                try {
                    DBConnect connection = new DBConnect();
                    connection.stat = connection.conn.createStatement();
                    String query = "EXEC sp_LoadBarang";
                    connection.result = connection.stat.executeQuery(query);

                    while (connection.result.next()) {

                        JPanel panel = new JPanel();
                        panel.setPreferredSize(new Dimension(100, 300));
                        panel.setBackground(Color.LIGHT_GRAY);
                        panel.setLayout(new BorderLayout());

                        JLabel labelTop = new JLabel(connection.result.getString("nama_barang"));
                        String id_barang = connection.result.getString("id_barang");
                        JLabel labelMiddle = new JLabel();
                        labelMiddle.setPreferredSize(new Dimension(100,100));

                        // Execute the SwingWorker in the background
                        SwingWorker<ImageIcon, Void> worker = new SwingWorker<ImageIcon, Void>() {
                            @Override
                            protected ImageIcon doInBackground() throws Exception {
                                // Retrieve the image from the database
                                return retrieveImageFromDatabase(id_barang);
                            }

                            @Override
                            protected void done() {
                                try {
                                    // Get the image result from doInBackground()
                                    ImageIcon imageIcon = get();

                                    // Resize image to fit JLabel
                                    Image image = imageIcon.getImage().getScaledInstance(labelMiddle.getWidth(), labelMiddle.getHeight(), Image.SCALE_SMOOTH);
                                    labelMiddle.setIcon(new ImageIcon(image));


                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                        };


                        JLabel labelBottom = new JLabel("Label Bawah");

                        panel.add(labelTop, BorderLayout.NORTH);
                        panel.add(labelMiddle, BorderLayout.CENTER);
                        panel.add(labelBottom, BorderLayout.SOUTH);

                        panelContent.add(panel);
                    ScrollBarang.setViewportView(panelContent);

                    }



                    model.getDataVector().removeAllElements();
                    model.fireTableDataChanged();

                    connection.stat.close();
                    connection.result.close();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error while loading data: " + ex);
                }
            }
        });

        loadDataThread.start();
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
