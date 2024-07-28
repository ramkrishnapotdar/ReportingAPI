package com.psl.prd;

import DBConnector.ApiDetails;
import DBConnector.ICABIDetails;
import java.io.IOException;
import java.net.*;

public class ReadAPI
{
  public static boolean checkPRDConnection(ICABIDetails ICABI_Instance) throws IOException {
	  HttpURLConnection conn = null;
	  try {
		  	
		  //String IPADDRESS = "10.46.43.252";
			URL url = new URL("https://"+ ICABI_Instance.IPADDRESS + "/dashboards");
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(2000);
			conn.connect();
	        System.out.println(ICABI_Instance.HOSTNAME + " Connected!");
	        conn.disconnect();
	        return true;
	       }
	  catch(javax.net.ssl.SSLHandshakeException sslEx)
		{
			return true;
		}
	  catch(java.net.SocketTimeoutException socketTimeOut)
		{
			return false;
		}
	  catch(java.net.ConnectException e)
	  {
		  return false;
	  }
	  catch(Exception e)
	  {
		  return false;
	  }
  }
  
  public static boolean checkApiConnection(ApiDetails API_Instance) throws IOException {
	  HttpURLConnection conn = null;
	  try {
		  	
		  //String IPADDRESS = "10.46.43.252";
			URL url = new URL(API_Instance.URL);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(2000);
			conn.connect();
	        System.out.println(API_Instance.NAME+ " Connected!");
	        conn.disconnect();
	        return true;
	       }
	  catch(javax.net.ssl.SSLHandshakeException sslEx)
		{
			return true;
		}
	  catch(java.net.SocketTimeoutException socketTimeOut)
		{
			return false;
		}
	  catch(java.net.ConnectException e)
	  {
		  return false;
	  }
	  catch(Exception e)
	  {
		  return false;
	  }
  }
  
}