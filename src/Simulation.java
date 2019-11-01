import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Simulation {
	
	private static final Map<String, Integer> countOfCustomerForEachSeller = new HashMap<String, Integer>();
	private static int N;
	
	public void wakeupSellers() {

	}
	public static void main(String[] args) throws Exception {

		//number of customers per seller per hour
		N = 15;
		if (args.length > 0) N = Integer.parseInt(args[0]);

		final Object lock = new Object();

		int maxRows = 10;
		int maxCols = 10;
		Seat[][] seating = createSeating(maxRows, maxCols);
		
		//get next customer in each queue and place it a minHeap of size 10.
		NextCustomerQueue nextCustomerQueue =  new NextCustomerQueue(10);
		
		//create 10 threads representing 10 sellers
		Seller[] allSellers = new Seller[10];
		for (int numSeller = 0; numSeller < 10; numSeller++)
		{
			if (numSeller == 0) 
				allSellers[numSeller] = new SellerH(seating, "H" + (numSeller + 1), lock, nextCustomerQueue);
			else if (numSeller >= 1 && numSeller < 4) 
				allSellers[numSeller] = new SellerM(seating, "M" + (numSeller), lock, nextCustomerQueue);
			else if (numSeller >= 4 && numSeller < 10) 
				allSellers[numSeller] = new SellerL(seating, "L" + (numSeller - 3), lock, nextCustomerQueue);
		}

		//add N customers for each seller for each hour
		//initially add N customers for each seller's queue
		allSellers = addNewCustomers(allSellers, N);
		
		//fill the nextCustomerQueue
		addNextCustomerFromEachQueueToNCQueue(allSellers, nextCustomerQueue);

		//wake up all seller threads, so they can run in "parallel"
		//lock.notifyAll(); //use notifyAll for broadcast
		Thread []threads = new Thread[allSellers.length];
		for(int numSellers = 0; numSellers < allSellers.length; numSellers++)
		{
			Thread currentThread = new Thread(allSellers[numSellers]);
			currentThread.start();
		}
		for(int numSellers = 0; numSellers < allSellers.length; numSellers++)
		{
			threads[numSellers] = new Thread(allSellers[numSellers]);
			threads[numSellers].start();
		}
		for(int numSellers = 0; numSellers < allSellers.length; numSellers++)
		{
			try {
				threads[numSellers].join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
		}
		


		//print the following with the current time
		//- customer added to the queue
		//- customer is attended (given a seat or told there are no seats)
		//- customer gets a ticket and goes to seat
		//- print seating chart everytime a ticket is sold

		//wait for all sellers to finish



		synchronized(lock) {
			printSeating(seating, maxRows, maxCols);
		}

		printFinalStatusOfEachQueue();

		//seller needs to take in parameters or have some ref to
		//2D array, time?, universal lock
	}
	
	
	/**
	 * Create a seating chart and label with seat numbers
	 * @param maxRows: max number of rows for the chart
	 * @param maxCols max number of columns for the chart
	 * @return seating chart with the given size and fully labeled
	 */
	public static Seat[][] createSeating(int maxRows, int maxCols)
	{
		//create 10x10 seating and label with seat numbers
		Seat[][] seating = new Seat[maxRows][maxCols]; 
		int numSeat = 1;
		for (int row = 0; row < maxRows; row++)
		{
			for (int column = 0; column < maxCols; column++)
			{
				seating[row][column] = new Seat(numSeat);
				numSeat++;
			}
		}
		return seating;
	}

	/**
	 * Add the given number of customers for each seller's queue
	 * @param allSellers: array containing all the sellers
	 * @param numAdd: number of customers to add to each seller
	 * @return updated array with the new customers added to each queue
	 */
	public static Seller[] addNewCustomers(Seller[] allSellers, int numAdd)
	{
		for (int numSeller = 0; numSeller < allSellers.length; numSeller++)
		{
			for (int count = 0; count < numAdd; count++)
			{
				Customer c = new Customer(numSeller);
				allSellers[numSeller].addCustomer(c);
			}
			allSellers[numSeller].sortQueue();
		}
		return allSellers;
	}
	
	/**
	 * get the next customer from each queue and add to the nextCustomerQueue;
	 * @throws Exception 
	 */
	public static void addNextCustomerFromEachQueueToNCQueue(Seller[] allSellers, NextCustomerQueue ncQueue) throws Exception {

		for (Seller seller : allSellers) {
			if(!ncQueue.addCustomer(seller.firstCustomerForService())) {
				throw new Exception("Unable to add a customer to queue, Manage the size of the queue");
			}
		}
		
//		System.out.println(" ****** Next customers from each queue *****: \n " + ncQueue.toString());
	}

	/**
	 * Print the current seating chart
	 * @param seating: current seating chart
	 * @param maxRows: max number of rows for the chart
	 * @param maxCols: max number of columns for the chart
	 */
	/*
	public static void printSeating(Seat[][] seating, int maxRows, int maxCols)
	{
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		for (int row = 0; row < maxRows; row++)
		{
			for (int col = 0; col < maxCols; col++)
			{
				if (seating[row][col].isSeatEmpty()) System.out.printf("%5s ", "O");
				else System.out.printf("%5s ", "X");
			}
			System.out.println();
		}
	}
	*/
	public static void printSeating(Seat[][] seating, int maxRows, int maxCols)
	{
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		for (int row = 0; row < maxRows; row++)
		{
			for (int col = 0; col < maxCols; col++)
			{
				if (seating[row][col].isSeatEmpty()) 
					System.out.printf("%7s ", "O");
				else {
					System.out.printf("%7s ", seating[row][col].getCustomer().getTicket());
					String qName = seating[row][col].getCustomer().getqName().substring(0, 1);
					countOfCustomerForEachSeller.put(qName, countOfCustomerForEachSeller.getOrDefault(qName, 0)+1);
				}
			}
			System.out.println();
		}
	}
	
	public static void printFinalStatusOfEachQueue() {
		System.out.println("******** Count of customers serviced by each seller *********");
		int totalNumOfHCustomers = N;
		int totalNumOfLCustomers = N * 6;
		int totalNumOfMCustomers = N * 3;
		Set<Entry<String, Integer>> entrySet = countOfCustomerForEachSeller.entrySet();

		for (Entry<String, Integer> seller : entrySet) {
			String sellerId = seller.getKey();
			int totalCustomerCount = (sellerId.equals("H")) ? totalNumOfHCustomers
					: (sellerId.equals("M")) ? totalNumOfMCustomers : totalNumOfLCustomers;
			System.out.println("Seller ID : " + seller.getKey() + " , count of customers serviced by seller : "
					+ seller.getValue() + " , count of customers left out : " + (totalCustomerCount - seller.getValue()));
		}
	}

}