import java.util.Random;

public class SellerL extends Seller{
	private int serviceTime;
	
	
	public SellerL(Seat[][] s) {
		// Seller H takes 1 or 2 minutes to complete a ticket sale
		super(s);
		serviceTime = r.nextInt(7) + 4;
		
	}
}
