package models.forms;

import play.data.validation.Constraints.Required;

/**
 * Represents the form for a proposition.
 */
public class Proposition {
    public String name;
    public String email;
    public String location;
    public String topic;

    public String validate() {
        if (name.equals("")) {
            return "You must select a student.";
        }

        return null;
    }
}
