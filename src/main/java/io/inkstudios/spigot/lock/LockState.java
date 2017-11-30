package io.inkstudios.spigot.lock;

/**
 * LockState used to identify whether a {@link io.inkstudios.spigot.lock.account.LockAccount} is locking a chest,
 * unlocking a chest, or doing neither one.
 *
 * <p>There should be no reason for another enum to be created within LockState.</p>
 */
public enum LockState {
	
	LOCK,
	UNLOCK;
	
}
