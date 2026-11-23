package backend;

public class Sale extends Transaction 
{
	public Sale(Product product, int amount, String date, User user) {
		super(product, amount, date, user);
	}
	
	@Override
	public boolean validateTransaction() {
    if (!(StoreSystem.instance().productManager.getProductById(product.getId()) != null))
      return false;

		if (price > 0 || amount > 0)
			return true;

		return false;
	}
}
