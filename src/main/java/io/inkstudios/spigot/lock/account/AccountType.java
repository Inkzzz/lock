package io.inkstudios.spigot.lock.account;

import java.util.Objects;

public enum AccountType {
	
	PERSISTENT("persistent", PersistentLockAccount.class),
	MYSQL("sql", SQLLockAccount.class);
	
	private final String name;
	private final Class<? extends LockAccount> lockAccount;
	
	/**
	 * Constructs a new {@link AccountType}
	 *
	 * @param name the name of the account
	 * @param lockAccount the class owner of the account
	 */
	AccountType(String name, Class<? extends LockAccount> lockAccount) {
		this.name = name;
		this.lockAccount = lockAccount;
	}
	
	/**
	 * @return the name of the account type
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * @return the class owner of the account type
	 */
	public Class<? extends LockAccount> getLockAccount() {
		return this.lockAccount;
	}
	
	/**
	 * Returns an {@link AccountType} by name {@code name} or if it is an invalid name, it will return
	 * the PERSISTENT account type.
	 *
	 * @param name the name of the account type to find
	 * @return account type specified or PERSISTENT
	 */
	public static AccountType getAccountTypeOrPersistent(String name) {
		Objects.requireNonNull(name, "name");
		
		for (AccountType accountType : AccountType.values()) {
			if (accountType.getName().equalsIgnoreCase(name)) {
				return accountType;
			}
		}
		
		return AccountType.PERSISTENT;
	}
	
}
