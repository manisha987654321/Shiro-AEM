package com.practice.shiroaem.servlet;

import org.apache.felix.scr.annotations.*;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.IOException;
import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 * User: Manisha Bano
 * Date: 27/1/16
 * Time: 12:05 PM
 * To change this template use File | Settings | File Templates.
 */

@Component(immediate = true, metatype = false, label = "Login ")
@Service(Servlet.class)
@Properties(value = {
        @Property(name = "sling.servlet.methods", value = "POST"),
        @Property(name = "sling.servlet.paths", value = "/bin/shiroLogin")
})
public class ShiroLogin extends SlingAllMethodsServlet {
    private static final transient Logger log = LoggerFactory.getLogger(ShiroLogin.class);
    SecurityManager securityManager;
    BundleContext bundleContext;

    @Activate
    public void activate(BundleContext bundleContext) throws IOException {
        this.bundleContext = bundleContext;
    }

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        log.info("\n\n\n=========================================================\n\n\n");
        URL configURL = bundleContext.getBundle().getEntry("shiro.ini");
        log.info("\n\nConfigURL :: :: " + configURL);
        if (configURL != null) {

            // The easiest way to create a Shiro SecurityManager with configured realms, users, roles and permissions is to use the simple INI config.
            // We'll do that by using a factory that can ingest a .ini file and return a SecurityManager instance:

            // Use the shiro.ini file at the root of the classpath (file: and url: prefixes load from files and urls respectively):
            Factory<SecurityManager> factory = new IniSecurityManagerFactory("url:" + configURL);
            securityManager = factory.getInstance();
            log.info("\n\nSecurity Manager :: :: :: " + securityManager);
        }
        log.info("\n\n\n\t\t S H I R O   L O G I N \n\n");
        String userName = request.getParameter("username");
        String password = request.getParameter("password");
        log.info("Username :: " + userName);
        log.info("\nPassword :: " + password);
        String message = null;


        // for this simple example quickstart, make the SecurityManager accessible as a JVM singleton.
        // Most applications wouldn't do this and instead rely on their container configuration or web.xml for webapps.
        // That is outside the scope of this simple quickstart, so we'll just do the bare minimum so you can continue to get a feel for things.
        log.info("\nsecurityManager :: " + securityManager);
        SecurityUtils.setSecurityManager(securityManager);
        log.info("\nsecurityManager :: " + securityManager);
        // Now that a simple Shiro environment is set up, let's see what you can do:

        // get the currently executing user:
        Subject currentUser = SecurityUtils.getSubject();

        log.info("\n\n\tCurrent User :: :: ", currentUser);
        log.info("\n\tUser Authentication :: ::" + currentUser.isAuthenticated());
        // Do some stuff with a Session (no need for a web or EJB container!!!)
        currentUser.logout();
        log.info("\n\tAfter Logout User Authentication :: ::" + currentUser.isAuthenticated());

        currentUser = SecurityUtils.getSubject();

        // let's login the current user so we can check against roles and permissions:
        if (!currentUser.isAuthenticated()) {
            UsernamePasswordToken token = new UsernamePasswordToken(userName, password);
            log.info("\n\n\tCurrent User Token :: :: ", token);
            token.setRememberMe(true);
            try {
                currentUser.login(token);
                message = "User [" + currentUser.getPrincipal() + "] logged in successfully.";
                Session session = currentUser.getSession(false);
                if (session == null)
                    return;
                session.setAttribute(userName+"session", password+"session");
                String value = (String) session.getAttribute(password+"session");

                    log.info("\n\n\tRetrieved the correct value! [" + value + "]");

            } catch (UnknownAccountException uae) {
                message = "There is no user with username of " + token.getPrincipal();
            } catch (IncorrectCredentialsException ice) {
                message = "Password for account " + token.getPrincipal() + " was incorrect!";
            } catch (LockedAccountException lae) {
                message = "The account for username " + token.getPrincipal() + " is locked.  " + "Please contact your administrator to unlock it.";
            } catch (AuthenticationException ae) {
                message = "The account for username " + token.getPrincipal() + " is locked.  " + "Please contact your administrator to unlock it.";
            }
            log.info("\n\n\tYOU may get this RESULT :: :: \n" + message + "\n");
        }
        if (message == null)
            message = "There might be some error... Please Try Again!!!";
        response.getWriter().write(message);
    }
}
