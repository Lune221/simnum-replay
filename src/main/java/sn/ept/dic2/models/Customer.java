package sn.ept.dic2.models;

public class Customer{

	public int customer_type;
	public double arrival_time;
	public double begin_service_time;
	public double end_service_time;
	public int number_of_agent;
	public double waiting_time;
	public double hangup_time;
	public double les;
	private int queueSize = 0; // The length of his queue
	private int agentNumber;
	private int size_r_vector;

	public int getCustomer_type() {
		return customer_type;
	}
	public void setCustomer_type(int customer_type) {
		this.customer_type = customer_type;
	}
	public double getArrival_time() {
		return arrival_time;
	}
	public void setArrival_time(double arrival_time) {
		this.arrival_time = arrival_time;
	}
	public double getBegin_service_time() {
		return begin_service_time;
	}
	public void setBegin_service_time(double begin_service_time) {
		this.begin_service_time = begin_service_time;
	}
	public double getEnd_service_time() {
		return end_service_time;
	}
	public void setEnd_service_time(double end_service_time) {
		this.end_service_time = end_service_time;
	}
	public int getNumber_of_agent() {
		return number_of_agent;
	}
	public void setNumber_of_agent(int number_of_agent) {
		this.number_of_agent = number_of_agent;
	}
	public double getWaiting_time() {
		return waiting_time;
	}
	public void setWaiting_time(double waiting_time) {
		this.waiting_time = waiting_time;
	}
	public double getLes() {
		return les;
	}
	public void setLes(double les) {
		this.les = les;
	} 
	
	public int getQueueSize() {
		return queueSize;
	}
	public void setQueueSize(int queueSize) {
		this.queueSize = queueSize;
	}
	

	public int getAgentNumber() {
		return agentNumber;
	}
	public void setAgentNumber(int agentNumber) {
		this.agentNumber = agentNumber;
	}
	
	public int getSize_r_vector() {
		return size_r_vector;
	}
	public void setSize_r_vector(int size_r_vector) {
		this.size_r_vector = size_r_vector;
	}
	
	public double getTimeToLeaveQueue() {
		if (begin_service_time==-1) {
			return hangup_time - arrival_time;
		}
		return begin_service_time - arrival_time;
	}
	
	
}