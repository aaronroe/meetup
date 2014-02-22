package helper;

import org.apache.commons.net.FingerClient;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper class for getting data from Rice's finger server.
 */
public class FingerHelper {

    /**
     * Gets a student's name from their netId.
     * @param netId The netId to search.
     * @return The name in "firstname lastname" format. Returns null if the students netid did not provide a match or if
     *      the server is down.
     */
    public static String getNameFromNetId(String netId) {
        String name = null;

        FingerClient fingerClient = new FingerClient();

        // set timeout for ten seconds.
        fingerClient.setDefaultTimeout(10000);

        try {
            fingerClient.connect("rice.edu", 79);
            String fingerResponse = fingerClient.query(false, netId);

            // find the name in the response.
            Pattern p = Pattern.compile("name: (.*)\n");
            Matcher m = p.matcher(fingerResponse);
            boolean nameFound = m.find();

            // if the name is found, then get the match.
            if (nameFound) {
                name = m.group(1);
            }

            // name should now be "lastname, firstname middle name"
            String[] splitName = name.split(", ");
            String lastName = splitName[0];
            String firstAndMiddleName = splitName[1];
            String firstName = firstAndMiddleName.split(" ")[0];

            name = firstName +  " " + lastName;

            // disconnect from the server.
            fingerClient.disconnect();
        } catch (IOException e) {
            // error connecting to server.
            return name;
        }

        return name;
    }

}
