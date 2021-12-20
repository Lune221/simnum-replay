package sn.ept.dic2.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

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
	
	
	public static void finalCustomersListToCSV(String filename, ArrayList<Customer> final_list) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(filename, true));
		
		for( Customer c: final_list) {
			String info_cust = 
					  c.getCustomer_type() + ","
					+ c.getArrival_time() + ","
					+ c.getQueueSize() + ","
					+ c.getLes() + ","
					+ c.getNumber_of_agent() + ","
					+ c.getWaiting_time() + ","
					+ c.getSize_r_vector()
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
		bw.write("CustomerType,ArrivalTime,QueueSize,LES,NbAgents,WaitingTime,l\n");

		bw.close();
		
	}
	
	/**
	 * Function for creating the services and the list of agents for the service
	 * @throws IOException 
	 */
	public static HashMap<Integer, Set<Integer>> createServiceList(String file_jour_url) throws IOException {
		
		HashMap<Integer, Set<Integer>> matching = new HashMap<Integer, Set<Integer>>();
		
		BufferedReader br = new BufferedReader(new FileReader(new File("").getAbsolutePath() + "\\" +file_jour_url));
		
		String ReadLine = br.readLine();
		while(ReadLine!=null){
			ReadLine = br.readLine();
			// System.out.println(ReadLine);
			if(ReadLine!=null) {
				
				String elements[] = ReadLine.split(",");
				String callType = elements[1];
				String agentNumber = elements[2].replaceAll("\"", "");
				
				if(elements[3].equals("NULL")
						|| elements[3].equals("NA")
						|| Utils.getKeyFromType(Integer.parseInt(callType)) == -1
						|| elements[6].equals("NA")
						)
					continue;
				
				Integer serviceType = Integer.parseInt(callType);
				Integer agentId = Integer.parseInt(agentNumber);
				
				if(!matching.containsKey(serviceType)) {
					matching.put(serviceType, new HashSet<Integer>());
				}
				matching.get(serviceType).add(agentId);
			}
		}
		
		return matching;
	}
	
	
	
}
