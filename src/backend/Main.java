package backend;

import backend.VeriSetiOkuma;
import java.util.*;
import arayuz.BakiyeKart;
import backend.Yolcu;

public class Main {
	
    static double BitisMinMesafe ;
    static double BaslangicMinMesafe;
    static Arac.Taksi taksi = new Arac.Taksi();
    public static String sec1;
    public static String sec2;
    public static String sec3;
    
    static Yolcu.Ogrenci ogrenci = new Yolcu.Ogrenci();
    static Yolcu.Genel genel = new Yolcu.Genel();
    static Yolcu.Yasli yasli= new Yolcu.Yasli();
    
    public static void main(String[] args) {
    	
    	String dosyaYolu = "C:\\Users\\Mustafa Furkan\\Desktop\\PROLAB-2-1\\src\\backend\\veriseti.json"; 
        VeriModeli veri = VeriSetiOkuma.okuJSON(dosyaYolu);

        if (veri == null) {
            System.out.println("Veri okunamadı!");
            return;
        }else {
        	System.out.println("Veri başarıyla okundu ve işlendi.");
        }
        
        //BAŞLANGIÇ BE BİTİŞ KONUMUNUN ENLEM VE BOYLAMLARINI TUT
        double startLat = arayuz.Map.MapCustom.StartLat, startLon = arayuz.Map.MapCustom.StartLon;
        double endLat = arayuz.Map.MapCustom.EndLat, endLon = arayuz.Map.MapCustom.EndLon;
        
        //Başlangıç durağı için en yakın uygun durak ve taksi ya da yürüme seçeneğini bul.
        Durak baslangic = enYakinUygunBaslangicDurak(veri, startLat, startLon);   
        
        double baslangicUcret = TaksiYaDaYürüme(BaslangicMinMesafe);      	
        
        
        //Bitiş durağı için en yakın durak ve taksi ya da yürüme seçeneğini bul.
        Durak bitis = enYakinDurak(veri, endLat, endLon);

        double bitisUcret = TaksiYaDaYürüme(BitisMinMesafe);
        
        if (baslangic == null || bitis == null) {
            System.out.println("Başlangıç veya bitiş durağı bulunamadı.");
            return;
        }
        System.out.println("\nBaslangic Durağı : " +baslangic.getName());
        System.out.println("Bitiş Durağı: " + bitis.getName());

        GidisSonucu sonuc1 = enUcuzVeHizliGuzergah(veri, baslangic, bitis);

        if (sonuc1 == null || sonuc1.guzergah.isEmpty()) {
            System.out.println("Güzergah bulunamadı.");
            return;
        }
        
        //1. Seçenek olarak en ucuz rotayı bul.
        List<Durak> guzergah1 = sonuc1.guzergah;
        sec1 = Secenekler(guzergah1);
        
        //2.seçenek olarak süre olarak en kısa rotayı bul 
        GidisSonucu sonuc2 = enHizliVeUcuzGuzergah(veri,baslangic,bitis);
        if (sonuc2 == null || sonuc2.guzergah.isEmpty()) {
            System.out.println("Güzergah bulunamadı.");
            return;
        }

        List<Durak> guzergah2 = sonuc2.guzergah;
        sec2 = Secenekler(guzergah2);
        
        double taksiMesafe = MesafeHesaplama.uzaklik(startLat, startLon, endLat, endLon);
       
        if(taksiMesafe >3.0 )
        { sec3 = SadeceTaksiKullanimi(taksiMesafe);}
        else
        {
            sec3 = "Yürüyerek Gidilebilir" ;
        }
 
    }
    
    public static Durak enYakinDurak(VeriModeli veri, double lat, double lon) {
        Durak enYakin = null;

        BitisMinMesafe = Double.MAX_VALUE;
        for (Durak d : veri.getDuraklar()) {
            double mesafe = MesafeHesaplama.uzaklik(lat, lon, d.getLat(), d.getLon());

            if (mesafe < BitisMinMesafe) {
                BitisMinMesafe = mesafe;
                enYakin = d;
            }
        }

        return enYakin;
    }

