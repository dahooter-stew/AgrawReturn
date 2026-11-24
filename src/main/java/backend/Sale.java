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

// Sale subclass of Transaction
// tracks purchases by Customers, recorded by Users
public class Sale extends Transaction 
{
  private static final long serialVersionUID = 1L;

	public Sale(Product product, int amount, String date, User user) {
		super(product, amount, date, user);
	}
	

  // valid if ProductManager has product (officiall registerd by company
  // as one of our products)
  // And if price and amount are none negative
	@Override
	public boolean validateTransaction() {
    if (!(StoreSystem.instance().productManager.hasProduct(product)))
      return false;

		if (price > 0.0 || amount > 0)
			return true;

		return false;
	}
}
