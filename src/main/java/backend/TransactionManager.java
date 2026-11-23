package backend;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TransactionManager implements Serializable
{
  public double balance = 0.0;
  private List<Transaction> transactionHistory;

  TransactionManager()
  {
    transactionHistory = new ArrayList<>();
  }

  public Transaction[] getHistory() 
  {
    return transactionHistory.toArray(new Transaction[0]); 
  }
  
  public List<Transaction> getHistoryList() 
  {
    return transactionHistory;
  }

  public void processTransaction(Transaction transaction) 
  {
    if (!transaction.validateTransaction()) 
      return;

    int amount = StoreSystem.instance().inventory.getProductQuantity(transaction.getProduct());
    if (transaction.getAmount() > amount)
      return;

    StoreSystem.instance().inventory.decreaseProduct(transaction.getProduct(), transaction.getAmount());
    transactionHistory.add(transaction);
  }

  public double calculateProfit() 
  {
    double profit = 0.0;
    for (Transaction transaction : transactionHistory)
    {
      if (transaction instanceof Sale)
        profit += transaction.getPrice();
      else if (transaction instanceof Purchase)
        profit -= transaction.getPrice();
    }
    balance = profit;
    return profit;
  }

  public List<Transaction> filterByUser(User user)
  {
    List<Transaction> filtered = new ArrayList<>();

    for (Transaction transaction : transactionHistory)
      if (transaction.getUser().equals(user))
        filtered.add(transaction);

    return filtered;
  }

  public void printTransactions()
  {
    System.out.println("=====Transaction Manager=====");

    for (Transaction transaction : transactionHistory)
    {
      System.out.println(transaction.toString());
    }

    System.out.println("=============================");
  }
}
