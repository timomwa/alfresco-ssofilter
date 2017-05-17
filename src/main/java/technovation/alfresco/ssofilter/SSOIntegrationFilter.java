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
		
		 String url = httpServletRequest.getRequestURL().toString();
		 String queryString = httpServletRequest.getQueryString();
		 //System.out.println(" \n\n\n\n\t  url-->"+url+"\n\t   queryString-->"+queryString+"\n\n");
		 
        String remoteUser = proprieterayUserIdValidationAndExtractionMethod(httpServletRequest, httpServletResponse, req.getParameter(PARAM_REMOTE_USER)); 
       // if(remoteUser!=null && !remoteUser.isEmpty())
       // 	System.out.println(" \n\n\n\n\t  remoteUser-->"+remoteUser+"\n\n");
        if(remoteUser==null || remoteUser.isEmpty()){
        	//remoteUser = httpServletRequest.getHeader("X-Alfresco-Remote-User");
        	Cookie[] cookies = httpServletRequest.getCookies();

        	if (cookies != null) {
        	 for (Cookie cookie : cookies) {
        		 //System.out.println("----> cookie.getName(): "+cookie.getName() );
        	   if (cookie.getName().equalsIgnoreCase("X-Alfresco-Remote-User")) {
        		   remoteUser = cookie.getValue();
        		   //httpServletResponse.setHeader("x-alfresco-remote-user", remoteUser);
        		   //servletContext.setAttribute("x-alfresco-remote-user", remoteUser);
        		   break;
        	    }
        	  }
        	}
        }
        
        if(remoteUser==null || remoteUser.isEmpty()){
        	HttpSession session = httpServletRequest.getSession(); 
        	Object remuser = session.getAttribute(SESS_PARAM_REMOTE_USER);
        	remoteUser = remuser!=null ? (String) remuser : null; 
        }
        
        StringBuffer requestURL = httpServletRequest.getRequestURL();
        if (httpServletRequest.getQueryString() != null) {
            requestURL.append("?").append(httpServletRequest.getQueryString());
        }
        String completeURL = requestURL.toString();
        Enumeration<String> headerNames = httpServletRequest.getHeaderNames();

        if (headerNames != null) {
                while (headerNames.hasMoreElements()) {
                	String hn = headerNames.nextElement();
                        //System.out.println(hn+" : " + httpServletRequest.getHeader(hn));
                }
        }
        if (remoteUser != null && !remoteUser.isEmpty()) { 
            HttpSession session = httpServletRequest.getSession(); 
            session.setAttribute(SESS_PARAM_REMOTE_USER, remoteUser); 
        }
        
        
       HeaderMapRequestWrapper wrapper = new HeaderMapRequestWrapper(httpServletRequest);
        
		//wrapper.addHeader("x-alfresco-remote-user", "timothy");
        chain.doFilter(wrapper, res); 
        
        
        StringBuilder sb = new StringBuilder();
		sb.append( "\n\n\n");
		Enumeration<String> headernames = wrapper.getHeaderNames();
		boolean hasspringsurfheader = false;
		while(headernames.hasMoreElements()){
			String headername = headernames.nextElement();
			String headerValue = wrapper.getHeader( headername);
			if(headerValue.contains("Spring Surf"))
				hasspringsurfheader = true;
			sb.append( "\t\t  MODED..... Header:: [" ).append( headername ).append( "] : [" ).append( headerValue  ).append("]\n");
		}	logger.info(sb.toString());

	}

	private String proprieterayUserIdValidationAndExtractionMethod(HttpServletRequest req, HttpServletResponse resp, String remoteUser) {
		
		GenericHTTPClient httpclient = null;
		try {
			httpclient = new GenericHTTPClient("http");
			
			GenericHTTPParam params = new GenericHTTPParam();
			Map<String,String> headers = new HashMap<String,String>();
			String auth = "admin" + ":" + "";
			String basicauthhh = "dGltb213YUBnbWFpbC5jb206QWRtaW4xMjMjQCE=";
			headers.put("Authorization","Basic "+basicauthhh);//dGltb213YUBnbWFpbC5jb206QWRtaW4xMjMjQCE=");
			params.setHeaderParams(headers);
			params.setHttpmethod("GET");
			params.setUrl("http://localhost:8080/eacimscore/seam/resource/restv1/sso/"+remoteUser);
			
			
			GenericHttpResp response = httpclient.call(params);
			
			System.out.println("\n\n encodedAuth = ["+basicauthhh+"]  RESP CODE:: "+response.getResp_code()
			         +"\n\t\t RESP BODY:: "+response.getBody());
			
			if(response.getResp_code()==200)
				return response.getBody();
			return null;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}finally{
			try{
				if(httpclient!=null)
					httpclient.finalizeMe();
			}catch(Exception e){}
		}
		
		
		return null;
	}

	

}