    public static Durak enYakinUygunBaslangicDurak(VeriModeli veri, double lat, double lon) {
        Durak enUygun = null;
        BaslangicMinMesafe = Double.MAX_VALUE;

        for (Durak d : veri.getDuraklar()) {
            boolean gidilebilir = (d.getNextStops() != null && !d.getNextStops().isEmpty()) || d.getTransfer() != null;

            if (gidilebilir) {
                double mesafe = MesafeHesaplama.uzaklik(lat, lon, d.getLat(), d.getLon());

                if (mesafe < BaslangicMinMesafe) {
                    BaslangicMinMesafe = mesafe;
                    enUygun = d;
                }
            }
        }

        return enUygun;
    }

    public static GidisSonucu enUcuzVeHizliGuzergah(VeriModeli veri, Durak baslangic, Durak bitis) {
        class Durum {
            Durak durak;
            List<Durak> yol;
            double toplamUcret;
            int toplamSure;

            Durum(Durak d, List<Durak> y, double u, int s) {
                this.durak = d;
                this.yol = y;
                this.toplamUcret = u;
                this.toplamSure = s;
            }
        }

        PriorityQueue<Durum> kuyruk = new PriorityQueue<>(Comparator
            .comparingDouble((Durum d) -> d.toplamUcret)
            .thenComparingInt(d -> d.toplamSure));

        Set<String> ziyaretEdilen = new HashSet<>();
        kuyruk.add(new Durum(baslangic, new ArrayList<>(List.of(baslangic)), 0.0, 0));

        while (!kuyruk.isEmpty()) {
            Durum mevcut = kuyruk.poll();

            if (ziyaretEdilen.contains(mevcut.durak.getId())) continue;
            ziyaretEdilen.add(mevcut.durak.getId());

            if (mevcut.durak.getId().equals(bitis.getId())) {
                return new GidisSonucu(mevcut.yol, mevcut.toplamUcret, mevcut.toplamSure);
            }

            // nextStops
            for (NextStop ns : mevcut.durak.getNextStops()) {
                Durak komsu = veri.getDurakById(ns.getStopId());
                if (komsu != null && !ziyaretEdilen.contains(komsu.getId())) {
                    List<Durak> yeniYol = new ArrayList<>(mevcut.yol);
                    yeniYol.add(komsu);
                    kuyruk.add(new Durum(
                        komsu, yeniYol,
                        mevcut.toplamUcret + ns.getUcret(),
                        mevcut.toplamSure + ns.getSure()
                    ));
                }
            }

            // transfer
            if (mevcut.durak.getTransfer() != null) {
                Durak transferDurak = veri.getDurakById(mevcut.durak.getTransfer().getTransferStopId());
                if (transferDurak != null && !ziyaretEdilen.contains(transferDurak.getId())) {
                    List<Durak> yeniYol = new ArrayList<>(mevcut.yol);
                    yeniYol.add(transferDurak);
                    kuyruk.add(new Durum(
                        transferDurak, yeniYol,
                        mevcut.toplamUcret + mevcut.durak.getTransfer().getTransferUcret(),
                        mevcut.toplamSure + mevcut.durak.getTransfer().getTransferSure()
                    ));
                }
            }
        }

        return null;
    }
    
