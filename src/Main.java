/**
 * Created by rafaelcastro on 5/1/17.
 */
public class Main {

    public static void main(String[] args) {

        //For triadic closure computing only
        System.out.println("Starting program");
        System.out.println(System.currentTimeMillis());

        if (args.length != 2) {
            System.out.println("usage is: ");
            System.out.println("     java Parser /path/to/data/movies.dat /path/to/data/ratings.dat");
            System.out.println("have you setup your \"Run Configurations\"?");
            return;
        }
        TriadicClosureParser.parseMovies(args[0]);
        TriadicClosureParser.parseRatings(args[1]);
        System.out.println("Parsing done");
        System.out.println(System.currentTimeMillis());



        ComputationsForTC model = new ComputationsForTC(TriadicClosureParser.getRatings());
    }

}
