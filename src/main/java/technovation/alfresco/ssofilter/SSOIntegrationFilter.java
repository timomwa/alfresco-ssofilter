package technovation.alfresco.ssofilter;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import technovation.alfresco.httpclient.GenericHTTPClient;
import technovation.alfresco.httpclient.GenericHTTPParam;
import technovation.alfresco.httpclient.GenericHttpResp;

public class SSOIntegrationFilter implements Filter {

	private static final String PARAM_REMOTE_USER = "user";
	public static final String SESS_PARAM_REMOTE_USER = SSOIntegrationFilter.class.getName() + '.' + PARAM_REMOTE_USER;

	private ServletContext servletContext;
	private static Logger logger = Logger.getLogger(SSOIntegrationFilter.class);

	//@Override
	public void destroy() {
		logger.info(" \n\n\n\t\t ......in destroy().....\n\n\n");
	}
	
	//@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		BasicConfigurator.configure();
		 this.servletContext = filterConfig.getServletContext();

	}

	//@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) req; 
		HttpServletResponse httpServletResponse = (HttpServletResponse) res; 
		
		String remoteUser = proprieterayUserIdValidationAndExtractionMethod(httpServletRequest, httpServletResponse, req.getParameter(PARAM_REMOTE_USER)); 
        
        if (remoteUser != null && !remoteUser.isEmpty()) { 
            HttpSession session = httpServletRequest.getSession(); 
            session.setAttribute(SESS_PARAM_REMOTE_USER, remoteUser); 
        }
        String pathInfo = httpServletRequest.getPathInfo();
        if(pathInfo!=null && !pathInfo.isEmpty() && pathInfo.contains("logout")){
        	
        	System.out.println("^^ pathInfo ["+pathInfo+"]");
            HttpSession session = httpServletRequest.getSession(); 
            session.removeAttribute(SESS_PARAM_REMOTE_USER);
            session.setAttribute(SESS_PARAM_REMOTE_USER, null); 
            session.setMaxInactiveInterval(1);
            session.invalidate();
            return;
            
            
        }
        
        
       HeaderMapRequestWrapper wrapper = new HeaderMapRequestWrapper(httpServletRequest);
        
	   chain.doFilter(wrapper, res); 
        
	}

	private String proprieterayUserIdValidationAndExtractionMethod(HttpServletRequest req, HttpServletResponse resp, String remoteUser) {
		
		try {
			
			if(remoteUser==null || remoteUser.isEmpty())
				return null;
			
			final GenericHTTPClient httpclient = new GenericHTTPClient("http");
			
			try{
			GenericHTTPParam params = new GenericHTTPParam();
			Map<String,String> headers = new HashMap<String,String>();
			String auth = "admin" + ":" + "";
			String basicauthhh = "dGltb213YUBnbWFpbC5jb206QWRtaW4xMjMjQCE=";
			headers.put("Authorization","Basic "+basicauthhh);//dGltb213YUBnbWFpbC5jb206QWRtaW4xMjMjQCE=");
			params.setHeaderParams(headers);
			params.setHttpmethod("GET");
			params.setUrl("http://localhost:8080/eacimscore/seam/resource/restv1/sso/"+remoteUser);
			
			
			GenericHttpResp response = httpclient.call(params);
			
			System.out.println("\n\n remoteUser["+remoteUser+"] encodedAuth = ["+basicauthhh+"]  RESP CODE:: "+response.getResp_code()
			         +"\n\t\t RESP BODY:: "+response.getBody());
			
			if(response.getResp_code()==200 || response.getResp_code()==204)
				return response.getBody();
			return null;
			}catch(Exception e){
				throw e;
			}finally{
				try{
					if(httpclient!=null)
						httpclient.finalizeMe();
				}catch(Exception e){}
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}finally{
			
		}
		
		
		return null;
	}

	

}
