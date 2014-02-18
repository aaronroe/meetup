package models;

import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.List;
import java.util.UUID;

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
     * Whether or not a user has responded to the invitation yet.
     */
    private boolean responded;

    /**
     * Finder for invitations.
     */
    public Finder<Long, Invitation> find = new Finder<Long, Invitation>(Long.class, Invitation.class);

    /**
     * Default constructor for invitation.
     */
    public Invitation() {
        this.verificationCode = generateVerificationCode();
        this.responded = false;
    }

    /**
     * Generates a random verification code that is unique.
     * @return A random verification code that is unique.
     */
    private String generateVerificationCode() {
        SecureRandom random = new SecureRandom();

        String code = null;
        boolean foundUnique = false;
        while (foundUnique == false) {
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
    public boolean isCodeUnique(String code) {
        List<Invitation> invitationList = this.find.all();
        for (Invitation invitation : invitationList) {
            if (invitation.getVerificationCode().equals(code)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Static method that creates an invitation and encapsulates saving.
     * @return The newly created invitation.
     */
    public static Invitation create() {
        Invitation invitation = new Invitation();
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
     * Getter for whether or not this invitation has been responded to.
     * @return Gets whether or not this invitation has been responded to.
     */
    public boolean isResponded() {
        return responded;
    }
}
