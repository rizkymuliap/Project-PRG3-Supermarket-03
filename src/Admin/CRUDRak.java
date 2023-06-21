package Admin;

import connection.DBConnect;

import javax.jws.WebParam;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.SQLException;

public class CRUDRak extends JFrame {
    private JPanel JPRak;
    private JTextField txtNamaKaryawan;
    private JTextField txtKodeRak;
    private JTextField txtJumlah;
    private JButton btnSave;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnCancel;
    private JTextField txtSearch;
    private JButton btnSearch;
    private JTable tblRak;
    private JButton btnRefresh;
    private JTable TableKaryawan;

    private DefaultTableModel Model;
    DefaultTableModel Model2 = new DefaultTableModel(){
        @Override
        public boolean isCellEditable(int row, int column){
            return false;
        }
    };

    DBConnect connect = new DBConnect();

    String id_karyawan, id_rak, Nama;
    char Huruf;
    int Jumlah;

    public static void main(String[] args) {
        new CRUDRak().setVisible(true);
    }

    public void FrameConfig(){ //Con
        add(this.JPRak);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    public CRUDRak()
    {
        Model = new DefaultTableModel();
        Model2 = new DefaultTableModel();

        TableKaryawan.setModel(Model2);
        tblRak.setModel(Model);

        FrameConfig();
        addColumnDataKaryawan();
        addColumn();

        LoadDataKaryawan();




        btnSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showByNama(Model);
                txtSearch.setText("");
            }
        });

        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Huruf = txtKodeRak.getText().charAt(0);

                boolean found = false; // inisiasi awal kalau nama yang di input tidak sama


                // Mengambil jumlah baris pada table
                int baris = tblRak.getModel().getRowCount();

                for (int awal = 0; awal < baris; awal++) { // Mengulang pengecekan dari awal sampai jumlah baris


                    // Mengecek apakah nama jenis yang dimasukkan sama dengan nama pada kolom tertentu
                    if (Huruf == Model.getValueAt(awal, 2).toString().charAt(0)) {
                        found = true; // Menemukan data yang sama
                    }
                }

                if (found) { // Jika menemukan data yang sama pada tabel
                    JOptionPane.showMessageDialog(null, "Kode rak sudah digunakan!", "Information!",
                            JOptionPane.INFORMATION_MESSAGE); // Menampilkan pesan
                }else {
                    if (txtNamaKaryawan.getText().equals("") || txtKodeRak.getText().equals("") || txtJumlah.getText().equals("")) // Mengecek apakah txtbox kosong agar tidak ada data kosong
                    {
                        JOptionPane.showMessageDialog(null, "Tolong, isikan semua data!", "Warning!", JOptionPane.WARNING_MESSAGE); // Jika kosong maka akan menampilkan pesan data tidak boleh kosong
                    } else {
                        // Pencarian ID Karyawan
                        try {
                            DBConnect connection = new DBConnect();
                            connection.pstat = connection.conn.prepareStatement("SELECT id_karyawan FROM tblKaryawan WHERE nama_karyawan=?");
                            connection.pstat.setString(1, txtNamaKaryawan.getText());
                            connection.result = connection.pstat.executeQuery();

                            if (connection.result.next()) {
                                id_karyawan = connection.result.getString("id_karyawan");
                            }
                            connection.result.close();
                            connection.pstat.close();
                        } catch (SQLException ex) {
                            System.out.println("Terjadi error saat memeriksa id_karyawan terakhir: " + ex);
                        }

                        try {
                            DBConnect connection = new DBConnect();
                            String sql = "EXEC sp_InserttblRak @id_rak=?, @id_karyawan=?, @huruf=?, @jumlah=?, @status=1";

                            connection.pstat = connection.conn.prepareStatement(sql);

                            connection.pstat.setString(1, generateNextRakID()); // generate id supplier sebagai PK
                            connection.pstat.setString(2, id_karyawan);
                            connection.pstat.setString(3, txtKodeRak.getText()); // alamat sebagai parameter ketiga
                            connection.pstat.setInt(4, Integer.parseInt(txtJumlah.getText())); // notelp sebagai parameter keempat
                            connection.pstat.executeUpdate();
                            connection.pstat.close();

                            clear(); // Mengosongkan semua textbox

                            LoadDataKaryawan();
                            JOptionPane.showMessageDialog(null, "Data Rak berhasil disimpan!", "Informasi",
                                    JOptionPane.INFORMATION_MESSAGE); // Menampilkan pesan berhasil input data Supplier
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                }

            }
        });
        btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean found = false;
                // validasi tidak boleh sama
                Object[] obj = new Object[1];
                obj[0] = txtNamaKaryawan.getText();

                if(found) {
                    JOptionPane.showMessageDialog(null, "Data Rak sudah ada!", "Information"
                            , JOptionPane.INFORMATION_MESSAGE); //Jika Sudah diinput
                }else
                {
                    if (txtNamaKaryawan.getText().equals("") || txtKodeRak.getText().equals("") || txtJumlah.getText().equals("")) //Mengecek apakah txtbox kosong agar tidak ada data kosong
                    {
                        JOptionPane.showMessageDialog(null, "Tolong, isikan semua data!", "Warning!",
                                JOptionPane.WARNING_MESSAGE);
                    }
                    else
                    {
                        try {
                            int i = tblRak.getSelectedRow();
                            if (i == -1) return;
                            id_rak = String.valueOf(Model.getValueAt(i, 0));
                            Nama = txtNamaKaryawan.getText();
                            Huruf = txtKodeRak.getText().charAt(0);
                            Jumlah = Integer.parseInt(txtJumlah.getText());

                            DBConnect connection = new DBConnect();

                            String query = "EXEC sp_UpdateRak @id_rak=?, @nama=?, @huruf=?, @jumlah=?, @status=1";
                            connection.pstat = connection.conn.prepareStatement(query);
                            connection.pstat.setString(1, id_rak);  // Memasukkan nilai id_rak
                            connection.pstat.setString(2, Nama);  // Memasukkan nilai Nama
                            connection.pstat.setString(3, String.valueOf(Huruf));  // Memasukkan nilai Huruf
                            connection.pstat.setInt(4, Jumlah);  // Memasukkan nilai Jumlah

                            connection.pstat.executeUpdate();
                            connection.stat.close();
                            connection.pstat.close();

                            clear();
                            JOptionPane.showMessageDialog(null, "Data updated successfully!");

                            LoadDataKaryawan();

                            btnUpdate.setEnabled(false);
                            btnDelete.setEnabled(false);
                            btnSave.setEnabled(true);

                        }
                        catch (SQLException ex) {
                            JOptionPane.showMessageDialog(null, "Please, enter the valid number.");
                        }
                    }

                }
            }
        });
        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnUpdate.setEnabled(true);
                btnDelete.setEnabled(true);
                btnSave.setEnabled(true);
                int opsi;
                if (txtNamaKaryawan.getText().equals("") || txtKodeRak.getText().equals("") || txtJumlah.getText().equals("")) //Mengecek apakah txtbox kosong agar tidak ada data kosong
                {
                    JOptionPane.showMessageDialog(null, "Tolong, isikan semua data!", "Warning!",
                            JOptionPane.WARNING_MESSAGE);
                }
                else
                {
                    try
                    {
                        int kode = tblRak.getSelectedRow();
                        opsi = JOptionPane.showConfirmDialog(null, "Yakin akan menghapus data rak?",
                                "Confirmation", JOptionPane.YES_OPTION, JOptionPane.WARNING_MESSAGE);
                        if (opsi != 0) {
                            JOptionPane.showMessageDialog(null, "Data failed to delete");
                        } else {
                            id_rak = String.valueOf(Model.getValueAt(kode, 0));
                            String query = "EXEC sp_DeleteRak @id_rak=?";
                            connect.pstat = connect.conn.prepareStatement(query);
                            connect.pstat.setString(1, id_rak);
                            connect.pstat.executeUpdate();
                            connect.pstat.close();
                        }
                    }
                    catch (SQLException ex)
                    {
                        JOptionPane.showMessageDialog(null, "Please, enter the valid number ."+ ex.getMessage());
                    }
                    JOptionPane.showMessageDialog(null, "Hapus data rak berhasil!", "Informasi",
                            JOptionPane.INFORMATION_MESSAGE);

                    LoadDataKaryawan();


                    btnSave.setEnabled(true);
                    btnUpdate.setEnabled(false);
                    btnDelete.setEnabled(false);

                    clear();
                }
            }
        });

        TableKaryawan.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int i = TableKaryawan.getSelectedRow();
                if(i == -1){
                    return;
                }
                txtNamaKaryawan.setText((String) Model2.getValueAt(i,1));

            }
        });

        tblRak.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                btnDelete.setEnabled(true);
                btnUpdate.setEnabled(true);
                btnSave.setEnabled(false);
                int i = tblRak.getSelectedRow();
                if(i == -1){
                    return;
                }
                txtNamaKaryawan.setText((String) Model.getValueAt(i,1));
                txtKodeRak.setText(Model.getValueAt(i,2).toString());
                txtJumlah.setText((String) Model.getValueAt(i,3));

            }
        });

    }

    public void addColumn(){
        Model.addColumn("ID Rak");
        Model.addColumn("ID Karyawan");
        Model.addColumn("Huruf");
        Model.addColumn("Jumlah");
    }

    public void clear()
    {
        txtJumlah.setText("");
        txtKodeRak.setText("");
        txtNamaKaryawan.setText("");
        txtSearch.setText("");
    }

    public void loadDataRak() {
        Model.getDataVector().removeAllElements();
        Model.fireTableDataChanged();

        try {
            DBConnect connection = new DBConnect();
            connection.stat = connection.conn.createStatement();
            String query = "EXEC sp_LoadRak";
            connection.result = connection.stat.executeQuery(query);

            while (connection.result.next()) {
                String namaKaryawan = "";
                try {
                    DBConnect connection2 = new DBConnect();
                    connection2.pstat = connection2.conn.prepareStatement("SELECT nama_karyawan FROM tblKaryawan WHERE id_karyawan=?");
                    connection2.pstat.setString(1, connection.result.getString("id_karyawan"));
                    connection2.result = connection2.pstat.executeQuery();

                    if (connection2.result.next()) {
                        namaKaryawan = connection2.result.getString("nama_karyawan");
                    }
                    connection2.result.close();
                    connection2.pstat.close();
                } catch (SQLException ex) {
                    System.out.println("Terjadi error saat memeriksa id_karyawan terakhir: " + ex);
                }

                if (connection.result.getInt("status") == 1) {
                    Object[] obj = new Object[4];
                    obj[0] = connection.result.getString("id_rak");
                    obj[1] = namaKaryawan;
                    obj[2] = connection.result.getString("huruf");
                    obj[3] = connection.result.getString("jumlah");
                    Model.addRow(obj);
                }
            }
            connection.stat.close();
            connection.result.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error while loading data: " + ex);
        }
    }


    public void showByNama(DefaultTableModel model) {
        model.getDataVector().removeAllElements();
        model.fireTableDataChanged();

        DBConnect connection = new DBConnect();
        try {
            connection.stat = connection.conn.createStatement();
            String query = "SELECT * FROM tblKaryawan WHERE nama_karyawan LIKE '%" + txtSearch.getText() + "%'";
            connection.result = connection.stat.executeQuery(query);

            while (connection.result.next()) {
                if (connection.result.getInt("status") == 1) {
                    Object[] obj = new Object[2];

                    obj[0] = connection.result.getString("id_karyawan");
                    obj[1] = connection.result.getString("nama_karyawan");

                    model.addRow(obj);
                }
            }

            connection.result.close();
            connection.stat.close();

        } catch (Exception e) {
            System.out.println("Terjadi error saat load data item: " + e);
        }
    }

    public void addColumnDataKaryawan()
    {
        Model2.addColumn("ID Karyawan");
        Model2.addColumn("Nama Karyawan");
    }

    public void LoadDataKaryawan(){
        Model.getDataVector().removeAllElements();
        Model.fireTableDataChanged();

        loadDataRak();

        try{
            connect.stat = connect.conn.createStatement();
            String query = "EXEC sp_LoadKaryawan_Rak";
            connect.result = connect.stat.executeQuery(query);

            while(connect.result.next()){
                Object[] obj = new Object[2];
                obj[0] = connect.result.getString("id_karyawan");
                obj[1] = connect.result.getString("nama_karyawan");

                Model2.addRow(obj);
            }
            connect.stat.close();
            connect.result.close();
            btnSave.setEnabled(true);
            btnUpdate.setEnabled(false);
            btnDelete.setEnabled(false);
        }
        catch(Exception e){
            JOptionPane.showMessageDialog(null, "an error occurred while loading data.\n" + e);
        }
    }

    public String generateNextRakID() {
        DBConnect connection = new DBConnect();
        try {
            connection.pstat = connection.conn.prepareStatement("SELECT TOP 1 id_rak FROM tblRak ORDER BY id_rak DESC");
            connection.result = connection.pstat.executeQuery();

            if (connection.result.next()) {
                String lastItemID = connection.result.getString("id_rak");
                int lastNumber = Integer.parseInt(lastItemID.substring(2));
                int nextNumber = lastNumber + 1;

                String nextItemID = "RK" + String.format("%03d", nextNumber);
                return nextItemID;
            } else {
                return "RK001";
            }
        } catch (Exception e) {
            System.out.println("Terjadi error saat memeriksa id_rak terakhir: " + e);
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

}
