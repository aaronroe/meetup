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
import java.util.Random;

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
                String month = String.format("%02d", currentDate.get(Calendar.MONTH) + 1); // months indexed at 0.
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
                    .post("NewEventName=Meet up!&DateTypes=SpecificDates&PossibleDates="+possibleDates+"&NoEarlierThan=9&NoLaterThan=0").map(
                    new F.Function<WS.Response, Result>() {
                        public Result apply(WS.Response response) {
                            final CommonProfile profile = getUserProfile();
                            String netId = profile.getId();

                            // get the person's name from their netId.
                            String inviterName = FingerHelper.getNameFromNetId(netId);

                            // get the responses from the filled out form.
                            Proposition proposition = filledForm.get();

                            // get the created when2meet url.
                            String responseBody = response.getBody();
                            String[] splitResponseBody = responseBody.split("'");
                            String urlArgument = splitResponseBody[1];
                            String when2MeetUrl = "http://www.when2meet.com"+urlArgument;

                            // create the invitation in the database.
                            Invitation invitation = Invitation.create(inviterName, proposition.name, netId+"@rice.edu", proposition.email, when2MeetUrl, proposition.location, proposition.topic);

                            // generate a random number to put at the bottom of the mail.
                            Random random = new Random();
                            int randomInt = random.nextInt(2147483647);

                            MailerAPI mail = play.Play.application().plugin(MailerPlugin.class).email();
                            mail.setSubject("You have received a Rice Meetup Invite!");
                            mail.setRecipient(invitation.getInvitedEmail());
                            mail.setFrom(invitation.getInviterEmail());
                            mail.sendHtml("<html>\n" +
                                    "\t<h1 style=\"color: #333\">Hello "+proposition.name+"!</h1>\n" +
                                    "\t<h1 style=\"color: #333\">I would love to buy you a drink from <i>"+proposition.location+"</i> sometime and talk about <i>"+proposition.topic+"</i>.</h1>\n" +
                                    "\t<button style=\"display: inherit;background-color: #47a447;padding: 10px 20px;margin:10px 0;border-radius: 4px;border: 1px solid transparent;font-size: 14px;cursor: pointer;\"><a href=\""+invitation.getAcceptURL()+"\" style=\"color: #fff;text-decoration: none;\" href=\"\">Sounds great! When would you like to meet?</a></button>\n" +
                                    "\t<button style=\"display: inherit;background-color: #d9534f;padding: 10px 20px;margin:10px 0;border-radius: 4px;border: 1px solid transparent;font-size: 14px;cursor: pointer;\"><a href=\""+invitation.getRejectURL()+"\" style=\"color: #fff;text-decoration: none;\" href=\"\">Sorry, I'm busy and can't meet! Maybe some other time.</a></button>\n" +
                                    "\t<h4 style=\"color: #333\">Powered by <a href=\"http://meetup.riceapps.org\">Rice Meetup</a></h4>\n" +
                                    "\t<span style=\"color: #fff;;\">"+randomInt+"</span>" +
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
        if (invitation == null) {
            return ok(message.render("Whoooops!", "We can't find what you're looking for : ("));
        }
        else if (invitation.isResponded()) {
            return ok(message.render("You have already responded to this invitation!", "You have already responded to this invitation"));
        }
        else {
            // set the invitation as responded.
            invitation.setResponded(true);

            // generate a random number to put at the bottom of the mail.
            Random random = new Random();
            int randomInt = random.nextInt(2147483647);

            // send a confirmation email to the invited.
            MailerAPI mail = play.Play.application().plugin(MailerPlugin.class).email();
            mail.setSubject("You have accepted an invite to meetup!");
            mail.setRecipient(invitation.getInvitedEmail());
            mail.setFrom(invitation.getInviterEmail());
            mail.sendHtml("<html>\n" +
                    "\t<br />\n" +
                    "\t<h1 style=\"background-color: #5cb85c;display: inline;padding: .2em .6em .3em;font-weight: 700;color: #fff;text-align: center;border-radius: .25em;\">You have accepted an invite from "+invitation.getInviterName()+"!</h1>\n" +
                    "\t<h1 style=\"color: #333\">"+invitation.getInviterName()+" offered to buy you a drink from <i>"+invitation.getLocation()+"</i> in order to talk about <i>"+invitation.getTopic()+"</i>.</h1>\n" +
                    "\t<h2 style=\"color: #333\">Here is the <a href=\""+invitation.getWhen2MeetURL()+"\">when2meet</a> that you can use for scheduling. Alternatively you can just reply to this email to talk.</h2>\n" +
                    "\t<h2 style=\"color: #333\">Hope things work out!</h2>\n" +
                    "\t<h4 style=\"color: #333\">Powered by <a href=\"http://meetup.riceapps.org\">Rice Meetup</a></h4>\n" +
                    "\t<span style=\"color: #fff;;\">"+randomInt+"</span>" +
                    "</html>");

            randomInt = random.nextInt(2147483647);

            // send the notification email to the inviter.
            mail.setSubject("Your Rice Meetup invite has been accepted!");
            mail.setRecipient(invitation.getInviterEmail());
            mail.setFrom(invitation.getInvitedEmail());
            mail.sendHtml("<html>\n" +
                    "\t<br />\n" +
                    "\t<h1 style=\"background-color: #5cb85c;display: inline;padding: .2em .6em .3em;font-weight: 700;color: #fff;text-align: center;border-radius: .25em;\">"+invitation.getInvitedName()+" has accepted your invite!</h1>\n" +
                    "\t<h1 style=\"color: #333\">To jog your memory, you offered to buy a drink from <i>"+invitation.getLocation()+"</i> in order to talk about <i>"+invitation.getTopic()+"</i>.</h1>\n" +
                    "\t<h2 style=\"color: #333\">Here is a <a href=\""+invitation.getWhen2MeetURL()+"\">when2meet</a> to schedule your meet up.</h2>\n" +
                    "\t<h2 style=\"color: #333\">Hope things work out!</h2>\n" +
                    "\t<h4 style=\"color: #333\">Powered by <a href=\"http://meetup.riceapps.org\">Rice Meetup</a></h4>\n" +
                    "\t<span style=\"color: #fff;;\">"+randomInt+"</span>" +
                    "</html>");

            return ok(response.render("You have successfully accepted your invitation to meet up!", true, invitation.getWhen2MeetURL()));
        }
    }

    public static Result rejectInvitation(String verificationCode) {
        Invitation invitation = Invitation.getInvitationByVerificationCode(verificationCode);
        if (invitation == null) {
            return ok("Invalid url.");
        }
        else if (invitation.isResponded()) {
            return ok(message.render("You have already responded to this invitation!", "You have already responded to this invitation"));
        }
        else {
            // set the invitation as responded and send a notification email.
            invitation.setResponded(true);

            // generate a random number to put at the bottom of the mail.
            Random random = new Random();
            int randomInt = random.nextInt(2147483647);

            MailerAPI mail = play.Play.application().plugin(MailerPlugin.class).email();
            mail.setSubject("Your Rice Meetup invite has been declined...");
            mail.setRecipient(invitation.getInviterEmail());
            mail.setFrom(invitation.getInvitedEmail());
            mail.sendHtml("<html>\n" +
                    "\t<br />\n" +
                    "\t<h1 style=\"background-color: #d9534f;display: inline;padding: .2em .6em .3em;font-weight: 700;color: #fff;text-align: center;border-radius: .25em;\">Sadly, "+invitation.getInvitedName()+" has declined your invite...</h1>\n" +
                    "\t<h2 style=\"color: #333\">Sorry and hope things work out next time!</h2>\n" +
                    "\t<h4 style=\"color: #333\">Powered by <a href=\"http://meetup.riceapps.org\">Rice Meetup</a></h4>\n" +
                    "\t<span style=\"color: #fff;;\">"+randomInt+"</span>" +
                    "</html>");

            return ok(response.render("You have successfully rejected your invitation to meet up...", false, invitation.getWhen2MeetURL()));
        }
    }
}
