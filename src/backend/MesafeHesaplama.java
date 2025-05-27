package backend;


public class MesafeHesaplama {
	public MesafeHesaplama() {
		
	}
    private static final double EARTH_RADIUS = 6371.0;
	public static double uzaklik(double lat1, double lon1, double lat2, double lon2) {
		double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);

        // Enlem ve boylam farkları
        double LatFark = lat2Rad - lat1Rad;
        double LonFark = lon2Rad - lon1Rad;

        // Haversine formülü (a=açısal fark)
        double a = Math.pow(Math.sin(LatFark / 2), 2) +
                   Math.cos(lat1Rad) * Math.cos(lat2Rad) * Math.pow(Math.sin(LonFark / 2), 2);
        // (C=iki nokta arasındaki açısal değeri bulur)
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        // Mesafeyi hesapla
        return EARTH_RADIUS * c;	

	}
}
