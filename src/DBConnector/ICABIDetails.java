package DBConnector;

public class ICABIDetails {
	public String NAME,IPADDRESS,HOSTNAME;

	public ICABIDetails()
	{
		NAME=IPADDRESS=HOSTNAME=null;
	}

	public ICABIDetails(String nAME, String iPADDRESS, String hOSTNAME) {
		super();
		NAME = nAME;
		IPADDRESS = iPADDRESS;
		HOSTNAME = hOSTNAME;
	}
	
	

}