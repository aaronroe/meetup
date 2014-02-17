package controllers;

import com.typesafe.plugin.MailerPlugin;
import models.forms.Proposition;
import play.*;
import play.data.Form;
import play.mvc.*;

import org.pac4j.play.java.JavaController;
import org.pac4j.play.java.RequiresAuthentication;

import com.typesafe.plugin.MailerAPI;

import views.html.*;

import static play.data.Form.form;
import static play.mvc.Results.badRequest;
import static play.mvc.Results.ok;
import static play.mvc.Results.redirect;

public class Application extends JavaController {

    public static Result index() {
        return ok(index.render());
    }

    @RequiresAuthentication(clientName = "CasClient")
    public static Result proposition() {
        return ok(proposition.render(form(Proposition.class)));
    }

    public static Result success() {
        return ok(success.render());
    }

    @RequiresAuthentication(clientName = "CasClient")
    public static Result handleForm() {
        final Form<Proposition> filledForm = form(Proposition.class).bindFromRequest();

        if (filledForm.hasErrors()) {
            return badRequest(proposition.render(filledForm));
        }
        else {
            // Proposition proposition = filledForm.get();

            // Client client = Client.create();
            // client.addFilter(new HTTPBasicAuthFilter("api",
            //         "key-4bvvfj1i2hk6x1aps7hrt5qrwzai-vd2"));
            // WebResource webResource =
            //         client.resource("https://api.mailgun.net/v2/sandbox83236.mailgun.org/messages");
            // MultivaluedMapImpl formData = new MultivaluedMapImpl();
            // formData.add("from", "Mailgun Sandbox <postmaster@sandbox83236.mailgun.org>");
            // formData.add("to", "Aaron <acr2@rice.edu>");
            // formData.add("subject", "Hello Aaron");
            // formData.add("text", "Congratulations Aaron, you just sent an email with Mailgun!  You are truly awesome!  You can see a record of this email in your logs: https://mailgun.com/cp/log .  You can send up to 300 emails/day from this sandbox server.  Next, you should add your own domain so you can send 10,000 emails/month for free.");
            // return webResource.type(MediaType.APPLICATION_FORM_URLENCODED).
            //         post(ClientResponse.class, formData);

            MailerAPI mail = play.Play.application().plugin(MailerPlugin.class).email();
            mail.setSubject("You have received a Rice Meetup Invite!");
            mail.setRecipient("aaronalkan@gmail.com");
//            mail.setRecipient("Aaron Roe <Aaron.C.Roe@rice.edu>");
            mail.setFrom("Aaron Roe <acr2@rice.edu>");
            mail.sendHtml("<html>\n" +
                    "\t<h1 style=\"color: #333\">Hello Aaron Roe!</h1>\n" +
                    "\t<h1 style=\"color: #333\">I would love to buy you a drink from <i>Coffeehouse</i> and talk about <i>Play Framework</i>.</h1>\n" +
                    "\t<button style=\"display: inherit;background-color: #47a447;padding: 10px 20px;margin:10px 0;border-radius: 4px;border: 1px solid transparent;font-size: 14px;cursor: pointer;\"><a style=\"color: #fff;text-decoration: none;\" href=\"\">Sounds awesome! When would you like to meet?</a></button>\n" +
                    "\t<button style=\"display: inherit;background-color: #d9534f;padding: 10px 20px;margin:10px 0;border-radius: 4px;border: 1px solid transparent;font-size: 14px;cursor: pointer;\"><a style=\"color: #fff;text-decoration: none;\" href=\"\">Sorry, I'm busy and can't meet! Maybe some other time.</a></button>\n" +
                    "</html>" );
        }

        return redirect(routes.Application.success());
    }
}
