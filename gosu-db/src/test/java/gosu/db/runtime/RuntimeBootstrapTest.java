package gosu.db.runtime;


import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;

public class RuntimeBootstrapTest
{
  @BeforeClass
  static public void beforeClass() throws SQLException
  {
    gosu.db.GosuDB.setDBUrl( "jdbc:h2:mem:runtimebootstraptest;DB_CLOSE_DELAY=-1" );
    gosu.db.GosuDB.execStatement( "CREATE TABLE CONTACTS (\n" +
                            "    id bigint auto_increment,\n" +
                            "    user_id int,\n" +
                            "    first_name nchar(50),\n" +
                            "    last_name nchar(50),\n" +
                            "    age int\n" +
                            ");" );

  }

  @Before
  public void clearContacts() throws SQLException
  {
    gosu.db.GosuDB.execStatement( "DELETE FROM CONTACTS" );
  }

  @Test
  public void basicCreateWorks() throws SQLException
  {
    Assert.assertEquals( 0, gosu.db.GosuDB.count( "CONTACTS" ) );

    SQLRecord thing = new SQLRecord( "CONTACTS", "id" );
    thing.setRawValue( "first_name", "Carson" );
    thing.setRawValue( "last_name", "Gross" );
    thing.setRawValue( "age", 39 );
    thing.create();

    Assert.assertEquals( 1, gosu.db.GosuDB.count( "CONTACTS" ) );
  }

  @Test
  public void basicReadWorks() throws SQLException
  {
    Assert.assertEquals( 0, gosu.db.GosuDB.count( "CONTACTS" ) );

    SQLRecord thing = new SQLRecord( "CONTACTS", "id" );
    thing.setRawValue( "first_name", "Carson" );
    thing.setRawValue( "last_name", "Gross" );
    thing.setRawValue( "age", 39 );
    thing.create();

    Assert.assertEquals( 1, gosu.db.GosuDB.count( "CONTACTS" ) );

    SQLRecord record = SQLRecord.read( "CONTACTS", "id", thing.getRawValue( "id" ) );

    Assert.assertNotNull( record );
    Assert.assertEquals( "Carson", record.getRawValue( "first_name" ) );
    Assert.assertEquals( "Gross", record.getRawValue( "last_name" ) );
    Assert.assertEquals( 39, record.getRawValue( "age" ) );
  }

  @Test
  public void basicUpdateWorks() throws SQLException
  {
    Assert.assertEquals( 0, gosu.db.GosuDB.count( "CONTACTS" ) );

    SQLRecord thing = new SQLRecord( "CONTACTS", "id" );
    thing.setRawValue( "first_name", "Carson" );
    thing.setRawValue( "last_name", "Gross" );
    thing.setRawValue( "age", 39 );
    thing.create();

    Assert.assertEquals( 1, gosu.db.GosuDB.count( "CONTACTS" ) );

    SQLRecord record = SQLRecord.read( "CONTACTS", "id", thing.getRawValue( "id" ) );

    Assert.assertNotNull( record );
    Assert.assertEquals( "Carson", record.getRawValue( "first_name" ) );
    Assert.assertEquals( "Gross", record.getRawValue( "last_name" ) );
    Assert.assertEquals( 39, record.getRawValue( "age" ) );

    record.setRawValue( "first_name", "Scott" );
    record.setRawValue( "last_name", "McKinney" );
    record.setRawValue( "age", 46 );

    record.update();

    Assert.assertEquals( 1, gosu.db.GosuDB.count( "CONTACTS" ) );
    record = SQLRecord.read( "CONTACTS", "id", thing.getRawValue( "id" ) );

    Assert.assertNotNull( record );
    Assert.assertEquals( "Scott", record.getRawValue( "first_name" ) );
    Assert.assertEquals( "McKinney", record.getRawValue( "last_name" ) );
    Assert.assertEquals( 46, record.getRawValue( "age" ) );
  }

  @Test
  public void basicDeleteWorks() throws SQLException
  {
    Assert.assertEquals( 0, gosu.db.GosuDB.count( "CONTACTS" ) );

    SQLRecord thing = new SQLRecord( "CONTACTS", "id" );
    thing.setRawValue( "first_name", "Carson" );
    thing.setRawValue( "last_name", "Gross" );
    thing.setRawValue( "age", 39 );
    thing.create();

    Assert.assertEquals( 1, gosu.db.GosuDB.count( "CONTACTS" ) );

    thing.delete();

    Assert.assertEquals( 0, gosu.db.GosuDB.count( "CONTACTS" ) );
  }

  @Test
  public void basicManualTransactions() throws SQLException
  {

    clearContacts();
    gosu.db.GosuDB.establishConnection();
    Connection con = gosu.db.GosuDB.getConnection();
    con.setAutoCommit(false);

    Savepoint save1 = con.setSavepoint();


    Assert.assertEquals(0, gosu.db.GosuDB.count("CONTACTS"));

    SQLRecord thing = new SQLRecord( "CONTACTS", "id" );
    thing.setRawValue( "first_name", "Carson" );
    thing.setRawValue("last_name", "Gross");
    thing.setRawValue("age", 39);
    thing.create();



    con.rollback(save1);



    Assert.assertEquals(0, gosu.db.GosuDB.count("CONTACTS"));

    gosu.db.GosuDB.releaseConnection();

  }

}