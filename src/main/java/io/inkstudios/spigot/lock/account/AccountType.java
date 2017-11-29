package io.inkstudios.spigot.lock.account;

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
	
}
