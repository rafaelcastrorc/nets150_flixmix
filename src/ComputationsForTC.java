import com.sun.tools.corba.se.idl.InterfaceGen;

import java.util.*;

/**
 * Created by rafaelcastro on 4/11/17.
 * Using Triadic closure, and membership closure, creates a graph and gives movie recommendations
 */
 class ComputationsForTC {
    Graph<String> graph;
    //All movies with the users who rated them
    private HashMap<String, MovieRating> ratedMovies = new HashMap<>();
    // movieID -> Map.Entry={movie title, list of genres}
    private HashMap<String, Map.Entry<String, List<String>>> movies;
    // userID -> List<Entry={movieID, rating by user}>
    private  HashMap<String, List<Map.Entry<String, Double>>> ratings;


    /**
     * Class constructor. Generates the graph based on the data collected by the Parser
     * @param movies
     * @param ratings
     *
     */

    ComputationsForTC(HashMap<String, Map.Entry<String, List<String>>> movies, HashMap<String, List<Map.Entry<String, Double>>> ratings) {
        this.graph = new Graph<>();
        this.movies = movies;
        this.ratings = ratings;
        createGraph();
        System.out.println(getFavoriteMoviesOfUser("u2"));
        System.out.println(getAllPossibleSuggestion("m2"));
    }


    /**
     * Generates a weighted directed graph.
     */

    private void createGraph() {
        for (String userID : ratings.keySet()) {
            List<Map.Entry<String, Double>> listOfRatedMovies = ratings.get(userID);
            for ( Map.Entry<String, Double> entry : listOfRatedMovies){
                String movieID = entry.getKey();
                Double rating = entry.getValue();
                //Adds edge from user to movie
                graph.addEdge(userID, movieID, Optional.of(rating));

                //If there is no MovieRating obj for the current movie
                if (ratedMovies.get(movieID) == null) {
                    MovieRating movieRating = new MovieRating(movieID);
                    //Adds the rating of the user to the movie
                    movieRating.addNewRating(userID, rating);
                    //Adds the movieRating obj to the map
                    addMovieRatingToMap(movieID, movieRating);
                }
                else {
                   MovieRating curr =  getMovieRatingObj(movieID);
                   curr.addNewRating(userID, rating);
                    addMovieRatingToMap(movieID, curr);

                }

            }
        }
    }


    /**
     * Adds a MovieRatingObj to a collection that stores all MovieRating objects
     * @param movieID
     * @param movieRatingObj - MovieRating object that wants to be added
     */
    private void addMovieRatingToMap(String movieID, MovieRating movieRatingObj) {
        ratedMovies.put(movieID, movieRatingObj);

    }

    /**
     * Gets the MovieRating object of the given movieID
     * @param movieID
     * @return MovieRating object or null if there is no such object
     */
    private MovieRating getMovieRatingObj(String movieID) {
       return ratedMovies.get(movieID);
    }

    //Todo: do suggestions based on genre
    //Todo: Do suggestions based on the user instead of a single movie

    //If a movie really liked the given movie, gets other users who also liked the movie and the movies they like

    /**
     * Based on a movie that a user likes, get other movies of any genre that users who like this movie also like.
     * @param movieID - Movie that the user likes
     * @return ArrayList with movie recommendations, sorted in descending order based on popularity among other users
     */
    protected ArrayList<String> getAllPossibleSuggestion(String movieID) {
        MovieRating movieRating = getMovieRatingObj(movieID);
        TreeSet<String> orderedUsers;
        orderedUsers = movieRating.getStronglyConnectedUsers();
        HashMap<String, Integer> numOfUsersWhoLikedItToMovie = new HashMap<>();
        for (String userID : orderedUsers) {
            for (String currMovieID : getFavoriteMoviesOfUser(userID)) {
                if (!currMovieID.equals(movieID)) {
                    if (!numOfUsersWhoLikedItToMovie.containsKey(currMovieID)) {
                        numOfUsersWhoLikedItToMovie.put(currMovieID, 1);
                    } else {
                        int currUsersWhoLikeMovie = numOfUsersWhoLikedItToMovie.get(currMovieID);
                        numOfUsersWhoLikedItToMovie.put(currMovieID, currUsersWhoLikeMovie + 1);
                    }
                }
            }
        }

        return  getSortedListOfMovies(numOfUsersWhoLikedItToMovie);
    }

    /**
     * Helper method that sorts the results based on popularity among users.
     * @param map
     * @return sorted list of movie suggestions.
     */


    private ArrayList<String> getSortedListOfMovies(HashMap<String, Integer> map) {
        TreeMap<Integer, Set<String>>  orderedMap = new TreeMap<>(Collections.reverseOrder());
        for (String movieID : map.keySet()) {
            int curr = map.get(movieID);
            if (!orderedMap.containsKey(curr)) {
                Set<String> movies = new TreeSet<>();
                movies.add(TriadicClosureParser.getMovieTitle(movieID));
                orderedMap.put(curr, movies);
            }
            else {
                Set currMovies = orderedMap.get(curr);
                currMovies.add(TriadicClosureParser.getMovieTitle(movieID));
                orderedMap.put(curr, currMovies);

            }

        }
        ArrayList<String> result = new ArrayList<>();
        for (Set<String> movies : orderedMap.values()) {
            for (String movie : movies) {
                result.add(movie);
            }
        }
        return result;
    }

    /**
     * Gets all the movies that the user rated 4.0 and above.
     * @param userID
     * @return List of the favorite movies of the user, in descending order
     */
    private ArrayList<String> getFavoriteMoviesOfUser(String userID) {
        Set<String> moviesRatedByUser = graph.outNeighbors(userID);
        TreeMap<Double, Set<String>>  ratingToMovieID = new TreeMap<>(Collections.reverseOrder());

        for (String movieID : moviesRatedByUser) {
            Double userRating = TriadicClosureParser.getRatingOfMovie(userID, movieID);
            if (userRating >= 4.0) {

                if (!ratingToMovieID.containsKey(userRating)) {
                    Set<String> movies = new TreeSet<>();
                    movies.add(movieID);
                    ratingToMovieID.put(userRating, movies);
                }
                else {
                    Set movies = ratingToMovieID.get(userRating);
                    movies.add(movieID);
                    ratingToMovieID.put(userRating, movies);

                }

            }
        }
        ArrayList<String> result = new ArrayList<>();
        for (Set<String> movies : ratingToMovieID.values()) {
            for (String movie : movies) {
                result.add(movie);
            }
        }
        return result;
    }


    /**
     * MovieRating object. Each movieID has a movie rating obj, that stores all users who have rated the given movie
     */

    class MovieRating {
        private String movieID;
        private TreeMap<Double, Set<String>>  ratingToUser = new TreeMap<>(Collections.reverseOrder());

        MovieRating(String movieID) {
            this.movieID = movieID;
        }

        /**
         * Add a new user rating to the given movie.
         * @param userID
         * @param rating - Double
         */
        protected void addNewRating(String userID, Double rating) {
            if (!ratingToUser.containsKey(rating)) {
                Set<String> users = new TreeSet<>();
                users.add(userID);
                ratingToUser.put(rating, users);
            }
            else {
                Set currentUsers = ratingToUser.get(rating);
                currentUsers.add(userID);
                ratingToUser.put(rating, currentUsers);

            }

        }

        /**
         * Gets all users who are strongly connected to the movie. That is, that they have rated the movie 4.0 or above
         * @return Ordered Set of users who like the movie, in descending order
         */
        protected TreeSet<String> getStronglyConnectedUsers () {
            TreeSet<String> users = new TreeSet<>(Collections.reverseOrder());
            for (Double rating : ratingToUser.keySet()) {
                if (rating >= 4.0) {
                    for (String user : ratingToUser.get(rating)) {
                        users.add(user);
                    }

                }
            }
            return users;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            MovieRating that = (MovieRating) o;

            return movieID != null ? movieID.equals(that.movieID) : that.movieID == null;
        }

        @Override
        public int hashCode() {
            return movieID != null ? movieID.hashCode() : 0;
        }
    }








}
