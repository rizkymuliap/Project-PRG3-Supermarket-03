package orangGudang;

import connection.DBConnect;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

public class CRUDJenisBarang extends JFrame{
    private JTable tbJenisBarang;
    private JButton cancelButton;
    private JButton saveButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JTextField txtJenisBarang;
    private JButton refreshButton;
    private JPanel panel1;

    DefaultTableModel model = new DefaultTableModel(){
        @Override
        public boolean isCellEditable(int row, int column){
            return false;
        }
    };
    DBConnect connection = new DBConnect();

    //Variabel
    String id, namaJenis;  //Untuk menampung nama jenis yang diinput

    public CRUDJenisBarang() {
        add(this.panel1);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);

        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);

        tbJenisBarang.setModel(model);

        addColumn(); //Menambahkan header dari table
        loadData(); //Memasukkan data dari database ke table

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //mengambil nama jenis dari text pada txtJenisBarang
                namaJenis = txtJenisBarang.getText();

                //Validasi agar tidak ada nama jenis barang yang sama
                boolean found = false; //inisiasi awal kalau nama yang di input tidak sama

                //Mengambil jumlah baris pada table
                int baris = tbJenisBarang.getModel().getRowCount();

                for(int awal = 0; awal < baris; awal++) //Mengulang pengecekan dari awal sampai jumlah baris
                {
                    //Mengecek apakah nama jenis yang dimasukkan sama dengan nama pada kolom tertentu
                    if(namaJenis.toLowerCase().equals(model.getValueAt(awal, 1).toString().toLowerCase()))
                    {
                        found = true; //Menemukan data yang sama
                    }
                }

                if(found) //Jika menemukan data yang sama pada tabel
                {
                    JOptionPane.showMessageDialog(null, "Jenis Barang sudah ada!", "Warning!",
                            JOptionPane.WARNING_MESSAGE); //Menampilkan pesan
                    txtJenisBarang.setText(""); //Mensetting agar textbox kosong
                }else{
                        if (txtJenisBarang.getText().equals("")) //Mengecek apakah txtbox kosong
                        {
                            JOptionPane.showMessageDialog(null, "Tolong, isikan semua data!", "Warning!"
                                    , JOptionPane.WARNING_MESSAGE); //Jika kosong maka akan menampilkan pesan data tidak boleh kosong
                        } else {
                            try {
                                String sql = "EXEC sp_InserttblJenisBarang @nama_jenis=?, @status=1";
                                connection.pstat = connection.conn.prepareStatement(sql);
                                connection.pstat.setString(1, namaJenis);
                                connection.pstat.executeUpdate();
                                connection.pstat.close();

                                clear(); //Mengosongkan semua textbox
                                JOptionPane.showMessageDialog(null, "Data jenis barang berhasil disimpan!", "Informasi",
                                        JOptionPane.INFORMATION_MESSAGE); //Menampilkan pesan berhasil input data jenis barang
                                loadData();
                            } catch (SQLException ex) {
                                throw new RuntimeException(ex);
                            }
                        }
                }

            }
        });
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadData();
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //mengaktifkan dan menonakt
                saveButton.setEnabled(true);
                updateButton.setEnabled(false);
                deleteButton.setEnabled(false);
                clear(); //Mengosongkan semua textbox
                loadData(); //melakukan refresh data table
            }
        });
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Menampilkan kotak dialog konfirmasi
                int option = JOptionPane.showConfirmDialog(null, "Apakah Anda yakin ingin menghapus data?", "Konfirmasi Penghapusan Data", JOptionPane.YES_NO_OPTION);

                // Menggunakan hasil pilihan dari kotak dialog
                if (option == JOptionPane.YES_OPTION) {
                    // Proses penghapusan data
                    try {
                        int i = tbJenisBarang.getSelectedRow();
                        if (i == -1) return; //Jika tidak ada data dari table yang dipilih
                        id = String.valueOf(model.getValueAt(i, 0)); //mengambil nilai id dari kolom pertama daribaris yang dipilih


                        String query = "EXEC sp_HapusJenisBarang @id_jenis_barang=?";
                        connection.pstat = connection.conn.prepareStatement(query);
                        connection.pstat.setString(1, id); //variabel id dari

                        connection.stat.close();
                        connection.pstat.executeUpdate();
                        connection.pstat.close();

                        clear();
                        JOptionPane.showMessageDialog(null, "Hapus Jenis Barang Berhasil!", "Informasi", JOptionPane.INFORMATION_MESSAGE);
                        loadData();

                        updateButton.setEnabled(false);
                        deleteButton.setEnabled(false);
                        saveButton.setEnabled(true);

                    } catch (Exception e1) {
                        JOptionPane.showMessageDialog(null, "an error occurred while updating data into the database.\n" + e1);
                    }


                } else {
                    // Tidak melakukan penghapusan data
                    JOptionPane.showMessageDialog(null, "Jenis Barang batal dihapus", "Informasi", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                namaJenis  = txtJenisBarang.getText();
                boolean found = false;
                // validasi tidak boleh sama
                Object[] obj = new Object[2];
                obj[0] = id;
                obj[1] = namaJenis;

                int j = tbJenisBarang.getModel().getRowCount();
                for(int k=0; k<j; k++){
                    if(obj[1].toString().toLowerCase().equals((model.getValueAt(k, 1).toString().toLowerCase()))){
                        found = true;
                    }
                }
                if(found) {
                    JOptionPane.showMessageDialog(null, "Tolong, isikan semua data!", "Warning!"
                            , JOptionPane.WARNING_MESSAGE); //Jika kosong maka akan menampilkan pesan data tidak boleh kosong
                } else{
                    try {
                        if (txtJenisBarang.getText().equals(""))
                            JOptionPane.showMessageDialog(null, "Please, fill in all data!");
                        else {
                            try {
                                int i = tbJenisBarang.getSelectedRow();
                                if (i == -1) return;
                                id = String.valueOf(model.getValueAt(i, 0));
                                namaJenis = txtJenisBarang.getText();

                                String query = "EXEC sp_UpdatetblJenisBarang @id_jenis=?, @nama_jenis=?";
                                connection.pstat = connection.conn.prepareStatement(query);
                                connection.pstat.setString(1, id);
                                connection.pstat.setString(2, namaJenis);

                                connection.stat.close();
                                connection.pstat.executeUpdate();
                                connection.pstat.close();

                                clear();
                                JOptionPane.showMessageDialog(null, "Data updated successfully!");
                                loadData();
                                updateButton.setEnabled(false);
                                deleteButton.setEnabled(false);
                                saveButton.setEnabled(true);

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

        tbJenisBarang.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int i = tbJenisBarang.getSelectedRow();
                if(i == -1){
                    return;
                }
                txtJenisBarang.setText((String) model.getValueAt(i,1));
                saveButton.setEnabled(false);
                updateButton.setEnabled(true);
                deleteButton.setEnabled(true);
            }
        });
    }

    public void loadData(){
        model.getDataVector().removeAllElements();
        model.fireTableDataChanged();

        try{
            connection.stat = connection.conn.createStatement();
            String query = "EXEC sp_LoadJenisBarang";
            connection.result = connection.stat.executeQuery(query);

            while(connection.result.next()){
                if(connection.result.getInt("status") == 1) { //Mengecek apakah jenis barang masih tersedia
                    Object[] obj = new Object[2];
                    obj[0] = connection.result.getString("id_jenis_barang"); //Mengambil ID
                    obj[1] = connection.result.getString("nama_jenis"); //Mengambil nama
                    model.addRow(obj);
                }
            }
            connection.stat.close();
            connection.result.close();
//            btSave.setEnabled(true);
//            btnHapus.setEnabled(false);
//            btUpdate.setEnabled(false);
        }catch (Exception ex){
            JOptionPane.showMessageDialog(null, "Eror while loading for data : "+ex);
        }
    }

    public void addColumn(){
        model.addColumn("ID Jenis Barang");
        model.addColumn("Nama Jenis Barang");
    }

    public void clear()
    {
        txtJenisBarang.setText("");
    }

    public static void main(String[] args) {
        new CRUDJenisBarang().setVisible(true);
    }
}
