package sn.ept.dic2;

import umontreal.ssj.simevents.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

import sn.ept.dic2.models.Customer;
import sn.ept.dic2.utils.Utils;

public class ReplayDay {
	
	//input fichier excel
	//A determiner:
	//longueur de la file
	// longueur des autres files
	// LES : temps attente du dernier client entree en service
	// 
	HashMap<Integer ,LinkedList<Customer>> file = new HashMap<Integer ,LinkedList<Customer>>();
	
	HashMap<Integer, Double> les = new HashMap<Integer, Double>();
	//final_list contient les clients deja servis
	ArrayList<Customer> final_list = new ArrayList<Customer>();
	final int NUMBER_OF_SERVICES = 8;
	private double lastTime = 0.0; // The last hangup of the day. We will stop the simulation for the day at this moment.
	
	// Matching between the services and the agents
	HashMap<Integer, Set<Integer>> matching = new HashMap<Integer, Set<Integer>>();
	
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
						|| elements[6].equals("NA")
						)
					continue;
				
				String answered = (elements[3]).split(" ")[1];
				String hangup = elements[6].split(" ")[1];
				this.lastTime = Utils.DateToSecond(hangup);
				
				Customer c = new Customer();
				c.setCustomer_type(Integer.parseInt(callType));
				c.setArrival_time(Utils.DateToSecond(date_received));
				c.setAgentNumber(Integer.parseInt(agentNumber));
				c.setNumber_of_agent(matching.get(Integer.parseInt(callType)).size());
				
				if (answered.equals("NULL")) {
					c.setBegin_service_time(-1);
				}else {
					c.setBegin_service_time(Utils.DateToSecond(answered));
				}
				
				c.setEnd_service_time(Utils.DateToSecond(hangup));
				c.setWaiting_time(c.getTimeToLeaveQueue());
				
				// Prevoir l'evenement arrive du client dans la fils
				new Arrival(c, this).schedule(c.arrival_time);
				// System.out.println();
			}
		}
	}
	
	
	public int getAgentQueuesSize(Integer agentNumber) {
		
		int somme = 0;
		
		for(Integer service : matching.keySet()) {
			
			if(!matching.get(service).contains(agentNumber)) continue;
			
			somme += file.get(service).size();
		}
		
		
		return somme;
	}
	
	/**
	 * Simulate a day
	 * @param timeHorizon
	 * @throws IOException 
	 */
	public void simulateOneDay(double timeHorizon, String file) throws IOException {
		Sim.init();
       	matching = Utils.createServiceList(file);
       	this.ReadFileAndCreateCustomer(file);
       	this.InitializeQueue();
       	this.InitializeLES();
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
		ReplayDay day;
		public Arrival (Customer c, ReplayDay day) {
			cust = c;
			this.day = day;
		}
		
		@Override
		public void actions() {
			// Set the length of his queue
			cust.setQueueSize(file.get(cust.getCustomer_type()).size());
			cust.setSize_r_vector(day.getAgentQueuesSize(cust.getAgentNumber()));
			cust.setLes(les.get(cust.getCustomer_type()));
			// Put the client in his specific queue 
			file.get(cust.getCustomer_type()).add(cust);
			
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
			file.get(cust.getCustomer_type()).remove(cust);
			if (cust.getBegin_service_time() != -1) {
				les.put(cust.getCustomer_type(), cust.getWaiting_time());
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
				ReplayAll.nextDay();
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
		for (Integer service : matching.keySet()) {
			file.put(service, new LinkedList<Customer>());
		}
	}
	
	/**
	 * Initialize the LES hashmap
	 */
	public void InitializeLES() {
		for (Integer service : matching.keySet()) {
			les.put(service, 0.0);
		}
	}

}
