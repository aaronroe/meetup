package controllers;

import com.typesafe.plugin.MailerPlugin;
import helper.FingerHelper;
import models.Invitation;
import models.forms.Proposition;
import org.pac4j.core.profile.CommonProfile;
import play.data.Form;
import play.libs.F;
import play.libs.WS;
import play.mvc.*;

import org.pac4j.play.java.JavaController;
import org.pac4j.play.java.RequiresAuthentication;

import com.typesafe.plugin.MailerAPI;

import views.html.*;

import java.util.Calendar;
import java.util.GregorianCalendar;

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
            // figure out the appropriate range of days.
            String possibleDates = "";
            Calendar currentDate = new GregorianCalendar();
            for (int i = 0; i < 14; i++) {
                String year = Integer.toString(currentDate.get(Calendar.YEAR));
                String month = String.format("%02d", currentDate.get(Calendar.MONTH));
                String day = String.format("%02d", currentDate.get(Calendar.DAY_OF_MONTH));
                possibleDates += year+"-"+month+"-"+day;
                if (i < 13) {
                    possibleDates += "%7C";
                }

                // increment the day of the month.
                currentDate.add(Calendar.DAY_OF_MONTH, 1);
            }


            play.libs.WS.url("http://www.when2meet.com/SaveNewEvent.php")
                    .setContentType("application/x-www-form-urlencoded; charset=utf-8")
                    .post("NewEventName=Meet up!&DateTypes=SpecificDates&PossibleDates="+possibleDates+"&NoEarlierThan=0&NoLaterThan=0").map(
                    new F.Function<WS.Response, Result>() {
                        public Result apply(WS.Response response) {
                            final CommonProfile profile = getUserProfile();
                            String netId = profile.getId();

                            // get the person's name from their netId.
                            String name = FingerHelper.getNameFromNetId(netId);

                            // create the recipient address according to whether they have shared their contact info.
                            String recipient;
                            if (name == null) {
                                recipient = netId + "@rice.edu";
                            }
                            else {
                                recipient = name+" <"+netId + "@rice.edu>";
                            }

                            // get the responses from the filled out form.
                            Proposition proposition = filledForm.get();

                            // get the created when2meet url.
                            String responseBody = response.getBody();
                            String[] splitResponseBody = responseBody.split("'");
                            String urlArgument = splitResponseBody[1];
                            String when2MeetUrl = "http://www.when2meet.com"+urlArgument;

                            // create the invitation in the database.
                            Invitation invitation = Invitation.create(proposition.name, netId+"@rice.edu", proposition.email, when2MeetUrl, proposition.location, proposition.topic);

                            MailerAPI mail = play.Play.application().plugin(MailerPlugin.class).email();
                            mail.setSubject("You have received a Rice Meetup Invite!");
                            mail.setRecipient(proposition.email);
                            mail.setFrom(recipient);
                            mail.sendHtml("<html>\n" +
                                    "\t<h1 style=\"color: #333\">Hello "+proposition.name+"!</h1>\n" +
                                    "\t<h1 style=\"color: #333\">I would love to buy you a drink from <i>"+proposition.location+"</i> sometime and talk about <i>"+proposition.topic+"</i>.</h1>\n" +
                                    "\t<button style=\"display: inherit;background-color: #47a447;padding: 10px 20px;margin:10px 0;border-radius: 4px;border: 1px solid transparent;font-size: 14px;cursor: pointer;\"><a href=\""+invitation.getAcceptURL()+"\" style=\"color: #fff;text-decoration: none;\" href=\"\">Sounds great! When would you like to meet?</a></button>\n" +
                                    "\t<button style=\"display: inherit;background-color: #d9534f;padding: 10px 20px;margin:10px 0;border-radius: 4px;border: 1px solid transparent;font-size: 14px;cursor: pointer;\"><a href=\""+invitation.getRejectURL()+"\" style=\"color: #fff;text-decoration: none;\" href=\"\">Sorry, I'm busy and can't meet! Maybe some other time.</a></button>\n" +
                                    "</html>" );

                            return ok();
                        }
                    }
            );
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
                    "\t<h2 style=\"color: #333\">Here is a <a href=\""+invitation.getWhen2MeetURL()+"\">when2meet</a> to schedule your meet up.</h2>\n" +
                    "\t<h2 style=\"color: #333\">Hope things work out!</h2>\n" +
                    "</html>");

            return ok(response.render("You have successfully accepted your invitation to meet up!", true, invitation.getWhen2MeetURL()));
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

            return ok(response.render("You have successfully rejected your invitation to meet up...", false, invitation.getWhen2MeetURL()));
        }
    }
}
