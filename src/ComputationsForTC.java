import java.util.*;

/**
 * Created by rafaelcastro on 4/11/17.
 * Using Triadic closure, and membership closure, creates a graph and gives movie recommendations
 */
 class ComputationsForTC {
    private Graph<String> graph;
    //All movies with the users who rated them
    private HashMap<String, MovieRating> ratedMovies = new HashMap<>();
    // userID -> List<Entry={movieID, rating by user}>
    private  HashMap<String, List<Map.Entry<String, Double>>> ratings;


    /**
     * Class constructor. Generates the graph based on the data collected by the Parser
     * @param ratings  - Rating hashmap obtained from the parser
     *
     */

    ComputationsForTC(HashMap<String, List<Map.Entry<String, Double>>> ratings) {
        this.graph = new Graph<>();
        this.ratings = ratings;

        createGraph();
        System.out.println("Graph created");
        System.out.println(System.currentTimeMillis());

        System.out.println("Results");



        System.out.println(getFavoriteMoviesOfUser("u1"));
        System.out.println(getAllPossibleSuggestion("m4", true));
        System.out.println(getSuggestionsBasedOnGenre("m4"));
        System.out.println(System.currentTimeMillis());


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
     * @param movieID - the id of the movie you are looking for
     * @param movieRatingObj - MovieRating object that wants to be added
     */
    private void addMovieRatingToMap(String movieID, MovieRating movieRatingObj) {
        ratedMovies.put(movieID, movieRatingObj);

    }

    /**
     * Gets the MovieRating object of the given movieID
     * @param movieID - id of the movie you are looking for
     * @return MovieRating object or null if there is no such object
     */
    private MovieRating getMovieRatingObj(String movieID) {
       return ratedMovies.get(movieID);
    }


    /**
     * Limits the output to just the top n results
     * @param list - list that contains the result.
     * @param n - number of results that you want to display
     * @return ArrayList with n movie recommendations, sorted in descending order based on popularity among other users
     */
    protected ArrayList<String> getTopNResults(ArrayList<String> list, int n) {
        ArrayList<String> result = new ArrayList<>();
        int i = 0;
        for (String item : list) {
            if (i == n) {
                break;
            }
            result.add(item);
            i++;
        }
        return result;
    }


    /**
     * Based on a movie that a user likes, get other movies (of the same genre) that users who like this movie also like.
     * @param movieID - Movie that the user likes
     * @return ArrayList with movie recommendations, sorted in descending order based on popularity among other users
     */
    protected ArrayList<String> getSuggestionsBasedOnGenre(String movieID) {
        ArrayList<String> setOfMovies = getAllPossibleSuggestion(movieID, false);
        List<String> genresList = TriadicClosureParser.getMovieCategory(movieID);
        ArrayList<String> result = new ArrayList<>();


        for (String movie : setOfMovies) {
            List<String> genresListCurrMovie = TriadicClosureParser.getMovieCategory(movie);
            for (String genre : genresList) {
                if (genresListCurrMovie.contains(genre)) {
                    result.add(TriadicClosureParser.getMovieTitle(movie));
                    break;
                }
            }

        }
        return result;
    }

    /**
     * Based on a user, find all movies that the user currently likes, and suggest new movies based on other users who also like the movies the user likes
     * @param userID - id of the user
     * @return ArrayList with movie recommendations, sorted in descending order based on popularity among other users
     */
    protected ArrayList<String> getSuggestionsBasedOnUser(String userID) {
        //Todo: Do suggestions based on the user instead of a single movie
        return null;
    }


    //If a movie really liked the given movie, gets other users who also liked the movie and the movies they like

    /**
     * Based on a movie that a user likes, get other movies of any genre that users who like this movie also like.
     * @param movieID - Movie that the user likes
     * @param getMovieNames - If true, return the list with the movie names rather than the movieIDs
     * @return ArrayList with movie recommendations, sorted in descending order based on popularity among other users
     */
    protected ArrayList<String> getAllPossibleSuggestion(String movieID, boolean getMovieNames) {
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

        return  getSortedListOfMovies(numOfUsersWhoLikedItToMovie, getMovieNames);
    }

    /**
     * Helper method that sorts the results based on popularity among users.
     * @param map - Map with movies to number of users who liked the movie
     * @param getMovieNames - if true,  gets the names of the movies, instead of their movieIDs
     * @return sorted list of movie suggestions.
     */
    private ArrayList<String> getSortedListOfMovies(HashMap<String, Integer> map, boolean getMovieNames) {
        TreeMap<Integer, Set<String>>  orderedMap = new TreeMap<>(Collections.reverseOrder());
        for (String movieID : map.keySet()) {
            int curr = map.get(movieID);
            if (!orderedMap.containsKey(curr)) {
                Set<String> movies = new TreeSet<>();
                if (getMovieNames) {
                    movies.add(TriadicClosureParser.getMovieTitle(movieID));
                }
                else {
                    movies.add(movieID);
                }
                orderedMap.put(curr, movies);
            }
            else {
                Set<String> currMovies = orderedMap.get(curr);
                if (getMovieNames) {
                    currMovies.add(TriadicClosureParser.getMovieTitle(movieID));
                }
                else {
                    currMovies.add(movieID);

                }
                orderedMap.put(curr, currMovies);

            }

        }
        ArrayList<String> result = new ArrayList<>();
        for (Set<String> movies : orderedMap.values()) {
            result.addAll(movies);
        }
        return result;
    }

    /**
     * Gets all the movies that the user rated 4.0 and above.
     * @param userID - User we want to get favorite movies from
     * @return List of the favorite movies of the user, in descending order
     */
    private ArrayList<String> getFavoriteMoviesOfUser(String userID) {
        Set<String> moviesRatedByUser = graph.outNeighbors(userID);
        TreeMap<Double, Set<String>>  ratingToMovieID = new TreeMap<>(Collections.reverseOrder());

        for (String movieID : moviesRatedByUser) {
            Double userRating;
            userRating = TriadicClosureParser.getRatingOfMovie(userID, movieID);
            if (userRating >= 4.0) {
                ratingToMovieID = doubleToStringMapHelper(ratingToMovieID, userRating, movieID);
            }
        }
        ArrayList<String> result = new ArrayList<>();
        for (Set<String> movies : ratingToMovieID.values()) {
            result.addAll(movies);
        }
        return result;
    }

    /**
     * Helper method to add elements to a map that maps ratings to a set of movies or users
     * @param map - Map we want to add the information to
     * @param rating - rating we want to add
     * @param id - userID or movieID
     * @return TreeMap
     */
    private TreeMap<Double, Set<String>> doubleToStringMapHelper(TreeMap<Double, Set<String>> map, double rating, String id ) {

        if (!map.containsKey(rating)) {
            Set<String> movies = new TreeSet<>();
            movies.add(id);
            map.put(rating, movies);
        }
        else {
            Set<String> movies = map.get(rating);
            movies.add(id);
            map.put(rating, movies);

        }

        return map;
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
         * @param userID - user that rated the movie
         * @param rating - Double
         */
        void addNewRating(String userID, Double rating) {
            doubleToStringMapHelper(ratingToUser, rating, userID);
        }

        /**
         * Gets all users who are strongly connected to the movie. That is, that they have rated the movie 4.0 or above
         * @return Ordered Set of users who like the movie, in descending order
         */
        TreeSet<String> getStronglyConnectedUsers() {
            TreeSet<String> users = new TreeSet<>(Collections.reverseOrder());
            for (Double rating : ratingToUser.keySet()) {
                if (rating >= 4.0) {
                    users.addAll(ratingToUser.get(rating));

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
