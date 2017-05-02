import java.io.File;
import java.io.FileNotFoundException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

class TriadicClosureParser {

    protected static HashMap<String, Map.Entry<String, List<String>>> getMovies() {
        return movies;
    }

    protected static HashMap<String, List<Map.Entry<String, Double>>> getRatings() {
        return ratings;
    }

    // movieID -> Map.Entry={movie title, list of genres}
    private static HashMap<String, Map.Entry<String, List<String>>> movies = new HashMap<>();
    // userID -> List<Entry={movie title, rating by user}>
    private static HashMap<String, List<Map.Entry<String, Double>>> ratings = new HashMap<>();

    /**
     * Parses the movies.dat file specified by the
     * {@code moviePath}.
     * @param moviePath the movie path to the movies.dat file
     */
    static void parseMovies(String moviePath) {
        try {
            Scanner sc = new Scanner(new File(moviePath));
            String line = sc.nextLine();
            while (true) {
                String[] movieInfo = line.split("::");			// dataset uses "::" to separate fields
                int movieID = -1;
                try {
                    movieID = Integer.parseInt(movieInfo[0]);
                }
                catch (NumberFormatException e) {
                    // let the movieID be -1 to indicate error with parsing
                }
                String movie = movieInfo[1];
                int indexOfParen = movie.indexOf('(');				// all movies have parenthesis in title for year released
                if (indexOfParen != -1) {
                    movie = movie.substring(0, indexOfParen - 1);
                }
                // put 'The' in front of the movie title instead of behind
                if (movie.contains(", The")) {
                    int indexOfThe = movie.indexOf(", The");
                    movie = "The " + movie.substring(0, indexOfThe);
                }
                String genres = movieInfo[2];
                String[] genresArray = genres.split("\\|");			// dataset uses "|" to separate genres of movie
                Map.Entry<String, List<String>> entry =
                        new AbstractMap.SimpleEntry<>(movie, Arrays.asList(genresArray));

                movies.put("m" + movieID, entry);
                System.out.println(movieID + "::" + movie + "::" + Arrays.asList(genresArray).toString());
                if (!sc.hasNextLine()) {
                    break;
                }
                line = sc.nextLine();
            }
            sc.close();
        } catch (FileNotFoundException e) {
            System.out.println("Movies file not found.");
        }
    }

    /** Parses the ratings.dat file specified by the
     * {@code ratingsPath}.
     * @param ratingsPath the path to the ratings.dat file.
     */
    static void parseRatings(String ratingsPath) {
        try {
            Scanner sc = new Scanner(new File(ratingsPath));
            String line = sc.nextLine();
            while (true) {
                String[] ratingsInfo = line.split("::");				// dataset uses "::" to separate fields
                int userID = -1;
                int movieID = -1;
                Double rating = -1.0;
                String movie = "";
                try {
                    userID = Integer.parseInt(ratingsInfo[0]);
                    movieID = Integer.parseInt(ratingsInfo[1]);
                   // movie = movies.get(movieID).getKey();
                    rating = Double.parseDouble(ratingsInfo[2]);
                }
                catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                if (!ratings.containsKey("u"+userID)) {
                    List<Map.Entry<String, Double>> list = new ArrayList<Map.Entry<String, Double>>();
                    Map.Entry<String, Double> entry = new AbstractMap.SimpleEntry<String, Double>("m"+movieID, rating);
                    list.add(entry);
                    ratings.put("u" + userID, list);
                }
                else {
                    Map.Entry<String, Double> entry = new AbstractMap.SimpleEntry<String, Double>("m" + movieID, rating);
                    ratings.get("u"+userID).add(entry);
                }
				//System.out.println(userID + "::" + movieID + "::" + rating);
                if (!sc.hasNextLine()) {
                    break;
                }
                line = sc.nextLine();
            }
            sc.close();
        }
        catch (FileNotFoundException e) {
            System.out.println("Ratings file not found.");
        }
    }


   protected static Double getRatingOfMovie(String userID, String movieID) {
       List<Map.Entry<String, Double>> listOfRatedMovies = ratings.get(userID);
       for (Map.Entry<String, Double> entry : listOfRatedMovies) {
           String currMovie = entry.getKey();
           Double rating = entry.getValue();
           if (currMovie.equals(movieID)) {
               return rating;
           }

       }
       return null;
   }

   protected static String getMovieTitle(String movieID) {
       return movies.get(movieID).getKey();

   }

}
