package technovation.alfresco.ssofilter;

/**
 * Hello world!
 *
 */
public class App 
{
	private static final String PARAM_REMOTE_USER = "remoteUser";
	private static final String SESS_PARAM_REMOTE_USER = SSOIntegrationFilter.class.getName() + '.' + PARAM_REMOTE_USER;

    public static void main( String[] args )
    {
        System.out.println( SESS_PARAM_REMOTE_USER );
    }
}
