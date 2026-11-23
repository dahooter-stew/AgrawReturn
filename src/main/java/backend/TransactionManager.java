package backend;

import java.util.ArrayList;
import java.util.List;

public class TransactionManager 
{
  private static List<Transaction> transactionHistory;

  TransactionManager()
  {
    transactionHistory = new ArrayList<>();
  }

  public static Transaction[] getHistory() 
  {
    return transactionHistory.toArray(new Transaction[0]); 
  }
  
  public static List<Transaction> getHistoryList() 
  {
    return transactionHistory;
  }

  public static void processTransaction(Transaction transaction) 
  {
    if (!transaction.validateTransaction()) return;
    transactionHistory.add(transaction);
  }

  public static double calculateProfit() 
  {
    double profit = 0.0;
    for (Transaction transaction : transactionHistory)
    {
      if (transaction instanceof Sale)
        profit += transaction.getPrice();
      else if (transaction instanceof Purchase)
        profit -= transaction.getPrice();
    }
    return profit;
  }

  public List<Transaction> filterByUser(User user)
  {
    return transactionHistory;
  }
}
