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
	
	UUID getUniqueId();
	
	Optional<Player> getPlayer();
	
	LockState getState();
	
	void setState(LockState state);
	
	Optional<Lock> getLock(Location location);
	
	void addLock(Lock lock);
	
	void removeLock(Lock lock);
	
	void removeLock(Location location);
	
	boolean isLock(Location location);
	
	List<Lock> getLocks();
	
	Stream<Lock> streamLocks();
	
	void loadDataIfPresent();
	
}
