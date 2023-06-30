package Admin;

import connection.DBConnect;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.EditorKit;
import java.awt.event.*;
import java.sql.SQLException;

public class CRUDBundle extends JFrame {
    public JPanel JPBundle;
    private JTextField txtNamaBundle;
    private JButton btnSave;
    private JButton btnUpdate;
    private JButton btnHapus;
    private JButton btnCancel;
    private JTable tblBundle;
    private JTextField txtSearch;
    private JButton btnRefresh;

    private final int MAX_CHARACTERS = 30;


    DefaultTableModel Model;

    String Nama, id_bundle;

    DBConnect connect = new DBConnect();

    public static void main(String[] args) {
        JFrame frame = new JFrame("CRUDBundle");
        frame.setContentPane(new CRUDBundle().JPBundle);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setSize(500, 500);
        frame.setVisible(true);
    }

    public CRUDBundle() {
        Model = new DefaultTableModel();
        tblBundle.setModel(Model);
        addColomn();
        loadData();
        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (txtNamaBundle.getText().equals("")) {
                    JOptionPane.showMessageDialog(null, "Please fill in all data!");
                }
                Nama = txtNamaBundle.getText();
                try {
                    DBConnect connection = new DBConnect();
                    String sql = "EXEC sp_InsertBundle ?, ?";
                    connection.pstat = connection.conn.prepareStatement(sql);
                    connection.pstat.setString(1, generateNextBundleID());
                    connection.pstat.setString(2, Nama);

                    connection.pstat.executeUpdate();

                    JOptionPane.showMessageDialog(null, "Data Berhasil ditambahkan!!");
                    loadData();
                    clear();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error saat memasukan data : " + ex);
                } finally {
                    try {
                        if (connect.pstat != null) {
                            connect.pstat.close();
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        txtNamaBundle.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                if (txtNamaBundle.getText().length() > MAX_CHARACTERS) { //Jika inputan lebih dari max char > 50
                    e.consume(); // Mengkonsumsi event jika jumlah karakter lebih dari 50
                }

            }
        });
        btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (txtNamaBundle.getText().equals("")) {
                    JOptionPane.showMessageDialog(null, "Masukan data terlebih dahulu !!");
                } else {
                    Nama = txtNamaBundle.getText();

                    try {
                        DBConnect connect = new DBConnect();
                        String Sql = "EXEC sp_UpdateBundle @id_bundle = ?, @nama_bundle = ?";
                        int selectedRow = tblBundle.getSelectedRow();
                        connect.pstat = connect.conn.prepareStatement(Sql);
                        connect.pstat.setString(1, (String) Model.getValueAt(selectedRow, 0));
                        connect.pstat.setString(2, Nama);

                        connect.pstat.executeUpdate();
                        connect.pstat.close();

                        JOptionPane.showMessageDialog(null, "Data Berhasil Di-Update!!");
                        loadData();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Error Update : " + ex);
                    }
                }

            }
        });
        btnHapus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int opsi;
                if (txtNamaBundle.getText().equals("")) {
                    JOptionPane.showMessageDialog(null, "Pilih data terlebih dahulu ! ");
                } else {
                    try {
                        int kode = tblBundle.getSelectedRow();
                        opsi = JOptionPane.showConfirmDialog(null, "Apakah Anda Yakin ingin menghapus data ini ?", "Konfirmasi", JOptionPane.YES_OPTION, JOptionPane.WARNING_MESSAGE);
                        if (opsi != 0) {
                            JOptionPane.showMessageDialog(null, "Data failed to delete");
                        } else {
                            id_bundle = String.valueOf(Model.getValueAt(kode, 0));
                            String query = "EXEC sp_DeleteBundle @id_bundle=?";
                            connect.pstat = connect.conn.prepareStatement(query);
                            connect.pstat.setString(1, id_bundle);
                            connect.pstat.executeUpdate();
                            connect.pstat.close();
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "an error occurred while deleting data into the database.\n" + ex);
                    }
                    JOptionPane.showMessageDialog(null, "Data Berhasil diHapus!!");
                    loadData();
                    clear();
                }
            }
        });
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clear();
            }
        });
        tblBundle.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int selectedRow = tblBundle.getSelectedRow();
                if (selectedRow == -1) {
                    return;
                }
                String Nama = (String) Model.getValueAt(selectedRow, 1);

                txtNamaBundle.setText(Nama);

                btnSave.setEnabled(false);
                btnUpdate.setEnabled(true);
                btnHapus.setEnabled(true);
            }
        });
        btnRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String namaBundle = txtSearch.getText();
                if(!namaBundle.isEmpty())
                {
                    SearchBundle(namaBundle);
                }
                else
                {
                    loadData();
                }
            }
        });
    }

    public void addColomn(){
        Model.addColumn("Id Bundle");
        Model.addColumn("Nama Bundle");
    }
    public void clear()
    {
        txtNamaBundle.setText("");
    }


    public void loadData() {
        DBConnect connection = null;
        try {
            Model.getDataVector().removeAllElements();
            Model.fireTableDataChanged();

            connection = new DBConnect();
            connection.stat = connection.conn.createStatement();
            String query = "EXEC sp_LoadBundle";
            connection.result = connection.stat.executeQuery(query);

            while (connection.result.next()) {
                Object[] obj = new Object[2];
                obj[0] = connection.result.getString("id_bundle");
                obj[1] = connection.result.getString("nama_bundle");

                Model.addRow(obj);
            }

            btnSave.setEnabled(true);
            btnUpdate.setEnabled(false);
            btnHapus.setEnabled(false);
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

    public String generateNextBundleID() {
        DBConnect connection = new DBConnect();
        try {
            connection.pstat = connection.conn.prepareStatement("SELECT TOP 1 id_bundle FROM tblBundle ORDER BY id_bundle DESC");
            connection.result = connection.pstat.executeQuery();

            if (connection.result.next()) {
                String lastItemID = connection.result.getString("id_bundle");
                int lastNumber = Integer.parseInt(lastItemID.substring(3));
                int nextNumber = lastNumber + 1;

                String nextItemID = "BDL" + String.format("%03d", nextNumber);
                return nextItemID;
            } else {
                return "BDL001";
            }
        } catch (Exception e) {
            System.out.println("Terjadi error saat memeriksa id_supplier terakhir: " + e);
        } finally {
            try {
                if (this.connect.result != null) {
                    this.connect.result.close();
                }
                if (this.connect.pstat != null) {
                    this.connect.pstat.close();
                }
                if (this.connect.conn != null) {
                    this.connect.conn.close();
                }
            } catch (SQLException ex) {
                System.out.println("Terjadi error saat menutup koneksi: " + ex);
            }
        }

        return null;
    }

    public void SearchBundle(String Nama)
    {
        DBConnect connect = new DBConnect();
        Model.getDataVector().removeAllElements();
        Model.fireTableDataChanged();

        try {
            String query = "EXEC sp_SearchBundle ?";
            connect.pstat = connect.conn.prepareStatement(query);
            connect.pstat.setString(1, Nama);
            connect.result = connect.pstat.executeQuery();

            while (connect.result.next())
            {
                Object[] obj = new Object[2];
                obj[0] = connect.result.getString("id_bundle");
                obj[1] = connect.result.getString("nama_bundle");

                Model.addRow(obj);

                btnUpdate.setEnabled(true);
                btnHapus.setEnabled(true);
                txtSearch.setText("");
            }
        }
        catch (Exception ex)
        {
            JOptionPane.showMessageDialog(null, "An error occurred while searching data.\n" + ex);
        }
    }

}
