package DBConnector;

public class DBDetails {
	public String URL,Username,Password;

	public DBDetails()
	{
		URL=Username=Password=null;
	}
	public DBDetails(String uRL, String username, String password) {
		super();
		URL = uRL;
		Username = username;
		Password = password;
	}
	

}
