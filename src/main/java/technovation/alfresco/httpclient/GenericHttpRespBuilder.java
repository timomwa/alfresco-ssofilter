package technovation.alfresco.httpclient;

import java.util.Map;

public class GenericHttpRespBuilder {
	
	private static GenericHttpResp instance = new GenericHttpResp();
	
	public  GenericHttpRespBuilder respCode(int respCode_){
		instance.setResp_code(respCode_);
		return this;
	}
	public  GenericHttpRespBuilder body(String body_){
		instance.setBody(body_);
		return this;
	}
	
	public  GenericHttpRespBuilder contenttype(String contenttype){
		instance.setContenttype(contenttype);
		return this;
	}
	
	public  GenericHttpRespBuilder responseHeaders(Map<String, String> responseHeaders){
		instance.setResponseHeaders(responseHeaders);
		return this;
	}
	
	public  GenericHttpRespBuilder responseHeader(String key, String value){
		instance.getResponseHeaders().put(key, value);
		return this;
	}
	
	public GenericHttpResp build(){
		assert instance.getResp_code() >-1;
		return instance;
	}
	public static GenericHttpRespBuilder create() {
		return new GenericHttpRespBuilder();
	}

}
