package hoang.db;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;


public class MongoDatabaseConnection
{
	public static final String	DEFAULT_MONGO_HOST	= "localhost";
	public static final int	   DEFAULT_MONGO_PORT	= 27017;
	public static final String	DEFAULT_DATABASE	= "music";

	private MongoDatabase	   mongoDB;

	private MongoClient	       client;

	public MongoDatabaseConnection(String _host, int _port, String _database)
	{
		client = new MongoClient(_host, _port);
		mongoDB = client.getDatabase(_database);
	}

	public MongoDatabase getDbConn()
	{
		return mongoDB;
	}

	public String getAllDocumentsInCollection()
	{
		String returnJSONString = "";

		MongoCollection<Document> musicCollection = mongoDB
		        .getCollection("db_album_metadata");

		MongoCursor<Document> cursor = musicCollection.find().iterator();

		try
		{
			while (cursor.hasNext())
			{
				String document = cursor.next().toJson();
				returnJSONString = "\n" + document;
			}
		} catch (Exception e)
		{

		} finally
		{
			cursor.close();

			client.close();
		}

		return returnJSONString;
	}
	
	public InputStream getFirstFileFromMongoDB()
	{
		return getFileFromMongoDB("5580899e8c95fd101838af9d");
	}

	public InputStream getFileFromMongoDB(String _objectID)
	{
		GridFS fs = new GridFS(client.getDB("music"), "db_files");
		GridFSDBFile outputFile = fs.findOne(new ObjectId(_objectID));
		return outputFile.getInputStream();
	}

	public void putFileInMongoDB(String _fullPath)
	{
		@SuppressWarnings("deprecation")
		// replace with new GridFS-API when mongodb-driver 3.1 is available!
		GridFS fs = new GridFS(client.getDB("music"), "db_files");

		File file = new File(_fullPath);
		GridFSInputFile inputFile = null;
		try
		{
			inputFile = fs.createFile(file);
			inputFile.setFilename(file.getName());
			inputFile.save();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args)
	{
		MongoDatabaseConnection conn = new MongoDatabaseConnection(
		        DEFAULT_MONGO_HOST, DEFAULT_MONGO_PORT, "music");

		switch (args[0])
		{
		case "-o":
			System.out.println(conn.getAllDocumentsInCollection());
			break;
		case "-i":
			if (args.length != 1)
			{
				conn.putFileInMongoDB(args[1]);
			} else
			{
				System.out
				        .println("No input file found");
			}
			break;
		default:
			System.out.println(conn.getAllDocumentsInCollection());
			break;
		}
	}
}
