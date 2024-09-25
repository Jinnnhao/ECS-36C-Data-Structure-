package edu.ucdavis.cs.ecs036c

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import kotlin.math.absoluteValue
import kotlin.random.Random

class HashTableTest {

    @Test
    fun testLots() {
        for (x in 0..<100) {
            val h = HashTable<String, Int>()
            val tweak = "Tweak:" + Random.nextInt().toString()
            val iterations = 10000
            var strings : Array<String?> = arrayOfNulls(iterations)
            for (i in 0 ..< iterations){
                strings[i] = i.toString() + tweak
            }
            @Suppress("UNCHECKED_CAST")
            strings as Array<String>
            strings.shuffle()
            var i = 0
            for (s in strings) {
                assert(s !in h)
                h[s] = i
                assert(s in h)
                assert(h[s] == i)
                assert(h.size == i + 1)
                i++
            }
            strings.shuffle()
            i = 0
            for (s in strings) {
                assert(s in h)
                h.remove(s)
                assert(s !in h)
                assert(h[s] == null)
                if(i < iterations -1 ) {
                    h[strings[i + 1]] = i
                    assert(h[strings[i+1]] == i)
                }
                assert(h.size == iterations - 1 - i)
                assert(h.occupied == iterations)
                i++
            }
            val savedSize = h.storage.size
            strings.shuffle()
            i = 0
            for (s in strings) {
                assert(s !in h)
                h[s] = i
                assert(h.size == i + 1)
                assert(h[s] == i)
                assert(h.occupied == iterations)
                i++
            }
            // There shouldn't be any growth in the internal storage
            // as deleted items just get re-overwritten.
            assert(h.storage.size == savedSize)
        }
    }

    @Test
    fun testSet()
    {
        var hashtable = HashTable<Int, String>()
        hashtable.set(1, "A")
        hashtable.set(2, "B")
        hashtable.set(3, "C")
        hashtable.set(4, "E")
        hashtable.set(5, "F")
        hashtable.set(6, "G")
        hashtable.set(7, "H")
        hashtable.set(8, "I")
        hashtable.set(9, "J")

        hashtable.remove(9)
        assert(9 !in hashtable)

        hashtable[9] = "K"
        assert(9 in hashtable)
        assert(hashtable[9] == "K")

        assert(hashtable[1] == "A")









    }
}