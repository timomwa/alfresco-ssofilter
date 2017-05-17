package technovation.alfresco.httpclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

public class GenericHTTPClient implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6923599491151170899L;
	private Logger logger = Logger.getLogger(getClass());
	private HttpClient httpclient = null;
	private String name;
	private boolean run = true;
	private volatile boolean success = true;
	private boolean finished = false;
	private boolean busy = false;
	private String protocol;
	

	@SuppressWarnings("unused")
	private GenericHTTPClient(){}
	
	public GenericHTTPClient(String proto) throws KeyManagementException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException{
		this.protocol = proto;
		initHttpClient();
		init();
	}
	
	/**
	 * When we've been throttled
	 * we release all resources, close
	 * the connection and client
	 */
	public void releaseConnection(){
		finalizeMe();
	}
	
	
	public void initHttpClient() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
		
		if(protocol.trim().equalsIgnoreCase("http")){
			
			httpclient = new DefaultHttpClient();//HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).setConnectionManager(cm).build();
		
		}else{
			
			throw new RuntimeException("");
		}
		
	}
	

	public GenericHTTPClient(HttpClient httpclient_){
		this.httpclient = httpclient_;
		init();
	}
	
	

	private void init(){
	}
	
	
	/**
	 * Sends the MT message
	 * @param mt - com.pixelandtag.MTsms
	 */
	public GenericHttpResp call(GenericHTTPParam genericparams){
		this.success = true;
		GenericHttpRespBuilder respBuilder = GenericHttpRespBuilder.create();
		setBusy(true);
		HttpRequestBase httpmethod = null;
		HttpResponse response = null;
		
		try {
			
			httpmethod = HttpMethodFactory.getMethod(genericparams);
			
			Map<String,String> headerparams = genericparams.getHeaderParams();
			
			if(headerparams!=null && headerparams.size()>0)
				for(String key : headerparams.keySet()){
					System.out.println("\n\t\t     "+key+" : "+headerparams.get(key));
					httpmethod.setHeader(key, headerparams.get(key));
				}
			
			if(genericparams.getStringentity()==null || genericparams.getStringentity().isEmpty()){
				HttpParams params = new BasicHttpParams();
				URI uri = httpmethod.getURI();
				String url = uri.toURL().toString().concat(  (  genericparams.getHttpParams().size()>0 ? "?" : "")  );
				for(NameValuePair nvp : genericparams.getHttpParams()){
					url = url.concat( (url.endsWith("?") ? "" : "&") ).concat(  URLEncoder.encode( nvp.getName(), "UTF-8")  ).concat("=").concat(  URLEncoder.encode( nvp.getValue() , "UTF-8") );
				}
				URL urle = new URL(url);
				uri = urle.toURI();
				httpmethod.setURI(uri);
				
				httpmethod.setParams(params);
				response = httpclient.execute(httpmethod);
			}else if(genericparams.getStringentity()!=null){
				
				URI uri = httpmethod.getURI();
				String url = uri.toURL().toString().concat(  (  genericparams.getHttpParams().size()>0 ? "?" : "")  );
				if(genericparams.getHttpParams()!=null && genericparams.getHttpParams().size()>0)
					for(NameValuePair nvp : genericparams.getHttpParams()){
						url = url.concat( (url.endsWith("?") ? "" : "&") ).concat(  URLEncoder.encode( nvp.getName(), "UTF-8")  ).concat("=").concat(  URLEncoder.encode( nvp.getValue() , "UTF-8") );
					}
				
				URL urle = new URL(url);
				uri = urle.toURI();
				httpmethod.setURI(uri);
				
				StringEntity se = new StringEntity(genericparams.getStringentity());
				HttpEntityEnclosingRequestBase method = (HttpEntityEnclosingRequestBase) httpmethod;
				method.setEntity(se);
				response = httpclient.execute(method);
				httpmethod = (HttpRequestBase) method;
			}
			
			try{
				//String link = genericparams.getUrl();
				Map<String,String> responseHeaders = new HashMap<String,String>();
				Header[] headers = response.getAllHeaders();
				if(headers!=null)
					for(int i=0;i<headers.length; i++){
						Header header = headers[i];
						responseHeaders.put(header.getName(), header.getValue());
						if(header.getName().toLowerCase().contains("content")){
							respBuilder.contenttype(header.getValue());
						}
					}
				
				respBuilder = respBuilder.responseHeaders(responseHeaders);
				
			}catch(Exception exp){
				logger.error(exp.getMessage()+genericparams.getUrl(), exp);
			}
			
			int resp_code = response.getStatusLine().getStatusCode();
			respBuilder = respBuilder.respCode(resp_code);
			
			setBusy(false);
			
			String respose_msg  = convertStreamToString(response.getEntity().getContent());
			respBuilder = respBuilder.body(respose_msg);
			logger.debug(getName()+" PROXY ::::::: finished attempt to deliver SMS via HTTP :::: RESP::: "+respose_msg);
			try {
				EntityUtils.getContentCharSet(response.getEntity());
				//EntityUtils.consume(response.getEntity());
			
			} catch (Exception e) {
				
				logger.error(e);
			
			}
		}catch(java.lang.IllegalStateException ise){
			try {
				initHttpClient();
			} catch (KeyManagementException e) {
				logger.error(e.getMessage(),e);
			} catch (NoSuchAlgorithmException e) {
				logger.error(e.getMessage(),e);
			} catch (KeyStoreException e) {
				logger.error(e.getMessage(),e);
			}
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage(),e);
			httpmethod.abort();
			this.success = false;
		} catch (ClientProtocolException e) {
			logger.error(e.getMessage(),e);
			httpmethod.abort();
			this.success = false;
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
			httpmethod.abort();
			this.success = false;
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			httpmethod.abort();
			this.success = false;
		}finally{
			setBusy(false);
			try {
				if(response!=null && response.getEntity()!=null)
					response.getEntity().consumeContent();
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
			}
		}
		return respBuilder.build();
	}
	
	
	private synchronized void setBusy(boolean busy) {
		this.busy = busy;
		notify();
	}
	
	public void printHeader(HttpPost httppost) {
		logger.debug("\n===================HEADER=========================\n");
		try{
			
			for(org.apache.http.Header h : httppost.getAllHeaders()){
				if(h!=null){
					logger.debug("name: "+h.getName());
					logger.debug("value: "+h.getValue());
					for(org.apache.http.HeaderElement hl : h.getElements()){
						if(hl!=null){
							logger.debug("\tname: "+hl.getName());
							logger.debug("\tvalue: "+hl.getValue());
							if(hl.getParameters()!=null)
								for(NameValuePair nvp : hl.getParameters()){
									if(nvp!=null){
										logger.debug("\t\tname: "+nvp.getName());
										logger.debug("\t\tvalue: "+nvp.getValue());
									}
								}
						}
					}
				}
			}
		}catch(Exception e){
		
		logger.warn(e.getMessage(),e);
	
		}
	
		logger.debug("\n===================HEADER END======================\n");
	}

	public boolean isRunning() {
		return run;
	}
	
	public void setRun(boolean run) {
		this.run = run;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void logAllParams(List<NameValuePair> params) {
		
		for(NameValuePair np: params){
			
			if(np.getName().equals("SMS_MsgTxt"))
				logger.debug(np.getName()+ "=" + np.getValue()+" Length="+np.getValue().length());
			else
				logger.debug(np.getName() + "=" + np.getValue());
			
		}
		
	}

	public synchronized void rezume(){
		this.notify();
	}
	
	public synchronized void pauze(){
		try {
			
			this.wait();
		
		} catch (InterruptedException e) {
			
			logger.debug(getName()+" we now run!");
		
		}
	}
	
	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public boolean isBusy() {
		return busy;
	}
	
	public boolean isFinished() {
		return finished;
	}


	
	
	
	/**
	 * Utility method for converting Stream To String
	 * To convert the InputStream to String we use the
	 * BufferedReader.readLine() method. We iterate until the BufferedReader
	 * return null which means there's no more data to read. Each line will
	 * appended to a StringBuilder and returned as String.
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 */
	private String convertStreamToString(InputStream is)
			throws IOException {
		
		StringBuilder sb = null;
		BufferedReader reader = null;
		
		if (is != null) {
			sb = new StringBuilder();
			String line;

			try {
				reader = new BufferedReader(
						new InputStreamReader(is, "UTF-8"));
				while ((line = reader.readLine()) != null) {
					sb.append(line).append("\n");
				}
			} finally {
				is.close();
			}
			return sb.toString();
		} else {
			return "";
		}
	}
	
	/**
	 * Releases resources, never throws
	 * an exception
	 */
	public void finalizeMe(){
		try{
			/*if(httpclient!=null)
				httpclient.close();*/
			httpclient = null;
		}catch(Exception e){logger.error(e.getMessage(), e);}
		try{
			//if(cm!=null)
			//	cm.close();
		}catch(Exception e){logger.error(e.getMessage(), e);}
		try{
			//if(cm!=null)
			//	cm.shutdown();
			//cm = null;
		}catch(Exception e){logger.error(e.getMessage(), e);}
	}
	
}
