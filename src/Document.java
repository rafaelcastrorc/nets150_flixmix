/**
 * Created by saradwyer on 5/5/17.
 */

import java.util.HashMap;
import java.util.Set;

/**
 * This class represents one document.
 * It will keep track of the term frequencies.
 * @author swapneel
 *
 */
public class Document implements Comparable<Document> {

    /**
     * A hashmap for term frequencies.
     * Maps a term to the number of times this terms appears in this document.
     */
    private HashMap<String, Integer> termFrequency;

    /**
     * The name of the file to read.
     */
    private String userName;

    /**
     * The text for the user
     */
    private String userText;

    /**
     * The constructor
     * Takes in the userName and the MoviePreferenceText
     * @param uName, uInfo
     */
    public Document(String uName, String uInfo) {
        this.userName = uName;
        this.userText = uInfo;
        termFrequency = new HashMap<String, Integer>();
        normalize();
    }

    /**
     * This method will read in the file and do some pre-processing.
     * The following things are done in pre-processing:
     * Every word is converted to lower case.
     * Every character that is not a letter or a digit is removed.
     * We don't do any stemming.
     * Once the pre-processing is done, we create and update the
     */
    private void normalize() {

        String[] movies = userText.split(" ");
        String title;
        for (int i = 0; i < movies.length; i++) {
            title = movies[i];

            if (!(title.equalsIgnoreCase(""))) {
                if (termFrequency.containsKey(title)) {
                    int oldCount = termFrequency.get(title);
                    termFrequency.put(title, ++oldCount);
                } else {
                    termFrequency.put(title, 1);
                }
            }
        }
    }

    /**
     * This method will return the term frequency for a given word.
     * If this document doesn't contain the word, it will return 0
     * @param word The word to look for
     * @return the term frequency for this word in this document
     */
    public double getTermFrequency(String word) {
        if (termFrequency.containsKey(word)) {
            return termFrequency.get(word);
        } else {
            return 0;
        }
    }

    /**
     * This method will return a set of all the terms which occur in this document.
     * @return a set of all terms in this document
     */
    public Set<String> getTermList() {
        return termFrequency.keySet();
    }

    /**
     * @return the filename
     */
    private String getUserName() {
        return userName;
    }

    /**
     * This method returns the user text
     */
    public String toString() {
        return userText;
    }

    /**
     * This method returns the HashMap of Term Frequency
     */
    public HashMap<String, Integer> getFrequency() {
        return termFrequency;
    }

    @Override
    public int compareTo(Document o) {
        return this.getUserName().compareTo(o.getUserName());
    }
}
