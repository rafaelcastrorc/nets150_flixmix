import com.sun.tools.corba.se.idl.InterfaceGen;

import java.util.*;

/**
 * Created by rafaelcastro on 4/11/17.
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
     * Method constructor. Generates the graph based on the data collected by the Parser
     * @param movies
     * @param ratings
     *
     */

    ComputationsForTC(HashMap<String, Map.Entry<String, List<String>>> movies, HashMap<String, List<Map.Entry<String, Double>>> ratings) {
        this.graph = new Graph<>();
        this.movies = movies;
        this.ratings = ratings;
        createGraph();
        getFavoriteMoviesOfUser("u2");
        System.out.println(getAllPossibleSuggestion("m2"));
    }


    /**
     * Generates a weighted directed graph. Maps userID to movieID
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




    private void addMovieRatingToMap(String movieID, MovieRating movieRatingObj) {
        ratedMovies.put(movieID, movieRatingObj);

    }
    private MovieRating getMovieRatingObj(String movieID) {
       return ratedMovies.get(movieID);
    }

    //Todo: do suggestions based on genre
    //Todo: Do suggestions based on the user instead of a single movie

    //If a movie really liked the given movie, gets other users who also liked the movie and the movies they like
    //Todo avoid including the user who requested the info and the curr movie
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

    //Helper method that returns all possible movie suggestions
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

    //Gets the favorite movies of the user, in descending order (rated 4.0 or above)
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




    class MovieRating {
        private String movieID;
        private TreeMap<Double, Set<String>>  ratingToUser = new TreeMap<>(Collections.reverseOrder());

        MovieRating(String movieID) {
            this.movieID = movieID;
        }

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

        //Get all the users who rated highly the movie (4 and above)
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
