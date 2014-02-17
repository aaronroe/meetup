package controllers;

import models.forms.Proposition;
import play.*;
import play.data.Form;
import play.mvc.*;

import org.pac4j.play.java.JavaController;
import org.pac4j.play.java.RequiresAuthentication;

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
            Proposition proposition = filledForm.get();
        }

        return redirect(controllers.routes.Application.success());
    }

    public static Result locationsJSON() {
        return ok("[" +
                "{\"name\": \"Brochstein\"}, " +
                "{\"name\": \"Coffeehouse\"}," +
                "{\"name\": \"Valhalla\"}," +
                "{\"name\": \"Willy's Pub\"}" +
                "]");
    }

    public static Result topicsJSON() {
        return ok("[" +
                "{\"name\": \"Ancient Mediterranean Civilizations\"}," +
                "{\"name\": \"Asian Studies\"}," +
                "{\"name\": \"Art History\"}," +
                "{\"name\": \"Classical Studies\"}," +
                "{\"name\": \"English\"}," +
                "{\"name\": \"French Studies\"}," +
                "{\"name\": \"German\"}," +
                "{\"name\": \"Hispanic Studies\"}," +
                "{\"name\": \"History\"}," +
                "{\"name\": \"Kinesiology\"}," +
                "{\"name\": \"Linguistics\"}," +
                "{\"name\": \"Medieval Studies\"}," +
                "{\"name\": \"Philosophy\"}," +
                "{\"name\": \"Religious Studies\"}," +
                "{\"name\": \"Slavic Studies\"}," +
                "{\"name\": \"Sport Management\"}," +
                "{\"name\": \"Visual & Dramatic Arts\"}," +
                "{\"name\": \"Anthropology\"}," +
                "{\"name\": \"Cognitive Sciences\"}," +
                "{\"name\": \"Economics\"}," +
                "{\"name\": \"Managerial Studies\"}," +
                "{\"name\": \"Mathematical Economic Analysis\"}," +
                "{\"name\": \"Policy Studies\"}," +
                "{\"name\": \"Political Science\"}," +
                "{\"name\": \"Psychology\"}," +
                "{\"name\": \"Sociology\"}," +
                "{\"name\": \"Women, Gender, and Sexuality\"}," +
                "{\"name\": \"School of Architecture\"}," +
                "{\"name\": \"Architectural Studies\"}," +
                "{\"name\": \"Architecture\"}," +
                "{\"name\": \"Music \"}," +
                "{\"name\": \"Astronomy\"}," +
                "{\"name\": \"Astrophysics\"}," +
                "{\"name\": \"Biochemistry & Cell Biology\"}," +
                "{\"name\": \"Biological Sciences\"}," +
                "{\"name\": \"Chemical Physics\"}," +
                "{\"name\": \"Chemistry\"}," +
                "{\"name\": \"Earth Science\"}," +
                "{\"name\": \"Ecology & Evolutionary Biology\"}," +
                "{\"name\": \"Environmental Science\"}," +
                "{\"name\": \"Mathematics\"}," +
                "{\"name\": \"Physics\"}," +
                "{\"name\": \"Bioengineering\"}," +
                "{\"name\": \"Chemical Engineering\"}," +
                "{\"name\": \"Civil & Environmental Engineering\"}," +
                "{\"name\": \"Computational & Applied Mathematics\"}," +
                "{\"name\": \"Computer Science\"}," +
                "{\"name\": \"Electrical Engineering\"}," +
                "{\"name\": \"Environmental Engineering Sciences\"}," +
                "{\"name\": \"Materials Science and Engineering\"}," +
                "{\"name\": \"Mechanical Engineering\"}," +
                "{\"name\": \"Statistics\"}," +
                "{\"name\": \"African Studies\"}," +
                "{\"name\": \"Anthropology\"}," +
                "{\"name\": \"Biochemistry & Cell Biology\"}," +
                "{\"name\": \"Business\"}," +
                "{\"name\": \"Computational & Applied Math\"}," +
                "{\"name\": \"Ecology & Evolutionary Biology\"}," +
                "{\"name\": \"Energy & Water Sustainability\"}," +
                "{\"name\": \"Financial Computation and Modeling\"}," +
                "{\"name\": \"Global Health Technologies\"}," +
                "{\"name\": \"Jewish Studies\"}," +
                "{\"name\": \"Mathematics\"}," +
                "{\"name\": \"Poverty, Justice, and Human Capabilities\"}," +
                "{\"name\": \"Programming\"}," +
                "{\"name\": \"Web Development\"}," +
                "{\"name\": \"Medicine\"}," +
                "{\"name\": \"Ethics\"}," +
                "{\"name\": \"Anime\"}," +
                "{\"name\": \"Movies\"}," +
                "{\"name\": \"Film\"}," +
                "{\"name\": \"Culture\"}," +
                "{\"name\": \"Entrepreneurship\"}," +
                "{\"name\": \"The Environment\"}," +
                "{\"name\": \"good restaurants\"}," +
                "{\"name\": \"Travel\"}," +
                "{\"name\": \"studying abroad\"}," +
                "{\"name\": \"whatever you feel like talking about!\"}," +
                "{\"name\": \"Relationships\"}," +
                "{\"name\": \"Sociology\"}" +
                "]");
    }

}
