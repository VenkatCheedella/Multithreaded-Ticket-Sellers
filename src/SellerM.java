import java.util.Random;

public class SellerM extends Seller{
	//Seat[][] seating;
	private int serviceTime;

	public SellerM(Seat[][] s) {
		// Seller H takes 1 or 2 minutes to complete a ticket sale
		super(s);
		serviceTime = r.nextInt(4) + 2;
	}

	public void sell() throws InterruptedException {
		while (!customers.isEmpty()) {						
			Object lock = new Object();
			synchronized(lock) {
				while (customers.isEmpty()) wait();
				// Get customer in queue that is ready
				Customer customer = customers.peek();

				// Find seat for the customer
				// Case for Seller H
				boolean found = false;
				boolean flag = true;
				int counter = 1;
				find_seat:
				for(int i = 5; i >= 0 && i < seating.length;) {
					for (int j = 0; j < seating[0].length; j++) {
						if (seating[i][j].isSeatEmpty()) {
							// Assign seat to customer
							// Seat number = (Row x 10) + (Col + 1)
							int seatNum = (i*10)+j+1;
							Seat seat = new Seat(seatNum);
							seat.assignSeat(customer);
							seating[i][j] = seat;
							found = true;
							break find_seat;
						}
					}
					if(flag == true){
						i += counter;
						flag = false;
					}
					else{
						i -= counter;
						flag = true;
					}
					
				}
					

				if (!found) System.out.println("Sorry, the concert is sold out. Please come again!");

				notifyAll();
			}
		}
	}
}
