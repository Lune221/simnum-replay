package sn.ept.dic2.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import sn.ept.dic2.models.Customer;

public class Utils {
	
	// Convertit la l'heure en seconde
	public static double DateToSecond(String d) {
		String date[]  = d.split(":");
		int h= Integer.parseInt(date[0]);
		int m = Integer.parseInt(date[1]);
		int s = Integer.parseInt(date[2]);
		
		return h*3600 + m*60 + s;
	}
	
	// Recupere la cle en fonction du type customer
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
		}
		/*
		if(val == -1) {
			System.out.println(type);
		}
		*/
		return val;
	}
	
	public static String QueuesSizeToString(Customer c) {
		String info = "";
		for (int i = 0; i < c.queues_size.length; i++) {
			info = info + c.queues_size[i] + ",";
		}
		return info;
	}
	
	
	public static void finalCustomersListToCSV(String filename, ArrayList<Customer> final_list) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(filename, true));
		
		for( Customer c: final_list) {
			String info_cust = 
					  c.getCustomer_type() + ","
					+ c.getArrival_time() + ","
					+ QueuesSizeToString(c)
					+ c.getLes() + ","
					+ c.getNumber_of_agent() + ","
					+ c.getWaiting_time()
					+ "\n";
			bw.write(info_cust);
		}
		bw.close();
		
	}
	
	/**
	 * Function for writing the headers to the final CSV file
	 * @param filename
	 * @throws IOException
	 */
	public static void writeHeadersToCSV(String filename) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
		// Ps : SQX = Size of the Queue X
		bw.write("CustomerType,ArrivalTime,SQ1,SQ2,SQ3,SQ5,SQ5,SQ6,SQ7,SQ8,LES,NbAgents,WaitingTime\n");

		bw.close();
		
	}
}
