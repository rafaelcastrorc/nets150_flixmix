import java.io.File;
import java.io.FileNotFoundException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class TriadicClosureParser {

    // movieID -> Map.Entry={movie title, list of genres}
    private static HashMap<Integer, Map.Entry<String, List<String>>> movies = new HashMap<Integer, Map.Entry<String, List<String>>>();
    // userID -> List<Entry={movie title, rating by user}>
    private static HashMap<Integer, List<Map.Entry<String, Double>>> ratings = new HashMap<Integer, List<Map.Entry<String, Double>>>();

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
                        new AbstractMap.SimpleEntry<String, List<String>>(movie, Arrays.asList(genresArray));

                movies.put(movieID, entry);
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
                    movie = movies.get(movieID).getKey();
                    rating = Double.parseDouble(ratingsInfo[2]);
                }
                catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                if (!ratings.containsKey(userID)) {
                    List<Map.Entry<String, Double>> list = new ArrayList<Map.Entry<String, Double>>();
                    Map.Entry<String, Double> entry = new AbstractMap.SimpleEntry<String, Double>(movie, rating);
                    list.add(entry);
                    ratings.put(userID, list);
                }
                else {
                    Map.Entry<String, Double> entry = new AbstractMap.SimpleEntry<String, Double>(movie, rating);
                    ratings.get(userID).add(entry);
                }
//				System.out.println(userID + "::" + movieID + "::" + rating);
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

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("usage is: ");
            System.out.println("     java Parser /path/to/data/movies.dat /path/to/data/ratings.dat");
            System.out.println("have you setup your \"Run Configurations\"?");
            return;
        }
        TriadicClosureParser.parseMovies(args[0]);
        TriadicClosureParser.parseRatings(args[1]);
    }

    public static HashMap<Integer, Map.Entry<String, List<String>>> getMovieID() {
        return movies;
    }

    public static HashMap<Integer, List<Map.Entry<String,Double>>> getRatings() {
        return ratings;
    }
}
