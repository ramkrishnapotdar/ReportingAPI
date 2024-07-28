package com.psl.prd;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import DBConnector.ApiDetails;
import DBConnector.DBConnector;
import DBConnector.DBDetails;
import DBConnector.ICABIDetails;
import DBConnector.InstanceState;
import DBConnector.Metadata;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import java.io.File;
import java.io.IOException;
import java.sql.*;

@Path("/ReadDataBaseStatus")
public class ReadData implements ContainerRequestFilter, ContainerResponseFilter{
	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");  
	public static Map<String,List<InstanceState> > DataCache = new HashMap<String, List<InstanceState> >();
	public static long CacheUpdated =0;
	
	@SuppressWarnings("finally")
	@GET
	@Path("/test")
	@Produces(MediaType.APPLICATION_JSON)
	public String testConn() {
		String Status = null;
		try {
			DBDetails Dbd = readDBConnDetails();
			
			Status = "[{\"Status\" : " + (DBConnector.checkConnection("H2", Dbd.URL, Dbd.Username,
					Dbd.Password) ? "Connected\"}]" : "Not Connected\"}]");
		} catch (Exception E) {
			Status = "{\"Error While Test Connection\":\"" + E.getMessage() + "\"}";
		} finally {
			return Status;
		}
	}
	
