package gosu.db;

import java.sql.*;
import java.util.Collections;
import java.util.List;

public class GosuDB
{
  private static ThreadLocal<Connection> _THREAD_CONNECTION = new ThreadLocal<>();

  private static String g_DBURL = "";

  public static void setDBUrl(String url) {
    g_DBURL = url;
  }

  public static String getDBUrl() {
    return g_DBURL;
  }

  public static Connection getConnection() throws SQLException
  {
    Connection c = _THREAD_CONNECTION.get();
    if(c != null) {
      return c;
    } else {
      return DriverManager.getConnection(g_DBURL);
    }
  }

  public static void establishConnection() throws SQLException {
    _THREAD_CONNECTION.set(getConnection());
  }

  public static void releaseConnection() throws SQLException {
    Connection c = _THREAD_CONNECTION.get();
    if(c != null) {
      c.close();
    }
    _THREAD_CONNECTION.remove();
  }

  public static PreparedStatement prepareStatement( String sql, List vals) throws SQLException
  {
    Connection conn = getConnection();
//    System.out.println(sql + " @RagnarDB 27"); debugging logging info
    maybeLog(sql, vals);
    PreparedStatement stmt = conn.prepareStatement( sql );
    setVals(vals, stmt);
    return stmt;
  }

  public static PreparedStatement prepareStatement( String sql, List vals, int autoGeneratedKeys ) throws SQLException
  {
    Connection conn = getConnection();
    maybeLog(sql, vals);
    PreparedStatement stmt = conn.prepareStatement( sql, autoGeneratedKeys );
    setVals(vals, stmt);
    return stmt;
  }

  private static void maybeLog( String sql, List vals )
  {
    System.out.println("RagnarDB SQL : " + sql + " : " + vals);
  }

  private static void setVals( List vals, PreparedStatement stmt ) throws SQLException
  {
    for( int i = 0; i < vals.size(); i++ )
    {
      Object obj = vals.get(i);
      if(obj instanceof String) {
        stmt.setString(i + 1, (String) obj);
      } else if(obj instanceof Boolean){
        stmt.setBoolean(i + 1, (Boolean) obj);
      } else if(obj instanceof Integer){
        stmt.setInt(i + 1, (Integer) obj);
      } else if(obj instanceof Double){
        stmt.setDouble(i + 1, (Double) obj);
      } else if(obj instanceof Long){
        stmt.setLong(i + 1, (Long) obj);
      } else if(obj instanceof Date){
        stmt.setDate(i + 1, (Date) obj);
      } else {
        stmt.setObject( i + 1, obj );
      }
    }
  }

  public static boolean execStatement( String setup ) throws SQLException
  {
    PreparedStatement stmt = prepareStatement( setup, Collections.emptyList() );
    return stmt.execute();
  }

  public static int count( String tableName ) throws SQLException
  {
    Connection conn = getConnection();
    PreparedStatement stmt = prepareStatement( "SELECT COUNT(1) FROM " + tableName, Collections.emptyList() );
    ResultSet resultSet = stmt.executeQuery();
    resultSet.next();
    return resultSet.getInt( 1 );
  }
}
