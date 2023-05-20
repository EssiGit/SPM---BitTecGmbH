package user_handling;
 
import java.io.IOException;

public class User {
	private String name;
	
	public User(String name) throws IOException {
		this.name = name;
		
		
	}
	
	public String getName() {
		return name;
	}

}
