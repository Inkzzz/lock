package io.inkstudios.spigot.lock.account;

import io.inkstudios.spigot.lock.Lock;
import io.inkstudios.spigot.lock.LockState;
import io.inkstudios.spigot.lock.data.PersistentData;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public interface LockAccount extends PersistentData {
	
	/**
	 * @return the account owners unique id
	 */
	UUID getUniqueId();
	
	/**
	 * @return an optional player of the accounts owner determined by the accounts owners unique id
	 */
	Optional<Player> getPlayer();
	
	/**
	 * @return the current lock state of the account
	 */
	LockState getState();
	
	/**
	 * Sets the state of the account
	 * @param state the new state of the account
	 */
	void setState(LockState state);
	
	/**
	 * Returns an optional value of the lock that could be present at the location {@code location}
	 *
	 * @param location the alleged location of a lock
	 * @return an optional value of the lock at the location {@code location}
	 */
	Optional<Lock> getLock(Location location);
	
	/**
	 * Registers a lock {@code lock} to the account
	 *
	 * @param lock the lock to register
	 */
	void addLock(Lock lock);
	
	/**
	 * Unregisters a lock {@code lock} from the account
	 *
	 * @param lock the lock to unregister
	 */
	void removeLock(Lock lock);
	
	/**
	 * Unregisters a lock found at the specified location {@code location}
	 *
	 * <p>If there is no lock at the location {@code location}, then it is ignored</p>
	 *
	 * @param location location of lock
	 */
	void removeLock(Location location);
	
	/**
	 * Checks if there is a lock at the location specified {@code location}
	 *
	 * @param location the location to check
	 * @return true if there is a lock present at the location
	 */
	boolean isLock(Location location);
	
	/**
	 * @return a list of locks owned by this account
	 */
	List<Lock> getLocks();
	
	/**
	 * @return a stream of locks owned by this account
	 */
	Stream<Lock> streamLocks();
	
	/**
	 * Loads data from its storage location if there is data present
	 */
	void loadDataIfPresent();
	
}
