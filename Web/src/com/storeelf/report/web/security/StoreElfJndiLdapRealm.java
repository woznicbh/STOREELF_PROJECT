package com.storeelf.report.web.security;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;

import org.apache.log4j.Logger;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.ldap.JndiLdapRealm;
import org.apache.shiro.realm.ldap.LdapContextFactory;
import org.apache.shiro.realm.ldap.LdapUtils;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.CollectionUtils;
import org.apache.shiro.util.StringUtils;

import com.storeelf.report.web.Constants;
import com.storeelf.report.web.init.ReportActivator;
import com.storeelf.util.SQLUtils;

// TODO: finish Auth implementation
public class StoreElfJndiLdapRealm extends JndiLdapRealm{
	static final Logger logger							= Logger.getLogger(StoreElfJndiLdapRealm.class);


	public static final String DEFAULT_SEARCH_FILTER = "(uid={0})";
	private String			searchBase;
	private String			searchFilter;
	private String			assignedRoles;
	private String[]		allowedUsers;
	private boolean			turnOffLogin;
	private boolean			allowAllUsers;

	public StoreElfJndiLdapRealm() {
		super();
		searchFilter = DEFAULT_SEARCH_FILTER;
		searchBase = null;
	}

	@Override
	public void setUserDnTemplate(String template) throws IllegalArgumentException {
		throw new RuntimeException("This method is not implemented, please use setSeachFilter and setBaseDn");
	}

	@Override
	public String getUserDnTemplate() {
		throw new RuntimeException("This method is not implemented, please use getSeachFilter and getBaseDn");
	}

	protected String getSearchFilter(String principal) throws IllegalArgumentException, IllegalStateException {
		if (!StringUtils.hasText(principal)) {
			throw new IllegalArgumentException("User principal cannot be null or empty.");
		}

		if (!searchFilter.contains("{0}")) {
			logger.warn("You didn't include {0} in your searchFilter, I assume you know what you are doing!");
		}
		return searchFilter.replace("{0}", principal);
	}
 	
	@Override
	protected AuthenticationInfo queryForAuthenticationInfo(AuthenticationToken token, LdapContextFactory ldapContextFactory)
			throws NamingException {
		logger.debug("-------------------------------------[Initialising LDAP Authentication]-------------------------------------");
		Object principal = token.getPrincipal();
		Object credentials = token.getCredentials();

		AuthenticationInfo info = null;
 
		//Skip Login authentication (i.e. create generic session)
		//if(turnOffLogin){
		if(true){
			logger.debug("No Ldap validation enabled, creating generic session");
			return createAuthenticationInfo(token, principal, null, null);
		}
		//}
		
		
		//assure we're searching the LDAP server using the proper credentials
		if (searchBase == null || searchBase.trim().length() == 0){		logger.error("searchBase must be defined");	return null;}
		if (searchFilter == null || searchFilter.trim().length() == 0){	logger.error("searchFilter must be defined");	return null;}

		if (!(principal instanceof String)) {
			logger.error("principal must be a string");
			return null;
		}

		String filter = getSearchFilter((String) principal);

		logger.debug("Using base: {"+searchBase+"}");
		logger.debug("Using filter: {"+filter+"}");

		//Restricted users validation
		boolean validUser = false;
		if(this.allowAllUsers){
			validUser=true;
			logger.debug("Allowing all ldap users");
		} else {
			logger.debug("Restricted users");
			for(String uid: allowedUsers){
				if(((String)principal).trim().equals(uid.trim())){
					validUser=true;
					break;
				}
			}
		}
		
		//if the user login fails, kick them out ! ^_^
		if(!validUser) throw new NamingException("User: '" + (String) principal + "' not authorized!");

		LdapContext ctx = null;

		SearchControls searchControls = new SearchControls();
		searchControls.setReturningAttributes(new String[] { "dn" });
		searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

		/*
		 * We don't know what the search filter will look like
		 * So if we get multiple results, we should try them all.
		 */
		List<String> possibleDns = new ArrayList<String>();

		/* Lets search for the user first */
		try {
			logger.debug("Searching for user '{" + principal + "}' through LDAP");
			ctx = ldapContextFactory.getSystemLdapContext();

			NamingEnumeration<SearchResult> response = ctx.search(searchBase, filter, searchControls);

			while (response.hasMoreElements()) {
				possibleDns.add(((SearchResult) response.next()).getNameInNamespace());
			}
		} catch(Exception e){
			logger.error(e.getLocalizedMessage(), e);
			throw new NamingException(e.getLocalizedMessage());
		}finally {
			LdapUtils.closeContext(ctx);
		}

		/* Now lets authenticate */
		if (!possibleDns.isEmpty()) {
			for (String dn : possibleDns) {
				ctx = null;
				try {
					logger.debug("Attempting dn '{" + dn + "}' through LDAP");
					ctx = ldapContextFactory.getLdapContext(dn, credentials);

					logger.debug("Authenticated: {" + dn + "}!");
					info = createAuthenticationInfo(token, principal, credentials, ctx);
					// uses simple -> change to subclass to support holding the dn?
				}
				catch (NamingException e) {	logger.warn("Failed to authenticate for: {" + dn + "}");}
				catch (Exception e) {		logger.error("Failed to authenticate for: {" + dn + "}", e);}
				finally {LdapUtils.closeContext(ctx);}				
				logger.debug("-------------------------------------[LDAP Authentication Complete]-------------------------------------");
				return info;
			}
		}
		throw new NamingException("User: '" + (String) principal + "' not authenticated!");
	}
	
