package io.inkstudios.spigot.lock.account;

import io.inkstudios.spigot.lock.Lock;

import java.util.List;
import java.util.UUID;

final class SQLLockAccount extends SimpleLockAccount {
	
	private final Object lock = new Object();
	
	public SQLLockAccount(UUID uniqueId, List<Lock> locks) {
		super(uniqueId, locks);
	}
	
	public SQLLockAccount(UUID uniqueId) {
		super(uniqueId);
	}
	
	@Override
	public void loadDataIfPresent() {
		
	}
	
	@Override
	public void save() {
		if (!this.isMarkedForWrite()) {
			return;
		}
		
		synchronized (this.lock) {
			
		}
	}
	
}