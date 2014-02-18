package models.forms;

import play.data.validation.Constraints;
import play.data.validation.Constraints.Required;

/**
 * Represents the form for a proposition.
 */
public class Proposition {
    @Constraints.MaxLength(100)
    public String name;
    @Constraints.MaxLength(100)
    public String email;
    @Constraints.MaxLength(100)
    public String location;
    @Constraints.MaxLength(100)
    public String topic;

    public String validate() {
        if (name.equals("")) {
            return "You must select a student.";
        }

        return null;
    }
}
