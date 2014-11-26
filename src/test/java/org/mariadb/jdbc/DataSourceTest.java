package org.mariadb.jdbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Test;

public class DataSourceTest extends BaseTest {
    @Test
    public void testDataSource() throws SQLException {
    	MySQLDataSource ds = new MySQLDataSource(mHostname, mPort, mDatabase);
    	Connection connection = ds.getConnection(mUsername, mPassword);
        try {
            assertEquals(connection.isValid(0),true);
        } finally  {
            connection.close();
        }
    }
    @Test
    public void testDataSource2() throws SQLException {
    	MySQLDataSource ds = new MySQLDataSource(mHostname, mPort, mDatabase);
    	Connection connection = ds.getConnection(mUsername, mPassword);
        try {
            assertEquals(connection.isValid(0),true);
        }finally {
            connection.close();
        }
    }
    
    /**
     * CONJ-80
     * @throws SQLException
     */
    @Test
    public void setDatabaseNameTest() throws SQLException {
    	MySQLDataSource ds = new MySQLDataSource(mHostname, mPort, mDatabase);
    	Connection connection = ds.getConnection(mUsername, mPassword);
    	connection.createStatement().execute("CREATE DATABASE IF NOT EXISTS test2");
    	ds.setDatabaseName("test2");
    	connection = ds.getConnection(mUsername, mPassword);
    	assertEquals("test2", ds.getDatabaseName());
    	assertEquals(ds.getDatabaseName(), connection.getCatalog());
    	connection.createStatement().execute("DROP DATABASE IF EXISTS test2");
    	connection.close();
    }
    
    /**
     * CONJ-80
     * @throws SQLException
     */
    @Test
    public void setServerNameTest() throws SQLException {
    	MySQLDataSource ds = new MySQLDataSource(mHostname, mPort, mDatabase);
    	Connection connection = ds.getConnection(mUsername, mPassword);
    	ds.setServerName("127.0.0.1");
    	connection = ds.getConnection(mUsername, mPassword);
    	connection.close();
    }
    
    /**
     * CONJ-80
     * @throws SQLException
     */
    @Test(expected=SQLException.class) // unless port 3307 can be used
    public void setPortTest() throws SQLException {
    	MySQLDataSource ds = new MySQLDataSource(mHostname, mPort, mDatabase);
    	Connection connection = ds.getConnection(mUsername, mPassword);
    	ds.setPort(3307);
    	connection = ds.getConnection(mUsername, mPassword);
    	connection.close();
    }
    
    /**
     * CONJ-123:
     * Session variables lost and exception if set via MySQLDataSource.setProperties/setURL
     * @throws SQLException 
     */
    @Test
    public void setPropertiesTest() throws SQLException {
    	MySQLDataSource ds = new MySQLDataSource(mHostname, mPort, mDatabase);
    	ds.setProperties("sessionVariables=sql_mode='PIPES_AS_CONCAT'");
    	Connection connection = ds.getConnection(mUsername, mPassword);
    	ResultSet rs = connection.createStatement().executeQuery("SELECT @@sql_mode");
    	assertTrue(rs.next());
    	assertEquals("PIPES_AS_CONCAT", rs.getString(1));
    	ds.setUrl(connURI + "&sessionVariables=sql_mode='ALLOW_INVALID_DATES'");
    	connection = ds.getConnection(mUsername, mPassword);
    	rs = connection.createStatement().executeQuery("SELECT @@sql_mode");
    	assertTrue(rs.next());
    	assertEquals("ALLOW_INVALID_DATES", rs.getString(1));
    	connection.close();
    }
}
