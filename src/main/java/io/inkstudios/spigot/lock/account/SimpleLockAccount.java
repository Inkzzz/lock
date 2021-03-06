package io.inkstudios.spigot.lock.account;

import io.inkstudios.spigot.lock.Lock;
import io.inkstudios.spigot.lock.LockState;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * This is a skeletal {@link LockAccount} where all the data used in all types of lock accounts are held.
 */
abstract class SimpleLockAccount implements LockAccount {

	private final UUID uniqueId;
	private final List<Lock> locks = new ArrayList<>();
	private final Map<Location, Lock> locationToLock = new ConcurrentHashMap<>();
	
	private boolean marked;
	private LockState lockState;
	
	/**
	 * Constructs a new {@link SimpleLockAccount} instance
	 * @param uniqueId the owner of the account
	 * @param locks the pre-set locks of the account
	 */
	protected SimpleLockAccount(UUID uniqueId, List<Lock> locks) {
		this.uniqueId = uniqueId;
		locks.forEach(this::addLockWithoutSaving);
	}
	
	/**
	 * Constructs a new {@link SimpleLockAccount} instance
	 * @param uniqueId the owner of the account
	 */
	protected SimpleLockAccount(UUID uniqueId) {
		this.uniqueId = uniqueId;
	}
	
	@Override
	public UUID getUniqueId() {
		return this.uniqueId;
	}
	
	@Override
	public Optional<Player> getPlayer() {
		return Optional.ofNullable(Bukkit.getPlayer(this.uniqueId));
	}
	
	@Override
	public LockState getState() {
		return this.lockState;
	}
	
	@Override
	public void setState(LockState lockState) {
		this.lockState = lockState;
	}
	
	@Override
	public Optional<Lock> getLock(Location location) {
		return Optional.ofNullable(this.locationToLock.get(location));
	}
	
	protected final void addLockWithoutSaving(Lock lock) {
		this.locks.add(lock);
		lock.getLockLocations().forEach(location -> this.locationToLock.put(location, lock));
	}
	
	@Override
	public void addLock(Lock lock) {
		Objects.requireNonNull(lock, "lock");
		
		if (this.locks.add(lock)) {
			this.markForWrite();
			lock.getLockLocations().forEach(location -> this.locationToLock.put(location, lock));
		}
	}
	
	@Override
	public void removeLock(Lock lock) {
		Objects.requireNonNull(lock, "lock");
		
		if (this.locks.remove(lock)) {
			this.markForWrite();
			lock.getLockLocations().forEach(this.locationToLock::remove);
			
			this.removedLock(lock);
		}
	}
	
	/**
	 * Called when a lock is successfully removed
	 * @param lock the lock removed
	 */
	abstract void removedLock(Lock lock);
	
	@Override
	public void removeLock(Location location) {
		if (!this.isLock(location)) {
			return;
		}
		
		Iterator<Lock> lockIterator = this.locks.iterator();
		
		while (lockIterator.hasNext()) {
			Lock lock = lockIterator.next();
			
			if (lock.isLockLocation(location)) {
				lockIterator.remove();
				this.markForWrite();
				lock.getLockLocations().forEach(this.locationToLock::remove);
				break;
			}
		}
	}
	
	@Override
	public boolean isLock(Location location) {
		Objects.requireNonNull(location, "location");
		return this.locationToLock.containsKey(location);
	}
	
	@Override
	public List<Lock> getLocks() {
		return Collections.unmodifiableList(this.locks);
	}
	
	@Override
	public Stream<Lock> streamLocks() {
		return this.locks.stream();
	}
	
	@Override
	public final void markForWrite() {
		this.marked = true;
	}
	
	@Override
	public final void unmarkForWrite() {
		this.marked = false;
	}
	
	@Override
	public final boolean isMarkedForWrite() {
		return this.marked;
	}
	
}
