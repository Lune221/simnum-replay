package sn.ept.dic2;

import umontreal.ssj.simevents.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
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
	double les[] = new double[10];
	//final_list contient les clients deja servis
	ArrayList<Customer> final_list = new ArrayList<Customer>();
	final int NUMBER_OF_SERVICES = 8;
	private double lastTime = 0.0; // The last hangup of the day. We will stop the simulation for the day at this moment.
	public void ReadFileAndCreateCustomer(String file_jour_url) throws IOException {
		
		BufferedReader br = new BufferedReader(new FileReader(new File("").getAbsolutePath() + "\\" +file_jour_url));
		
		String ReadLine = br.readLine();
		while(ReadLine!=null){
			ReadLine = br.readLine();
			// System.out.println(ReadLine);
			if(ReadLine!=null) {
				
				String elements[] = ReadLine.split(",");
				String date_received = (elements[0]).split(" ")[1];
				String callType = elements[1];
				String agentNumber = elements[2].replaceAll("\"", "");
				
				if(elements[3].equals("NULL")
						|| elements[3].equals("NA")
						|| Utils.getKeyFromType(Integer.parseInt(callType)) == -1
						)
					continue;
				
				String answered = (elements[3]).split(" ")[1];
				String hangup = elements[6].split(" ")[1];
				this.lastTime = Utils.DateToSecond(hangup);
				Customer c = new Customer();
				c.setCustomer_type(Integer.parseInt(callType));
				c.setArrival_time(Utils.DateToSecond(date_received));
				if (answered.equals("NULL")) {
					c.setBegin_service_time(-1);
				}else {
					c.setBegin_service_time(Utils.DateToSecond(answered));
				}
				
				c.setEnd_service_time(Utils.DateToSecond(hangup));
				c.setWaiting_time(c.getTimeToLeaveQueue());
				
				// Prevoir l'evenement arrive du client dans la fils
				new Arrival(c).schedule(c.arrival_time);
				// System.out.println();
			}
		}
	}

	/**
	 * Simulate a day
	 * @param timeHorizon
	 * @throws IOException 
	 */
	public void simulateOneDay(double timeHorizon, String file) throws IOException {
		Sim.init();
		this.InitializeQueue();
       	this.ReadFileAndCreateCustomer(file);
       	new EndOfSim(this).schedule (this.lastTime);
       	Sim.start();
	}
	/**
	 * When a customer arrives in the queue
	 * @author ASUS
	 *
	 */
	class Arrival extends Event{
		Customer cust;
		
		public Arrival (Customer c) {
			cust = c;
		}
		
		
		@Override
		public void actions() {
			// Update the array that store the queues size
			for (int i = 0; i < NUMBER_OF_SERVICES; i++) {
				cust.queues_size[i] = file.get(i).size();
			}
			// Put the client in his specific queue by getting the number of the queue
			// from the type of the client
			int type = Utils.getKeyFromType(cust.getCustomer_type());
			(file.get(type)).add(cust);

			new Depart(cust).schedule(cust.getTimeToLeaveQueue());
		}
		
	}
	
	/**
	 * When a customer leaves his queue in order to be served by a server
	 * @author ASUS
	 *
	 */
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
	
	/**
	 * End of service of a specific customer
	 * @author ASUS
	 *
	 */
	class EndOfService extends Event{
		Customer cust=null;
		public EndOfService(Customer c) {
			cust =c;
		}
		@Override
		public void actions() {
			final_list.add(cust);
		}
	}
	
	/**
	 * End of the simulation for the day
	 * @author ASUS
	 *
	 */
	class EndOfSim extends Event{
		
		ReplayDay day = null;
		
		public EndOfSim(ReplayDay day) {
			this.day = day;
		}
		@Override
		public void actions() {
			try {
				Utils.finalCustomersListToCSV("final.csv", final_list);
			} catch (IOException e) {
				e.printStackTrace();
			}
			Sim.stop();
		}
	}
	
	/**
	 * Initialize the queues by creating empty LinkedLists
	 */
	public void InitializeQueue() {
		for (int i = 0; i < NUMBER_OF_SERVICES; i++) {
			file.add(new LinkedList<Customer>());
		}
	}

}
