package technovation.alfresco.httpclient;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;



public class GenericHttpResp implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1543853773676611632L;
	
	private int resp_code = 0;
	
	private String body;
	
	private String contenttype;
	
	private Map<String,String> responseHeaders = new HashMap<String,String>();

	public int getResp_code() {
		return resp_code;
	}

	public void setResp_code(int resp_code) {
		this.resp_code = resp_code;
	}
	
	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getContenttype() {
		return contenttype;
	}

	public void setContenttype(String contenttype) {
		this.contenttype = contenttype;
	}

	public Map<String, String> getResponseHeaders() {
		return responseHeaders;
	}

	public void setResponseHeaders(Map<String, String> responseHeaders) {
		this.responseHeaders = responseHeaders;
	}
	
	

}
