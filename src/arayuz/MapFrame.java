package arayuz;

import arayuz.Map.MapCustom;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MapFrame extends JFrame {
public static JButton btnAction = new JButton("Baslangic Noktası Secin");
   
    public MapFrame() {
        // Pencere başlığı
        setTitle("Harita Görüntüsü");


        // Pencereyi kapatma işlemi
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Pencere boyutları
        setSize(1810, 958);

        // Ana panel (JPanel) oluşturuluyor
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());  // BorderLayout kullanıyoruz

        // Harita bileşenini oluşturuyoruz
        MapCustom mapCustom = new MapCustom(this);
        
        // Harita panelini oluşturuyoruz ve mapCustom'ı ekliyoruz
        JPanel mapPanel = new JPanel();
        mapPanel.setLayout(new BorderLayout());  // Harita için BorderLayout kullanıyoruz
        mapPanel.add(mapCustom, BorderLayout.CENTER);  // Harita paneli ortasında gösterilecek

        // Harita panelini ana panele ekliyoruz
        mainPanel.add(mapPanel, BorderLayout.CENTER);

        // Alt tarafa bir buton ekliyoruz
        JPanel buttonPanel = new JPanel();
      
        // Butona basıldığında harita merkezindeki konumu gösterelim
        btnAction.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Harita üzerinde tıklanan konumun koordinatlarını konsola yazdırıyoruz
                mapCustom.showLocation();
            }
        });

        // Buton panelini ana panele ekliyoruz
        buttonPanel.add(btnAction);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);  // Butonları alt tarafa ekliyoruz

        // Ana paneli pencereye ekliyoruz
        setContentPane(mainPanel);

        // Pencereyi görünür hale getiriyoruz
        setVisible(true);
    }

    // Main metodu, MapFrame'i başlatmak için
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // MapFrame nesnesini oluşturup görünür hale getiriyoruz
                new MapFrame().setVisible(true);
              
            }
        });
    }
}
