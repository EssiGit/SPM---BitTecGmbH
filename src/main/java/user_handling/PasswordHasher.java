package user_handling;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHasher {
	

	
	public String hashPassword(String password) {
		
		String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
		System.out.println("new pw: " + hashedPassword);
		return hashedPassword;
	}
	
	public boolean checkPassword(String passwordPlain, String hashed) {
		boolean pwIsCorrect = false;
		try {
		pwIsCorrect = BCrypt.checkpw(passwordPlain, hashed);
		System.out.println("pw is correct: " + pwIsCorrect);
		}catch(java.lang.IllegalArgumentException e) {
		pwIsCorrect = false;
		}
		return pwIsCorrect;
	}


}
