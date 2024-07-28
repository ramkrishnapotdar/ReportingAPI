package com.psl.prd.reporting;



import java.io.File;
import java.io.FileNotFoundException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

import com.psl.prd.ReadData;

import DBConnector.DBConnector;

public class ReadResources {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		try{
			//testDBConn();
			//testAPIConn();
			Connection h2con = DBConnector.getConnectionForH2("jdbc:h2:tcp:10.53.18.60:9008/tnpmoed", "sadmin", "spass");
			Connection pgcon = DBConnector.getConnectionForPostgres("jdbc:postgresql://10.53.20.172:5432/prddb", "prddba", "@dm1n4prdDB");
			ResultSet rs =  DBConnector.getData(h2con, "select * from \"INSTANCES\".\"ICABI_TIMELINE\"");
			//PreparedStatement ps = h2con.prepareStatement("update \"INSTANCES\".\"DATABASES_TIMELINE\" set)
			while (rs.next())
			{
				System.out.println("epoch " + rs.getString("TSTAMP2") );
				ResultSet pgdate = DBConnector.getData(pgcon, "SELECT to_timestamp(" + rs.getString("TSTAMP2") + "/1000) from dual");
				while (pgdate.next())
				{
					String pgstring = pgdate.getString(1).substring(0, pgdate.getString(1).length()-7)+".0";
					String update = "update \"INSTANCES\".\"ICABI_TIMELINE\" set tstamp = '" +
									pgstring + "' where tstamp2 = " + rs.getString("TSTAMP2");
					PreparedStatement ps = h2con.prepareStatement(update);
					ps.execute();
					System.out.println(pgstring);
				}
			}
			h2con.close();
			pgcon.close();
		}
		
		catch(Exception E)
		{
			E.printStackTrace();
			System.out.println("{\"Error\":\""+E.getMessage()+"\"}");
		}
		finally{
			
		}

	}
	private static void testAPIConn() throws Exception {
		//String IPADDRESS = "10.46.43.253";
		String IPADDRESS = "10.46.43.252";
		URL url = new URL("https://"+ IPADDRESS + "/dashboards");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        //conn.setRequestMethod("HEAD");
       conn.setRequestMethod("GET");
       conn.setConnectTimeout(5000);
       conn.connect();
        System.out.println(IPADDRESS + " Connected!");
        conn.disconnect();
		
	}
	private static void testDBConn() throws FileNotFoundException
	{
		@SuppressWarnings("resource")
		Scanner SC = new Scanner(new File("D://databaseDetails.txt"));
		String URL=null, Username=null, Password=null;
		URL = SC.next();
		Username = SC.next();
		Password = SC.next();
		
		System.out.println("URL : "+URL);
		System.out.println("User : "+Username);
		System.out.println("Pass : "+Password);
		System.out.println(DBConnector.checkConnection("H2", URL, Username, Password)?"Connected":"Not Connected");
		
		SC.close();
	}

}
