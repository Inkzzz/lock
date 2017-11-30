package io.inkstudios.spigot.lock.database;

import org.bukkit.configuration.file.FileConfiguration;

import java.sql.DriverManager;
import java.sql.SQLException;

public class LockMySQLDatabase extends SQLDatabase {
	
	private final String host;
	private final int port;
	private final String database;
	private final String username;
	private final String password;
	
	/**
	 * Constructs a new {@link LockMySQLDatabase} instance
	 *
	 * <p>The configuration is expected to have the correct configuration</p>
	 *
	 * @param configuration the configuration file containing the database authentication details
	 */
	public LockMySQLDatabase(FileConfiguration configuration) {
		this.host = configuration.getString("mysql.host", "localhost");
		this.port = configuration.getInt("mysql.port", 3306);
		this.database = configuration.getString("mysql.database", "database");
		this.username = configuration.getString("mysql.username", "root");
		this.password = configuration.getString("mysql.password", "password");
	}
	
	@Override
	public void connect() throws SQLException {
		this.connection = DriverManager.getConnection("jdbc:mysql://" + this.host +
				":" + String.valueOf(this.port) + "/" + this.database, this.username, this.password);
	}
	
	/**
	 * Creates the default tables required for this plugin
	 */
	public final void createDefaultTables() {
		try {
			this.inject("CREATE TABLE IF NOT EXISTS locks (LOCK_PKEY INT AUTO_INCREMENT NOT NULL," +
					" UNIQUE_ID VARCHAR(36) NOT NULL, LOCK_LOCATIONS TEXT, PRIMARY KEY (LOCK_PKEY));");
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
}
