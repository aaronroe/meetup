package controllers;

import com.typesafe.plugin.MailerPlugin;
import models.Invitation;
import models.forms.Proposition;
import org.pac4j.core.profile.CommonProfile;
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
            final CommonProfile profile = getUserProfile();
            String netId = profile.getId();

            Proposition proposition = filledForm.get();

            // create the invitation in the database.
            Invitation invitation = Invitation.create(proposition.name, netId+"@rice.edu", proposition.email, proposition.location, proposition.topic);

            MailerAPI mail = play.Play.application().plugin(MailerPlugin.class).email();
            mail.setSubject("You have received a Rice Meetup Invite!");
            mail.setRecipient(proposition.email);
            mail.setFrom(netId + "@rice.edu");
            mail.sendHtml("<html>\n" +
                    "\t<h1 style=\"color: #333\">Hello "+proposition.name+"!</h1>\n" +
                    "\t<h1 style=\"color: #333\">I would love to buy you a drink from <i>"+proposition.location+"</i> sometime and talk about <i>"+proposition.topic+"</i>.</h1>\n" +
                    "\t<button style=\"display: inherit;background-color: #47a447;padding: 10px 20px;margin:10px 0;border-radius: 4px;border: 1px solid transparent;font-size: 14px;cursor: pointer;\"><a href=\""+invitation.getAcceptURL()+"\" style=\"color: #fff;text-decoration: none;\" href=\"\">Sounds great! When would you like to meet?</a></button>\n" +
                    "\t<button style=\"display: inherit;background-color: #d9534f;padding: 10px 20px;margin:10px 0;border-radius: 4px;border: 1px solid transparent;font-size: 14px;cursor: pointer;\"><a href=\""+invitation.getRejectURL()+"\" style=\"color: #fff;text-decoration: none;\" href=\"\">Sorry, I'm busy and can't meet! Maybe some other time.</a></button>\n" +
                    "</html>" );
        }

        return redirect(routes.Application.success());
    }

    public static Result acceptInvitation(String verificationCode) {
        Invitation invitation = Invitation.getInvitationByVerificationCode(verificationCode);
        if (invitation == null || invitation.isResponded()) {
            return ok("Invalid url.");
        }
        else {
            // set the invitation as responded and send a notification email.
            invitation.setResponded(true);

            MailerAPI mail = play.Play.application().plugin(MailerPlugin.class).email();
            mail.setSubject("Your Rice Meetup invite has been accepted!");
            mail.setRecipient(invitation.getInviterEmail());
            mail.setFrom(invitation.getInvitedEmail());
            mail.sendHtml("<html>\n" +
                    "\t<br />\n" +
                    "\t<h1 style=\"background-color: #5cb85c;display: inline;padding: .2em .6em .3em;font-weight: 700;color: #fff;text-align: center;border-radius: .25em;\">"+invitation.getInviterName()+" has accepted your invite!</h1>\n" +
                    "\t<h1 style=\"color: #333\">To jog your memory, you offered to buy a drink from <i>"+invitation.getLocation()+"</i> in order to talk about <i>"+invitation.getTopic()+"</i>.</h1>\n" +
                    "\t<h2 style=\"color: #333\">You can reply to this email to schedule a proper time.</h2>\n" +
                    "\t<h2 style=\"color: #333\">Hope things work out!</h2>\n" +
                    "</html>");

            return ok("You have successfully accepted your invitation to meet up!");
        }
    }

    public static Result rejectInvitation(String verificationCode) {
        Invitation invitation = Invitation.getInvitationByVerificationCode(verificationCode);
        if (invitation == null || invitation.isResponded()) {
            return ok("Invalid url.");
        }
        else {
            // set the invitation as responded and send a notification email.
            invitation.setResponded(true);

            MailerAPI mail = play.Play.application().plugin(MailerPlugin.class).email();
            mail.setSubject("Your Rice Meetup invite has been declined...");
            mail.setRecipient(invitation.getInviterEmail());
            mail.setFrom(invitation.getInvitedEmail());
            mail.sendHtml("<html>\n" +
                    "\t<br />\n" +
                    "\t<h1 style=\"background-color: #d9534f;display: inline;padding: .2em .6em .3em;font-weight: 700;color: #fff;text-align: center;border-radius: .25em;\">Sadly, "+invitation.getInviterName()+" has declined your invite...</h1>\n" +
                    "\t<h2 style=\"color: #333\">Sorry and hope things work out next time!</h2>\n" +
                    "</html>");

            return ok("You have successfully rejected your invitation to meet up... : (");
        }
    }
}
