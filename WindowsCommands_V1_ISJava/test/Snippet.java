package test;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

public class Snippet {
	public static void main(String[] args) {
		try {
			Properties env = new Properties();
			String serviceUserDN = "uid=admin,ou=system";
			String serviceUserPassword = "secret";
			String identifier = "vishnu";
			String password = "vishnu";
			String base = "ou=system";
			env.put(Context.INITIAL_CONTEXT_FACTORY,
					"com.sun.jndi.ldap.LdapCtxFactory");
			env.put(Context.PROVIDER_URL, "ldap://localhost:10389");
			env.put(Context.SECURITY_AUTHENTICATION, "simple");
			env.put(Context.SECURITY_PRINCIPAL, serviceUserDN);
			env.put(Context.SECURITY_CREDENTIALS, serviceUserPassword);

			LdapContext ctx = new InitialLdapContext(env, null);
			ctx.setRequestControls(null);
			NamingEnumeration<?> namingEnum = ctx.search(base,
					String.format("(uid=%s)", identifier),
					getSimpleSearchControls());
			while (namingEnum.hasMore()) {
				SearchResult result = (SearchResult) namingEnum.next();
				Attributes attrs = result.getAttributes();
				System.out.println(String.format("attrs=[%s], cn=[%s]", attrs,
						attrs.get("cn")));
				authenticateUser(result, password);
			}
			namingEnum.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void authenticateUser(SearchResult result,String password)
			throws NamingException {
		String distinguishedName = result.getNameInNamespace();

		// attempt another authentication, now with the user
		Properties authEnv = new Properties();
		authEnv.put(Context.INITIAL_CONTEXT_FACTORY,
				"com.sun.jndi.ldap.LdapCtxFactory");
		authEnv.put(Context.PROVIDER_URL, "ldap://localhost:10389");
		authEnv.put(Context.SECURITY_PRINCIPAL, distinguishedName);
		System.out.println(String.format("distinguishedName=[%s]",
				distinguishedName));
		authEnv.put(Context.SECURITY_CREDENTIALS, password);
		new InitialDirContext(authEnv);

		System.out.println("Authentication successful");
	}

	private static SearchControls getSimpleSearchControls() {
		SearchControls searchControls = new SearchControls();
		searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		searchControls.setTimeLimit(30000);
		// String[] attrIDs = {"objectGUID"};
		// searchControls.setReturningAttributes(attrIDs);
		return searchControls;
	}
}
