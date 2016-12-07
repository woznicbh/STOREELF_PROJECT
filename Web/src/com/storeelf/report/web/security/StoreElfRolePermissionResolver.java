package com.storeelf.report.web.security;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.LinkedList;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.permission.RolePermissionResolver;
import org.apache.shiro.authz.permission.WildcardPermission;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.mgt.CachingSecurityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.storeelf.report.web.Constants;
import com.storeelf.report.web.init.ReportActivator;

/**
 * @author tkmagh4
 *
 * Resolves provided role to preselected permissions
 *
 * Example:
 * 	pRoleNAme = Administrator
 * 		permission = (*)
 *
 * 	pRoleName = Analyst
 * 		permission = (reports:weekly:read)
 * 		permission = (reports:weekly:update)
 *
 */
public class StoreElfRolePermissionResolver implements RolePermissionResolver, org.apache.shiro.cache.CacheManagerAware{
	private static final Logger		logger			= LoggerFactory.getLogger(StoreElfRolePermissionResolver.class);
	private CacheManager cacheManager;
	private Connection		con		= null;
	
	public StoreElfRolePermissionResolver() {
		try {
			this.setConnection(ReportActivator.getInstance().getConnection(Constants.STOREELF_RO));
		}
			catch (FileNotFoundException e) {e.printStackTrace();}
			catch (ClassNotFoundException e) {e.printStackTrace();}
			catch (SQLException e) {e.printStackTrace();}
			catch (IOException e) {e.printStackTrace();
		}
	}


