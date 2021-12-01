package com.mysite.core.servlets;

import com.day.cq.replication.Replicator;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.jackrabbit.oak.spi.security.principal.EveryonePrincipal;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.jcr.base.util.AccessControlUtil;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.PropertyType;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFactory;
import javax.jcr.security.AccessControlEntry;
import javax.jcr.security.AccessControlList;
import javax.jcr.security.AccessControlManager;
import javax.jcr.security.Privilege;
import javax.servlet.Servlet;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@Component(immediate = true, service = Servlet.class, property = {
        ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_GET,
        ServletResolverConstants.SLING_SERVLET_PATHS + "=/bin/mca/usercreation",
        Constants.SERVICE_DESCRIPTION + "= Servlet for User Creation",
        Constants.SERVICE_VENDOR + "= LTI"
})
public class UserCreationServlet extends SlingAllMethodsServlet {
    public static final Logger log = LoggerFactory.getLogger(UserCreationServlet.class);

    @Reference
    ResourceResolverFactory resourceResolverFactory;

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) {
        try {
            createGroupUser(request);
            response.getWriter().write("User Created.");
            response.getWriter().close();
        }catch (IOException e){
            log.error("Error " + e);
        }
    }
    public void createGroupUser(SlingHttpServletRequest request) {

        String userName = request.getParameter("userName");
        String password = request.getParameter("password");
        String groupName = request.getParameter("groupName");
        String userEmail = request.getParameter("userEmail");
        String firstname = request.getParameter("firstname");
        String lastname = request.getParameter("lastname");
        Session session = null;
        ResourceResolver resourceResolver = null;

        try {
            Map<String, Object> param = new HashMap<String, Object>();
            param.put(ResourceResolverFactory.SUBSERVICE, "getResourceResolver");
            resourceResolver = resourceResolverFactory.getServiceResourceResolver(param);
            session = resourceResolver.adaptTo(Session.class);
            // Create UserManager Object
            final UserManager userManager = AccessControlUtil.getUserManager(session);
            // Create a Group
            Group group = null;
            if (userManager.getAuthorizable(groupName) == null) {
                group = userManager.createGroup(groupName);
                ValueFactory valueFactory = session.getValueFactory();
                Value groupNameValue = valueFactory.createValue(groupName, PropertyType.STRING);
                group.setProperty("./profile/givenName", groupNameValue);
                session.save();
            } else {
                log.info("Group already exist.");
            }
            User user = null;
            if (userManager.getAuthorizable(userName) == null) {
                user = userManager.createUser(userName, password);
                ValueFactory valueFactory = session.getValueFactory();
                Value firstNameValue = valueFactory.createValue(firstname, PropertyType.STRING);
                user.setProperty("./profile/givenName", firstNameValue);
                Value lastNameValue = valueFactory.createValue(lastname, PropertyType.STRING);
                user.setProperty("./profile/familyName", lastNameValue);
                Value emailValue = valueFactory.createValue(userEmail, PropertyType.STRING);
                user.setProperty("./profile/email", emailValue);
                session.save();
                Group addUserToGroup = (Group) (userManager.getAuthorizable(groupName));
                addUserToGroup.addMember(userManager.getAuthorizable(userName));
                session.save();
                String nodePath = user.getPath();
                setAclPrivileges(nodePath, session);
            } else {
                log.info("User already exist.");
            }
        } catch (Exception e) {
            log.error("Not able to perform User Management.." + e.getMessage());
        } finally {
            if (session != null && session.isLive()) {
                session.logout();
            }
            if (resourceResolver != null)
                resourceResolver.close();
        }
    }

    public static void setAclPrivileges(String path, Session session) {
        try {
            AccessControlManager aMgr = session.getAccessControlManager();
            // create a privilege set
            Privilege[] privileges = new Privilege[] {
                    aMgr.privilegeFromName(Privilege.JCR_VERSION_MANAGEMENT),
                    aMgr.privilegeFromName(Privilege.JCR_MODIFY_PROPERTIES),
                    aMgr.privilegeFromName(Privilege.JCR_ADD_CHILD_NODES),
                    aMgr.privilegeFromName(Privilege.JCR_LOCK_MANAGEMENT),
                    aMgr.privilegeFromName(Privilege.JCR_NODE_TYPE_MANAGEMENT),
                    aMgr.privilegeFromName(Replicator.REPLICATE_PRIVILEGE) };

            AccessControlList acl;
            try {
                // get first applicable policy (for nodes w/o a policy)
                acl = (AccessControlList) aMgr.getApplicablePolicies(path).nextAccessControlPolicy();
            } catch (NoSuchElementException e) {
                // else node already has a policy, get that one
                acl = (AccessControlList) aMgr.getPolicies(path)[0];
            }
            // remove all existing entries
            for (AccessControlEntry e : acl.getAccessControlEntries()) {
                acl.removeAccessControlEntry(e);
            }
            // add a new one for the special "everyone" principal
            acl.addAccessControlEntry(EveryonePrincipal.getInstance(), privileges);
            // the policy must be re-set
            aMgr.setPolicy(path, acl);
            // and the session must be saved for the changes to be applied
            session.save();
        } catch (Exception e) {
            log.error("Not able to perform ACL Privileges." + e.getMessage());
        }
    }
}