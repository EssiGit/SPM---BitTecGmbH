package user_handling;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHasher {
	

	/**
	 * 
	 * hashing the plain password
	 * @param password
	 * @return good ol hash
	 */
	public String hashPassword(String password) {
		
		String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
		System.out.println("new pw: " + hashedPassword);
		return hashedPassword;
	}
	
	/**
	 * checks if then plain password is the same as the hashed
	 * @param passwordPlain
	 * @param hashed
	 * @return true if its the same, false if not
	 */
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
