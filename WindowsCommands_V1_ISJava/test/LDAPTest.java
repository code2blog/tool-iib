package test;

import java.util.Properties;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.*;
 
public class LDAPTest {
 
    public void run() {
        try {
            DirContext context = getContext();
//            String name = "uid=admin,ou=system";
            String name = "employeeNumber=00001,ou=system";
            createLDAPObject(context, name);
            createAttribute(context, name, "displayName", "JOBS");
            viewAttribute(context, name, "displayName");
            updateAttribute(context, name, "displayName", "STEVE");
            viewAttribute(context, name, "displayName");
//            removeAttribute(context, name, "displayName");
//            removeLDAPObject(context, name);
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }
 
    private void removeLDAPObject(DirContext context, String name) throws NamingException {
        context.destroySubcontext(name);
    }
 
    private void createLDAPObject(DirContext context, String name) throws NamingException {
        Attributes attributes = new BasicAttributes();
 
        Attribute attribute = new BasicAttribute("objectClass");
        attribute.add("inetOrgPerson");
        attributes.put(attribute);
 
        Attribute sn = new BasicAttribute("sn");
        sn.add("Steve");
        attributes.put(sn);
 
        Attribute cn = new BasicAttribute("cn");
        cn.add("Jobs");
        attributes.put(cn);
 
        attributes.put("telephoneNumber", "123456");
        context.createSubcontext(name, attributes);
    }
 
    private void removeAttribute(DirContext context, String name , String attrName) throws NamingException {
        Attribute attribute = new BasicAttribute(attrName);
        ModificationItem[] item = new ModificationItem[1];
        item[0] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE, attribute);
        context.modifyAttributes(name, item);
    }
 
    private void createAttribute(DirContext context, String name , String attrName, Object attrValue) throws NamingException {
        Attribute attribute = new BasicAttribute(attrName, attrValue);
        ModificationItem[] item = new ModificationItem[1];
        item[0] = new ModificationItem(DirContext.ADD_ATTRIBUTE, attribute);
        context.modifyAttributes(name, item);
    }
 
    private void updateAttribute(DirContext context, String name , String attrName, Object attrValue) throws NamingException {
        Attribute attribute = new BasicAttribute(attrName, attrValue);
        ModificationItem[] item = new ModificationItem[1];
        item[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, attribute);
        context.modifyAttributes(name, item);
    }
 
    private void viewAttribute(DirContext context, String name , String attrName) throws NamingException {
        Attributes attrs = context.getAttributes(name);
        System.out.println(attrName + ":" + attrs.get(attrName).get());
//        System.out.println(attrName + ":" + attrs.get(attrName));
    }
 
    private DirContext getContext() throws NamingException {
        Properties env = new Properties();
        env.put(Context.INITIAL_CONTEXT_FACTORY,
                "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, "ldap://localhost:10389");
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, "uid=admin,ou=system");
        env.put(Context.SECURITY_CREDENTIALS, "secret");

        return new InitialDirContext(env);
    }
 
    public static void main(String[] args) {
        new LDAPTest().run();
    }
}