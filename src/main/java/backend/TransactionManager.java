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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// Transaction manager, handles Sales and Purchases
// Also calculates profit and updates current balance
public class TransactionManager implements Serializable
{
  private static final long serialVersionUID = 1L;

  // Attributes
  public double balance = 0.0;
  private List<Transaction> transactionHistory;

  // Constructor
  TransactionManager()
  {
    transactionHistory = new ArrayList<>();
  }

  // Returns copy, static array
  public Transaction[] getHistory() 
  {
    return transactionHistory.toArray(new Transaction[0]); 
  }
  
  // Returns actual list
  public List<Transaction> getHistoryList() 
  {
    return transactionHistory;
  }

  // checks if transaction is valid 
  // if there's enough stock 
  // then decreases stock and adds to history
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

  // If sale it's add 
  // if purchase minus
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


  // Filter
  public List<Transaction> filterByUser(User user)
  {
    List<Transaction> filtered = new ArrayList<>();

    for (Transaction transaction : transactionHistory)
      if (transaction.getUser().equals(user))
        filtered.add(transaction);

    return filtered;
  }

  public boolean removeTransaction(Transaction transaction)
  {
    return transactionHistory.remove(transaction);
  }

  // For debugging
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
