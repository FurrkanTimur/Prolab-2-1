package arayuz.Map;

import backend.*;
import java.io.FileReader;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class VeriSetiOkuma {

    public static VeriModeli okuJSON(String dosyaYolu) {
        try {
            Gson gson = new GsonBuilder().create();
            FileReader reader = new FileReader(dosyaYolu);
            return gson.fromJson(reader, VeriModeli.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

class VeriModeli {
    private String city;
    private Taxi taxi;
    private List<Durak> duraklar;

    public String getCity() { return city; }
    public Taxi getTaxi() { return taxi; }
    public List<Durak> getDuraklar() { return duraklar; }

    public Durak getDurakById(String id) {
        for (Durak d : duraklar) {
            if (d.getId().equals(id)) {
                return d;
            }
        }
        return null;
    }

    class Taxi {
        private double openingFee;
        private double costPerKm;

        public double getOpeningFee() { return openingFee; }
        public double getCostPerKm() { return costPerKm; }
    }
}

class Durak {
    private String id;
    private String name;
    private String type;
    private double lat;
    private double lon;
    private boolean sonDurak;
    private List<NextStop> nextStops;
    private Transfer transfer;

    public String getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public double getLat() { return lat; }
    public double getLon() { return lon; }
    public boolean isSonDurak() { return sonDurak; }
    public List<NextStop> getNextStops() { return nextStops; }
    public Transfer getTransfer() { return transfer; }

    class Transfer {
        private String transferStopId;
        private int transferSure;
        private double transferUcret;

        public String getTransferStopId() { return transferStopId; }
        public int getTransferSure() { return transferSure; }
        public double getTransferUcret() { return transferUcret; }
    }
}

class NextStop {
    private String stopId;
    private double mesafe;
    private int sure;
    private double ucret;

    public String getStopId() { return stopId; }
    public double getMesafe() { return mesafe; }
    public int getSure() { return sure; }
    public double getUcret() { return ucret; }
}