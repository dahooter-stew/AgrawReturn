package backend;

import java.io.Serializable;

public class System implements Serializable
{
  private static final long serialVersionUID = 1L;

  private static System instance = null;

  public final ProductManager productManager = new ProductManager();
  public final TransactionManager transactionManager = new TransactionManager();
  public final UserManager userManager = new UserManager();
  public final Inventory inventory = new Inventory();
  public final BalanceManager balanceManager = new BalanceManager();

  public static System instance()
  {
    if (instance == null)
      instance = new System();

    return instance;
  }


  private System() {}
}
