package edu.upenn.cis455.storage;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import static com.sleepycat.persist.model.Relationship.*;
import com.sleepycat.persist.model.SecondaryKey;

@Entity
public class UserEntityClass {

	// username is primary key
	@PrimaryKey
	private String username;

	private String password;

	public void setUsername(String name) {
		username = name;
	}

	public void setPassword(String passwd) {
		password = passwd;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	@Override
	public String toString() {
		return "UserEntityClass [username=" + username + ", password="
				+ password + "]";
	}

}
