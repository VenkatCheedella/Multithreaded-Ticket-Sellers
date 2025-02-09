
public class SellerL extends Seller {

	private Object lock;
	private NextCustomerQueue nextCustomerQueue;

	public SellerL(Seat[][] s, String sellerID, Object lk, NextCustomerQueue nextCustomerQueue) {
		// Seller H takes 1 or 2 minutes to complete a ticket sale
		super(s, r.nextInt(4) + 4, sellerID, lk);
		lock = lk;
		this.nextCustomerQueue = nextCustomerQueue;
	}

	public void sell() {
		while (!customers.isEmpty()) {
			// Object lock = new Object();
			synchronized (lock) {
				if (customers.isEmpty())
					return;
				// Get customer in queue that is ready
				Customer customer = customers.peek();

				// Find seat for the customer
				// Case for Seller L
				Seat seat = null;

				if (nextCustomerQueue.peek().getqName().equals(this.sellerID)) {
					find_seat: for (int i = seating.length - 1; i >= 0; i--) {
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
					}
					// lock.notifyAll();
					if (seat != null) {
						try {
							Thread.sleep(serviceTime);
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
