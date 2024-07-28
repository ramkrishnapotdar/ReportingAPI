package DBConnector;

import javax.xml.bind.annotation.XmlRootElement;

import com.google.gson.annotations.SerializedName;
import com.owlike.genson.annotation.JsonProperty;

@XmlRootElement
public class Metadata {
	@SerializedName("data")
	String Data;
	@SerializedName("field")
	String Field;
	
	public Metadata(String field, String data) {
		Data = data;
		Field = field;
	}
	@JsonProperty("Field")
	public String getField() {
		return Field;
	}
	public void setField(String field) {
		Field = field;
	}
	@JsonProperty("Data")
	public String getData() {
		return Data;
	}
	public void setData(String data) {
		Data = data;
	}
	

}