	@Override
	public Collection<Permission> resolvePermissionsInRole(String pRoleName){
		logger.debug("pRoleName: " + pRoleName);
		Collection<Permission> permissions = new LinkedList<Permission>();
		Connection con = null;
		Permission permission = null;
		StoreElfCache cache = null;
		String cacheName = "ldapRealm.resolvedPermissionsCache";
		Statement stmnt = null;
		ResultSet result = null;
		// load cache 'ldapRealm.resolvedPermissionsCache' from cache manager

		CachingSecurityManager sManager = (CachingSecurityManager) SecurityUtils.getSecurityManager();

		logger.debug("containsBackingCache:"
				+ ((StoreElfCacheManager) sManager.getCacheManager()).containsBackingCache(cacheName));
		logger.debug("containsCache:" + ((StoreElfCacheManager) sManager.getCacheManager()).containsCache(cacheName));

		try {
			// load cache for ldapRealm.resolvedPermissionsCache
			cache = (StoreElfCache) ((StoreElfCacheManager) sManager.getCacheManager())
					.getCache("ldapRealm.resolvedPermissionsCache");
		} catch (CacheException e) {
			e.printStackTrace();
			logger.error("error loading permissions from cache: 'ldapRealm.resolvedPermissionsCache' : CacheException",
					e);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error loading permissions from cache: Exception", e);
		}

		try {
			// if exists in cache, use cached instance and return
			if (cache != null) {
				if (cache.get(pRoleName) != null) {

					// load permissions for this pRoleName from cache
					permissions = (Collection<Permission>) cache.get(pRoleName);

					logger.debug("returning cached permissions for role " + pRoleName + " | permission list size:"
							+ permissions.size());

					return permissions;
				} else {
					logger.debug("no permissions exist in cache for role: " + pRoleName);
				}
			} else {
				logger.warn("NOT loading permissions from cache!, cache doesn't exist yet!");
			}

			try {
				con = ReportActivator.getInstance().getConnection(Constants.STOREELF_RO);
			} catch (Exception e) {
				logger.warn("Unable to connect to MySQL server, it doesn't like me anymore; "
						+ "check the MySQL max_connection setting, this may need to be increased.");
			}

			if (con != null) {
				stmnt = con.createStatement();
				result = stmnt.executeQuery(
						"SELECT up.section, up.function, up.servlet_uri FROM se_user_roles r, se_user_role_list rl, se_user_permissions up WHERE rl.user_permission_key = up.user_permission_key AND rl.user_role_key = r.user_role_key AND r.name = '"
								+ pRoleName + "'");

				String section, function, servlet_uri = "-";

				// add each permission to the permissions map for caching
				while (result.next()) {
					section = String.valueOf(result.getString("SECTION"));
					function = String.valueOf(result.getString("FUNCTION"));
					servlet_uri = String.valueOf(result.getString("SERVLET_URI"));

					logger.debug("ROLE:" + pRoleName + ": " + section + ':' + function + ':' + servlet_uri);

					permission = new WildcardPermission(section + ':' + function + ':' + servlet_uri);
					permissions.add(permission);
				}

				// cache result and return a collection of permissions retrieved
				cache.put(pRoleName, permissions);

				// try {if(con!=null){con.close();}} catch (Exception e2)
				// {logger.error("error closing DB connection");}

				return permissions;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("error processing permission : SQLException", e);
		} catch (CacheException e) {
			e.printStackTrace();
			logger.error("error processing permission : CacheException", e);
		} catch (NullPointerException e) {
			e.printStackTrace();
			logger.error("error processing permission : NullPointerException", e);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error processing permission : Exception", e);
		} finally {
			try{
			if(stmnt!=null){stmnt.close();}
			if(result!=null){result.close();}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		// finally {try {if(con!=null){con.close();}} catch (Exception e2) {}}

		// add permissions for default role if not added yet
		try {
			// only validate default role
			if (StringUtils.equals(pRoleName, "default")) {

				// confirm permissions collection is empty
				if (permissions.isEmpty()) {
					if (cache != null) {
						if (cache.get("default") == null) {
							// put permissions collection into cache
							cache.put(pRoleName, getDefaultPermissions(permissions));
						} else {
							// well that's fortunate, the record is cached ...
							// return it!!
							return permissions = (Collection<Permission>) cache.get(pRoleName);
						}
					} else {
						// cache is null, well this is awkward ... *sigh*, just
						// return permissions either way
						return getDefaultPermissions(permissions);
					}
				} else {
					// permissions collection is not empty, assume permissions
					// have been loaded from MySQL database
					// do nothing
				}
			}
		} catch (CacheException e) {
			e.printStackTrace();
			logger.error("error configuring default permissions from cache: CacheException", e);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("error configuring default permissions from cache: Exception", e);
		}

		return permissions;
	}
	
	//set default permissions and return
	public Collection<Permission> getDefaultPermissions(Collection<Permission> permissions){
		if(Constants.STOREELF_SECURITY_ENABLED){
			permissions.add(new WildcardPermission("dashboard:create:*:*"));
			permissions.add(new WildcardPermission("dashboard:update:*:*"));
			permissions.add(new WildcardPermission("dashboard:delete:*:*"));
			permissions.add(new WildcardPermission("dashboard:read:*:*"));
			permissions.add(new WildcardPermission("utility:create:*:*"));
			permissions.add(new WildcardPermission("utility:read:*:*"));
			permissions.add(new WildcardPermission("utility:update:*:*"));
			permissions.add(new WildcardPermission("utility:delete:*:*"));
			permissions.add(new WildcardPermission("environment:create:*:*"));
			permissions.add(new WildcardPermission("environment:read:*:*"));
			permissions.add(new WildcardPermission("environment:update:*:*"));
			permissions.add(new WildcardPermission("environment:delete:*:*"));
			permissions.add(new WildcardPermission("help:create:*:*"));
			permissions.add(new WildcardPermission("help:read:*:*"));
			permissions.add(new WildcardPermission("help:update:*:*"));
			permissions.add(new WildcardPermission("help:delete:*:*"));
			permissions.add(new WildcardPermission("report:create:*:*"));
			permissions.add(new WildcardPermission("report:read:*:*"));
			permissions.add(new WildcardPermission("report:update:*:*"));
			permissions.add(new WildcardPermission("report:delete:*:*"));
			permissions.add(new WildcardPermission("security:read:Authenticate:logout"));
		}else{
			permissions.add(new WildcardPermission("*:*:*:*"));
		}
		return permissions;
	}

	@Override
	public void setCacheManager(CacheManager pcacheManager) {
		this.cacheManager = pcacheManager;
	}
	
	public Connection getConnection() {
		try {
			if(con==null || con.isClosed()){
				logger.debug("previous connection stale, creating new one");
				this.setConnection(ReportActivator.getInstance().getConnection(Constants.STOREELF_RO));
			}
		}	
		catch (SQLException e) {			e.printStackTrace();} catch (FileNotFoundException e) {	e.printStackTrace();}
		catch (ClassNotFoundException e) {	e.printStackTrace();} catch (IOException e) {			e.printStackTrace();}
		
		return con;
	}

	public void setConnection(Connection con) {
		this.con = con;
	}


}
