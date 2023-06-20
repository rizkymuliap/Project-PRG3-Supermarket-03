package Admin;

import connection.DBConnect;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    private JTextField txtCariKaryawan;
    private JTable TableKaryawan;

    private DefaultTableModel Model;

    DBConnect connect = new DBConnect();

    String id_karyawan, id_rak, Huruf, Nama;
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
        tblRak.setModel(Model);
        FrameConfig();

        //addColumn();
        //loadDataRak();
        //LoadDataKaryawan();

        btnSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showByNama(Model);
            }
        });
        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Nama = txtNamaKaryawan.getText();
                Jumlah = Integer.parseInt(txtJumlah.getText());
                Huruf = txtKodeRak.getText();


                //Validasi agar tidak ada nama jenis barang yang sama
                boolean found = false; //inisiasi awal kalau nama yang di input tidak sama

                //Mengambil jumlah baris pada table
                int baris = tblRak.getModel().getRowCount();

                for(int awal = 0; awal < baris; awal++) //Mengulang pengecekan dari awal sampai jumlah baris
                {
                    //Mengecek apakah nama jenis yang dimasukkan sama dengan nama pada kolom tertentu
                    if(Nama.toLowerCase().equals(Model.getValueAt(awal, 1).toString().toLowerCase()))
                    {
                        found = true; //Menemukan data yang sama
                    }
                }

                if(found) //Jika menemukan data yang sama pada tabel
                {
                    JOptionPane.showMessageDialog(null, "Karyawan Sudah memegang Rak!", "Information!",
                            JOptionPane.INFORMATION_MESSAGE); //Menampilkan pesan

                }else{
                    if (txtNamaKaryawan.getText().equals("") || txtKodeRak.getText().equals("") || txtJumlah.getText().equals("")) //Mengecek apakah txtbox kosong agar tidak ada data kosong
                    {
                        JOptionPane.showMessageDialog(null, "Tolong, isikan semua data!", "Warning!"
                                , JOptionPane.WARNING_MESSAGE); //Jika kosong maka akan menampilkan pesan data tidak boleh kosong

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
                        } catch (SQLException ex) {
                            System.out.println("Terjadi error saat memeriksa id_karyawan terakhir: " + ex);
                        }

                        try {
                            DBConnect connection = new DBConnect();
                            String sql = "EXEC sp_InserttblRak @id_rak=?, @id_karyawan=?, @huruf=?, @jumlah=? ,@status=1";

                            connection.pstat = connection.conn.prepareStatement(sql);

                            connection.pstat.setString(1, generateNextRakID()); //generate id supplier sebagai PK

                            connection.pstat.setString(2,id_karyawan);
                            connection.pstat.setString(3, Huruf); //alamat sebagai parameter ketiga
                            connection.pstat.setInt(4, Jumlah); //notelp sebagai parameter keempat
                            connection.pstat.executeUpdate();
                            connection.pstat.close();


                            clear(); //Mengosongkan semua textbox

                            JOptionPane.showMessageDialog(null, "Data Supplier berhasil disimpan!", "Informasi",
                                    JOptionPane.INFORMATION_MESSAGE); //Menampilkan pesan berhasil input data Supplier
                           // loadDataRak();
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

                    }

                }
            }
        });
    }

    public void addColumn(){
        Model.addColumn("ID Rak");
        Model.addColumn("ID Karyawan");
        Model.addColumn("Huruf");
        Model.addColumn("Jumlah");
        Model.addColumn("Status");
    }

    public void clear()
    {
        txtJumlah.setText("");
        txtCariKaryawan.setText("");
        txtKodeRak.setText("");
        txtNamaKaryawan.setText("");
        txtSearch.setText("");
    }

    public void loadDataRak()
    {
        Model.getDataVector().removeAllElements();
        Model.fireTableDataChanged();

        try{
            DBConnect connection = new DBConnect();
            connection.stat = connection.conn.createStatement();
            String query = "EXEC sp_LoadRak";
            connection.result = connection.stat.executeQuery(query);

            while(connection.result.next()){
                if(connection.result.getInt("status") == 1) { //Mengecek apakah Rak masih tersedia
                    Object[] obj = new Object[5];
                    obj[0] = connection.result.getString("id_rak"); //Mengambil ID
                    obj[1] = connection.result.getString("id_karyawan"); //Mengambil nama
                    obj[2] = connection.result.getString("huruf"); //Mengambil nama
                    obj[3] = connection.result.getString("jumlah"); //Mengambil nama
                    obj[4] = connection.result.getString("status");
                    Model.addRow(obj);
                }
            }
            connection.stat.close();
            connection.result.close();
        }catch (Exception ex){
            JOptionPane.showMessageDialog(null, "Eror while loading for data : "+ex);
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

    public void LoadDataKaryawan(){
        Model.getDataVector().removeAllElements();
        Model.fireTableDataChanged();

        try{
            connect.stat = connect.conn.createStatement();
            String query = "EXEC sp_LoadKaryawan";
            connect.result = connect.stat.executeQuery(query);

            while(connect.result.next()){
                String temp = connect.result.getString("password");

                Object[] obj = new Object[8];
                obj[0] = connect.result.getString("id_karyawan");
                obj[1] = connect.result.getString("nama_karyawan");
                obj[2] = connect.result.getString("jenis_kelamin");
                obj[3] = connect.result.getString("no_telp");
                obj[4] = connect.result.getString("email");
                obj[5] = connect.result.getString("alamat");
                obj[6] = connect.result.getString("username");
                obj[7] = connect.result.getString("password");

                Model.addRow(obj);
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
