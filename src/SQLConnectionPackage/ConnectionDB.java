package SQLConnectionPackage;

import java.sql.*;

public class ConnectionDB {
    private static Connection connection;
    private static Statement statement;
    private static DatabaseMetaData dbmeta;
    private static ResultSet resultSet;

    public static ResultSet getResultSet() {
        return resultSet;
    }
    public static void getConnection(String server, String username, String password) throws SQLException {
        connection = DriverManager.getConnection(server,username,password);
        statement = connection.createStatement();
        dbmeta = connection.getMetaData();
    }
    public static ResultSet getDatabases() throws SQLException {
        statement.execute("SHOW DATABASES");
        return statement.getResultSet();
    }
    public static ResultSet getTables(String db) throws SQLException {
        return dbmeta.getTables(db, null, null, new String[] {"TABLE"});
    }
    public static ResultSet getResultSetfromInput(String db,String buff) throws SQLException {
        connection.setCatalog(db);
        statement = connection.createStatement();
        if(buff.contains("SELECT")&&buff.contains("FROM")&&buff.contains("WHERE")){
            return statement.executeQuery(buff);
        } else if ((buff.contains("INSERT")||buff.contains("UPDATE")||buff.contains("DELETE"))&&buff.contains("FROM")&&buff.contains("WHERE")){
            statement.execute(buff);
            return statement.getResultSet();
        } else {
            ResultSet resultSet = null;
            return resultSet;
        }
    }
    public static void closeConnection() throws SQLException {
        connection.close();
    }
}
