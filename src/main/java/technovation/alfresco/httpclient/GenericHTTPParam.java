package technovation.alfresco.httpclient;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.HttpMethod;

import org.apache.http.NameValuePair;

/**
 * 
 * @author Timothy
 * Holds generic http params.
 * TODO add header.
 *
 */
public class GenericHTTPParam implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8834391423692237679L;
	private String url;
	private Long id;
	
	private List<NameValuePair> httpParams = new ArrayList<NameValuePair>();
	private Map<String,String> headerParams;
	private String stringentity;
	private String httpmethod;
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public List<NameValuePair> getHttpParams() {
		return httpParams;
	}
	public void setHttpParams(List<NameValuePair> httpParams) {
		this.httpParams = httpParams;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Map<String, String> getHeaderParams() {
		return headerParams;
	}
	public void setHeaderParams(Map<String, String> headerParams) {
		this.headerParams = headerParams;
	}
	public String getStringentity() {
		return stringentity;
	}
	public void setStringentity(String stringentity) {
		this.stringentity = stringentity;
	}
	public String getHttpmethod() {
		return httpmethod==null ? HttpMethod.POST : httpmethod;
	}
	public void setHttpmethod(String httpmethod) {
		this.httpmethod = httpmethod;
	}
	@Override
	public String toString() {
		return "GenericHTTPParam [url=" + url + ",\n id=" + id + ",\n httpParams="
				+ httpParams + ",\n headerParams=" + headerParams
				+ ",\n stringentity=" + stringentity + ",\n httpmethod="
				+ httpmethod + "]";
	}
	
	
	
	
	
}
