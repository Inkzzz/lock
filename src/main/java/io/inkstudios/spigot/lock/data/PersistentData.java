package io.inkstudios.spigot.lock.data;

public interface PersistentData {
	
	void markForWrite();
	
	void unmarkForWrite();
	
	boolean isMarkedForWrite();
	
	void save();
	
}
