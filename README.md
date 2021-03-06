# nets150_flixmix

Name of Project:  FlixMix - A Movie Recommendation System

DESCRIPTION:
Our project combined two common types of recommendation systems: collaborative filtering based and content-based. We used the MovieLens database (cited in our longer write-up) to access preference data. This data was then fed into two algorithms - a modified version of cosine similarity, and a recommendation system based on triadic closure.

TRIADIC CLOSURE: 
For the recommendation system based on triadic closure (focal closure), we assume that there exists a strong connection between a movie and a user if the user rated a movie 4.0 or more (out of 5). To give the recommendations, we ask a user A to rate any movie that is inside of the database. If user A gives a rating of 4.0 or more to movie1 and there exists a set of users Z = {B1, B2,... Bn}, where each user who is part of the set Z rated movie1 with a 4.0 or more, based on triadic closure (focal closure) there must exist a weak or strong tie between A and this set of users. 

Since there exists a tie between user A and the users inside of Z, we recommend movies that users who are part of Z have a strong connection with. This is based on membership closure where the focus is a movie (ex: movie2) that a user in Z (ex: B1) has a strong connection to. By this property, B1 will introduce movie2 to user A.

To provide an even better recommendation, we sort the movies based on how many users in Z really like a given movie. So for instance, if 3 users in Z gave movie2 a rating of 4.0+ and only 1 user in Z gave movie3 a rating of 4.0+, we will show the most popular movie first. So the recommendations provided are sorted by “popularity”.

COSINE SIMILARITY:
For this recommendation system, we modified the Vector Space model to fit the category of Movie Ratings/Recommendations. The program is run from main, with the invariant that the TriadicClosureParser must be run first. The program first parses data into a text that reflects ratings. For example, if I rate movie1 "2/5", movie2 "1/5", and movie3 "0/5", the text file should contain "movie1 movie1 movie2". Once this parsing has been done for all users in the database, it is stored in a HashMap, and then the user is asked for a series of answers. First, the user must generate a unique username (i.e. not in the HashMap). Second, the user must rate 9 movies (chosen by our group's preference for these movies!), which we use to generate the user's profile. Next, we create documents for all users, using the Document.java class. This function takes in the user and the user's text, pre-processes the text, and creates a term frequency HashMap. These documents in the form of an ArrayList are used as input to the Corpus class (and thus we create a corpus object), which creates an inverted index and calculates the idf. Finally, this is compiled by the VectorSpaceModel, which takes in a corpus and creates the tf-idf weight vectors to calculate the cosine similarity. Finally, we calculate the Cosine Similarity values between the user and the data-set of users. Once we find user2 with the highest cosine similarity to the user, we look at that user's profile to return other movies user2 likes, and provide suggestions. 

CATEGORIES:
We used graph and graph algorithms, and in a way created our own movie information network! Further, we used the concept of relationships in social networks for our algorithms. 

WORK BREAKDOWN: 
Triadic Closure algorithm: Rafael Castro
Data Parsing: Sara Dwyer, Desmond Howard
Website for user interface: Desmond Howard
Cosine Similarity: Sara Dwyer, Rohan Shah
