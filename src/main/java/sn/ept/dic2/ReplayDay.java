package sn.ept.dic2;

import umontreal.ssj.simevents.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import sn.ept.dic2.models.Customer;
import sn.ept.dic2.utils.Utils;

public class ReplayDay {
	
	//input fichier excel
	//A determiner:
	//longueur de la file
	// longueur des autres files
	// LES : temps attente du dernier client entree en service
	// 
	ArrayList<LinkedList<Customer>> file = new ArrayList<LinkedList<Customer>>();
	double les[] = new double[8];
	//final_list contient les clients deja servis
	ArrayList<Customer> final_list = new ArrayList<Customer>();
	final int NUMBER_OF_SERVICES = 9;
	
	public void ReadFileAndCreateCustomer(String file_jour_url) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(file_jour_url));
		
		String ReadLine = br.readLine();
		while(ReadLine!=null){
			ReadLine = br.readLine();
			if(ReadLine!=null) {
				
				String elements[] = ReadLine.split(",");
				String date_received = (elements[0]).split(" ")[1];
				String callType = elements[1];
				String agentNumber = elements[2];
				if(!elements[3].equals("NULL")) { // Si un agent a répondu à l'appel.
					String answered = (elements[3]).split(" ")[1];
					String hangup = elements[6].split(" ")[1];
					
					Customer c = new Customer();
					c.setCustomer_type(Integer.parseInt(callType));
					c.setArrival_time(Utils.DateToSecond(date_received));
					if (answered == "NULL") {
						c.setBegin_service_time(-1);
					}else {
						c.setBegin_service_time(Utils.DateToSecond(answered));
					}
					
					c.setEnd_service_time(Utils.DateToSecond(hangup));
					c.setWaiting_time(c.getTimeToLeaveQueue());
					
					// Prevoir l'evenement arrive du client dans la fils
					new Arrival(c).schedule(c.arrival_time);
				}

			}
		}
		
	}

	/**
	 * Simulate a day
	 * @param timeHorizon
	 * @throws IOException 
	 */
	public void simulateOneDay(double timeHorizon) throws IOException {
		Sim.init();
		this.InitializeQueue();
       	this.ReadFileAndCreateCustomer("calls-2014-01.csv");
       	Sim.start();
	}
	//Arrive du client au niveau de la file
	class Arrival extends Event{
		Customer cust;
		
		public Arrival (Customer c) {
			cust = c;
		}
		
		
		@Override
		public void actions() {
			//Placer le client au niveau de sa file d'attente
			for (int i = 0; i < file.size(); i++) {
				cust.queues_size[i] = file.get(i).size();
			}
			//Recuperer l'index du customer a partir du type
			int type = Utils.getKeyFromType(cust.getCustomer_type());
			System.out.println(cust.getCustomer_type());
			(file.get(type)).add(cust);

			new Depart(cust).schedule(cust.getTimeToLeaveQueue());
		}
		
	}
	
	//Depart de la file
	class Depart extends Event{
		Customer cust = null ;
		public Depart(Customer c) {
			cust = c;
		}

		@Override
		public void actions() {
			int type = Utils.getKeyFromType(cust.getCustomer_type());
			file.get(type).remove(cust);
			if (cust.getBegin_service_time() != -1) {
				les[type] = cust.getWaiting_time();
				new EndOfService(cust).schedule(cust.end_service_time - cust.begin_service_time);
			}
				
		}
		
	}
	
	class EndOfService extends Event{
		Customer cust=null;
		public EndOfService(Customer c) {
			// TODO Auto-generated constructor stub
			cust =c;
		}
		@Override
		public void actions() {
			final_list.add(cust);
		}
	}
	
	class EndofSim extends Event{
		@Override
		public void actions() {
			Sim.stop();
		}
	}
	
	public void InitializeQueue() {
		for (int i = 0; i < NUMBER_OF_SERVICES; i++) {
			file.add(new LinkedList<Customer>());
		}
	}
		
	public String QueuesSizeToString(Customer c) {
		String info = "";
		for (int i = 0; i < c.queues_size.length; i++) {
			info = info + c.queues_size[i] + ",";
		}
		return info;
	}
	
	public void FinalListToCsv(String filename) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
		
		for( Customer c: final_list) {
			String info_cust = c.getCustomer_type() + ","
					+ c.getArrival_time() + ","
					+ QueuesSizeToString(c)
					+ c.getLes() + ","
					+ c.getNumber_of_agent() + ","
					+ c.getWaiting_time();
			bw.write(info_cust);
		}
		
	}
	
	public static void main(String[] args) throws IOException {
		//Initialize queue
		ReplayDay simulation = new ReplayDay();
		simulation.simulateOneDay(10000.0);
	}

}
