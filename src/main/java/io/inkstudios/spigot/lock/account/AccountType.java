package io.inkstudios.spigot.lock.account;

import java.util.Objects;

public enum AccountType {
	
	PERSISTENT("persistent", PersistentLockAccount.class),
	MYSQL("sql", SQLLockAccount.class);
	
	private final String name;
	private final Class<? extends LockAccount> lockAccount;
	
	AccountType(String name, Class<? extends LockAccount> lockAccount) {
		this.name = name;
		this.lockAccount = lockAccount;
	}
	
	public String getName() {
		return this.name;
	}
	
	public Class<? extends LockAccount> getLockAccount() {
		return this.lockAccount;
	}
	
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
