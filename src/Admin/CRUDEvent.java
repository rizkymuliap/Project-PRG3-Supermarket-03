package Admin;

import com.toedter.calendar.JDateChooser;
import connection.DBConnect;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.sql.SQLException;
import java.text.Format;
import java.text.SimpleDateFormat;

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


    String Nama, status, jenis, NamaBUndle, StatusTersedia, tanggalmulai, tanggalselesai, bundle, diskon;
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
                    if (jenis.equals("Bundle")) {
                        Diskon = 0;
                        bundle = txtNamaBundle.getText();
                    } else {
                        Diskon = Integer.parseInt(txtDiskon.getText());
                        bundle = "";
                    }

                   //System.out.print(Nama, chooser.getDate(), chooser1.getDate(), minimal, status.toString(), diskon.toString(), bundle.toString());

                    Format format = new SimpleDateFormat("yyyy-MM-dd");
                    tanggalmulai = format.format(chooser.getDate());
                    tanggalselesai = format.format(chooser1.getDate());

                    try {
                        DBConnect connect = new DBConnect();
                        String query = "EXEC sp_InsertEvent ?, ?, ?, ?, ?, ?, ?, ?, ?, ?";
                        connect.pstat = connect.conn.prepareStatement(query);
                        connect.pstat.setString(1, generateNextEventID());
                        System.out.println(generateNextEventID());
                        connect.pstat.setString(2, txtNama.getText());
                        System.out.println(txtNama.getText());
                        connect.pstat.setString(3, format.format(chooser.getDate()));
                        System.out.println(chooser);
                        connect.pstat.setString(4, format.format(chooser1.getDate()));
                        System.out.println(chooser1);
                        connect.pstat.setString(5, jenis);
                        System.out.println(jenis);
                        connect.pstat.setString(6, txtNamaBundle.getText());
                        System.out.println(txtNamaBundle.getText());

                        //Mncari Id bundle
//                        try
//                        {
//                            DBConnect connect1 = new DBConnect();
//                            connect1.stat = connect1.conn.createStatement();
//                            String SQL = "SELECT * FROM tblBundle WHERE nama_bundle LIKE '%" + txtNamaBundle.getText() + "%'";
//                            connect1.result = connect1.stat.executeQuery(SQL);
//                            while (connect1.result.next())
//                            {
//                                connect.pstat.setString(6, connect1.result.getString("id_bundle"));
//
//                            }
//
//                            connect1.stat.close();
//                            connect1.result.close();
//                        }
//                        catch (Exception ex)
//                        {
//                            System.out.println("Terjadi Error saat memasukan data : " + ex);
//                        }


                        connect.pstat.setDouble(7, Diskon);
                        System.out.println(Diskon);
                        connect.pstat.setString(8, txtMinBelanja.getText());
                        System.out.println(txtMinBelanja.getText());
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
            }
        });
        rbDiskon.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jenis = "Diskon";
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
                obj[7] = connection.result.getString("minimal_belanja");
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
}
