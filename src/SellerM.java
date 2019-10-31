
public class SellerM extends Seller {
	private Object lock;
	private NextCustomerQueue nextCustomerQueue;

	public SellerM(Seat[][] s, String sellerID, Object lk, NextCustomerQueue nextCustomerQueue) {
		// Seller H takes 1 or 2 minutes to complete a ticket sale
		super(s, r.nextInt(3) + 2, sellerID, lk);
		lock = lk;
		this.nextCustomerQueue = nextCustomerQueue;
	}

	public void sell() {
		while (!customers.isEmpty()) {
			synchronized (lock) {
				// Object lock = new Object();

				if (customers.isEmpty())
					return;
				// Get customer in queue that is ready
				Customer customer = customers.peek();

				// Find seat for the customer
				// Case for Seller M
				boolean flag = true;
				int counter = 1;

				// check if the current customer is in the top of the queue

				Seat seat = null;
				if (nextCustomerQueue.peek().getqName().equals(this.sellerID)) {

					find_seat: for (int i = 5; i >= 0 && i < seating.length;) {
						for (int j = 0; j < seating[0].length; j++) {
							if (seating[i][j].isSeatEmpty()) {
								// Assign seat to customer
								// Seat number = (Row x 10) + (Col + 1)
								int seatNum = (i * 10) + j + 1;
								seat = new Seat(seatNum);
								super.assignSeat(customer, seat, i, j);
								break find_seat;
							}
						}
						if (flag == true) {
							i += counter;
							flag = false;
						} else {
							i -= counter;
							flag = true;
						}
						counter++;
					}
					// lock.notifyAll();

					if (seat != null) {
						try {
							Thread.sleep(serviceTime * 100);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					printMsg(customer, seat);
					customers.remove();
					nextCustomerQueue.removeTop();
					if (customers.size() > 0)
						nextCustomerQueue.addCustomer(customers.peek());
				}
			}

		}
	}

}
