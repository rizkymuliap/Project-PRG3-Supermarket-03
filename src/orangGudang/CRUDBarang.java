package orangGudang;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class CRUDBarang extends JFrame{
    private JPanel PanelBarang;
    private JPanel Panel_Template;
    private JPanel Pembatas_Kiri;
    private JPanel Pembatas_Kanan;
    private JPanel Pembatas_Atas;
    private JPanel Pembatas_Bawah;
    private JPanel Panel_Konten;
    private JPanel Panel_Kontrol;
    private JPanel Panel_Form;
    private JPanel Panel_Gambar;
    private JPanel Pembatas_Gambar_Atas;
    private JPanel Pembatas_Gambar_Bawah;
    private JButton Tombol_Browse;
    private JPanel Pembatas_Gambar_Kanan;
    private JPanel Pembatas_Gambar_Kiri;
    private JPanel Panel_Konten_Gambar;
    private JLabel gambar_barang;
    private JTextField txtFileName;
    private JPanel Panel_Attribut;
    private JTextField txtStock;
    private JPanel Panel_Pembatas_Bantuan;
    private JTextField txtHargaBeli;
    private JTextField txtHargaJual;
    private JTextField txtNama;
    private JComboBox cmbCategory;
    private JComboBox cmbSupplier;
    private JPanel Panel_Tabel;
    private JTable tbBarang;
    private JTextField Textbox_Pencarian;
    private JButton btnSave;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnCari;
    private JButton btRefresh;
    private JTextField txtStockBarang;

    //Variabel
    String selectedImagePath = "";

    public void FrameConfigure()
    {
        add(this.PanelBarang);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

public CRUDBarang() {
        FrameConfigure();

    Tombol_Browse.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser browseImageFile = new JFileChooser();
            //Filter extensions
            FileNameExtensionFilter fnef = new FileNameExtensionFilter("IMAGES", "png", "jpg", "jpeg"); //Menentukan jenis file yang bisa

            browseImageFile.addChoosableFileFilter(fnef);
            int showOpenDialogue = browseImageFile.showOpenDialog(null);

            if (showOpenDialogue == JFileChooser.APPROVE_OPTION) {
                File selectedImageFile = browseImageFile.getSelectedFile();
                selectedImagePath = selectedImageFile.getAbsolutePath();

                JOptionPane.showMessageDialog(null, "Insert gambar barang berhasil!", "Informasi",
                        JOptionPane.INFORMATION_MESSAGE);

                //Display image on jlable
                ImageIcon ii = new ImageIcon(selectedImagePath);
                //Resize image to fit jlabel
                Image image = ii.getImage().getScaledInstance(gambar_barang.getWidth(), gambar_barang.getHeight(), Image.SCALE_SMOOTH);
                gambar_barang.setIcon(new ImageIcon(image));
                txtFileName.setText(selectedImagePath);
            }
        }
    });
    }

    public static void main(String[] args) {
        new CRUDBarang().setVisible(true);
    }
}

