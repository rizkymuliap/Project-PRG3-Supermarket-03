package Admin;

import connection.DBConnect;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class DetailBundle {
    private JPanel DetailBundle;
    private JTextField txtNamaBundle;
    private JTextField txtIDBundle;
    private JButton tambahBundleButton;
    private JTable tblBundle;
    private JButton cancelButton;
    private JButton simpanButton;
    private JButton btnBatal;

    DBConnect connect = new DBConnect();

    DefaultTableModel model = new DefaultTableModel();

    public static void main(String[] args) {
        JFrame frame = new JFrame("Transaksi Pengadaan");
        frame.setContentPane(new DetailBundle().DetailBundle);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setSize(600, 600);
    }

    public DetailBundle()
    {
        tblBundle.setModel(model);
        model.addColumn("ID Barang");
        model.addColumn("Nama Barang");
        model.addColumn("Harga Beli");
        txtNamaBundle.setEnabled(false);
        tambahBundleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.addRow(new Object[]{"", "", ""});
            }
        });
        tblBundle.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                super.keyTyped(e);
                if (e.getKeyChar() == KeyEvent.VK_ESCAPE) {  // Pengecekan tombol ESC pada keyboard
                    int i = tblBundle.getSelectedRow();
                    if (i == -1) {
                        return;
                    }
                    // Menghapus baris dari tabel
                    ((DefaultTableModel) tblBundle.getModel()).removeRow(i);
                } else {
                    String idbarang, nama;
                    int qty, harga, total;
                    int i = tblBundle.getSelectedRow();
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
                            //model.setValueAt(model.getValueAt(i, 2).toString().equals("") ? 1 : Integer.parseInt(model.getValueAt(i, 2).toString()), i, 2);
                            model.setValueAt(harga, i, 2);
                        }
                        connect.stat.close();
                        connect.result.close();
                    } catch (Exception ex) {
                        System.out.println("Terjadi eror saat mengambil data nama dan harga barang: " + ex);
                    }
                }
            }
        });
        simpanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String idBundle, NamaBundle;
                idBundle = txtIDBundle.getText();
                NamaBundle = txtNamaBundle.getText();
                int j = tblBundle.getModel().getRowCount();

                try
                {
                    for(int i = 0; i < j; i++) {
                        String sql = "INSERT INTO tblDetailBundle VALUES (?, ?)";
                        connect.pstat = connect.conn.prepareStatement(sql);
                        connect.pstat.setString(1, idBundle);
                        connect.pstat.setString(2, model.getValueAt(i, 0).toString());
                        connect.pstat.executeUpdate();

                        connect.pstat.close();
                    }
                    JOptionPane.showMessageDialog(null, "Insert Data Berhasil !!");
                }
                catch (Exception ex)
                {
                    System.out.println("Terjadi Error Pada Bagian Simpan : " + ex);
                }
            }
        });

        btnBatal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int i = tblBundle.getSelectedRow();
                if (i == -1) {
                    return;
                }
                // Menghapus baris dari tabel
                ((DefaultTableModel) tblBundle.getModel()).removeRow(i);
            }
        });
    }
}
