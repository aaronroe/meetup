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
     * The email of the inviter.
     */
    private String inviter;

    /**
     * The email of the invited.
     */
    private String invited;

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
     */
    public Invitation(String inviter, String invited, String location, String topic, String verificationCode) {
        this.verificationCode = verificationCode;
        this.responded = false;
        this.inviter = invited;
        this.invited = invited;
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
     * @param inviter The email of the person sending the invitation.
     * @param invited The email of the person receiving the invitation.
     * @param location The location for the invitation.
     * @param topic The topic for the invitation.
     * @return The newly created invitation.
     */
    public static Invitation create(String inviter, String invited, String location, String topic) {
        Invitation invitation = new Invitation(inviter, invited, location, topic, Invitation.generateVerificationCode());
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
