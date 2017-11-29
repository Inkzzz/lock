package io.inkstudios.spigot.lock.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

abstract class SQLDatabase {
	
	protected Connection connection;
	
	public abstract void connect() throws SQLException;
	
	public void closeConnection() throws SQLException {
		if (!this.isConnected()) {
			return;
		}
		
		this.connection.close();
	}
	
	public boolean isConnected() throws SQLException {
		return this.connection != null && !this.connection.isClosed();
	}
	
	public Connection getConnection() {
		return this.connection;
	}
	
	public final ResultSet query(String query) throws SQLException {
		Objects.requireNonNull(query, "query");
		
		if (!this.isConnected()) {
			this.connect();
		}
		
		Statement statement = this.connection.createStatement();
		
		return statement.executeQuery(query);
	}
	
	public final void inject(String query) throws SQLException {
		Objects.requireNonNull(query, "query");
		
		if (!this.isConnected()) {
			this.connect();
		}
		
		PreparedStatement statement = this.connection.prepareStatement(query);
		
		statement.executeUpdate();
	}
	
}
