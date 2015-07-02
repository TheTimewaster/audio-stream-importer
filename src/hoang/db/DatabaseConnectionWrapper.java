package hoang.db;


public class DatabaseConnectionWrapper 
{
	private static DatabaseConnectionWrapper INSTANCE;
	
	private DatabaseConnectionWrapper()
	{
		//empty constructor
	}
	
	public static DatabaseConnectionWrapper getInstance()
	{
		if(INSTANCE == null)
		{
			INSTANCE = new DatabaseConnectionWrapper();
		}
		
		return INSTANCE;
	}
	
	public MongoDatabaseConnection getMongoDatabaseConnection(String _host, int port, String _database)
	{
		return new MongoDatabaseConnection(_host, port, _database);
	}

}
