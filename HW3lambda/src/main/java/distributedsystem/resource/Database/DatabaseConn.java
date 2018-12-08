package distributedsystem.resource.Database;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;


public class DatabaseConn {


  private static final String url = "jdbc:mysql://hw3.caawdrlrzc9a.us-west-2.rds.amazonaws.com:3306/hw2db";
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

    dataSourcePool.setAcquireIncrement(1);
    dataSourcePool.setInitialPoolSize(1);
    dataSourcePool.setMinPoolSize(0);
    dataSourcePool.setMaxPoolSize(1);

    dataSourcePool.setMaxIdleTime(120);



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