	protected AuthenticationInfo createAuthenticationInfo(AuthenticationToken token, Object ldapPrincipal, Object ldapCredentials, LdapContext ldapContext) throws NamingException {
		return new SimpleAuthenticationInfo(token.getPrincipal(), token.getCredentials(), getName());
	}


	@Override
	protected AuthorizationInfo queryForAuthorizationInfo(PrincipalCollection principals, LdapContextFactory ldapContextFactory) throws NamingException {
		//if no principle provided, quit
		if (principals == null) return null;

		Set<String> roleNames = new HashSet<String>();
		String username = (String) getAvailablePrincipal(principals);

		if (StringUtils.hasLength(username)) {
			Set<String> 	roles	= new HashSet<String>();
			String 			role	= "";
			Connection		con		= null;

			try{
				try{
					con = ReportActivator.getInstance().getConnection(Constants.STOREELF_RO);
				}catch(Exception e){
					logger.warn("Unable to connect to MySQL server, it doesn't like me anymore; "
							+ "check the MySQL max_connection setting, this may need to be increased.");
				}
				
				if(con!=null){
					String sql = "SELECT grl.user_role_key, r.name"
							+ " FROM storeelf.lh_user_group_list gl, storeelf.lh_user_group_role_list grl, storeelf.lh_user_roles r "
							+ " WHERE gl.username = '"+username+"' and gl.user_group_key = grl.user_group_key and grl.user_role_key = r.user_role_key";
									
					ConcurrentHashMap<Integer, HashMap<String, Object>> result = SQLUtils.getSQLResult(sql, con);
					logger.debug("result size:"+result.size());
					
					//add all of the roles pulled from the database to the in-memory map
					for (HashMap<String, Object> map : result.values())		roles.add(	String.valueOf(map.get("NAME")) 	);
					//try {if(con!=null){con.close();}} catch (Exception e2) {logger.error("error closing DB connection");}
				}else{throw new Exception("StoreElf Mysql Connection for AuthInfo null");}								
			}
			catch (IOException e)			{logger.debug("error processing permission : IOException", e);}
			catch (ClassNotFoundException e){logger.debug("error processing permission : ClassNotFoundException", e);}
			catch (SQLException e)			{logger.debug("error processing permission : SQLException", e);}
			catch (Exception e)				{logger.debug("error processing permission : Exception", e);}

			//this assures every unrecognized user has a role assigned
			if(roles.size()==0)	roles.add("default");

			SimpleAuthorizationInfo info = new SimpleAuthorizationInfo(roleNames);
									info.setRoles(roles);
			return info;
		}
		return null;
	}

	protected Object getAvailablePrincipal(PrincipalCollection principals) {
		Object primary = null;
		if (!CollectionUtils.isEmpty(principals)) {
			Collection thisPrincipals = principals.fromRealm(getName());
			if (!CollectionUtils.isEmpty(thisPrincipals)) {
				primary = thisPrincipals.iterator().next();
			} else {
				// no principals attributed to this particular realm. Fall back
				// to the 'master' primary:
				primary = principals.getPrimaryPrincipal();
			}
		}

		return primary;
	}

	/**
	 * @return the searchBase
	 */
	public String getSearchBase() {
		return searchBase;
	}

	/**
	 * @param searchBase
	 *            the searchBase to set
	 */
	public void setSearchBase(String searchBase) {
		this.searchBase = searchBase;
	}

	/**
	 * @return the searchFilter
	 */
	public String getSearchFilter() {
		return searchFilter;
	}

	/**
	 * @param searchFilter
	 *            the searchFilter to set
	 */
	public void setSearchFilter(String searchFilter) {
		this.searchFilter = searchFilter;
	}

	/**
	 * @return the assignedPermissions
	 */
	public String getAssignedRoles() {
		return assignedRoles;
	}

	/**
	 * @param assignedPermissions
	 *            the assignedPermissions to set
	 */
	public void setAssignedRoles(String assignedRoles) {
		this.assignedRoles = assignedRoles;
	}

	public String[] getAllowedUsers() {
		return allowedUsers;
	}

	public void setAllowedUsers(String[] allowedUsers) {
		this.allowedUsers = allowedUsers;
	}

	public boolean isAllowAllUsers() {
		return allowAllUsers;
	}

	public void setAllowAllUsers(boolean allowAllUsers) {
		this.allowAllUsers = allowAllUsers;
	}

	public boolean isTurnOffLogin() {
		return turnOffLogin;
	}

	public void setTurnOffLogin(boolean turnOffLogin) {
		this.turnOffLogin = turnOffLogin;
	}
	
	@Override
    public void clearCachedAuthorizationInfo(PrincipalCollection principals) 
    {
        super.clearCachedAuthorizationInfo(principals);
    }
	
	@Override
    public void clearCachedAuthenticationInfo(PrincipalCollection principals) 
    {
        super.clearCachedAuthenticationInfo(principals);
    }

}
