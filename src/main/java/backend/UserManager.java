package backend;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserManager implements Serializable
{
  private final List<User> users = new ArrayList<>();

  public User login(String username, String password) 
  {
    for (User u : users)
      if (u.logIn(username, password))
        return u;
    return null;
  }

  public void addUser(User user) 
  {
    if (user.getUsername().isEmpty() || user.getPassword().isEmpty())
      throw new IllegalArgumentException("Username and password can't be empty.");
    if (isUsernameTaken(user.getUsername(), null))
      throw new IllegalArgumentException("That username is already taken.");

    users.add(user);
  }

  public void removeUser(User user) 
  {
    users.remove(user);
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
