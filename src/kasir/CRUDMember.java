package kasir;

<<<<<<< HEAD
import connection.DBConnect;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CRUDMember extends JFrame{
=======
import javax.swing.*;

public class CRUDMember {
>>>>>>> origin/master
    private JPanel JPMember;
    private JTextField txtNamaMember;
    private JTextField txtNotelp;
    private JTextField txtPoint;
    private JButton btnSave;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnCancel;
    private JTextField txtSearch;
    private JButton btnSearch;
    private JTable tblMember;
    private JButton btnRefresh;
<<<<<<< HEAD
    DefaultTableModel model = new DefaultTableModel(){
        @Override
        public boolean isCellEditable(int row, int column){
            return false;
        }
    };

    String namaMember, notelp, point, id_member;

    public CRUDMember() {

        FrameConfig();
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);

        tblMember.setModel(model);

        addColumn();
        loadData();

        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //mengambil nilai variabel dari textbox
                namaMember = txtNamaMember.getText();
                notelp = txtNotelp.getText();
                point = txtPoint.getText();


                //Validasi agar tidak ada nama jenis barang yang sama
                boolean found = false; //inisiasi awal kalau nama yang di input tidak sama

                //Mengambil jumlah baris pada table
                int baris = tblMember.getModel().getRowCount();

                for(int awal = 0; awal < baris; awal++) //Mengulang pengecekan dari awal sampai jumlah baris
                {
                    //Mengecek apakah nama jenis yang dimasukkan sama dengan nama pada kolom tertentu
                    if(notelp.toLowerCase().equals(model.getValueAt(awal, 2).toString().toLowerCase()))
                    {
                        found = true; //Menemukan data yang sama
                    }
                }

                if(found) //Jika menemukan data yang sama pada tabel
                {
                    JOptionPane.showMessageDialog(null, "Data Member sudah ada!", "Information!",
                            JOptionPane.INFORMATION_MESSAGE); //Menampilkan pesan

                }else{
                    if (txtNamaMember.getText().equals("") || txtPoint.getText().equals("") ||  txtNotelp.getText().equals("")) //Mengecek apakah txtbox kosong agar tidak ada data kosong
                    {
                        JOptionPane.showMessageDialog(null, "Tolong, isikan semua data!", "Warning!"
                                , JOptionPane.WARNING_MESSAGE); //Jika kosong maka akan menampilkan pesan data tidak boleh kosong

                    } else {
                        try {

                            Boolean valid_no_telp = validateInput(notelp);

                            if(!valid_no_telp){  //Melakukan cek apakah inputan no_telp valid
                                JOptionPane.showMessageDialog(null, "Nomor telepon harus dalam format 628xxx"
                                        , "Warning!", JOptionPane.WARNING_MESSAGE);
                                txtNotelp.setText("");
                                txtNotelp.requestFocus();
                                return;
                            }


                            DBConnect connection = new DBConnect();
                            String sql = "EXEC sp_InserttblMember @id_member=?, @nama_member=?, @no_telp=?, @point=? ,@status=1";

                            connection.pstat = connection.conn.prepareStatement(sql);
                            connection.pstat.setString(1,generateNextMemberID());
                            connection.pstat.setString(2,namaMember);
                            connection.pstat.setString(3,notelp);
                            connection.pstat.setString(4,point);


                            connection.pstat.executeUpdate();
                            connection.pstat.close();

                            clear(); //Mengosongkan semua textbox

                            JOptionPane.showMessageDialog(null, "Data Member berhasil disimpan!", "Informasi",
                                    JOptionPane.INFORMATION_MESSAGE); //Menampilkan pesan berhasil input data Supplier
                            loadData();
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
                obj[0] = txtNotelp.getText();


                if(found) {
                    JOptionPane.showMessageDialog(null, "Data Member sudah ada!", "Information"
                            , JOptionPane.INFORMATION_MESSAGE); //Jika Sudah diinput
                } else{
                    try {
                        if (txtNamaMember.getText().equals("") || txtNotelp.getText().equals("") || txtPoint.getText().equals(""))
                            JOptionPane.showMessageDialog(null, "Tolong, isikan semua data!", "Warning!",
                                    JOptionPane.WARNING_MESSAGE);
                        else {
                            try {
                                int i = tblMember.getSelectedRow();
                                if (i == -1) return;
                                 id_member = String.valueOf(model.getValueAt(i, 0));
                                namaMember = txtNamaMember.getText();
                                point = txtPoint.getText();
                                notelp = txtNotelp.getText();

                                //validasi inputan no telp
                                Boolean valid_no_telp = validateInput(notelp);
                                if(!valid_no_telp){  //Melakukan cek apakah inputan no_telp valid
                                    JOptionPane.showMessageDialog(null, "Nomor telepon harus dalam format 628xxx"
                                            , "Warning!", JOptionPane.WARNING_MESSAGE);
                                    txtNotelp.setText("");
                                    txtNotelp.requestFocus();
                                    return;
                                }


                                DBConnect connection = new DBConnect();

                                String query = "EXEC sp_UpdatetblMember @id_member=?, @nama_member=?, @no_telp=?, @point=?";
                                connection.pstat = connection.conn.prepareStatement(query);
                                connection.pstat.setString(1, id_member);
                                connection.pstat.setString(2, namaMember);
                                connection.pstat.setString(3, notelp);
                                connection.pstat.setString(4, point);

                                connection.stat.close();
                                connection.pstat.executeUpdate();
                                connection.pstat.close();

                                clear();
                                JOptionPane.showMessageDialog(null, "Update data member berhasil!", "Informasi"
                                , JOptionPane.INFORMATION_MESSAGE);

                                loadData();

                                btnUpdate.setEnabled(false);
                                btnDelete.setEnabled(false);
                                btnSave.setEnabled(true);

                            } catch (NumberFormatException nex) {
                                JOptionPane.showMessageDialog(null, "Please, enter the valid number.");
                            } catch (Exception e1) {
                                JOptionPane.showMessageDialog(null, "an error occurred while updating data into the database.\n" + e1);
                            }
                        }
                    } catch(Exception e1){

                    }
                }
            }
        });

        tblMember.addMouseListener(new MouseAdapter() { //Untuk mengatur agar saat di klik
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                btnDelete.setEnabled(true);
                btnUpdate.setEnabled(true);
                btnSave.setEnabled(false);

                int i = tblMember.getSelectedRow();
                if(i == -1){
                    return;
                }
                txtNamaMember.setText((String) model.getValueAt(i,1));
                txtNotelp.setText((String) model.getValueAt(i,2));
                txtPoint.setText((String) model.getValueAt(i,3));

                //Mengatur agar beberapa button diaktifkan
                btnSave.setEnabled(false); //dimatikan
                btnUpdate.setEnabled(true); //diaktifkan
                btnDelete.setEnabled(true); //diaktifkan
            }
        });
        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Menampilkan kotak dialog konfirmasi
                int option = JOptionPane.showConfirmDialog(null, "Apakah Anda yakin ingin menghapus data?", "Konfirmasi Penghapusan Data", JOptionPane.YES_NO_OPTION);

                // Menggunakan hasil pilihan dari kotak dialog
                if (option == JOptionPane.YES_OPTION) {
                    // Proses penghapusan data
                    try {
                        int i = tblMember.getSelectedRow();
                        if (i == -1) return; //Jika tidak ada data dari table yang dipilih
                        id_member = String.valueOf(model.getValueAt(i, 0)); //mengambil nilai id dari kolom pertama daribaris yang dipilih

                        DBConnect connection = new DBConnect();

                        String query = "EXEC sp_HapusMember @id_member=?";
                        connection.pstat = connection.conn.prepareStatement(query);
                        connection.pstat.setString(1, id_member); //variabel id baris yang dipilih

                        connection.stat.close();
                        connection.pstat.executeUpdate();
                        connection.pstat.close();

                        clear();
                        JOptionPane.showMessageDialog(null, "Hapus Member Berhasil!", "Informasi", JOptionPane.INFORMATION_MESSAGE);
                        loadData();

                        btnUpdate.setEnabled(false);
                        btnDelete.setEnabled(false);
                        btnSave.setEnabled(true);

                    } catch (Exception e1) {
                        JOptionPane.showMessageDialog(null, "an error occurred while updating data into the database.\n" + e1);
                    }


                } else {
                    // Tidak melakukan penghapusan data
                    JOptionPane.showMessageDialog(null, "Member batal dihapus", "Informasi", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        btnRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadData();
            }
        });
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadData();
                clear();
                btnSave.setEnabled(true);
                btnUpdate.setEnabled(false);
                btnDelete.setEnabled(false);
            }
        });
        btnSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showByNama(model);
                txtSearch.setText("");
            }
        });
    }

    public void showByNama(DefaultTableModel model) {
        model.getDataVector().removeAllElements();
        model.fireTableDataChanged();

        DBConnect connection = new DBConnect();
        try {
            connection.stat = connection.conn.createStatement();
            String query = "SELECT * FROM tblMember WHERE nama_member LIKE '%" + txtSearch.getText() + "%'";
            connection.result = connection.stat.executeQuery(query);

            while (connection.result.next()) {
                if (connection.result.getInt("status") == 1) {
                    Object[] obj = new Object[4];

                    obj[0] = connection.result.getString("id_member");
                    obj[1] = connection.result.getString("nama_member");
                    obj[2] = connection.result.getString("no_telp");
                    obj[3] = connection.result.getString("point");
                    model.addRow(obj);
                }
            }

            connection.result.close();
            connection.stat.close();

        } catch (Exception e) {
            System.out.println("Terjadi error saat load data item: " + e);
        }
    }

    public void addColumn(){
        model.addColumn("ID Member");
        model.addColumn("Nama Member");
        model.addColumn("No. Telepon");
        model.addColumn("Point");
    }

    public void loadData()
    {
        model.getDataVector().removeAllElements();
        model.fireTableDataChanged();

        try{
            DBConnect connection = new DBConnect();
            connection.stat = connection.conn.createStatement();
            String query = "EXEC sp_LoadMember";
            connection.result = connection.stat.executeQuery(query);

            while(connection.result.next()){
                if(connection.result.getInt("status") == 1) { //Mengecek apakah jenis barang masih tersedia
                    Object[] obj = new Object[5];
                    obj[0] = connection.result.getString("id_member"); //Mengambil ID
                    obj[1] = connection.result.getString("nama_member"); //Mengambil nama
                    obj[2] = connection.result.getString("no_telp"); //Mengambil no. telepon
                    obj[3] = connection.result.getString("point"); //Mengambil point
                    model.addRow(obj);
                }
            }
            connection.stat.close();
            connection.result.close();
        }catch (Exception ex){
            JOptionPane.showMessageDialog(null, "Eror while loading for data : "+ex);
        }
    }


    public void FrameConfig(){
        add(this.JPMember);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    public String generateNextMemberID() {
        DBConnect connection = new DBConnect();
        try {
            connection.pstat = connection.conn.prepareStatement("SELECT TOP 1 id_member FROM tblMember ORDER BY id_member DESC");
            connection.result = connection.pstat.executeQuery();

            if (connection.result.next()) {
                String lastItemID = connection.result.getString("id_member");
                int lastNumber = Integer.parseInt(lastItemID.substring(3));
                int nextNumber = lastNumber + 1;

                String nextItemID = "MBR" + String.format("%03d", nextNumber);
                return nextItemID;
            } else {
                return "MBR001";
            }
        } catch (Exception e) {
            System.out.println("Terjadi error saat memeriksa id_supplier terakhir: " + e);
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

    public static boolean validateInput(String input) { //Digunakan untuk validasi inputan agar berformat 628
        // Regex pattern untuk memvalidasi format input
        String regexPattern = "^628\\d{1,14}$";

        // Membuat objek Pattern dari regex pattern
        Pattern pattern = Pattern.compile(regexPattern);

        // Mencocokkan input dengan pattern menggunakan Matcher
        Matcher matcher = pattern.matcher(input);

        // Mengembalikan true jika input cocok dengan pattern, false jika tidak cocok
        return matcher.matches();
    }

    public static boolean validateEmail(String email) {
        // Regex pattern untuk validasi email
        String regexPattern = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

        // Membuat objek Pattern dari regex pattern
        Pattern pattern = Pattern.compile(regexPattern);

        // Mencocokkan email dengan pattern menggunakan Matcher
        Matcher matcher = pattern.matcher(email);

        // Mengembalikan true jika email cocok dengan pattern, false jika tidak cocok
        return matcher.matches();
    }

    public void clear()
    {
        txtNamaMember.setText("");
        txtNotelp.setText("");
        txtPoint.setText("");
        txtSearch.setText("");
    }

    public static void main(String[] args) {
        new CRUDMember().setVisible(true);
    }

=======
>>>>>>> origin/master
}
