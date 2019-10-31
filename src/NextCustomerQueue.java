import java.util.PriorityQueue;

public class NextCustomerQueue{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final int maxSize;
	private final PriorityQueue<Customer> nextCustomersFromEachQueue;
	
	public NextCustomerQueue(int maxSize) {
		this.maxSize= maxSize;
		this.nextCustomersFromEachQueue = new PriorityQueue<Customer>();
	}
	
	public boolean addCustomer(Customer customer) {
		if(this.nextCustomersFromEachQueue.size() < maxSize) {
			this.nextCustomersFromEachQueue.add(customer);
			return true;
		}
		else {
			System.out.println("Current size of the queue :" + this.nextCustomersFromEachQueue.size());
			return false;
		}
	}
	
	public Customer removeTop() {
		return this.nextCustomersFromEachQueue.poll();
	}
	
	@Override
	public String toString() {
		return nextCustomersFromEachQueue.toString();
	}
	
	public Customer peek() {
		return nextCustomersFromEachQueue.peek();
	}

}
