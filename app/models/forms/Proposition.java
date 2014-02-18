package models.forms;

import play.data.validation.Constraints.Required;

/**
 * Represents the form for a proposition.
 */
public class Proposition {
    @Required
    public String name;
    public String email;
    public String location;
    public String topic;
}
