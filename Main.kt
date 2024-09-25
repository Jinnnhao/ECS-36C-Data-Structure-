package edu.davis.cs.ecs36c.homework0

import java.io.File
import java.io.FileNotFoundException
import java.io.FileReader
import java.io.InputStream
import java.util.*
import kotlin.system.exitProcess


val defaultFile = "/usr/share/dict/words"

// A regular expression pattern that will match words:

// A useful trick: Given the regular expression object re,
// re.findAll returns a Collection (an iteratable structure)
// that contains the words, while re.split() returns an array
// of the strings that AREN'T matched.  So you can take an input
// line, create both the split and the collection, and iterate over
// the collection keeping track of the iteration count.
val splitString = "\\p{Alpha}+"

/**
 * This function should take a filename (either the default file or the
 * one specified on the command line.)  It should create a new MutableSet,
 * open the file, and load each line into the set.
 *
 * @param filename may not exist, and in that case the function should
 * throw a FileNotFound exception.
 */
fun loadFile(filename: String): Set<String>{
    val set = mutableSetOf<String>()
    // You need to implement this
    val file = File(filename)

    if (!file.exists())
    {
        throw FileNotFoundException ("File not found: $filename")
    }
    file.forEachLine { set.add(it) }
    return set
}

/**
 * This function should check if a word is valid by checking the word,
 * the word in all lower case, and the word with all but the first character
 * converted in lower case with the first character unchanged.
 */
fun checkWord(word: String, dict: Set<String>): Boolean {
    // You need to implement this
    val lowerCase = word.lowercase()
    val upperCase = word.uppercase()
   // val upperFirstChar = word.substring(0,1).uppercase() + lowerCase.substring(1)
    val lowerRestChar = word.substring(0,1) + word.substring(1).lowercase()


    if (word in dict) {
        return true
    } else if (lowerCase in dict) {
        return true
    }
    else if (upperCase in dict)
    {
        return true
    }
    /*else if (upperFirstChar in dict)
    {
        return true
    }*/
    else if (lowerRestChar in dict)
    {
        return true
    }

    return false

}

/**
 * This function should take a set (returned from loadFile) and then
 * processes standard input one line at a time using readLine() until standard
 * input is closed
 *
 * Note: readLine() returns a String?: that is, a string or null, with null
 * when standard input is closed.  Under Unix or Windows in IntelliJ you can
 * close standard input in the console with Control-D, while on the mac it is
 * Command-D
 *
 * Once you have the line you should split it with a regular expression
 * into words and nonwords,
 */
fun processInput(dict: Set<String>){
    val re = Regex(splitString)
    // You need to implement this

    while (true) {
        val line = readLine() ?: break

        val allWords = re.findAll(line)  // get all words from the line in a collect
        val nonWords = re.split(line)   // get all non-words from the line in a list
        var i = 0


        for(match in allWords) {         // the for loop will loop each word in the collect
            val word = match.value
            print(nonWords[i])
            if (checkWord(word, dict)) {
                print(word)

            }
            else
            {                           // if the word is not pass the check
                print("$word [sic]")

            }

            i++
        }
        // Print any remaining non-words at the end of the line
        while (i < nonWords.size) {
            print(nonWords[i])
            i++
        }

        println()
    }


}

/**
 * Your main function should accept an argument on the command line or
 * use the default filename if no argument is specified.  If the dictionary
 * fails to load with a FileNotFoundException it should use
 * kotlin.system.exitProcess with status code of 55
 */
fun main(args :Array<String>) {
    // You need to implement this
    val filename : String
    if (args.isNotEmpty())
    {
        filename = args[0]
    }
    else
    {
        filename = defaultFile
    }
  try{
      val set = loadFile(filename)
      processInput(set)
  } catch(e:FileNotFoundException) {
      exitProcess(55)
  }


}