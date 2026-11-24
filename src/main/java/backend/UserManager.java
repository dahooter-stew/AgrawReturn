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

// Manages users, Managers can add Clerks here
public class UserManager implements Serializable
{
  private static final long serialVersionUID = 1L;

  // Attributes
  private final List<User> users = new ArrayList<>();

  // fancy name for finding via username and password
  public User login(String username, String password) 
  {
    for (User u : users)
      if (u.logIn(username, password))
        return u;
    return null;
  }

  public User addUser(User user) 
  {
    if (user.getUsername().isEmpty() || user.getPassword().isEmpty())
      throw new IllegalArgumentException("Username and password can't be empty.");
    if (isUsernameTaken(user.getUsername(), null))
      throw new IllegalArgumentException("That username is already taken.");

    users.add(user);
    return user;
  }

  public void removeUser(User user) 
  {
    users.remove(user);
  }

  public User getUser(String name)
  {
    for (User user : users)
      if (name.equals(user.getUsername()))
        return user;

    return null;
  }

  public boolean isUsernameTaken(String username, User except) 
  {
    for (User u : users)
      if (u != except && u.getUsername().equalsIgnoreCase(username))
        return true;
    return false;
  }

  public List<Clerk> getClerks() 
  {
    List<Clerk> clerks = new ArrayList<>();
    for (User x : users)
      if (x instanceof Clerk)
        clerks.add((Clerk) x);  
    return clerks;
  }

  public List<Manager> getManagers() 
  {
    List<Manager> managers = new ArrayList<>();
    for (User x : users)
      if (x instanceof Manager)
          managers.add((Manager) x);  
    return managers;
  }

  // for debugging
  public void printUsers()
  {
    System.out.println("=====User Manager=====");

    for (User user : users)
    {
      System.out.println(user.toString());
    }

    System.out.println("======================");
  }
}
