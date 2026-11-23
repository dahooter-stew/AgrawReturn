package backend;


public class Manager extends User 
{
	
	// can add products to inventory
	// restock
	// 

	public Manager(String username, String password) { 
		super(username, password); 
	}
  	@Override public boolean isManager() { 
		return true; 
	}
	
	public void addProduct(Product product) {
	}
	
	public boolean restockProduct(String productId, int amt) {
    return false;
	}
	
	// TODO: MONITOR SALE HISTORY
	// TODO: ACCESS TRANSACTION HISTORY
	
	

}
