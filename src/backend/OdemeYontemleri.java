package backend;

abstract class OdemeYontemleri {
	public static double nakit_bakiye = 100;
	public static double KentKart_bakiye = 100;
	public static double KrediKarti_bakiye = 100;
	
	public OdemeYontemleri() {
	}

	public abstract void OdemeYap(double miktar);
}

class Nakit extends OdemeYontemleri {

	public Nakit() {
	}
	
	@Override
	public void OdemeYap(double miktar) {
		System.out.println("Nakit ile " + miktar + "TL ödeme yapıldı. ");
		OdemeYontemleri.nakit_bakiye -= miktar;
		/*System.out.println("Kalan Nakit bakiye : "+ this.nakit_bakiye);
		System.out.println("Kalan Kent Kart bakiye : " + this.KentKart_bakiye);
		System.out.println("Kalan Kredi Kartı bakiye :" + this.KrediKarti_bakiye);*/
	}
	
}
class KrediKarti extends OdemeYontemleri {
	public KrediKarti() {
		
	}

	@Override
	public void OdemeYap(double miktar) {
		System.out.println("Kredi Kartı ile " + miktar + "TL ödeme yapıldı. ");
		OdemeYontemleri.KrediKarti_bakiye -= miktar;
		/*System.out.println("Kalan Nakit bakiye : "+ this.nakit_bakiye);
		System.out.println("Kalan Kent Kart bakiye : " + this.KentKart_bakiye);
		System.out.println("Kalan Kredi Kartı bakiye :" + this.KrediKarti_bakiye);*/			
	}
	
}
class KentKart extends OdemeYontemleri {
	public KentKart() {
		
	}

	@Override
	public void OdemeYap(double miktar) {
		System.out.println("Kent Kart ile " + miktar + "TL ödeme yapıldı. ");
		OdemeYontemleri.KentKart_bakiye -= miktar;
		/*System.out.println("Kalan Nakit bakiye : "+ this.nakit_bakiye);
		System.out.println("Kalan Kent Kart bakiye : " + this.KentKart_bakiye);
		System.out.println("Kalan Kredi Kartı bakiye :" + this.KrediKarti_bakiye);
		*/
	}
	
}

