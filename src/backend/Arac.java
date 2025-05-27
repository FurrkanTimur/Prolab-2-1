package backend;


abstract class Arac {
	static class Otobus {
		
	}
	
	static class Tramvay {
		
	}
	static class Taksi {
		private double taksi_tutari ;
		
		public Taksi() {
		}
		
		public double taksiUcreti(double km) {
			int acilis = 10;
			return acilis + km*4;
		}
		public double getTaksiTutari() {
			return taksi_tutari;
		}
	}
}
