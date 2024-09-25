import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.io.FileNotFoundException
import edu.davis.cs.ecs36c.homework0.*

class MainKtTest {

    /**
     * A basic sanity test to ensure that loadFile raises the right exception on failure,
     * and that the test dictionary has 5 entries
      */
    @Test
    fun testLoadFile() {
        val s = loadFile("test.dict")
        assert(s.size == 5)
        assert("TeST" in s)
        assert("Fubar" !in s)
        try {
            loadFile("BadFilename")
        }
        catch (e:FileNotFoundException) {
            return
        }
        assert(false)
    }

    @Test
    fun testCheckWord() {
        val s = setOf("this", "thAT", "Phat")
        assert(checkWord("this",s))
        assert(checkWord("PHAT",s))
        assert(!checkWord("phAt",s))
        assert(checkWord("THIS",s))
        assert(checkWord("thAT",s))
        assert(!checkWord("hiss",s))
        assert(!checkWord("pHAT",s))
    }




}