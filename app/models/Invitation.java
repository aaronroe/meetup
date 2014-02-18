package models;

import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;

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
        this.verificationCode = "";
        this.responded = false;
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

}
