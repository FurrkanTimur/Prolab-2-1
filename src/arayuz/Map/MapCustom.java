package arayuz.Map;

import arayuz.MapFrame;
import arayuz.Rota;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import javax.swing.*;
import java.awt.*;
import org.jxmapviewer.input.ZoomMouseWheelListenerCenter;
import org.jxmapviewer.input.PanMouseInputListener;
import javax.swing.event.MouseInputListener;
import java.util.HashSet;
import java.util.Set;
import java.awt.geom.Point2D;
import java.awt.MediaTracker;
import arayuz.Map.VeriSetiOkuma;
import java.util.List;

public class MapCustom extends JXMapViewer {
    public static int sayac = 0;
    public static double StartLon, StartLat, EndLat, EndLon;
    private final JFrame parentFrame;
    private MediaTracker tracker;
    
    // Markerlar için waypoint'ler ve durak tipleri
    private Set<StopInfo> stops;
    private GeoPosition startPosition;
    private GeoPosition endPosition;
    
    // Özel marker simgeleri için
    private ImageIcon busStopIcon;
    private ImageIcon tramStopIcon;
    private ImageIcon startIcon;
    private ImageIcon endIcon;

    // Durak bilgilerini tutmak için iç sınıf
    private class StopInfo {
        GeoPosition position;
        String type;

        StopInfo(GeoPosition position, String type) {
            this.position = position;
            this.type = type;
        }
    }

