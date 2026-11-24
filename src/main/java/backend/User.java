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

// User base class
// User objects are mainly used for identification, the User object 
// themselves do not access functionality, but are used as i.e. 
// determine functionality available if User is Manager or Clerk
public abstract class User implements Serializable
{
  private static final long serialVersionUID = 1L;
	
  // Atributes
	protected String username;
	protected String password;
	
	// log in takes in username and password then checks
	public boolean logIn(String username, String password) {
		return password.equals(this.password) && username.equals(this.username);
	}

  // Constructor
	public User(String username, String password) 
  {
		this.username = username;
		this.password = password;
	}

  // Getters
	public String getUsername() 
  { 
		return username; 
	}

	public String getPassword() 
  {
    return password; 
  }

  // Setters
	public void setUsername(String username) 
  {
	  this.username = username; 
	}

	public void setPassword(String password) 
  {
		this.password = password; 
	}

  // java Object overrides, for debugging
  @Override
  public String toString()
  {
    return username + " | " + password + " | " + (this instanceof Manager ? "Manager" : "Clerk"); 
  }

  @Override
  public boolean equals(Object o)
  {
    if (o instanceof User other)
      return username.equals(other.getUsername()) && password.equals(other.getPassword());
    else
      return false;
  }
}
