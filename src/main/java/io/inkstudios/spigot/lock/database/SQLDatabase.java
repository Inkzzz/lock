package io.inkstudios.spigot.lock.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

/**
 * Constructs a skeletal structure for SQL-relational databases
 */
abstract class SQLDatabase {
	
	protected Connection connection;
	
	/**
	 * Connects to the database
	 * @throws SQLException if the connection process fails
	 */
	public abstract void connect() throws SQLException;
	
	/**
	 * Closes the database connection
	 * @throws SQLException if the connection cannot be closed properly
	 */
	public void closeConnection() throws SQLException {
		if (!this.isConnected()) {
			return;
		}
		
		this.connection.close();
	}
	
	/**
	 * Checks if there is a connection present to the database
	 * @return true if there is a valid connection present
	 * @throws SQLException if there is a problem checking the connection
	 */
	public boolean isConnected() throws SQLException {
		return this.connection != null && !this.connection.isClosed();
	}
	
	/**
	 * @return the current connection state
	 */
	public Connection getConnection() {
		return this.connection;
	}
	
	/**
	 * Runs a query {@code query} through {@link #getConnection()}
	 *
	 * @param query the query to execute
	 * @return the result of the query
	 * @throws SQLException if there was a problem executing the query {@code query}
	 */
	public final ResultSet query(String query) throws SQLException {
		Objects.requireNonNull(query, "query");
		
		if (!this.isConnected()) {
			this.connect();
		}
		
		Statement statement = this.connection.createStatement();
		
		return statement.executeQuery(query);
	}
	
	/**
	 * Executes a query {@code query} through {@link #getConnection()}
	 *
	 * @param query query to execute
	 * @throws SQLException if there was proiblem executing the query {@code query}
	 */
	public final void inject(String query) throws SQLException {
		Objects.requireNonNull(query, "query");
		
		if (!this.isConnected()) {
			this.connect();
		}
		
		PreparedStatement statement = this.connection.prepareStatement(query);
		
		statement.executeUpdate();
	}
	
}
