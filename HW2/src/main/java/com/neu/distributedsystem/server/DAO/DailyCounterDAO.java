package com.neu.distributedsystem.server.DAO;


import com.neu.distributedsystem.server.DO.DailyCounter;
import com.neu.distributedsystem.server.Database.DatabaseConn;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DailyCounterDAO {

  private static final String tableName = "counter";
  private static final String insertStatement = "insert into "+ tableName + " (userId, dayNum, timeInterval, stepCount)" + "VALUES(?,?,?,?)";
  private static final String queryStatement = "select sum(stepCount) from " +tableName +" where userID = ? and dayNum = ?";

  public void insert(DailyCounter dailyCounter) throws SQLException {

    Connection connection = null;
    PreparedStatement statement = null;
    try {
      connection = DatabaseConn.getConn();
      statement = connection.prepareStatement(insertStatement);
      statement.setInt(1, dailyCounter.getUserId());
      statement.setInt(2, dailyCounter.getDay());
      statement.setInt(3, dailyCounter.getTimeInterval());
      statement.setInt(4, dailyCounter.getStepCount());
      statement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
      throw e;
    } finally {
      if(connection != null) {
        connection.close();
      }
      if(statement != null) {
        statement.close();
      }
    }
  }

  public int getCountForDay(int userID, int dayNum) throws SQLException {
    Connection connection = null;
    PreparedStatement statement = null;
    ResultSet results = null;
    int cnt = 0;
    try {
      connection = DatabaseConn.getConn();
      statement = connection.prepareStatement(queryStatement);
      statement.setInt(1, userID);
      statement.setInt(2, dayNum);
      results = statement.executeQuery();

      while(results.next()) {
        cnt += results.getInt("sum(stepCount)");
      }
    } catch (SQLException e) {
      e.printStackTrace();
      throw e;
    } finally {
      if(connection != null) {
        connection.close();
      }
      if(statement != null) {
        statement.close();
      }
      if(results != null) {
        results.close();
      }
    }
    return cnt;
  }



}
