package Admin;

import com.toedter.calendar.JDateChooser;
import connection.DBConnect;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CRUDEvent extends JFrame {
    private JPanel JPEvent;
    private JTextField txtNama;
    private JTextField txtMinBelanja;
    private JComboBox cbStatus;
    private JButton btnSave;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnClear;
    private JTextField txtSearch;
    private JButton btnRefresh;
    private JTable tblEvent;
    private JPanel JPTglMulai;
    private JPanel JPTglSelesai;
    private JRadioButton rbBundle;
    private JRadioButton rbDiskon;
    private JTextField txtNamaBundle;
    private JTextField txtDiskon;
    private JComboBox cbBundle;

    DefaultTableModel Model;

    DBConnect connect = new DBConnect();

    JDateChooser chooser = new JDateChooser();
    JDateChooser chooser1 = new JDateChooser();


    String Nama, status, jenis, idbundle, StatusTersedia, tanggalmulai, tanggalselesai, bundle, diskon;
    int minimal;
    double Diskon;

    public static void main(String[] args) {
        JFrame frame = new JFrame("CRUDEvent");
        frame.setContentPane(new CRUDEvent().JPEvent);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setSize(500, 500);
        frame.setVisible(true);
    }

    public CRUDEvent()
    {
        JPTglMulai.add(chooser);
        JPTglSelesai.add(chooser1);
        Model = new DefaultTableModel();
        tblEvent.setModel(Model);
        addColomn();
        loadData();
        ButtonGroup group = new ButtonGroup();
        group.add(rbBundle);
        group.add(rbDiskon);
        tampilBundle();



        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (txtNama.getText().equals("") || cbStatus.getSelectedItem().equals("") || txtMinBelanja.getText().equals("") || cbBundle.getSelectedItem().equals("")) {
                    JOptionPane.showMessageDialog(null, "Please fill in all data!");
                } else {
                    Nama = txtNama.getText();
                    JPTglMulai.add(chooser);
                    JPTglSelesai.add(chooser1);
                    minimal = Integer.parseInt(txtMinBelanja.getText());
                    idbundle = cbBundle.getSelectedItem().toString();
                    status = cbStatus.getSelectedItem().toString();
                    if (jenis.equals("Bundle")) {
                        Diskon = 0;
                        bundle = txtNamaBundle.getText();
                    } else {
                        Diskon = Integer.parseInt(txtDiskon.getText());
                        bundle = "";
                    }
                    Format format = new SimpleDateFormat("yyyy-MM-dd");
                    tanggalmulai = format.format(chooser.getDate());
                    tanggalselesai = format.format(chooser1.getDate());

                    try {
                        DBConnect connect = new DBConnect();
                        String query = "EXEC sp_InsertEvent ?, ?, ?, ?, ?, ?, ?, ?, ?, ?";
                        connect.pstat = connect.conn.prepareStatement(query);
                        connect.pstat.setString(1, generateNextEventID());
                        connect.pstat.setString(2, txtNama.getText());
                        connect.pstat.setString(3, format.format(chooser.getDate()));
                        connect.pstat.setString(4, format.format(chooser1.getDate()));
                        connect.pstat.setString(5, jenis);
                        connect.pstat.setString(6, idbundle);
                        connect.pstat.setDouble(7, Diskon);
                        connect.pstat.setString(8, txtMinBelanja.getText());
                        connect.pstat.setString(9, "1");
                        connect.pstat.setInt(10, 1);

                        connect.pstat.executeUpdate();
                        connect.pstat.close();

                        JOptionPane.showMessageDialog(null, "Data Berhasil ditambahkan!!");
                        loadData();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Eror saat Menyimpan ke dalam database." + ex);

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
            }
        });
        rbBundle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jenis = "Bundle";
                txtDiskon.setEnabled(false);
                cbBundle.setEnabled(true);
            }
        });
        rbDiskon.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jenis = "Diskon";
                cbBundle.setEnabled(false);
                txtDiskon.setEnabled(true);
            }
        });
        tblEvent.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int selectedRow = tblEvent.getSelectedRow();
                if (selectedRow == -1) {
                    return;
                }

                // Mendapatkan nilai dari kolom yang dipilih
                txtNama.setText((String) Model.getValueAt(selectedRow, 1));

                // Mendapatkan nilai tglMulai dari model tabel
                String tglMulaiStr = Model.getValueAt(selectedRow, 2).toString();
                Date tglMulai = null;
                try {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    tglMulai = format.parse(tglMulaiStr);
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }
                chooser.setDate(tglMulai);

                String tglSelesaiStr = Model.getValueAt(selectedRow, 3).toString();
                Date tglSelesai = null;
                try {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    tglSelesai = format.parse(tglSelesaiStr);
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }
                chooser1.setDate(tglSelesai);

                String jenisEvent = (String) Model.getValueAt(selectedRow, 4);
                if (jenisEvent.equals("Bundle")) {
                    rbBundle.setSelected(true);
                    rbDiskon.setSelected(false);
                } else {
                    rbBundle.setSelected(false);
                    rbDiskon.setSelected(true);
                }

                cbBundle.setSelectedItem(Model.getValueAt(selectedRow, 5).toString());
                txtDiskon.setText(String.valueOf(Model.getValueAt(selectedRow, 6)));
                txtMinBelanja.setText((String) Model.getValueAt(selectedRow, 7));

                btnSave.setEnabled(false);
                btnUpdate.setEnabled(true);
                btnDelete.setEnabled(true);
            }
        });

        btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (txtNama.getText().equals("") || cbStatus.getSelectedItem().equals("") || txtMinBelanja.getText().equals("") || txtNamaBundle.getText().equals("")) {
                    JOptionPane.showMessageDialog(null, "Please fill in all data!");
                } else {
                    Nama = txtNama.getText();
                    JPTglMulai.add(chooser);
                    JPTglSelesai.add(chooser1);
                    minimal = Integer.parseInt(txtMinBelanja.getText());
                    idbundle = cbBundle.getSelectedItem().toString();
                    status = cbStatus.getSelectedItem().toString();
                    if (jenis.equals("Bundle")) {
                        Diskon = 0;
                        bundle = txtNamaBundle.getText();
                    } else {
                        Diskon = Integer.parseInt(txtDiskon.getText());
                        bundle = "";
                    }
                    Format format = new SimpleDateFormat("yyyy-MM-dd");
                    tanggalmulai = format.format(chooser.getDate());
                    tanggalselesai = format.format(chooser1.getDate());

                    try {
                        DBConnect connect = new DBConnect();
                        String query = "EXEC sp_UpdateEvent ?, ?, ?, ?, ?, ?, ?, ?, ?"; // Ubah sp_UpdateEvent sesuai dengan nama stored procedure Anda
                        connect.pstat = connect.conn.prepareStatement(query);
                        connect.pstat.setString(1, generateNextEventID()); // Ubah ID dengan ID yang sesuai dengan data yang ingin diupdate
                        connect.pstat.setString(2, txtNama.getText());
                        connect.pstat.setString(3, format.format(chooser.getDate()));
                        connect.pstat.setString(4, format.format(chooser1.getDate()));
                        connect.pstat.setString(5, jenis);
                        connect.pstat.setString(6, idbundle);
                        connect.pstat.setDouble(7, Diskon);
                        connect.pstat.setString(8, txtMinBelanja.getText());
                        connect.pstat.setString(9, "1");

                        connect.pstat.executeUpdate();
                        connect.pstat.close();

                        JOptionPane.showMessageDialog(null, "Data Berhasil diperbarui!!");
                        loadData();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Error saat melakukan update ke dalam database: " + ex);
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
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int opsi;
                int selectedRow = tblEvent.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(null, "Please select a row to delete.");
                    return;
                }

                opsi = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this event?",
                        "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (opsi == JOptionPane.YES_OPTION) {
                    try {
                        String eventID = (String) Model.getValueAt(selectedRow, 0);
                        String query = "EXEC sp_DeleteEvent @id_event = ?";
                        connect.pstat = connect.conn.prepareStatement(query);
                        connect.pstat.setString(1, eventID);
                        connect.pstat.executeUpdate();
                        connect.pstat.close();

                        JOptionPane.showMessageDialog(null, "Event deleted successfully!");
                        loadData();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "An error occurred while deleting event from the database: " + ex);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Deletion canceled.");
                }
            }
        });

        btnRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String namaEvent = txtSearch.getText();
                if(!namaEvent.isEmpty())
                {
                    searchEvent(namaEvent);
                }
                else
                {
                    loadData();
                }
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
        cbBundle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }

    public void loadData() {
        DBConnect connection = null;
        try {
            Model.getDataVector().removeAllElements();
            Model.fireTableDataChanged();

            connection = new DBConnect();
            connection.stat = connection.conn.createStatement();
            String query = "EXEC sp_LoadEvent";
            connection.result = connection.stat.executeQuery(query);

            while (connection.result.next()) {
                Object[] obj = new Object[10];
                obj[0] = connection.result.getString("id_event");
                obj[1] = connection.result.getString("nama_event");
                obj[2] = connection.result.getString("tanggal_mulai");
                obj[3] = connection.result.getString("tanggal_berakhir");
                obj[4] = connection.result.getString("jenis_promo");
                obj[5] = connection.result.getString("id_bundle");
                obj[6] = connection.result.getString("diskon");
                String harga_beli = connection.result.getString("minimal_belanja");
                int harga_beli_int = (int) Double.parseDouble(harga_beli);

                NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
                String harga_beli_str = formatRupiah.format(harga_beli_int);

                // Menambahkan jarak satu spasi antara "Rp" dan angka
                harga_beli_str = harga_beli_str.replace("Rp", "Rp ");


                obj[7] = harga_beli_str;
                obj[8] = connection.result.getString("status_tersedia");
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


    private void searchEvent(String nama) {
        Model.getDataVector().removeAllElements();
        Model.fireTableDataChanged();

        try {
            String query = "EXEC sp_SearchEvent ?";
            connect.pstat = connect.conn.prepareStatement(query);
            connect.pstat.setString(1, nama);
            connect.result = connect.pstat.executeQuery();

            while (connect.result.next()) {
                Object[] obj = new Object[10];
                obj[0] = connect.result.getString("id_event");
                obj[1] = connect.result.getString("nama_event");
                obj[2] = connect.result.getString("tanggal_mulai");
                obj[3] = connect.result.getString("tanggal_berakhir");
                obj[4] = connect.result.getString("jenis_promo");
                obj[5] = connect.result.getString("id_bundle");
                obj[6] = connect.result.getDouble("diskon");
                obj[7] = connect.result.getString("minimal_belanja");
                obj[8] = connect.result.getString("status_tersedia");
                obj[9] = connect.result.getString("status");

                Model.addRow(obj);

                btnUpdate.setEnabled(true);
                btnDelete.setEnabled(true);
                txtSearch.setText("");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "An error occurred while searching data.\n" + e);
        }
    }


    public void addColomn(){
        Model.addColumn("ID Event");
        Model.addColumn("Nama Event");
        Model.addColumn("Tanggal Mulai");
        Model.addColumn("Tanggal Selesai");
        Model.addColumn("Jenis Event");
        Model.addColumn("Nama Bundle");
        Model.addColumn("Diskon");
        Model.addColumn("Minimal Belanja");
        Model.addColumn("Status Tersedia");
        Model.addColumn("Status");
    }

    public void clear()
    {
        txtNama.setText("");
        txtDiskon.setText("");
        txtMinBelanja.setText("");
        txtNamaBundle.setText("");
        cbStatus.setSelectedIndex(0);
        rbBundle.setSelected(false);
        rbDiskon.setSelected(false);
        chooser.setDate(null);
        chooser1.setDate(null);
    }

    public String generateNextEventID() {
        DBConnect connection = new DBConnect();
        try {
            connection.pstat = connection.conn.prepareStatement("SELECT TOP 1 id_event FROM tblEvent ORDER BY id_Event DESC");
            connection.result = connection.pstat.executeQuery();

            if (connection.result.next()) {
                String lastItemID = connection.result.getString("id_event");
                int lastNumber = Integer.parseInt(lastItemID.substring(3));
                int nextNumber = lastNumber + 1;

                String nextItemID = "ENT" + String.format("%03d", nextNumber);
                return nextItemID;
            } else {
                return "ENT001";
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

    public void tampilBundle() {
        Thread tampilBundleThread = new Thread(new Runnable() {
            @Override
            public void run() {
                cbBundle.removeAllItems();
                DBConnect connection = new DBConnect();
                try {
                    connection.stat = connection.conn.createStatement();
                    String sql = "SELECT id_bundle FROM tblBundle";
                    connection.result = connection.stat.executeQuery(sql);

                    while (connection.result.next()) {
                        String idBundle = connection.result.getString("id_bundle");
                        cbBundle.addItem(String.valueOf(idBundle));
                    }

                    connection.stat.close();
                    connection.result.close();
                } catch (Exception ex) {
                    System.out.println("Terjadi error saat load data jenis Bundle " + ex);
                }
                cbBundle.insertItemAt("-- Pilih Jenis Barang --", 0);
                cbBundle.setSelectedIndex(0);
            }
        });

        tampilBundleThread.start();

    }

}
