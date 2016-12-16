package com.storeelf.report.web.security;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.naming.NamingException;
import javax.naming.ldap.LdapContext;

import org.apache.log4j.Logger;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.ldap.JndiLdapRealm;
import org.apache.shiro.realm.ldap.LdapContextFactory;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.CollectionUtils;
import org.apache.shiro.util.StringUtils;

import com.storeelf.report.web.Constants;
import com.storeelf.report.web.StoreElfConstants;
import com.storeelf.report.web.init.ReportActivator;
import com.storeelf.util.SQLUtils;
import com.storeelf.util.SecurityUtils;

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
		
		Object principal = token.getPrincipal();
	
		//Skip Login authentication (i.e. create generic session)

		if(turnOffLogin){
			
			return createAuthenticationInfo(token, principal, null, null);
			
		}else{
			Connection conRO = null;
			String password = null;
			String salt = null;
			String saltedPass = null;
			try {
				conRO = ReportActivator.getInstance().getConnection(StoreElfConstants.STOREELF_RO);
			} catch (ClassNotFoundException | IOException | SQLException e) {
				logger.error("Unable to Create StoreElf Connection");
			}
			
			try {
				ConcurrentHashMap<Integer, HashMap<String, Object>> userResults = SQLUtils.getSQLResult(
						"Select password, salt from se_user where username = '"+principal+"'",
						conRO);
				
				if(userResults.isEmpty()){
					throw new NamingException("User: '" + (String) principal + "' not authenticated!");
				} 
				
				for (HashMap<String, Object> map : userResults.values()) {
					password =  String.valueOf(map.get("PASSWORD")).trim();
					salt =  String.valueOf(map.get("SALT")).trim();
					saltedPass = SecurityUtils.returnSaltedPassword(token.getCredentials(), salt);
					
					if(saltedPass.equals(password)){
						return createAuthenticationInfo(token, principal, null, null);
					} else {
						throw new NamingException("User: '" + (String) principal + "' not authenticated!");
					}
					
				}
				
				
			} catch (SQLException e) {
				logger.error("Error in getting user password and salt from DB");
				throw new NamingException("User: '" + (String) principal + "' not authenticated!");
			}
			
			return null;
		}
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
					String sql = "SELECT distinct(r.name)"
							+ " FROM se_user_group_list gl, se_user_group_role_list grl, se_user_roles r "
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
