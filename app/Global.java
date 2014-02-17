import play.Application;
import play.GlobalSettings;

import org.pac4j.cas.client.CasClient;
import org.pac4j.core.client.Clients;

import org.pac4j.play.Config;

/**
 * Global settings for application.
 */
public class Global extends GlobalSettings {

    public void onStart(Application app) {
        // CAS
        final CasClient casClient = new CasClient();
        // casClient.setLogoutHandler(new PlayLogoutHandler());
        // casClient.setCasProtocol(CasProtocol.SAML);
        // casClient.setGateway(true);
        /*final CasProxyReceptor casProxyReceptor = new CasProxyReceptor();
        casProxyReceptor.setCallbackUrl("http://localhost:9000/casProxyCallback");
        casClient.setCasProxyReceptor(casProxyReceptor);*/
        casClient.setCasLoginUrl("https://netid.rice.edu/cas/login");

        final Clients clients = new Clients("http://localhost:9000/callback", casClient); // , casProxyReceptor);
        Config.setClients(clients);
    }

}
