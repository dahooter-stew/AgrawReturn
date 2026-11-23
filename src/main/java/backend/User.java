package backend;

import java.io.Serializable;

public abstract class User implements Serializable
{
	
	private String username;
	private String password;
	
	// log in takes in username and password then checks
	
	public boolean logIn(String username, String password) {
		return password.equals(this.password) && username.equals(this.username);
	}
	public abstract boolean isManager();

	public User(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public String getUsername() { 
		return username; 
	}
	public String getPassword() {
		 return password; 
		}
	public void setUsername(String username) {
		 this.username = username; 
	}
	public void setPassword(String password) {
		this.password = password; 
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
