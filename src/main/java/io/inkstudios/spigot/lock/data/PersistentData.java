package io.inkstudios.spigot.lock.data;

/**
 * Identifies objects as being persistent and written to a file.
 */
public interface PersistentData {
	
	/**
	 * Marks a file to be written
	 */
	void markForWrite();
	
	/**
	 * Unmarks a file to be written
	 */
	void unmarkForWrite();
	
	/**
	 * Checks if a file is to be written to
	 * @return true if the file should be written to
	 */
	boolean isMarkedForWrite();
	
	/**
	 * Executes the file to be saved
	 */
	void save();
	
}
