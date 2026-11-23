package backend;

import java.io.Serializable;

public class BalanceManager implements Serializable
{
  // for saving into files in the future
  private static final long serialVersionUID = 3L;

  private static double balance = 0.0;

  public double getBalance()
  {
    return balance;
  }

  public boolean addBalance(double amount) 
  {
    if (balance + amount < 0.0)
      return false;

    balance += amount;
    return true;
  }

  public boolean setBalance(double amount)
  {
    if (amount < 0.0)
      return false;

    balance = amount;
    return true;
  }
}
