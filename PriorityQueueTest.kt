import edu.ucdavis.cs.ecs036c.homework7.PriorityQueue
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class PriorityQueueTest {

    @Test
    fun testComprehensive() {
        val pair1 = Pair("string1", 3)
        val pair2 = Pair("string2", 5)
        val pair3 = Pair("string3", 2)
        val pair4 = Pair("string4", 4)
        val pair5 = Pair("string5", 6)
        val pair6 = Pair("string6", 7)
        val pair7 = Pair("string7", 8)

        val priorityQueue = PriorityQueue(pair1, pair2, pair3, pair4, pair5, pair6, pair7)

        assert(priorityQueue.size == 7)
        priorityQueue.update("string8", 0)

        priorityQueue.update("string8", 1)

        assert(priorityQueue.locationData["string8"] == 0)

        while (priorityQueue.priorityData.isNotEmpty())
        {
            println(priorityQueue.pop())
        }


        assert(priorityQueue.isValid())
    }
}