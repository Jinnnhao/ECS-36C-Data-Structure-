package edu.ucdavis.cs.ecs36c

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVRecord
import java.io.File


/*
 * The "Six Degrees of Kevin Bacon" problem is a classic game for
 * movie bufs.  The goal is to take an arbitrary actor and determine
 * the minimum sequence of movies based on "who starred with whom" to
 * reach the actor Kevin Bacon.
 *
 * As an example, Keanu Reeves was in 47 Ronin with Niel Fingleton, while
 * Neil Fingleton was in X-Men: First Class with Kevin Bacon.
 *
 * This class is designed to implement a solution to the Kevin Bacon problem.
 * The constructor itself takes a filename which is a .csv file of movie
 * information: the first column is the title and the second and subsequent columns
 * are the actors.
 *
 * Then there is the "Baconator" function.  This function performs a breadth-first
 * traversal to find a minimum BaconPath.  This is returned as a list of
 * Actor/Title/Actor/Title/Actor, or an empty list if the name isn't in the database
 * or there is no Bacon Path.  There may be multiple possible minimum Bacon Paths for
 * any particular actor but you only need to return one.
 */
class Baconator (val filename: String) {

    /*
     * Internally we store a key/value pair allowing us to go
     * from the name to the appropriate data structure for
     * the actor or movie.
     */
    //                        <Key = actor name, value= Actor object (set of movies acted by this actor)>
    val actors = mutableMapOf<String, Actor>()

    //                       <Key=movie name, value=Movie object (set of actors in this movie)>
    val movies = mutableMapOf<String, Movie>()

    /*
     * This is effectively a graph traversal problem, so we
     * need a class for nodes (actors) and edges (movies), and
     * we do it as an adjacency list for both for convenience*/

    data class Actor(val name: String) {
        val movies = mutableSetOf<Movie>()
    }

    data class Movie(val title: String) {
        val actors = mutableSetOf<Actor>()
    }

    /*
     * A useful little data class for the traversal.  You don't need to
     * use this but you might want to...
     */
    data class BaconLink(val actor1: Actor, val actor2: Actor, val movie: Movie)

    /*
     * Our constructor will load the specified CSV file
     */
    init {
        loadCSV()
    }

    /*
     * The heart of the 6-degrees of Kevin Bacon algorithm.  It should start
     * at Kevin Bacon and do a breadth-first search traversal until it finds
     * the target actor, and returns the BaconPath going the other way.
     * Alternatively, you could start at the targeted actor and go forward until
     * you reach Kevin Bacon: Either option is valid.
     *
     * Internally you will need a queue to implement the breadth first traversal,
     * a set to know if you have previously visited an actor, and some sort of
     * structure (most likely a map) to record each Actor->Movie->Actor link you
     * discover is possibly valid during the breadth first traversal.
     *
     * The returned data is a List of strings of the form Actor/Movie/Actor/Movie,
     * with the last actor being Kevin Bacon.
     *
     * Kevin Bacon should return a List of just "Kevin Bacon", and
     * if the name doesn't exist in the movie database OR there is no valid
     * baconpath for the name it should return an empty list.
     */
    fun getBaconpath(name: String): List<String> {
        var visit = ArrayDeque<Actor>()
        var seen = mutableSetOf<Actor>()
        var paths = mutableMapOf<Actor, BaconLink>()

        visit.add(actors["Kevin Bacon"]?:return emptyList())
        //Breadth First Search
        while (visit.isNotEmpty())
        {
            var currentActor = visit.removeFirst()

            if(currentActor.name == name)
            {
                // initialize the return list with currentActor name
                val baconList = mutableListOf(currentActor.name)

                while (currentActor.name!= "Kevin Bacon")
                {
                    val baconLink = paths[currentActor]!!
                    baconList.add(baconLink.movie.title)
                    baconList.add(baconLink.actor1.name)
                    currentActor = baconLink.actor1
                }
                return baconList
            }
            for(movie in currentActor.movies)
            {
                for(actor in movie.actors)
                {
                    if(actor !in seen)
                    {
                        seen.add(actor)
                        visit.add(actor)
                        paths[actor] = BaconLink(currentActor, actor, movie)
                    }
                }
            }
        }

        // if not found
        return emptyList()

    }


    /*
     * The function to load the CSV.  We use the Apache Commons CSV library (which
     * is under a freely permissive license and automagically downloaded by
     * the dependency reference in the build.gradle.kts file.
     *
     * The map executes the lambda for each line in the file, with the first entry
     * in the CSV Record being the title and the subsequent entries as names of
     * the actors in the movie.
     *
     * You can assume that the CSV file exists, is well-formed, and there are
     * no duplicate movie titles.
     */
    fun loadCSV() {
        CSVFormat.Builder.create(CSVFormat.DEFAULT).apply {
            setIgnoreSurroundingSpaces(true)
        }.build().parse(File(filename).bufferedReader())
            .map {
                loadLineData(it)
            }
    }

    // The CSVRecord hold a line of data from the file, and now use the loadLineData to split the data
    // and the data in CSVRecord is stored as a list, use index to access and separate by ","
    fun loadLineData(data:CSVRecord) {
        //creating a movie map with the movie title as the Key, and map to the Movie object of this title
        //the Movie object holds all the actors in this movie
        val movieTitle = data[0]
        //creat this a map for this movie
        movies[movieTitle] = Movie(movieTitle)
        val theMovie = movies[movieTitle]

        // For each actor in the record, add to the map & connect to the Movie object
        for (i in 1 until data.size()) {
            val actorName = data[i]

            // if the actor is not exist, creat a new map for this actor
            if(actors[actorName] == null)
            {
                actors[actorName] = Actor(actorName)
            }
            val theActor = actors[actorName]

            // add the actor in this Movie object
            theMovie?.actors?.add(theActor!!)
            // add the movie in this Actor object
            theActor?.movies?.add(theMovie!!)

        }
    }
}