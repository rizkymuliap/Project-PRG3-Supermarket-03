package Admin;

import com.toedter.calendar.JDateChooser;
import connection.DBConnect;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Date;
import java.sql.SQLException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Objects;

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

    DefaultTableModel Model;

    DBConnect connect = new DBConnect();

    JDateChooser chooser = new JDateChooser();
    JDateChooser chooser1 = new JDateChooser();


    String Nama, status, jenis, NamaBundle, tanggalmulai, tanggalselesai, bundle, id_event;
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

    public CRUDEvent() {
        JPTglMulai.add(chooser);
        JPTglSelesai.add(chooser1);
        Model = new DefaultTableModel();
        tblEvent.setModel(Model);
        addColomn();
        loadData();
        ButtonGroup group = new ButtonGroup();
        group.add(rbBundle);
        group.add(rbDiskon);


        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (txtNama.getText().equals("") || cbStatus.getSelectedItem().equals("") || txtMinBelanja.getText().equals("") || txtNamaBundle.getText().equals("")) {
                    JOptionPane.showMessageDialog(null, "Please fill in all data!");
                } else {
                    Nama = txtNama.getText();
                    JPTglMulai.add(chooser);
                    JPTglSelesai.add(chooser1);
                    minimal = Integer.parseInt(txtMinBelanja.getText());
                    status = cbStatus.getSelectedItem().toString();
                    if (Objects.equals(jenis, "Bundle")) {
                        Diskon = 0.0;
                        bundle = txtNamaBundle.getText();
                    } else {
                        Diskon = Double.parseDouble(txtDiskon.getText());
                        bundle = "";
                    }

                    if (group.getSelection() != null) {
                        jenis = group.getSelection().getActionCommand();
                    }

                    Format format = new SimpleDateFormat("yyyy-MM-dd");
                    tanggalmulai = format.format(chooser.getDate());
                    tanggalselesai = format.format(chooser1.getDate());

                    try {
                        String query = "EXEC sp_InsertEvent ?, ?, ?, ?, ?, ?, ?, ?, ?, ?";
                        connect.pstat = connect.conn.prepareStatement(query);
                        connect.pstat.setString(1, generateNextEventID());
                        connect.pstat.setString(2, Nama);
                        connect.pstat.setString(3, tanggalmulai);
                        connect.pstat.setString(4, tanggalselesai);
                        connect.pstat.setString(5, jenis);
                        connect.pstat.setString(6, NamaBundle);
                        connect.pstat.setDouble(7, Diskon);
                        connect.pstat.setInt(8, minimal);
                        int tersedia = cbStatus.getSelectedIndex() == 0 ? 1 : 0;
                        connect.pstat.setInt(9, tersedia);
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
            }
        });
        rbDiskon.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jenis = "Diskon";
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
                String nama = (String) Model.getValueAt(selectedRow, 1);
                chooser.setDate((Date) tblEvent.getValueAt(selectedRow, 2));
                chooser1.setDate((Date) tblEvent.getValueAt(selectedRow, 3));
                if (Model.getValueAt(selectedRow, 4).toString().equals("Bundle")) {
                    rbBundle.setSelected(true);
                    rbDiskon.setSelected(false);
                } else {
                    rbDiskon.setSelected(true);
                    rbBundle.setSelected(false);
                }
                String namaBundle = (String) Model.getValueAt(selectedRow, 5);
                String Diskon = (String) Model.getValueAt(selectedRow, 6);
                String Minimal = (String) Model.getValueAt(selectedRow, 7);
                int tersedia = Model.getValueAt(selectedRow, 8).toString().equals("Tersedia") ? 0 : 1;

                txtNama.setText(nama);
                txtMinBelanja.setText(Minimal);
                cbStatus.setSelectedIndex(tersedia);
                txtNamaBundle.setText(namaBundle);
                txtDiskon.setText(Diskon);

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
                    status = cbStatus.getSelectedItem().toString();
                    if (Objects.equals(jenis, "Bundle")) {
                        Diskon = 0.0;
                        bundle = txtNamaBundle.getText();
                    } else {
                        Diskon = Double.parseDouble(txtDiskon.getText());
                        bundle = "";
                    }

                    if (group.getSelection() != null) {
                        jenis = group.getSelection().getActionCommand();
                    }
                    if (rbBundle.isSelected())
                    {
                        jenis = "Bundle";
                    }
                    else
                    {
                        jenis = "Diskon";
                    }
                    Format format = new SimpleDateFormat("yyyy-MM-dd");
                    tanggalmulai = format.format(chooser.getDate());
                    tanggalselesai = format.format(chooser1.getDate());

                    try {
                        String query = "EXEC sp_UpdateEvent ?, ?, ?, ?, ?, ?, ?, ?, ?";
                        int selectedRow = tblEvent.getSelectedRow();
                        connect.pstat = connect.conn.prepareStatement(query);
                        connect.pstat.setString(1, (String) Model.getValueAt(selectedRow, 0));
                        connect.pstat.setString(2, Nama);
                        connect.pstat.setString(3, tanggalmulai);
                        connect.pstat.setString(4, tanggalselesai);
                        connect.pstat.setString(5, jenis);
                        connect.pstat.setString(6, txtNamaBundle.getText());
                        connect.pstat.setDouble(7, Diskon);
                        connect.pstat.setInt(8, minimal);
                        int tersedia = cbStatus.getSelectedIndex() == 0 ? 1 : 0;
                        connect.pstat.setInt(9, tersedia);
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
        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int opsi;
                if (txtNama.getText().equals("") || cbStatus.getSelectedItem().equals("") || txtMinBelanja.getText().equals("") || txtNamaBundle.getText().equals("")) {
                    JOptionPane.showMessageDialog(null, "Please fill in all data!");
                }else{
                    try {
                        int kode = tblEvent.getSelectedRow();
                        opsi = JOptionPane.showConfirmDialog(null, "Are you sure delete this data?",
                                "Confirmation", JOptionPane.YES_OPTION, JOptionPane.WARNING_MESSAGE);
                        if (opsi != 0) {
                            JOptionPane.showMessageDialog(null, "Data failed to delete");
                        } else {
                            id_event = String.valueOf(Model.getValueAt(kode, 0));
                            String query = "EXEC sp_DeleteEvent @id_event=?";
                            connect.pstat = connect.conn.prepareStatement(query);
                            connect.pstat.setString(1, id_event);
                            connect.pstat.executeUpdate();
                            connect.pstat.close();
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
        btnRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String namaEvent = txtSearch.getText();
                if(!namaEvent.isEmpty())
                {
                    SearchEvent();
                }
                else
                {
                    loadData();
                }
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
                Object[] obj = new Object[9];
                if (connection.result.getInt("status") == 1) {
                    obj[0] = connection.result.getString("id_event");
                    obj[1] = connection.result.getString("nama_event");
                    obj[2] = connection.result.getDate("tanggal_mulai");
                    obj[3] = connection.result.getDate("tanggal_berakhir");
                    obj[4] = connection.result.getString("jenis_promo");
                    obj[5] = connection.result.getString("id_bundle");
                    obj[6] = connection.result.getString("diskon");
                    String tersedia = connection.result.getString("status_tersedia").equals("1") ? "Tersedia" : "Tidak Tersedia";
                    Double minimal = Double.parseDouble(connection.result.getString("minimal_belanja"));
                    int min = minimal.intValue();
                    obj[7] = String.valueOf(min);
                    obj[8] = tersedia;


                    Model.addRow(obj);
                }
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

    public void SearchEvent()
    {
            Model.getDataVector().removeAllElements();
            Model.fireTableDataChanged();
            try
            {
            String query = "EXEC sp_SearchEvent ?";
                connect.pstat = connect.conn.prepareStatement(query);
                connect.pstat.setString(1, txtSearch.getText());
                connect.result = connect.pstat.executeQuery();

            while (connect.result.next()) {

                Object[] obj = new Object[9];
                if (connect.result.getInt("status") == 1) {
                    obj[0] = connect.result.getString("id_event");
                    obj[1] = connect.result.getString("nama_event");
                    obj[2] = connect.result.getDate("tanggal_mulai");
                    obj[3] = connect.result.getDate("tanggal_berakhir");
                    obj[4] = connect.result.getString("jenis_promo");
                    obj[5] = connect.result.getString("id_bundle");
                    obj[6] = connect.result.getString("diskon");
                    String tersedia = connect.result.getString("status_tersedia").equals("1") ? "Tersedia" : "Tersedia";
                    Double minimal = Double.parseDouble(connect.result.getString("minimal_belanja"));
                    int min = minimal.intValue();
                    obj[7] = String.valueOf(min);
                    obj[8] = tersedia;

                    Model.addRow(obj);
                }
            }

            btnSave.setEnabled(true);
            btnUpdate.setEnabled(false);
            btnDelete.setEnabled(false);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "An error occurred while loading data.\n" + e);
        } finally {
            // Pastikan untuk selalu menutup koneksi setelah digunakan
            try {
                if (connect != null) {
                    if (connect.stat != null)
                        connect.stat.close();
                    if (connect.result != null)
                        connect.result.close();
                    if (connect.conn != null)
                        connect.conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

