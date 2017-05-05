import java.util.*;

/**
 * Created by saradwyer on 5/1/17.
 */
public class CosineSimilarity {

    private static Scanner reader;
    private static String userName;
    private static String userText;
    private static Document similar;
    private static HashMap<String, String> userToPreferences;

    public static ArrayList<String> main(String user, double[] ratings) {
        userToPreferences = new HashMap<>();
        if (userToPreferences.size() == 0) {
            formatData();
        }
        createUserProfile(user, ratings);
        similar = vectorSpaceTester();

        ArrayList<String> suggestions = new ArrayList<>();

        //get the highest rated movies from the similar user;
        HashMap<String, Integer> f = similar.getFrequency();


        //add up to 10 movie suggestions
        int i = 0;
        for (Map.Entry<String, Integer> e : f.entrySet()) {
            if (i == 10) {
                break;
            }
            if (e.getValue() >= 2) {
                suggestions.add(e.getKey());
                i++;
            }
        }

        return suggestions;
    }


    public static void createUserProfile(String user, double[] ratings) {
        String[] movies = new String[9];
        movies[0] = "Beauty_and_the_Beast";
        movies[1] = "Grease";
        movies[2] = "Star_Trek_V:_The_Final_Frontier";
        movies[3] = "How_to_Be_a_Player";
        movies[4] = "Emma";
        movies[5] = "Hunt_for_Red_October";
        movies[6] = "George_of_the_Jungle";
        movies[7] = "Air_Bud";
        movies[8] = "Basic_Instinct";

        StringBuilder text = new StringBuilder();
        for (int i = 0; i < ratings.length; i++) {
            double rate = ratings[i];
            String m = movies[i];
            while (rate > 0) {
                text.append(" " + m);
                rate--;
            }
        }
        userText = text.toString();
    }

    public static String getSimilarUserName() {
        return similar.toString();
    }


    private static void formatData() {

        //Use the already inputted/formatted data from Triadic Closure Parser

        // movieID -> Map.Entry={movie title, list of genres} ***Don't need this at the moment - might use to customize
        //suggestions
        HashMap<Integer, Map.Entry<String, List<String>>> movieID = TriadicClosureParser.getMovieID();

        // userID -> List<Entry={movie title, rating by user}>
        HashMap<Integer, List<Map.Entry<String, Double>>> ratings = TriadicClosureParser.getRatings();

        if (movieID.size() == 0 || ratings.size() == 0) {
            System.out.println("Error reading in data- ensure TriadicClosureParser has been run");
        }

        /*The data will be stored with the format "UserID->Movie Preferences", where the prevalence of the movie word
         * corresponds to the user rating. For example, if User 1 rated the movie "Movie1" 2/10, "Movie2 "1/10", and
         * Movie3 "0/10", the User's info would be: <"User 1", "Movie1 Movie1 Movie2"  */

        StringBuilder text = new StringBuilder();
        String userID;
        //loop through each user
        for (Map.Entry<Integer, List<Map.Entry<String, Double>>> e : ratings.entrySet()) {
            userID = "" + e.getKey();

            List<Map.Entry<String, Double>> preferences = e.getValue();

            for (int i = 0; i < preferences.size(); i++) {
                Map.Entry<String, Double> info = preferences.get(i);

                //format the movie string to remove white spaces (that way it counts as a single uniqueword)
                String movie = info.getKey();
                movie = movie.replace(' ', '_');

                Integer rating = (info.getValue()).intValue();
                while (rating != 0) {
                    text.append(" " + movie);
                    rating--;
                }
            }

            userToPreferences.put(userID, text.toString());
        }
    }

    public static Document vectorSpaceTester() {

        ArrayList<Document> documents = new ArrayList<Document>();

        Document masterUser = new Document(userName, userText);
        documents.add(masterUser);

        //add all of the users to the Documents set
        for (Map.Entry<String, String> u : userToPreferences.entrySet()) {
            String docName =  u.getKey(); //name the doc with the username
            Document d = new Document(docName, u.getValue());
            documents.add(d);
        }

        Corpus corpus = new Corpus(documents);

        VectorSpaceModel vectorSpace = new VectorSpaceModel(corpus);

        Document similarUser = null;
        double cosineSimilarity = Double.NEGATIVE_INFINITY;
        double curr = 0.0;
        for (int i = 0; i < documents.size(); i++) {
            Document otherUser = documents.get(i);
            curr = vectorSpace.cosineSimilarity(masterUser, otherUser);
            if (curr > cosineSimilarity) {
                similarUser = otherUser;
                cosineSimilarity = curr;
            }
        }

        if (similarUser == null) {
            return null;
        }

        return similarUser;


    }
}
