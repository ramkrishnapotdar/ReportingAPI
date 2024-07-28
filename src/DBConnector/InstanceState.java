package DBConnector;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.gson.annotations.SerializedName;
import com.owlike.genson.annotation.JsonProperty;

@XmlRootElement
public class InstanceState {
	@SerializedName("name")
	public String NAME;
	@SerializedName("tstamp")
	public String TSTAMP;
	@SerializedName("tstamp2")
	public String TSTAMP2;
	@SerializedName("status")
	public String STATUS;
	@SerializedName("status_default")
	static long STATUS_DEFAULT = 0;
		
	@SerializedName("status_int")
	long STATUS_INT;
	public InstanceState(String nAME, String tSTAMP, String sTATUS,
			long sTATUS_INT,String tSTAMP2) {
		super();
		NAME = nAME;
		TSTAMP = tSTAMP;
		STATUS = sTATUS;
		STATUS_INT = sTATUS_INT;
		TSTAMP2 = tSTAMP2;
	}
	@JsonProperty("NAME")
	public String getNAME() {
		return NAME;
	}
	public void setNAME(String nAME) {
		NAME = nAME;
	}
	@JsonProperty("TSTAMP")
	public String getTSTAMP() {
		return TSTAMP;
	}
	public void setTSTAMP(String tSTAMP) {
		TSTAMP = tSTAMP;
	}
	@JsonProperty("TSTAMP2")
	public String getTSTAMP2() {
		return TSTAMP2;
	}
	public void setTSTAMP2(String tSTAMP2) {
		TSTAMP2 = tSTAMP2;
	}
	@JsonProperty("STATUS")
	public String getSTATUS() {
		return STATUS;
	}
	public void setSTATUS(String sTATUS) {
		STATUS = sTATUS;
	}
	@JsonProperty("STATUS_INT")
	public long getSTATUS_INT() {
		return STATUS_INT;
	}
	public void setSTATUS_INT(long sTATUS_INT) {
		STATUS_INT = sTATUS_INT;
	}
	
	@JsonProperty("STATUS_DEFAULT")
	public long getSTATUS_DEFAULT() {
		return STATUS_DEFAULT;
	}
	public void setSTATUS_DEFAULT(long sTATUS_DEFAULT) {
		STATUS_DEFAULT = sTATUS_DEFAULT;
	}

}
