package sn.ept.dic2.utils;

public class Utils {
	
	// Convertit la l'heure en seconde
	public static double DateToSecond(String d) {
		String date[]  = d.split(":");
		int h= Integer.parseInt(date[0]);
		int m = Integer.parseInt(date[1]);
		int s = Integer.parseInt(date[2]);
		
		return h*3600 + m*60 + s;
	}
	
	//Recupere la cle en fonction du type customer
	public static int getKeyFromType(int type) {
		int val = -1;
		if (type == 30175){
			val = 0;
		}else if(type == 30172) {
			val = 1;
		}else if(type == 30560) {
			val = 2;
		}else if(type == 30181) {
			val = 3;
		}else if(type == 30179) {
			val = 4;
		}else if(type == 30066) {
			val = 5;
		}else if(type == 30241) {
			val = 6;
		}else if(type == 30181) {
			val = 7;
		}else if(type == 30511) {
			val = 8;
		}
		if(val == -1) {
			System.out.println(type);
		}
		
		return val;
	}
	
}