    public static GidisSonucu enHizliVeUcuzGuzergah(VeriModeli veri, Durak baslangic, Durak bitis) {

        class Durum {
            Durak durak;
            List<Durak> yol;
            double toplamUcret;
            int toplamSure;

            Durum(Durak d, List<Durak> y, double u, int s) {
                durak = d;
                yol = y;
                toplamUcret = u;
                toplamSure = s;
            }
        }

        PriorityQueue<Durum> kuyruk = new PriorityQueue<>(new Comparator<Durum>() {
            public int compare(Durum d1, Durum d2) {
                if (d1.toplamSure != d2.toplamSure) {
                    return Integer.compare(d1.toplamSure, d2.toplamSure);  // 🔹 Öncelik süre
                } else {
                    return Double.compare(d1.toplamUcret, d2.toplamUcret); // 🔸 Eşitlikte ücret
                }
            }
        });

        Set<String> ziyaretEdilen = new HashSet<>();
        List<Durak> ilkYol = new ArrayList<>();
        ilkYol.add(baslangic);
        kuyruk.add(new Durum(baslangic, ilkYol, 0.0, 0));

        while (!kuyruk.isEmpty()) {
            Durum mevcut = kuyruk.poll();

            if (ziyaretEdilen.contains(mevcut.durak.getId())) continue;
            ziyaretEdilen.add(mevcut.durak.getId());

            if (mevcut.durak.getId().equals(bitis.getId())) {
                return new GidisSonucu(mevcut.yol, mevcut.toplamUcret, mevcut.toplamSure);
            }

            for (NextStop ns : mevcut.durak.getNextStops()) {
                Durak komsu = veri.getDurakById(ns.getStopId());
                if (komsu != null && !ziyaretEdilen.contains(komsu.getId())) {
                    List<Durak> yeniYol = new ArrayList<>(mevcut.yol);
                    yeniYol.add(komsu);
                    kuyruk.add(new Durum(
                        komsu, yeniYol,
                        mevcut.toplamUcret + ns.getUcret(),
                        mevcut.toplamSure + ns.getSure()
                    ));
                }
            }

            if (mevcut.durak.getTransfer() != null) {
                Durak transferDurak = veri.getDurakById(mevcut.durak.getTransfer().getTransferStopId());
                if (transferDurak != null && !ziyaretEdilen.contains(transferDurak.getId())) {
                    List<Durak> yeniYol = new ArrayList<>(mevcut.yol);
                    yeniYol.add(transferDurak);
                    kuyruk.add(new Durum(
                        transferDurak, yeniYol,
                        mevcut.toplamUcret + mevcut.durak.getTransfer().getTransferUcret(),
                        mevcut.toplamSure + mevcut.durak.getTransfer().getTransferSure()
                    ));
                }
            }
        }

        return null;
    }
    
    
  public static String Secenekler(List<Durak> guzergah1) {
    double toplamUcret = 0;
    int toplamSure = 0;
    StringBuilder sb = new StringBuilder(); // Çıktıyı tutacak StringBuilder

    for (int i = 0; i < guzergah1.size() - 1; i++) {
        Durak current = guzergah1.get(i);
        Durak next = guzergah1.get(i + 1);

        if (i == 0 && BaslangicMinMesafe <= 3.0) {
            sb.append("Baslangiç konumu -> ").append(current.getName())
              .append(" Mesafe : ").append(String.format("%.2f", BaslangicMinMesafe))
              .append(" km || Yürüyerek ortalama ").append(YürümeSüresi(BaslangicMinMesafe))
              .append(" dakikada gidilir. \n\n");
            toplamSure += YürümeSüresi(BaslangicMinMesafe);
            System.out.println("\n");
        } else if (i == 0 && BaslangicMinMesafe > 3) {
            sb.append("Baslangic konumu -> ").append(current.getName())
              .append(" Mesafe : ").append(String.format("%.2f", BaslangicMinMesafe))
              .append(" km || Taksi ile ").append(TaksiSüresi(BaslangicMinMesafe))
              .append(" dakika sürede ").append(String.format("%.2f", taksi.taksiUcreti(BaslangicMinMesafe)))
              .append(" ücret ile gidilir. \n\n");
            toplamSure += TaksiSüresi(BaslangicMinMesafe);
            toplamUcret += Math.round(taksi.taksiUcreti(BaslangicMinMesafe) * 100.0) / 100.0;
        }

        boolean bulundu = false;

        // nextStops kontrolü
        for (NextStop ns : current.getNextStops()) {
        	double indirimliUcret =ns.getUcret();
            if (ns.getStopId().equals(next.getId())) {
            	if(BakiyeKart.yolcuTipi.equals("ogrenci")) {
            		indirimliUcret = ogrenci.tutar(ns.getUcret());           		 
            	}else if(BakiyeKart.yolcuTipi.equals("yasli")) {
            		indirimliUcret = yasli.tutar(ns.getUcret());
            	}else if(BakiyeKart.yolcuTipi.equals("genel")) {
            		indirimliUcret = genel.tutar(ns.getUcret());
            	}
            	            	
                sb.append(String.format("%s -> %s | Süre: %d dk, Ücret: %.2f TL\n (Karta Özel İndirim uygulanmıştır.) \n\n",
                        current.getName(), next.getName(), ns.getSure(),indirimliUcret));
                toplamUcret += indirimliUcret;
                toplamSure += ns.getSure();
                bulundu = true;
                break;
            }
        }

        // transfer kontrolü
        if (!bulundu && current.getTransfer() != null) {
            if (current.getTransfer().getTransferStopId().equals(next.getId())) {
                sb.append(String.format("%s => %s (Transfer) | Süre: %d dk, Ücret: %.2f TL\n\n",
                        current.getName(), next.getName(),
                        current.getTransfer().getTransferSure(),
                        current.getTransfer().getTransferUcret()));
                toplamUcret += current.getTransfer().getTransferUcret();
                toplamSure += current.getTransfer().getTransferSure();
                System.out.println("\n");
            }
        }

        if (i == guzergah1.size() - 2 && BitisMinMesafe <= 3.0) {
            sb.append(next.getName()).append(" -> Bitiş Konumu ")
              .append(" Mesafe : ").append(String.format("%.2f", BitisMinMesafe))
              .append(" km || Yürüyerek ortalama ").append(YürümeSüresi(BitisMinMesafe))
              .append(" dakikada gidilir.\n\n");
            toplamSure += YürümeSüresi(BitisMinMesafe);
        } else if (i == guzergah1.size() - 2 && BitisMinMesafe > 3.0) {
            sb.append(next.getName()).append(" -> Bitiş Konumu ")
              .append(" Mesafe : ").append(String.format("%.2f", BitisMinMesafe))
              .append(" km || Taksi ile ").append(TaksiSüresi(BitisMinMesafe))
              .append(" dakika sürede ").append(String.format("%.2f", taksi.taksiUcreti(BitisMinMesafe)))
              .append(" ücret ile gidilir. \n\n");
            toplamSure += TaksiSüresi(BitisMinMesafe);
            toplamUcret += Math.round(taksi.taksiUcreti(BitisMinMesafe) * 100.0) / 100.0;
        }
        
        
    }

    sb.append("\nToplam Ücret: ").append(String.format("%.2f", toplamUcret)).append(" TL\n");
    sb.append("Toplam Süre: ").append(toplamSure).append(" dk");

    return sb.toString(); // Tüm çıktıyı döndürüyoruz
}

    
    
