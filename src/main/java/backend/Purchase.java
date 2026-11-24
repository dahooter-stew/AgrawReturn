/*************************************************************************
 *
 * CMSC 22 PROJECT
 * Agriculture Data Management System
 *
 * AgrawReturn()
 *
 * Description:
 * AgrawReturn() is an organization that aims to address local problems regarding 
 * fruit wastes by providing services for a) processing of fruit waste to develop 
 * agricultural-use products and b) distribution of fruit wastes for other 
 * organizations/parties use.
 *
 * By:
 * Bajade, Jann Alexandrei C.
 * Sta Romana, Maria Mikaela C.
 */

package backend;

// Purchase, subclass of Transaction
// tracks transactions, products bought by
// the company, typically used for tracking
// purchase of raw materials
public class Purchase extends Transaction 
{
  private static final long serialVersionUID = 1L;
	
  // Source company or person
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
	
  // For purchases, product doesnt have to be in 
  // product manager, because we are buying externally
	@Override
	public boolean validateTransaction() 
  {
		if (price >= 0 || amount > 0)
			return true;
		return false;
	}
}
