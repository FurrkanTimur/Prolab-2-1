package backend;

public class Yolcu {
	
	static class Genel {
        private double ucret = arayuz.BakiyeKart.KentKartDouble;
       
        public Genel() {

        }

        public double tutar(double ucret) {
            return ucret;
        }

        public double getUcret() {
            return ucret;
        }
    }
	
	static class Yasli {
        private double ucret = arayuz.BakiyeKart.KentKartDouble;

        public Yasli() {

        }

        public double tutar(double ucret) {
            return ucret * 0.5;
        }

        public double getUcret() {
            return ucret;
        }
    }
	
	static class Ogrenci {
        private double ucret = arayuz.BakiyeKart.KentKartDouble;

        public Ogrenci() {

        }

        public double tutar(double ucret) {
            return ucret * 0.75;
        }

        public double getUcret() {
            return ucret;
        }
    }
}
