package user_handling;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHasher {
	
	
	
	public String hashPassword(String password) {
		System.out.println(password);
		String salt = BCrypt.gensalt();
		String hashedPassword = BCrypt.hashpw(password, salt);
		return hashedPassword;
	}

}