    public MapCustom(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        this.stops = new HashSet<>();
        this.startPosition = null;
        this.endPosition = null;
        this.tracker = new MediaTracker(this);
        
        // İkonları yükle
        try {
            // Otobüs durağı ikonu
            java.net.URL busStopUrl = getClass().getResource("/arayuz/Map/bus_stop.png");
            if (busStopUrl != null) {
                busStopIcon = new ImageIcon(busStopUrl);
                Image busImg = busStopIcon.getImage();
                tracker.addImage(busImg, 0);
            }

            // Tramvay durağı ikonu
            java.net.URL tramStopUrl = getClass().getResource("/arayuz/Map/tram.png");
            if (tramStopUrl != null) {
                tramStopIcon = new ImageIcon(tramStopUrl);
                Image tramImg = tramStopIcon.getImage();
                tracker.addImage(tramImg, 1);
            }

            // İkonların yüklenmesini bekle
            try {
                tracker.waitForAll();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("Simge yükleme işlemi tamamlandı.");
        } catch (Exception e) {
            System.out.println("İkon yükleme hatası: " + e.getMessage());
            e.printStackTrace();
        }
   
        // Harita yapılandırması
        setTileFactory(new DefaultTileFactory(new OSMTileFactoryInfo("", "https://b.tile.openstreetmap.fr/hot/")));
        setAddressLocation(new GeoPosition(40.7717, 29.9506)); // Başlangıç konumu
        setZoom(5);
        
        // Mouse event listener ekleme
        MouseInputListener mm = new PanMouseInputListener(this);
        addMouseListener(mm);
        addMouseMotionListener(mm);
        addMouseWheelListener(new ZoomMouseWheelListenerCenter(this));
        
        // JSON'dan durakları oku ve ekle
        addBusStops();
    }
    
    // Otobüs duraklarını ekle
    private void addBusStops() {
        String dosyaYolu = "C:\\Users\\Mustafa Furkan\\Desktop\\PROLAB-2-1\\src\\backend\\veriseti.json"; 
        VeriModeli veri = VeriSetiOkuma.okuJSON(dosyaYolu);
        for (Durak d : veri.getDuraklar()) {
            stops.add(new StopInfo(new GeoPosition(d.getLat(), d.getLon()), d.getType()));
        } 
    }

    // Başlangıç ve bitiş noktalarını güncelle
    private void updateRouteMarkers() {
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        // Merkez işaretçi
        Image pin = new ImageIcon(getClass().getResource("/arayuz/Map/pin.png")).getImage();
        int x = getWidth() / 2 - 12;
        int y = getHeight() / 2 - 24;
        g2.drawImage(pin, x, y, 36,36,null);
        
        // Özel nokta marker'ları çiz
        Rectangle viewport = getViewportBounds();
        
        // Durakları çiz
        for (StopInfo stop : stops) {
            Point2D point2D = getTileFactory().geoToPixel(stop.position, getZoom());
            Point p = new Point((int)(point2D.getX() - viewport.getX()), 
                            (int)(point2D.getY() - viewport.getY()));
            
            // Durak tipine göre ikon seç
            ImageIcon icon = null;
            if ("bus".equalsIgnoreCase(stop.type) && busStopIcon != null) {
                icon = busStopIcon;
            } else if ("tram".equalsIgnoreCase(stop.type) && tramStopIcon != null) {
                icon = tramStopIcon;
            }

            // İkonu çiz
            if (icon != null && icon.getImage() != null) {
                g2.drawImage(icon.getImage(), p.x - 12, p.y - 12, 36, 36, this);
            }
        }
        
        // Başlangıç noktasını çiz
        if (startPosition != null) {
            Point2D point2D = getTileFactory().geoToPixel(startPosition, getZoom());
            Point p = new Point((int)(point2D.getX() - viewport.getX()), 
                            (int)(point2D.getY() - viewport.getY()));
            g2.setColor(new Color(0, 180, 0, 200)); // Yarı saydam yeşil
            g2.fillOval(p.x - 8, p.y - 8, 16, 16);
            g2.setColor(Color.BLACK);
            g2.drawOval(p.x - 8, p.y - 8, 16, 16);
            g2.drawString("Başlangıç", p.x + 10, p.y);
        }
        
        // Bitiş noktasını çiz
        if (endPosition != null) {
            Point2D point2D = getTileFactory().geoToPixel(endPosition, getZoom());
            Point p = new Point((int)(point2D.getX() - viewport.getX()), 
                            (int)(point2D.getY() - viewport.getY()));
            g2.setColor(new Color(180, 0, 0, 200)); // Yarı saydam kırmızı
            g2.fillOval(p.x - 8, p.y - 8, 16, 16);
            g2.setColor(Color.BLACK);
            g2.drawOval(p.x - 8, p.y - 8, 16, 16);
            g2.drawString("Hedef", p.x + 10, p.y);
        }
        
        g2.dispose();
    }

    public Point convertGeoPositionToPoint(GeoPosition pos) {
        Rectangle viewport = getViewportBounds();
        Point2D point2D = getTileFactory().geoToPixel(pos, getZoom());
        return new Point((int)(point2D.getX() - viewport.getX()), 
                        (int)(point2D.getY() - viewport.getY()));
    }

    // showLocation metodu - buton tıklamasıyla çalışır
    public void showLocation() {
        new Thread(new Runnable() {
            private String[] args;
            @Override
            public void run() {
                // Harita merkezi koordinatları
                GeoPosition centerPosition = getCenterPosition();
                
                if(sayac == 0) {
                    // Başlangıç noktası
                    String location = "Latitude: " + centerPosition.getLatitude() + ", Longitude: " + centerPosition.getLongitude();
                    StartLat = centerPosition.getLatitude();
                    StartLon = centerPosition.getLongitude();
                    startPosition = centerPosition;
                    
                    System.out.println("Baslangic: " + location);
                    sayac++;
                    MapFrame.btnAction.setText("Hedef Konumu Girin");
                    
                    // Marker'ları güncelle
                    updateRouteMarkers();
                }
                else if(sayac == 1) {
                    // Bitiş noktası
                    String location = "Latitude: " + centerPosition.getLatitude() + ", Longitude: " + centerPosition.getLongitude();
                    EndLat = centerPosition.getLatitude();
                    EndLon = centerPosition.getLongitude();
                    endPosition = centerPosition;
                    
                    System.out.println("Bitis: " + location);
                    sayac++;       
                    MapFrame.btnAction.setText("Rota Tercihleri");
                    
                    // Marker'ları güncelle
                    updateRouteMarkers();
                }
                else if(sayac == 2) {
                    parentFrame.dispose();
                    backend.Main.main(args);
                    new Rota().setVisible(true);
                }
            }
        }).start();
    }
} 