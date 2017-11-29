package io.inkstudios.spigot.lock.account;

import io.inkstudios.spigot.lock.Lock;

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

abstract class SimpleLockAccount implements LockAccount {

	private final UUID uniqueId;
	private final List<Lock> locks = new ArrayList<>();
	private final Map<Location, Lock> locationToLock = new ConcurrentHashMap<>();
	
	protected boolean marked;
	
	protected SimpleLockAccount(UUID uniqueId, List<Lock> locks) {
		this.uniqueId = uniqueId;
		this.locks.addAll(locks);
	}
	
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
	
	protected void addLockWithoutSaving(Lock lock) {
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
		}
	}
	
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
	public void markForWrite() {
		this.marked = true;
	}
	
	@Override
	public void unmarkForWrite() {
		this.marked = false;
	}
	
	@Override
	public boolean isMarkedForWrite() {
		return this.marked;
	}
	
}
