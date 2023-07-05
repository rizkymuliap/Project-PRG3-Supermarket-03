package orangGudang;

import com.toedter.calendar.JDateChooser;
import connection.DBConnect;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.SQLException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
    private JPanel JPtglKembali;
    private JButton btnBatal;
    private JComboBox cmbSupplier;

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

    public TransaksiPengadaan() {
        tblPengadaan.setModel(model);
        JPtglDatang.add(chooser);
        chooser.setDate(new Date());
        chooser1.setMinSelectableDate(getTomorrowDate());
        JPtglKembali.add(chooser1);
        model.addColumn("ID Barang");
        model.addColumn("Nama Barang");
        model.addColumn("QTY");
        model.addColumn("Harga");
        model.addColumn("Total Akhir");
        tampilSupplier();
        txtIDTransaksi.setText(generateNextPengadaanID());
        chooser.setEnabled(false);


        tambahPembelianButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                DBConnect connect1 = new DBConnect();
                int count = 0;

                try {
                    connect1.stat = connect1.conn.createStatement();
                    String sql = "SELECT COUNT(*) AS count " +
                            "FROM tblDetilPengadaan dp " +
                            "JOIN tblTrsPengadaan tp ON dp.id_trsPengadaan = tp.id_trsPengadaan " +
                            "WHERE dp.status = 1 AND tp.id_supplier = ?";
                    connect1.pstat = connect1.conn.prepareStatement(sql);
                    connect1.pstat.setString(1, getSupplierId(cmbSupplier.getSelectedItem().toString()));
                    connect1.result = connect1.pstat.executeQuery();

                    if (connect1.result.next()) {
                        count = connect1.result.getInt("count");
                        System.out.println(count);
                        // Lakukan sesuatu dengan nilai count yang ditangkap
                    }

                    connect1.stat.close();
                    connect1.result.close();
                } catch (Exception ex) {
                    System.out.println("Terjadi error saat load data supplier " + ex);
                }

                if (count > 0) {
                    JOptionPane.showMessageDialog(null, "Ada barang yang belum dikembalikan", "Peringatan!", JOptionPane.WARNING_MESSAGE);
                    return;
                } else {
                    if (tblPengadaan.getRowCount() == 0) {
                        Object[] row = {"", "", "", "", ""};
                        model.addRow(row);
                    } else {
                        boolean found = false;

                        for (int i = 0; i < tblPengadaan.getRowCount(); i++) {
                            if (model.getValueAt(i, 0) == null || model.getValueAt(i, 0).toString().isEmpty()) {
                                found = true;
                                break;
                            }
                        }

                        if (!found) {
                            Object[] row = {"", "", "", "", ""};
                            model.addRow(row);
                        }

                        try {
                            DBConnect connection = new DBConnect();
                            connection.pstat = connection.conn.prepareStatement("EXEC sp_barangSupplier @id_supplier = ?");
                            connection.pstat.setString(1, getSupplierId(cmbSupplier.getSelectedItem().toString()));
                            connection.result = connection.pstat.executeQuery();

                            while (connection.result.next()) {
                                String id_barang = connection.result.getString("id_barang");
                                found = false;

                                for (int i = 0; i < tblPengadaan.getRowCount(); i++) {
                                    if (id_barang.equals(model.getValueAt(i, 0))) {
                                        found = true;
                                        break;
                                    }
                                }

                                if (!found) {
                                    Object[] obj = new Object[5];
                                    obj[0] = id_barang;
                                    obj[1] = connection.result.getString("nama_barang");
                                    obj[2] = "1";
                                    obj[3] = connection.result.getString("harga_jual");
                                    obj[4] = connection.result.getString("harga_jual");
                                    // tambahkan kolom lain sesuai kebutuhan

                                    model.addRow(obj);
                                } else {
                                    model.addRow(new Object[]{"", "", "", "", ""});
                                }
                            }

                            connection.pstat.close();
                            connection.result.close();
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(null, "Error while loading data: " + ex);
                        }
                    }
                }

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
                int i = tblPengadaan.getSelectedRow();
                if (i == -1) {
                    return;
                }
                int j = tblPengadaan.getRowCount();
                total = 0;

                for (int k = 0; k < j; k++) {
                    String total_str = model.getValueAt(k, 4).toString(); // Menghilangkan semua karakter non-digit
                    temp = Integer.parseInt(total_str);
                    total += temp;
                }

                txtTotal.setText(String.valueOf(total));
            }
        });
        simpanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String idtrsPengadaan, IdKaryawan, IdSupplier, tglDatang, tglKembali, stock, hargajual;
                double totalakhir;
                Format formatter = new SimpleDateFormat("yyyy-MM-dd");
                idtrsPengadaan = txtIDTransaksi.getText();
                IdKaryawan = txtIDKaryawan.getText();
                IdSupplier = cmbSupplier.getSelectedItem().toString();
                Date tomorrowDate = getTomorrowDate();
                tglKembali = formatter.format(tomorrowDate.getTime());
                tglDatang = formatter.format(chooser.getDate());
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.setTime(chooser1.getDate());
                totalakhir = Double.parseDouble(txtTotal.getText());
                IdSupplier = getSupplierId(IdSupplier);


                try {
                    String sql = "INSERT INTO tblTrsPengadaan VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                    connect.pstat = connect.conn.prepareStatement(sql);
                    connect.pstat.setString(1, idtrsPengadaan);
                    connect.pstat.setString(2, IdKaryawan);
                    connect.pstat.setString(3, IdSupplier);
                    connect.pstat.setString(4, tglDatang);
                    connect.pstat.setString(5, tglKembali);
                    connect.pstat.setDouble(6, totalakhir);
                    connect.pstat.setString(7, "0");
                    connect.pstat.setString(8, "1");

                    connect.pstat.executeUpdate();
                    connect.pstat.close();

                    int j = tblPengadaan.getRowCount();
                    String sql1 = "INSERT INTO tblDetilPengadaan VALUES (?, ?, ?, ?, ?)";
                    connect.pstat = connect.conn.prepareStatement(sql1);

                    for (int i = 0; i < j; i++) {
                        connect.pstat.setString(1, idtrsPengadaan);
                        connect.pstat.setString(2, model.getValueAt(i, 0).toString());
                        connect.pstat.setString(3, model.getValueAt(i, 2).toString());
                        connect.pstat.setString(4, "0");
                        connect.pstat.setString(5, "1");
                        connect.pstat.executeUpdate();
                    }

                    String sql2 = "UPDATE tblBarang SET stock = ?, harga_jual = ? WHERE id_barang = ?";
                    connect.pstat = connect.conn.prepareStatement(sql2);

                    for (int k = 0; k < j; k++) {
                        connect.pstat.setString(1, model.getValueAt(k, 2).toString());
                        connect.pstat.setString(2, model.getValueAt(k, 3).toString());
                        connect.pstat.setString(3, model.getValueAt(k, 0).toString());
                        connect.pstat.executeUpdate();
                    }

                    connect.pstat.close();
                    JOptionPane.showMessageDialog(null, "Insert Data Berhasil !!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Terjadi eror : " + ex);
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

    public void tampilSupplier() {
        Thread tampilSupplierThread = new Thread(new Runnable() {
            @Override
            public void run() {
                cmbSupplier.removeAllItems();
                DBConnect connection = new DBConnect();
                try {
                    connection.stat = connection.conn.createStatement();
                    String sql = "SELECT id_supplier, nama_supplier, status FROM tblSupplier";
                    connection.result = connection.stat.executeQuery(sql);

                    while (connection.result.next()) {
                        if (connection.result.getString("status").equals("1")) {
                            cmbSupplier.addItem(connection.result.getString("nama_supplier"));
                        }
                    }

                    connection.stat.close();
                    connection.result.close();
                } catch (Exception ex) {
                    System.out.println("Terjadi error saat load data supplier " + ex);
                }
                cmbSupplier.setSelectedIndex(0);
            }
        });

        tampilSupplierThread.start();
    }

    public String getSupplierId(String supplierName) {
        String supplierId = "";

        try {
            DBConnect connect = new DBConnect();
            String sql = "SELECT id_supplier FROM tblSupplier WHERE nama_supplier = ?";
            connect.pstat = connect.conn.prepareStatement(sql);
            connect.pstat.setString(1, supplierName);
            connect.result = connect.pstat.executeQuery();

            if (connect.result.next()) {
                supplierId = connect.result.getString("id_supplier");
            }

            connect.pstat.close();
            connect.result.close();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

        return supplierId;
    }

    public String generateNextPengadaanID() {
        DBConnect connection = new DBConnect();
        try {
            connection.pstat = connection.conn.prepareStatement("SELECT TOP 1 id_trsPengadaan FROM tblTrsPengadaan ORDER BY id_trsPengadaan DESC");
            connection.result = connection.pstat.executeQuery();

            if (connection.result.next()) {
                String lastItemID = connection.result.getString("id_trsPengadaan");
                int lastNumber = Integer.parseInt(lastItemID.substring(3));
                int nextNumber = lastNumber + 1;

                String nextItemID = "PND" + String.format("%03d", nextNumber);
                return nextItemID;
            } else {
                return "PND001";
            }
        } catch (Exception e) {
            System.out.println("Terjadi error saat memeriksa id_trsPengadaan terakhir: " + e);
        }

        return null;
    }

    private Date getTomorrowDate() {
        Calendar tomorrowDate = Calendar.getInstance();
        tomorrowDate.add(Calendar.DAY_OF_YEAR, 1);
        setToBeginningOfDay(tomorrowDate);
        return tomorrowDate.getTime();
    }

    private void setToBeginningOfDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

}
