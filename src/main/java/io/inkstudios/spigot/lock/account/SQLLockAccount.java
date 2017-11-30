package io.inkstudios.spigot.lock.account;

import io.inkstudios.spigot.lock.Lock;
import io.inkstudios.spigot.lock.LockPlugin;
import io.inkstudios.spigot.lock.database.LockMySQLDatabase;
import io.inkstudios.spigot.lock.util.LocationUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

final class SQLLockAccount extends SimpleLockAccount {
	
	private static final Pattern LOCK_LOCATION_SEPARATOR = Pattern.compile("\\|");
	
	private final Object lock = new Object();
	
	/**
	 * Constructs a new {@link SQLLockAccount} instance
	 * @param uniqueId the owner of the account
	 * @param locks the pre-set locks of the account
	 */
	public SQLLockAccount(UUID uniqueId, List<Lock> locks) {
		super(uniqueId, locks);
	}
	
	/**
	 * Constructs a new {@link SQLLockAccount} instance
	 * @param uniqueId the owner of the account
	 */
	public SQLLockAccount(UUID uniqueId) {
		super(uniqueId);
	}
	
	@Override
	public void loadDataIfPresent() {
		LockMySQLDatabase database = LockPlugin.getInstance().getSqlDatabase();
		
		try (ResultSet resultSet = database.query("SELECT * FROM locks WHERE UNIQUE_ID = '" + this.getUniqueId().toString() + "';")) {
			while (resultSet.next()) {
				int id = resultSet.getInt("LOCK_PKEY");
				String lockLocations = resultSet.getString("LOCK_LOCATIONS");
				
				Lock.Builder builder = Lock.builder()
						.setOwner(this.getUniqueId())
						.setId(id);
				
				for (String serializedLocation : SQLLockAccount.LOCK_LOCATION_SEPARATOR.split(lockLocations)) {
					if (serializedLocation.isEmpty()) {
						continue;
					}
					
					builder.addLocation(LocationUtil.fromString(serializedLocation));
				}
				
				this.addLockWithoutSaving(builder.build());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void save() {
		if (!this.isMarkedForWrite()) {
			return;
		}
		
		LockMySQLDatabase database = LockPlugin.getInstance().getSqlDatabase();
		
		synchronized (this.lock) {
			for (Lock lock : this.getLocks()) {
				String serializedLocations = this.serializeLocations(lock);
				
				// lock.id == -1 - new record
				if (lock.getId() == -1) {
					// insert new record and generate new id
					try {
						database.inject("INSERT INTO locks (UNIQUE_ID, LOCK_LOCATIONS) VALUES" +
								" ('" + this.getUniqueId().toString() + "', '" + serializedLocations + "');");
					} catch (SQLException e) {
						e.printStackTrace();
					}
					
					// Update the lock objects id
					try (ResultSet resultSet = database.query("SELECT LOCK_PKEY FROM locks WHERE UNIQUE_ID = '" +
							this.getUniqueId().toString() + "' AND LOCK_LOCATIONS = '" + serializedLocations + "';")) {
						if (resultSet.next()) {
							lock.setId(resultSet.getInt("LOCK_PKEY"));
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
				} else {
					// update the information of the current record
					try {
						database.inject("INSERT INTO locks (LOCK_PKEY, UNIQUE_ID, LOCK_LOCATIONS) VALUES" +
								" (" + lock.getId() + ", '" + this.getUniqueId().toString() + "', '" + serializedLocations + "')" +
								" ON DUPLICATE KEY UPDATE LOCK_LOCATIONS = '" + serializedLocations + "';");
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
			
			this.unmarkForWrite();
		}
	}
	
	private String serializeLocations(Lock lock) {
		return lock.getLockLocations().stream().map(LocationUtil::toString)
				.collect(Collectors.joining(SQLLockAccount.LOCK_LOCATION_SEPARATOR.pattern()));
	}
	
	@Override
	void removedLock(Lock lock) {
		synchronized (this.lock) {
			if (lock.getId() == -1) {
				this.softInvalidation(lock);
			} else {
				this.hardInvalidation(lock);
			}
		}
	}
	
	/**
	 * Removes the lock from the database where the unique id is equal to the lock owner and the serialized
	 * location string of the locks location are equal
	 * @param lock the lock to remove from the database
	 */
	private void softInvalidation(Lock lock) {
		LockMySQLDatabase database = LockPlugin.getInstance().getSqlDatabase();
		
		String serializedLocations = this.serializeLocations(lock);
		
		try {
			database.inject("DELETE FROM locks WHERE LOCK_LOCATIONS = '" + serializedLocations + "' " +
					"AND UNIQUE_ID = '" + this.getUniqueId().toString() + "';");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Removes the lock from the database where the locks id is equal to LOCK_PKEY
	 * @param lock the lock to remove from the dabatase
	 */
	private void hardInvalidation(Lock lock) {
		LockMySQLDatabase database = LockPlugin.getInstance().getSqlDatabase();
		
		try {
			database.inject("DELETE FROM locks WHERE LOCK_PKEY = " + lock.getId() + ";");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}