package com.neu.distributedsystem.server.Database;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;


public class DatabaseConn {


  private static final String url = "jdbc:mysql://hw2large.chstt9bcp0re.us-west-2.rds.amazonaws.com:3306/hw2db";
  private static final String userName = "abcd1234";
  private static final String password = "abcd1234";
  private static final String schema = "hw2db";

private static ComboPooledDataSource dataSourcePool;


  static {


    dataSourcePool = new ComboPooledDataSource();
    dataSourcePool.setJdbcUrl(url);
    dataSourcePool.setUser(userName);
    dataSourcePool.setPassword(password);
    try {
      dataSourcePool.setDriverClass("com.mysql.jdbc.Driver");
    } catch (PropertyVetoException e) {
      e.printStackTrace();
    }

    dataSourcePool.setAcquireIncrement(5);
    dataSourcePool.setInitialPoolSize(20);
    dataSourcePool.setMinPoolSize(2);
    dataSourcePool.setMaxPoolSize(128);

    dataSourcePool.setMaxIdleTime(60);



  }



  public static Connection getConn() throws SQLException {
    return dataSourcePool.getConnection();
  }



  public void disconnection(Connection connection) {
    try {
      connection.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
