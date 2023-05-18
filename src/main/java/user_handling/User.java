package user_handling;
import java.io.File; 
import java.io.IOException;

import user_handling.UserHandler;
public class User {
	private String name;
	UserHandler usrHandler = new UserHandler();
	
	public User(String name) throws IOException {
		this.name = name;
		usrHandler.setupUser(name);
		
	}
	
	public String getName() {
		return name;
	}

}