	@SuppressWarnings({ "finally" })
	private DBDetails readDBConnDetails() throws IOException {
		
		DBDetails Dbd = null;
		try {
					
			//Scanner SC = new Scanner(new File("D://databaseDetails.txt"));
			Scanner SC = new Scanner(new File("/opt/ReportingDB/databaseDetails.txt"));
			String URL=null, Username=null, Password=null;
			

				//if (SC.next()!=null)
				URL = SC.next();
				//if (SC.next()!=null)
				Username = SC.next();
				//if (SC.next()!=null)
				Password = SC.next();
				
				Dbd = new DBDetails(URL, Username, Password);
				try{SC.close();}catch(Exception e){}
		} catch (Exception E) {
			
			System.out.println("{\"Error while Reading DB Connection Details\":\"" + E.getMessage() + "\"}");
			
		}
		finally {
			return Dbd;
		}
		
	}
	
	
	@GET
	@Path("/dbStatus.json")
	@Produces(MediaType.APPLICATION_JSON)
	public List<InstanceState> readAllDatabases() throws Exception {
		
		if(inCache("DatabaseStateList"))
			return cacheHit("DatabaseStateList");
		
		List<InstanceState> DatabaseStateList = new ArrayList<InstanceState>();
		Connection connection = null;
		ResultSet recordset = null;
		String ED = "SELECT * FROM \"INSTANCES\".\"DATABASES\"";
		String PreInsert = "INSERT INTO \"INSTANCES\".\"DATABASES_TIMELINE\" (NAME,TSTAMP,STATUS_INT,STATUS,TSTAMP2) VALUES ('";
		String StatusCode[] = { "DOWN", "UP" };
		
		
		try {
			DBDetails Dbd  = readDBConnDetails();
			connection = DBConnector.getConnectionForH2(Dbd.URL, Dbd.Username, Dbd.Password);
			recordset = DBConnector.getData(connection, ED);
			boolean Status = false;
			String TSTAMP = dtf.format(LocalDateTime.now());
			String TSTAMP2 = new Long(System.currentTimeMillis()).toString();
			
			while (recordset.next()) {
				String NAME = recordset.getString("NAME");
				Status = DBConnector.checkConnection(
						recordset.getString("VENDOR"),
						recordset.getString("URL"),
						recordset.getString("USERNAME"),
						recordset.getString("PASSWORD"));
				int StatusInt = 0;
				String StatusString = null;
				if (Status) {
					StatusInt = 1;
					StatusString = StatusCode[1];
				} else {
					StatusInt = -1;
					StatusString = StatusCode[0];
				}
				// add code to insert values with status and time stamp
				String InsertQuery = PreInsert + NAME + "','" + TSTAMP + "',"
						+ StatusInt + ",'" + StatusString + "','" + TSTAMP2 + "')";

				connection.createStatement().execute(InsertQuery);
				DatabaseStateList.add(new InstanceState(NAME, TSTAMP, StatusString, StatusInt,TSTAMP2));
				
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			recordset.close();
			connection.close();
		}
		cacheAdd("DatabaseStateList", DatabaseStateList);
		return DatabaseStateList;
	}
	@GET
	@Path("/dbStatusCount.json")
	@Produces(MediaType.APPLICATION_JSON)
	public String dbStatusCount() throws Exception {
		return statusCount(readAllDatabases());
	}
	
	@GET
	@Path("/dbInlineStatusCount.json")
	@Produces(MediaType.APPLICATION_JSON)
	public String dbInlineStatusCount() throws Exception {
		return inlineStatusCount(readAllDatabases());
	}
	
	@GET
	@Path("/PRDStatus.json")
	@Produces(MediaType.APPLICATION_JSON)
	public List<InstanceState> readAllPRDInstances() throws Exception {
		
		if(inCache("PRDStateList"))
			return cacheHit("PRDStateList");
		
		List<InstanceState> PRDStateList = new ArrayList<InstanceState>();
		Connection connection = null;
		ResultSet recordset = null;
		String ED = "SELECT * FROM \"INSTANCES\".\"ICABI\"";
		String PreInsert = "INSERT INTO \"INSTANCES\".\"ICABI_TIMELINE\" (NAME,TSTAMP,STATUS_INT,STATUS,TSTAMP2) VALUES ('";
		String StatusCode[] = { "DOWN", "UP" };
		try {
			DBDetails Dbd  = readDBConnDetails();
			connection = DBConnector.getConnectionForH2(Dbd.URL, Dbd.Username, Dbd.Password);
			recordset = DBConnector.getData(connection, ED);
			boolean Status = false;
			String TSTAMP = dtf.format(LocalDateTime.now());
			String TSTAMP2 = new Long(System.currentTimeMillis()).toString();
			
			while (recordset.next()) {
				String NAME = recordset.getString("NAME");
				String IPADDRESS = recordset.getString("IPADDRESS");
				String HOSTNAME = recordset.getString("HOSTNAME");
				String StatusString = null;
				int StatusInt = 0;
				
				Status = ReadAPI.checkPRDConnection(new ICABIDetails(NAME,IPADDRESS,HOSTNAME));
				if (Status) {
					StatusInt = 1;
					StatusString = StatusCode[1];
				} else {
					StatusInt = -1;
					StatusString = StatusCode[0];
				}
				// add code to insert values with status and time stamp
				String InsertQuery = PreInsert + NAME + "','" + TSTAMP + "',"
						+ StatusInt + ",'" + StatusString + "','" + TSTAMP2 + "')";
				connection.createStatement().execute(InsertQuery);
				PRDStateList.add(new InstanceState(NAME, TSTAMP, StatusString, StatusInt,TSTAMP2));
				
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			recordset.close();
			connection.close();
		}
		cacheAdd("PRDStateList",PRDStateList);
		return PRDStateList;
	}
	@GET
	@Path("/prdStatusCount.json")
	@Produces(MediaType.APPLICATION_JSON)
	public String prdStatusCount() throws Exception {
		return statusCount(readAllPRDInstances());
	}
	
	@GET
	@Path("/prdInlineStatusCount.json")
	@Produces(MediaType.APPLICATION_JSON)
	public String prdInlineStatusCount() throws Exception {
		return inlineStatusCount(readAllPRDInstances());
	}
	
	@GET
	@Path("/APIStatus.json")
	@Produces(MediaType.APPLICATION_JSON)
	public List<InstanceState> readAPIInstances() throws Exception {
		
		if(inCache("APIStateList"))
			return cacheHit("APIStateList");
		
		List<InstanceState> APIStateList = new ArrayList<InstanceState>();
		Connection connection = null;
		ResultSet recordset = null;
		String ED = "SELECT * FROM \"INSTANCES\".\"API_AND_SERVICE\"";
		String PreInsert = "INSERT INTO \"INSTANCES\".\"API_AND_SERVICE_TIMELINE\" (NAME,TSTAMP,STATUS_INT,STATUS,TSTAMP2) VALUES ('";
		String StatusCode[] = { "DOWN", "UP" };
		try {
			DBDetails Dbd  = readDBConnDetails();
			connection = DBConnector.getConnectionForH2(Dbd.URL, Dbd.Username, Dbd.Password);
			recordset = DBConnector.getData(connection, ED);
			boolean Status = false;
			String TSTAMP = dtf.format(LocalDateTime.now());
			String TSTAMP2 = new Long(System.currentTimeMillis()).toString();
			
			while (recordset.next()) {
				String NAME = recordset.getString("NAME");
				String URL = recordset.getString("URL");
				String DESCRIPTION = recordset.getString("DESCRIPTION");
				String StatusString = null;
				int StatusInt = 0;
				
				Status = ReadAPI.checkApiConnection(new ApiDetails(NAME,URL,DESCRIPTION));
				if (Status) {
					StatusInt = 1;
					StatusString = StatusCode[1];
				} else {
					StatusInt = -1;
					StatusString = StatusCode[0];
				}
				// add code to insert values with status and time stamp
				String InsertQuery = PreInsert + NAME + "','" + TSTAMP + "',"
						+ StatusInt + ",'" + StatusString + "','" + TSTAMP2 + "')";
				connection.createStatement().execute(InsertQuery);
				APIStateList.add(new InstanceState(NAME, TSTAMP, StatusString, StatusInt,TSTAMP2));
				
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			recordset.close();
			connection.close();
		}
		cacheAdd("APIStateList",APIStateList);
		return APIStateList;
	}
	
	@GET
	@Path("/apiStatusCount.json")
	@Produces(MediaType.APPLICATION_JSON)
	public String apiStatusCount() throws Exception {
		return statusCount(readAPIInstances());
	}
	
	@GET
	@Path("/apiInlineStatusCount.json")
	@Produces(MediaType.APPLICATION_JSON)
	public String inlineApiStatusCount() throws Exception {
		return inlineStatusCount(readAPIInstances());
	}
	
	private String statusCount(List<InstanceState> eL) {
	int up=0,down=0;
	try {
		
		for(InstanceState I :  eL)
		{
			if(I.getSTATUS().equals("UP"))
				up++;
			else
				down++;
		}
		
	} catch (Exception e) {
		e.printStackTrace();
	} 
	return "[{\"STATUS\":\"DOWN\",\"VALUE\":"+down + "},{\"STATUS\":\"UP\",\"VALUE\":"+up+ "}]" ;
	}
	
	private String inlineStatusCount(List<InstanceState> eL) {
		int up=0,down=0;
		try {
			
			for(InstanceState I :  eL)
			{
				if(I.getSTATUS().equals("UP"))
					up++;
				else
					down++;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return "[{\"DOWN\":"+down + ",\"UP\":"+up+ "}]" ;
		}
	
	@GET
	@Path("/CacheStatus.json")
	@Produces(MediaType.APPLICATION_JSON)
	public String cacheStatus() {
		return "[{\"size\":\"" + DataCache.size() + "\", \"Updated\":\"" + CacheUpdated + "\"}]";
	}
	
	@GET
	@Path("/CacheClear.json")
	@Produces(MediaType.APPLICATION_JSON)
	public String cacheClear() {
		DataCache.clear();
		return "[{\"ClearedAt\":\"" + System.currentTimeMillis() + "\"}]";
	}
	
	private void cacheAdd(String eD,List<InstanceState> eL) {
		// TODO Auto-generated method stub
		
			DataCache.put(eD, eL);
			
			CacheUpdated = System.currentTimeMillis();
		
	}

	private List<InstanceState>  cacheHit(String eD) {
		// TODO Auto-generated method stub
		return DataCache.get(eD);
	}

	private boolean inCache(String eD) {
		// TODO Auto-generated method stub
		if (System.currentTimeMillis()-CacheUpdated >= 300000)
			DataCache.clear();
		if(DataCache.containsKey(eD))
			return true;
		return false;
	}
	
	@GET
	@Path("/metadata")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Metadata>  getMetadataByinstance(@Context UriInfo info) {
		String Type = info.getQueryParameters().getFirst("type");
		String Name = info.getQueryParameters().getFirst("name");
		Type.replace("%20", " ");
		Name.replace("%20", " ");		
		System.out.println("Type: "+Type+" name: "+Name);
		List<Metadata> MetadataList = new ArrayList<Metadata>();
		String Sql;
		switch(Type)
		{
		case "Database":
		Sql = "SELECT * FROM \"INSTANCES\".\"DATABASES\" WHERE NAME = '" + Name + "'";	
		System.out.println("SQL "+Sql );
		break;
		
		case "PRDInstance":
			Sql = "SELECT * FROM \"INSTANCES\".\"ICABI\" WHERE NAME = '" + Name + "'";	
			break;
			
		case "WebsiteOrAPI":
			Sql = "SELECT * FROM \"INSTANCES\".\"API_AND_SERVICE\" WHERE NAME = '" + Name + "'";	
			break;
		default:
			Sql = "";
			break;
			
		}
		//System.out.println("SQL :" +Sql);
		//read record and convert to grid json format
		Connection connection = null;
		try {
		DBDetails Dbd  = readDBConnDetails();
		connection = DBConnector.getConnectionForH2(Dbd.URL, Dbd.Username, Dbd.Password);
		ResultSet RS = DBConnector.getData(connection, Sql);
		ResultSetMetaData rsMetaData = RS.getMetaData();
		ArrayList<String> Fields = new ArrayList<String>();
		//Retrieving the list of column names
	      int count = rsMetaData.getColumnCount();
	      
	      for(int i = 1; i<=count; i++) {
	         Fields.add(rsMetaData.getColumnName(i));
	      }
		
		
		
		while (RS.next())
		{
			for (String field : Fields)
			{
				MetadataList.add(new Metadata(field,RS.getString(field)));
			}
		}
				
		}
		catch(Exception e)
		{
			
		}
		finally{
			try {
				connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return MetadataList;
	}	

	
	@Override
    public void filter(ContainerRequestContext request,
            ContainerResponseContext response) throws IOException {
        response.getHeaders().add("Access-Control-Allow-Origin", "*");
        response.getHeaders().add("Access-Control-Allow-Headers",
                "CSRF-Token, X-Requested-By, Authorization, Content-Type");
        response.getHeaders().add("Access-Control-Allow-Credentials", "true");
        response.getHeaders().add("Access-Control-Allow-Methods",
                "GET, POST, PUT, DELETE, OPTIONS, HEAD");
    }

	@Override
	public void filter(ContainerRequestContext request) throws IOException {
		// TODO Auto-generated method stub
		
	}
}