    public static double TaksiYaDaYürüme(double mesafe) {
    	double ucret =0.0;
    	if(mesafe <= 3.0 ) {
    		ucret = 0;
    	}else if (mesafe >3.0){
    		ucret =taksi.taksiUcreti(mesafe);
    	}else {
    		System.out.println("Mesafe negatif!!!");
    	}
    	
	return ucret;
    	
    }
    
    public static int YürümeSüresi(double mesafe) {
    	//Ortalama insanın yürüme hızı saatte 5km olarak bulundu.
    	double ortalamaDakika = mesafe/5.0; 
    	ortalamaDakika *= 60;
    	
    	return (int)ortalamaDakika+1;
    }
    public static int TaksiSüresi(double mesafe) {
    	// ortalama taksi hızı 70km/saat olarak alınmıştır.
    	double ortalamaDakika = mesafe/70;
    	ortalamaDakika *= 60;
    	
    	return (int)ortalamaDakika +1;
    }
    
 public static String SadeceTaksiKullanimi(double taksiMesafe) {
    // Çıktıyı formatlayarak bir string değişkenine atıyoruz
    String sonuc = "Baslangic Konum -> Bitis Konum . Mesafe : " + String.format("%.2f", taksiMesafe) + " km || Süre : " + TaksiSüresi(taksiMesafe) + " dk || Ücret : " + String.format("%.2f", taksi.taksiUcreti(taksiMesafe)) + " TL";
    // Sonucu geri döndürüyoruz
    return sonuc;
}

    static class GidisSonucu {
        List<Durak> guzergah;
        double toplamUcret;
        int toplamSure;

        public GidisSonucu(List<Durak> guzergah, double toplamUcret, int toplamSure) {
            this.guzergah = guzergah;
            this.toplamUcret = toplamUcret;
            this.toplamSure = toplamSure;
        }
    }
}

