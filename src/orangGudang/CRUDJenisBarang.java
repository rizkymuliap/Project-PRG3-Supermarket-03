package orangGudang;

import connection.DBConnect;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

        model = new DefaultTableModel();
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
                clear();
                loadData();
            }
        });
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

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
