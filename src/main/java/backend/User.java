package backend;

public abstract class User {
	
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

}
