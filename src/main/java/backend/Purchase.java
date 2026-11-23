package backend;

public class Purchase extends Transaction {
	
	String source; // WHOEVER GAVE 
	
	// public Purchase(Product product, int amount, String date, User user) {
	// 	super(product, amount, date, user);
	// }

	public Purchase(Product product, int amount, String date, User user, String source) {
		super(product, amount, date, user);
    this.source = source;
	}

  public String getSource()
  {
    return source;
  }
	
	@Override
	public boolean validateTransaction() 
  {
		if (price >= 0 || amount > 0)
			return true;
		return false;
	}
}
