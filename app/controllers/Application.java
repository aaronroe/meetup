package controllers;

import play.*;
import play.mvc.*;

import views.html.*;

public class Application extends Controller {

    public static Result index() {
        return ok(index.render());
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
                "{\"name\": \"Sociology\"}," +
                "{\"name\": \"Statistics\"}" +
                "]");
    }

}
