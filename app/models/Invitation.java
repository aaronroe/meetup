package models;

import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.List;

import static play.mvc.Controller.request;

/**
 * Class that represents an invitation.
 */
@Entity
public class Invitation extends Model {

    /**
     * Id of the invitation in the database.
     */
    @Id
    private long id;

    /**
     * Code for verifying invitations.
     */
    private String verificationCode;

    /**
     * The name of the inviterEmail.
     */
    private String inviterName;

    /**
     * The email of the inviterEmail.
     */
    private String inviterEmail;

    /**
     * The email of the invitedEmail.
     */
    private String invitedEmail;

    /**
     * URL for the when2meet for this invitation.
     */
    private String when2MeetURL;

    /**
     * The location of the invitation.
     */
    private String location;

    /**
     * The location of the topic.
     */
    private String topic;

    /**
     * Whether or not a user has responded to the invitation yet.
     */
    private boolean responded;

    /**
     * Finder for invitations.
     */
    public static Finder<Long, Invitation> find = new Finder<Long, Invitation>(Long.class, Invitation.class);

    /**
     * Default constructor for invitation.
     * @param inviterName Name of the inviter.
     * @param inviterEmail Email of the inviter.
     * @param invitedEmail Email of the invited.
     * @param when2MeetURL URL for the when2meet.
     * @param location The location for the invitation.
     * @param topic The topic for the invitation.
     * @param verificationCode The verification code to accept/reject the invitation.
     */
    public Invitation(String inviterName, String inviterEmail, String invitedEmail, String when2MeetURL, String location, String topic, String verificationCode) {
        this.verificationCode = verificationCode;
        this.responded = false;
        this.inviterName = inviterName;
        this.inviterEmail = inviterEmail;
        this.invitedEmail = invitedEmail;
        this.when2MeetURL = when2MeetURL;
        this.location = location;
        this.topic = topic;
    }

    /**
     * Gets the invitation according to what its verification code is.
     * @return The invitation corresponding to the code, null otherwise.
     */
    public static Invitation getInvitationByVerificationCode(String verificationCode) {
        List<Invitation> invitationList = Invitation.find.all();
        for (Invitation invitation : invitationList) {
            if (invitation.getVerificationCode().equals(verificationCode)) {
                return invitation;
            }
        }
        return null;
    }

    /**
     * Generates a random verification code that is unique.
     * @return A random verification code that is unique.
     */
    public static String generateVerificationCode() {
        SecureRandom random = new SecureRandom();

        String code = null;
        boolean foundUnique = false;
        while (!foundUnique) {
            code = new BigInteger(130, random).toString(32);
            if(isCodeUnique(code)) {
                foundUnique = true;
            }
        }
        return code;
    }

    /**
     * Gets whether or not a code is unique by checking it against the database.
     * @param code The code to check.
     * @return Whether or not the code is unique in the database.
     */
    private static boolean isCodeUnique(String code) {
        List<Invitation> invitationList = Invitation.find.all();
        for (Invitation invitation : invitationList) {
            if (invitation.getVerificationCode().equals(code)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Static method that creates an invitation and encapsulates saving.
     * @param inviterName The name of the inviter.
     * @param inviterEmail The email of the person sending the invitation.
     * @param invitedEmail The email of the person receiving the invitation.
     * @param when2MeetURL The URL for the when2meet for this invite.
     * @param location The location for the invitation.
     * @param topic The topic for the invitation.
     * @return The newly created invitation.
     */
    public static Invitation create(String inviterName, String inviterEmail, String invitedEmail, String when2MeetURL, String location, String topic) {
        Invitation invitation = new Invitation(inviterName, inviterEmail, invitedEmail, when2MeetURL, location, topic, Invitation.generateVerificationCode());
        invitation.save();

        return invitation;
    }

    /**
     * Getter for verification code.
     * @return Gets the verification code for this invitation.
     */
    public String getVerificationCode() {
        return verificationCode;
    }

    /**
     * Getter for the location for the invitation.
     * @return The location for this invitation.
     */
    public String getLocation() {
        return location;
    }

    /**
     * Getter for the name of the person inviting.
     * @return The name of the person inviting.
     */
    public String getInviterName() {
        return inviterName;
    }

    /**
     * Getter for the email of the person who initiated the invite.
     * @return The email of the person who initiated the invite.
     */
    public String getInviterEmail() {
        return inviterEmail;
    }

    /**
     * Getter for the email of the person who is the target of the invite.
     * @return The person who is the one being invitedEmail.
     */
    public String getInvitedEmail() {
        return invitedEmail;
    }

    /**
     * Getter for the when2meet url.
     * @return The when2meet url for this invitation.
     */
    public String getWhen2MeetURL() {
        return when2MeetURL;
    }

    /**
     * Getter for the topic of this invitation.
     * @return The topic of this invitation.
     */
    public String getTopic() {
        return topic;

    }

    /**
     * Gets the URL to accept this invitation.
     * @return The URL to accept this invitation.
     */
    public String getAcceptURL() {
        return controllers.routes.Application.acceptInvitation(getVerificationCode()).absoluteURL(request());
    }

    /**
     * Gets the URL to reject this invitation.
     * @return The URL to reject this invitation.
     */
    public String getRejectURL() {
        return controllers.routes.Application.rejectInvitation(getVerificationCode()).absoluteURL(request());
    }

    /**
     * Getter for whether or not this invitation has been responded to.
     * @return Gets whether or not this invitation has been responded to.
     */
    public boolean isResponded() {
        return responded;
    }

    /**
     * Setter for responded.
     * @param responded What the new value of responded should be.
     */
    public void setResponded(boolean responded) {
        this.responded = responded;
        this.save();
    }
}
