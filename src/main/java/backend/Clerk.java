package backend;

public class Clerk extends User 
{
	
	private Inventory inventory;

	public Clerk(String username, String password) { 
		super(username, password); 
	}
  	@Override public boolean isManager() { 
		return false; 
	}
	
    public void recordTransaction(Transaction transaction) {
    TransactionManager.processTransaction(transaction);
	}
	
	// CHECK INVENTORY LEVELS

	
	// PROCESS PURCHASE
	
	// CHECK INVENTORY LEVELS
	
	
	
	

	

}
