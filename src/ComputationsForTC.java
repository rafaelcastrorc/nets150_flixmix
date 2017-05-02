import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rafaelcastro on 4/11/17.
 */
 class ComputationsForTC {
    Graph<String> graph;
    private HashMap<String, Map.Entry<String, List<String>>> movies;
    private  HashMap<String, List<Map.Entry<String, Double>>> ratings;



    ComputationsForTC(HashMap<String, Map.Entry<String, List<String>>> movies, HashMap<String, List<Map.Entry<String, Double>>> ratings) {
        Graph<String> graph = new Graph<>();
        this.movies = movies;
        this.ratings = ratings;
    }


    private void createGraph() {
      
    }








}
