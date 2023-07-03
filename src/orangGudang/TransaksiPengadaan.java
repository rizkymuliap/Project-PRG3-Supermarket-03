package orangGudang;

import com.toedter.calendar.JDateChooser;
import connection.DBConnect;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.text.Format;
import java.text.SimpleDateFormat;

public class TransaksiPengadaan extends JFrame {
    private JPanel TransaksiPengdaan;
    private JTextField txtIDTransaksi;
    private JButton simpanButton;
    private JButton tambahPembelianButton;
    private JTable tblPengadaan;
    private JButton cancelButton;
    private JTextField txtTotal;
    private JPanel JPtglDatang;
    private JTextField txtIDKaryawan;
    private JTextField txtIDSupllier;
    private JPanel JPtglKembali;
    private JButton btnBatal;

    JDateChooser chooser = new JDateChooser();
    JDateChooser chooser1 = new JDateChooser();

    DBConnect connect = new DBConnect();

    DefaultTableModel model = new DefaultTableModel();

    String id_Transaksi, id_Karyawan, tglDatang, tglKembali, idSupplier;

    double total, totalakhir;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Transaksi Pengadaan");
        frame.setContentPane(new TransaksiPengadaan().TransaksiPengdaan);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setSize(600, 600);
    }

    public TransaksiPengadaan()
    {
        tblPengadaan.setModel(model);
        JPtglDatang.add(chooser);
        JPtglKembali.add(chooser1);
        model.addColumn("ID Barang");
        model.addColumn("Nama Barang");
        model.addColumn("QTY");
        model.addColumn("Harga");
        model.addColumn("Total Akhir");

        tambahPembelianButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.addRow(new Object[]{"", "", "", "", "", ""});
            }
        });
        tblPengadaan.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                if (e.getKeyChar() == KeyEvent.VK_ESCAPE) {  // Pengecekan tombol ESC pada keyboard
                    int i = tblPengadaan.getSelectedRow();
                    if (i == -1) {
                        return;
                    }
                    // Menghapus baris dari tabel
                    ((DefaultTableModel) tblPengadaan.getModel()).removeRow(i);
                } else {
                    String idbarang, nama;
                    int qty, harga, total;
                    int i = tblPengadaan.getSelectedRow();
                    if (i == -1) {
                        return;
                    }
                    idbarang = (String) model.getValueAt(i, 0);
                    try {
                        connect.stat = connect.conn.createStatement();
                        String sql = "SELECT nama_barang, harga_beli FROM tblBarang WHERE id_barang = '" + idbarang + "'";
                        connect.result = connect.stat.executeQuery(sql);
                        while (connect.result.next()) {
                            nama = connect.result.getString("nama_barang");
                            harga = connect.result.getInt("harga_beli");
                            model.setValueAt(nama, i, 1);
                            model.setValueAt(model.getValueAt(i, 2).toString().equals("") ? 1 : Integer.parseInt(model.getValueAt(i, 2).toString()), i, 2);
                            model.setValueAt(harga, i, 3);
                            model.setValueAt(harga * Integer.parseInt(model.getValueAt(i, 2).toString()), i, 4);
                        }
                        connect.stat.close();
                        connect.result.close();
                    } catch (Exception ex) {
                        System.out.println("Terjadi eror saat mengambil data nama dan harga barang: " + ex);
                    }
                }
            }
        });
        txtTotal.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int temp = 0, total = 0;
                int  i = tblPengadaan.getSelectedRow();
                if(i == -1)
                {
                    return;
                }
                int j = tblPengadaan.getModel().getRowCount();
                for (int k = 0; k < j; k++)
                {
                    temp = Integer.parseInt(model.getValueAt(k, 4).toString());
                    total = total + temp;
                }
                txtTotal.setText(String.valueOf(total));
            }
        });
        simpanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String idtrsPengadaan, IdKaryawan, tglDatang, tglKembali, stock, hargajual;
                double totalakhir;
                Format formatter = new SimpleDateFormat("yyyy-MM-dd");
                int j = tblPengadaan.getModel().getRowCount();
                idtrsPengadaan = txtIDTransaksi.getText();
                IdKaryawan = txtIDKaryawan.getText();
                tglDatang = formatter.format(chooser.getDate());
                tglKembali = formatter.format(chooser1.getDate());
                totalakhir = Double.parseDouble(txtTotal.getText());
                try
                {
                    String sql = "INSERT INTO tblTrsPengadaan VALUES (?, ?, ?, ?, ?)";
                    connect.pstat = connect.conn.prepareStatement(sql);
                    connect.pstat.setString(1, idtrsPengadaan);
                    connect.pstat.setString(2, IdKaryawan);
                    connect.pstat.setString(3, tglDatang);
                    connect.pstat.setString(4, tglKembali);
                    connect.pstat.setDouble(5, totalakhir);
                    connect.pstat.executeUpdate();

                    for(int i = 0; i < j; i++)
                    {
                        String sql1 = "INSERT INTO tblDetailPengadaan VALUES (?, ?, ?, ?)";
                        connect.pstat = connect.conn.prepareStatement(sql1);
                        connect.pstat.setString(1, idtrsPengadaan);
                        connect.pstat.setString(2, model.getValueAt(i, 0).toString());
                        connect.pstat.setString(3, model.getValueAt(i, 2).toString());
                        connect.pstat.setString(4, "0");

                        connect.pstat.executeUpdate();

                        DBConnect connection = new DBConnect();
                        String sql2 = "UPDATE tblBarang SET stock = ?, harga_jual = ? WHERE id_barang = ?";
                        connection.pstat = connect.conn.prepareStatement(sql2);
                        connection.pstat.setString(1, model.getValueAt(i, 2).toString());
                        connection.pstat.setString(2, model.getValueAt(i, 3).toString());
                        connection.pstat.executeUpdate();

                        connection.pstat.close();
                    }
                    connect.pstat.close();
                    JOptionPane.showMessageDialog(null, "Insert Data Berhasil !!");

                }
                catch (Exception ex)
                {
                    System.out.println("Terjadi eror : " + ex);
                }
            }
        });
        btnBatal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int i = tblPengadaan.getSelectedRow();
                if (i == -1) {
                    return;
                }
                // Menghapus baris dari tabel
                ((DefaultTableModel) tblPengadaan.getModel()).removeRow(i);
            }
        });
    }
}
