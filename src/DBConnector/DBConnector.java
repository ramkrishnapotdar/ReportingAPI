package DBConnector;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
public class DBConnector {
    
    
	public static Connection getConnectionForDerby(String URL, String Username, String Password) {
        Connection connection = null;
        
        try {
            Class.forName("org.apache.derby.jdbc.ClientDriver");
            connection = DriverManager.getConnection(URL, Username, Password);
            }
         catch (Exception e) {
            return null;
        }
        finally{
        	//try{connection.close();} catch(Exception e) {}
        }
        return connection;
        }
    
	public static boolean checkConnection(String Vendor, String URL, String Username, String Password) {
        Connection connection = null;
        boolean status;
        try {
        	switch (Vendor)
        	{
        	case "Derby":
        		connection = DBConnector.getConnectionForDerby(URL, Username, Password);
				break;
			case "H2":
				connection = DBConnector.getConnectionForH2(URL, Username, Password);
				break;
			case "Postgres":
				connection = DBConnector.getConnectionForPostgres(URL, Username, Password);
				break;
			case "DB2":
				connection = DBConnector.getConnectionForDB2(URL, Username, Password);
				break;
			case "MySQL":
				connection = DBConnector.getConnectionForMySql(URL, Username, Password);
				break;
			default:
				return false;
        	}
            
           }
         catch (Exception e) {
            return false;
        }
        finally{
        	status = connection==null? false : true;
        	try{connection.close();} catch(Exception e) {}
        }
        return status;
        }
   	
	@SuppressWarnings("finally")
	public static Connection getConnectionForH2(String URL, String Username, String Password) {
        Connection connection = null;
        try {
        	Class.forName("org.h2.Driver");
            connection = DriverManager.getConnection(URL, Username, Password);
           }
         catch (Exception e) {
            e.printStackTrace();
        }
        finally{
        	return connection;
        }
	}
	
	@SuppressWarnings("finally")
	public static Connection getConnectionForMySql(String URL, String Username, String Password) {
        Connection connection = null;
        try {
        	Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(URL, Username, Password);
           }
         catch (Exception e) {
            e.printStackTrace();
        }
        finally{
        	return connection;
        }
	}
	
	@SuppressWarnings("finally")
	public static Connection getConnectionForDB2(String URL, String Username, String Password) {
        Connection connection = null;
        try {
        	Class.forName("com.ibm.db2.jcc.DB2Driver");
            connection = DriverManager.getConnection(URL, Username, Password);
           }
         catch (Exception e) {
            e.printStackTrace();
        }
        finally{
        	return connection;
        }
	}
	public static Connection getConnectionForPostgres(String URL, String Username, String Password) {
        Connection connection = null;
        
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(URL, Username, Password);
            }
         catch (Exception e) {
            return null;
        }
        finally{
        	//try{connection.close();} catch(Exception e) {}
        }
        return connection;
        }
	public static ResultSet getData(Connection conn,String selectQuery)
	{
		ResultSet recordset = null;
		try
		{
			recordset = conn.createStatement().executeQuery(selectQuery);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return recordset;
	}
}
    


