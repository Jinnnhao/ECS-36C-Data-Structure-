package edu.ucdavis.cs.ecs036c
/*
 * Welcome to the testing infrastructure for Homework 3
 *
 * Since I got a lot of cat photos, and since the testing strategies
 * for sorting are pretty straighforward, I'm going to be extra kind and
 * actually provide ALL the tests that Gradescope will use, as well as the
 * tests used for LinkedList code in general (to make sure you don't break anything).
 *
 * Don't get used to this however, Homework 4 is going back to both blind
 * testing and code coverage testing.
 *
 * It also shows the gradescope annotations, so you can see how those work:
 * We run a testfile and that dumps out JSON results that are then examined
 * by a python script and imported into Gradescope.
*/

import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.assertFailsWith
import edu.ucdavis.cs.ecs036c.testing.*
import kotlinx.serialization.descriptors.serialDescriptor
import java.security.SecureRandom
import java.util.concurrent.TimeUnit
import kotlin.random.asKotlinRandom
import kotlin.time.measureTime

/**
 * Randomness is often really useful for testing,
 * and this makes sure we have a guaranteed GOOD
 * random number generator.
 *
 * However, for your code you should just use the normal
 * random operation, because that can be re-seeded to be
 * deterministic for better testability.
 */
val secureRNG = SecureRandom().asKotlinRandom()

@Timeout(5, unit = TimeUnit.SECONDS)
@ExtendWith(GradescopeTestWatcher::class)
class ComprehensiveTests : GradescopeTest () {

    /**
     * This is a legacy test for the LinkedList class.
     *
     * One rule of testing: unless your tests are REALLY slow (e.g. minutes),
     * NEVER remove a test from your test suite, just to make sure you don't
     * introduce bugs back into old code.
     */
    @Test
    fun testListInit() {
        val testArray = arrayOf("A", "B", "C")
        // The * operator here expands an array into the arguments
        // for a variable-argument function
        val data = toLinkedList(*testArray)
        assert(data.toString() == "[A, B, C]")
    }

    /**
     * This is a legacy test for the LinkedList class.
     */
    @Test
    fun testListBasicInit() {
        val testArray = arrayOf(0, 1, 2, 3, 4, 5)
        testArray.shuffle(random = secureRNG)
        val testList = toLinkedList(*testArray)
        for (x in 0..<testArray.size) {
            assert(testArray[x] == testList[x])
        }
        assert(testArray.size == testList.size)
        assertFailsWith<IndexOutOfBoundsException>() {
            testList[-1]
        }
        assertFailsWith<IndexOutOfBoundsException>() {
            testList[testList.size]
        }
    }

    /*
     * This legacy test makes sure that the LinkedList class can
     * handle nullable data correctly.
     */
    @Test
    fun testBasicListNullableData() {
        val testList = LinkedList<Int?>()
        for (x in 0..<5) {
            testList.append(x)
        }
        for (x in 5..<10) {
            testList.append(null)
        }
        for (x in 0..<10) {
            if (x < 5) {
                assert(testList[x] == x)
            } else {
                assert(testList[x] == null)
            }
        }
        for (x in 0..<10) {
            val data = testList.removeAt(0)
            if (x < 5) {
                assert(data == x)
            } else {
                assert(data == null)
            }
        }

    }

    /*
     * This tests get and set and makes sure they are working properly
     */
    @Test
    fun testBasicListSetting() {
        val testArray = arrayOf(1, 2, 3, 4)
        val testList = toLinkedList(*testArray)
        for (x in 0..<testArray.size) {
            assert(testArray[x] == testList[x])
            testList[x] = testArray[x] * x
        }
        for (x in 0..<testArray.size) {
            assert(testArray[x] * x == testList[x])
        }
    }

    /*
     * This tests that exceptions are being properly raised on
     * illegal accesses for get and set
     */
    @Test
    fun testListExceptions() {
        val testArray = arrayOf(1, 2, 3, 4)
        val testList = toLinkedList(*testArray)
        assertFailsWith<IndexOutOfBoundsException>() {
            testList[-1]
        }
        assertFailsWith<IndexOutOfBoundsException>() {
            testList[testArray.size]
        }
        assertFailsWith<IndexOutOfBoundsException>() {
            testList[-1] = 3
        }
        assertFailsWith<IndexOutOfBoundsException>() {
            testList[testArray.size] = 3
        }

    }

    /*
     * This tests that the basic size is maintained when appending and prepending
     */
    @Test
    fun testListBasicSize() {
        val testList: LinkedList<Int> = toLinkedList()
        for (x in 0..<10) {
            assert(testList.size == x)
            testList.prepend(x)
        }
        assert(testList.size == 10)
        for (x in 10..<20) {
            assert(testList.size == x)
            testList.append(x)
        }
    }

    /*
     * This test creates a random input list, then randomly deletes entries
     * and makes sure that size and everything are consistently maintained.
     */
    @Test
    fun testListDeleteSize() {
        for (i in 0..<10) {
            val testArray = arrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
            val testList: LinkedList<Int> = LinkedList()
            for (j in 0..<5) {
                testArray.shuffle(secureRNG)
                for (x in 0..<10) {
                    if (secureRNG.nextBoolean()) {
                        testList.append(testArray[x])
                    } else {
                        testList.prepend(testArray[x])
                    }
                }
                testArray.shuffle(secureRNG)
                for (x in 0..<10) {
                    assertThrows<IndexOutOfBoundsException> {
                        testList.removeAt(-1)
                    }
                    assertThrows<IndexOutOfBoundsException> {
                        testList.removeAt(testList.size)
                    }
                    val index = testList.indexOf(testArray[x])
                    val removed = testList.removeAt(index)
                    assert(removed == testArray[x])
                    assert(removed !in testList)
                    assert(testList.size == (9 - x))
                }
            }
        }
    }

    /*
     * Test of fold and map functionality.  These tests are actually pretty
     * short becaues there really isn't much to those functions.
     */
    @Test
    fun testListMapEtc() {
        val testArray = arrayOf(0, 1, 2, 3, 4)
        val data = toLinkedList(*testArray)
        val f = testArray.fold(0) { a, b -> a + b }
        assert(f == data.fold(0) { a, b -> a + b })
        var i = 0
        for (x in data.map { it * 2 }) {
            assert(x == i * 2)
            i++
        }
        data.mapInPlace { it * 3 }
        for (x in 0..<data.size) {
            assert(x * 3 == data[x])
        }
    }

    /**
     * Ensures that append/prepend/size are all O(1) operations, and that
     * the special case for get and set are constant time as well
     */
    @Test
    fun testListTiming() {
        fun internal(iterationCount: Int) {
            val data: LinkedList<Int> = toLinkedList()
            for (x in 0..<iterationCount) {
                assert(data.size == 2 * x)
                data.prepend(x)
                data.append(x)
                assert(data[0] == x)
                assert(data[data.size - 1] == x)
            }
        }

        val small = measureTime { internal(1000) }
        val large = measureTime { internal(100000) }
        // This should be 100 + some epsilon, but I set it at 200 to give
        // plenty of error, since any error makes it linear instead of constant
        // time which would make it so much larger.
        assert(large < (small * 200))
    }


    /*
     * Comprehensive testing for insertAt().  It basically inserts a bunch at the beginning,
     * then a bunch at the end, and a bunch in the middle.  Each time it checks to make sure
     * things are consistent after the operations.
     */
    @Test
    fun testListInsertAt() {
        var data = LinkedList<Int>()
        for (x in 0..<5) {
            data.insertAt(data.size, x)
        }
        for (x in 0..<5) {
            assert(data[x] == x)
        }
        data = LinkedList<Int>()
        for (x in 0..<5) {
            data.insertAt(0, x)
        }
        for (x in 0..<5) {
            assert(data[x] == 4 - x)
        }
        data = toLinkedList(0, 5)
        for (x in 1..<5) {
            data.insertAt(x, x)
        }
        for (x in 0..<6) {
            assert(data[x] == x)
        }
        assertFailsWith<IndexOutOfBoundsException>() {
            data.insertAt(-1, 0)
        }
        assertFailsWith<IndexOutOfBoundsException>() {
            data.insertAt(data.size + 1, 0)
        }
    }

    /*
     * A timing test to make sure that the two cases of insertAt that
     * are supposed to be constant time are indeed constant time.
     */
    @Test
    fun testListInsertAtTiming() {
        fun internal(iterationCount: Int) {
            val data: LinkedList<Int> = toLinkedList()
            for (x in 0..<iterationCount) {
                assert(data.size == 2 * x)
                data.insertAt(0, x)
                data.insertAt(data.size, x)
                assert(data[0] == x)
                assert(data[data.size - 1] == x)
            }
        }

        val small = measureTime { internal(1000) }
        val large = measureTime { internal(100000) }
        assert(large < (small * 200))
    }

    /*
     * Test for filterInPlace.  It starts with a long list, does a
     * filter, ensures it is all OK, ensures that you can still add stuff,
     * then filters EVERYTHING out and makes sure the list still works.
     */
    @Test
    fun testListFilterInPlace() {
        val testArray = arrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
        val data = toLinkedList(*testArray)
        data.filterInPlace { it % 2 == 0 }
        assert(data.size == 5)
        for (x in 0..<10 step 2) {
            assert(data[x / 2] == x)
        }
        data.prepend(32)
        data.append(64)

        // And now make sure internal structures weren't messed up
        assert(data[0] == 32)
        assert(data[6] == 64)
        for (x in 0..<10 step 2) {
            assert(data[x / 2 + 1] == x)
        }
        data.filterInPlace { false }
        assert(data.size == 0)
        data.prepend(1)
        data.append(2)
        assert(data.size == 2)
        assert(data[0] == 1)
        assert(data[1] == 2)
    }

    /*
     * And the test for filter rather than filterInPlace
     */
    @Test
    fun testListFilter() {
        val testArray = arrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
        val data = toLinkedList(*testArray)
        var data2 = data.filter { it % 2 == 0 }
        assert(data2.size == 5)
        for (x in 0..<10 step 2) {
            assert(data2[x / 2] == x)
        }
        assert(!(data2 === data))
        data2 = data.filter { false }
        assert(data2.size == 0)
        data2 = data.filter { it % 2 == 0 }
        assert(data2.size == 5)
        for (x in 0..<10 step 2) {
            assert(data2[x / 2] == x)
        }
    }

    /*
     * This wasn't included in the HW1 autograder but was added as a result
     * of the reported bug in the reference LinkedList code dealing with
     * nullable data:  It would give a null pointer exception if you removed
     * the first element when it was null in a nullable type...
     */
    @Test
    fun testListTailCell() {
        val testList = LinkedList<Int?>()
        assert(testList.size == 0)
        for (x in 0..<5) {
            testList.append(null)
        }
        for (x in 5..<10) {
            testList.append(x)
        }
        testList.removeAt(0)
    }

    /*
     * This is a test of the insertion sort LinkedList code,
     * which was kept in, so you can see it in action.
     */
    @Test
    fun testInsertionSorting() {
        for (x in 0..<100) {
            for (y in 0..<5) {
                val testArray: Array<Int?> = arrayOfNulls(x)
                for (i in 0..<x) {
                    testArray[i] = i
                }
                @Suppress("UNCHECKED_CAST")
                testArray as Array<Int>
                testArray.shuffle(secureRNG)
                val testList = toLinkedList(*testArray)
                testList.insertionSort()
                var i = 0
                for (j in testList) {
                    if (i != j) {
                        val msg = testArray.joinToString(prefix = "[", postfix = "]")
                        println("Failing test array $msg")
                    }
                    assert(i == j)
                    i++
                }
            }
        }
    }

    @Test
    fun testSplit() {
        val testArray = arrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
        val data = toLinkedList(*testArray)
        val node5 = LinkedListCell(5, null)
        val node4 = LinkedListCell(4, node5)
        val node3 = LinkedListCell(3, node4)
        val node2 = LinkedListCell(2, node3)
        val node1 = LinkedListCell(1, node2)

        var (first, second) = data.split(node1)
        var node = first
        while (node?.next != null) {
            print("${node.data} -> ")
            node = node.next!!
        }
        println(node?.data)

        node = second
        while (node?.next != null) {
            print("${node.data} -> ")
            node = node.next!!
        }
        println(node?.data)
    }




    /*
     * THIS is the primary correctness test for MergeSort.
     *
     * This test also reports what the input looked like on the first
     * failure so you can use that for evaluation/debugging.  As you can
     * see this test is worth 15 points on Gradescope
     */
    @GradescopeAnnotation("Mergesort Correctness", 15)
    @Test
    fun testMergeSort() {
        for (x in 0..<100) {
            for (y in 0..<5) {
                val testArray: Array<Int?> = arrayOfNulls(x)
                for (i in 0..<x) {
                    testArray[i] = i
                }
                @Suppress("UNCHECKED_CAST")
                testArray as Array<Int>
                testArray.shuffle(secureRNG)
                val testList = toLinkedList(*testArray)
                testList.mergeSort()
                var i = 0
                for (j in testList) {
                    if (i != j) {
                        val msg = testArray.joinToString(prefix = "[", postfix = "]")
                        println("Failing input $msg")
                    }
                    assert(i == j)
                    i++
                }
                val testList2 = toLinkedList(*testArray)
                testList2.mergeSort(reverse = true)
                i = x - 1
                for (j in testList2) {
                    if (j != i) {
                        val msg = testArray.joinToString(prefix = "[", postfix = "]")
                        println("Failing reverse input $msg")
                    }
                    assert(j == i)
                    i--
                }
            }
        }
    }


    /*
     * This is a test of stability on MergeSort.  We create a new
     * class that has a compareTo operation that always returns 0,
     * so in a stable sort this should have the input order maintained.
     */
    @GradescopeAnnotation("Mergesort Stability", 5)
    @Test
    fun testMergeSortStable() {
        class StableData : Comparable<StableData> {
            constructor()

            override fun compareTo(other: StableData) = 0

        }
        for (i in 0..<100) {
            val testData: Array<StableData?> = arrayOfNulls(100)
            for (x in testData.indices) {
                testData[x] = StableData()
            }
            // A sanity check to make sure that these are indeed
            // separate objects
            assert(!(testData[0] === testData[1]))
            @Suppress("UNCHECKED_CAST")
            testData as Array<StableData>
            testData.shuffle(secureRNG)
            val testList = toLinkedList(*testData)
            testList.mergeSort()
            var j = 0
            for (x in testList) {
                assert(x === testData[j])
                j++
            }
        }
    }

    /*
     * And a performance test for MergeSort.  This isn't doing it randomly, instead it is
     * just randomly going onto the beginning and end.  Since order doesn't matter
     * for mergesort performance, and we already check correctness too, this is fine
     */
    @GradescopeAnnotation("Mergesort Performance", 15)
    @Test
    fun testMergeSortPerformance() {
        fun internal(iterationCount: Int) {
            val data: LinkedList<Int> = toLinkedList()
            for (x in 0..<iterationCount) {
                if (secureRNG.nextBoolean()) {
                    data.append(x)
                } else {
                    data.prepend(x)
                }
            }
            data.mergeSort()
            var j = 0
            for (i in data) {
                assert(i == j)
                j++
            }
        }

        val small = measureTime { internal(1000) }
        val large = measureTime { internal(100000) }
        assert(large < (small * 400))
    }



    /*
     * A basic correctness test for the SkipList.
     *
     */
    @GradescopeAnnotation("Test Skiplist Correctness", 10)
    @Test
    fun testSkiplistCorrectness() {
        for (x in 0..<100) {
            for (y in 0..<10) {
                val testData: Array<Int?> = arrayOfNulls(x)
                for (i in testData.indices) {
                    testData[i] = i
                }
                @Suppress("UNCHECKED_CAST")
                testData as Array<Int>
                testData.shuffle(secureRNG)
                val testSkipList = SkipList<Int, Int>()
                for (i in testData) {
                    testSkipList[i] = i * i
                }
                var j = 0
                for (i in testSkipList) {
                    if (i.first != j || i.second != j * j) {
                        val msg = testData.joinToString(prefix = "[", postfix = "]")
                        println("Skiplist Failed test array $msg")
                    }
                    assert(i.first == j)
                    assert(i.second == j * j)
                    j++
                }
            }
        }
        for (x in 0..<20) {
            val testArray = arrayOf(
                0 to "0", 1 to "1", 2 to "2", 3 to "3", 4 to "4",
                5 to "5", 6 to "6", 7 to "7", 8 to "8", 9 to "9", 10 to "10"
            )
            testArray.shuffle(secureRNG)
            val data = skipListOf(*testArray)
            for (y in 0..10) {
                assert(data[y] == y.toString())
            }
            var i = 0
            for (p in data) {
                assert(p.first == i)
                assert(p.second == i.toString())
                i++
            }
            assert(data[-1] == null)
            assert(data[11] == null)
        }
    }


    /*
     * And a performance test for the SkipList.  Does require that the correctness
     * test work because we aren't checking just inserting, so we call that first.
     */
    @GradescopeAnnotation("SkipList Performance", 15)
    @Test
    fun testSkipListPerformance() {
        fun internal(iterationCount: Int) {
            val data = SkipList<Int, Int>()
            for (x in 0..<iterationCount) {
                data[secureRNG.nextInt()] = x
            }
        }
        testSkiplistCorrectness()
        val small = measureTime { internal(1000) }
        val large = measureTime { internal(100000) }
        assert(large < (small * 400))
    }


    /*
     * A simple test to make sure your skiplist distribution of height is correct.
     */
    @GradescopeAnnotation("Test of Skiplist Distribution", 5)
    @Test
    fun testSkiplistDistribution() {
        val skipList = skipListOf(0 to "0")
        for (x in 0..<100000) {
            skipList[x] = x.toString()
        }
        for (x in 0..<100000) {
            assert(skipList[x] == x.toString())
        }
        val heights = skipList.heightCalculation()
        assert(heights[0] in 40000..<60000)
        assert(heights[1] in 20000..<30000)
    }

    /*
     * And now a test for Selection Sort, which again was kept into the
     * skeleton so you can see it in action.
     */
    @Test
    fun testSelectionSorting() {
        for (x in 0..<100) {
            for (y in 0..<5) {
                val testArray: Array<Int?> = arrayOfNulls(x)
                for (i in 0..<x) {
                    testArray[i] = i
                }
                @Suppress("UNCHECKED_CAST")
                testArray as Array<Int>
                testArray.shuffle(secureRNG)
                testArray.selectionSort()
                for (i in testArray.indices) {
                    assert(testArray[i] == i)
                }
                testArray.shuffle(secureRNG)
                testArray.selectionSort(reverse = true)
                for (i in testArray.indices) {
                    assert(testArray[i] == x - i - 1)
                }
                testArray.shuffle(secureRNG)
                testArray.selectionSortWith(object : Comparator<Int> {
                    override fun compare(a: Int, b: Int): Int {
                        return b.compareTo(a)
                    }
                })
                for (i in testArray.indices) {
                    assert(testArray[i] == x - i - 1)
                }
            }

        }
    }

    /*
     * And here is the correctness test for quicksort.
     */
    @Test
    @GradescopeAnnotation("Quicksort Correctness", 20)
    fun testQuicksort() {
        for (x in 3..<1000) {
            for (y in 0..<4) {
                val testArray: Array<Int?> = arrayOfNulls(x)
                val refArray: Array<Int?> = arrayOfNulls(x)
                for (i in 0..<x) {
                    testArray[i] = i
                }
                @Suppress("UNCHECKED_CAST")
                testArray as Array<Int>
                testArray.shuffle(secureRNG)
                for (i in 0..<x) {
                    refArray[i] = testArray[i]
                }
                testArray.quickSort()
                for (i in 0..<x) {
                    if (testArray[i] != i) {
                        val msg = refArray.joinToString(prefix = "[", postfix = "]")
                        println("Quicksort failed on $msg")
                    }
                    assert(testArray[i] == i)
                }
                testArray.shuffle(secureRNG)
                for (i in 0..<x) {
                    refArray[i] = testArray[i]
                }
                testArray.quickSort(true)
                for (i in 0..<x) {
                    if (testArray[i] != x - i - 1) {
                        val msg = refArray.joinToString(prefix = "[", postfix = "]")
                        println("Quicksort failed on $msg")
                    }
                    assert(testArray[i] == x - i - 1)
                }
            }
        }
    }


    /*
     * And finally performance testing for quicksort.  It doesn't just test
     * random (and shuffle is O(N) so that is OK in testing) but it also tests
     * already ordered and all the same to check for those pathological cases
     */
    @GradescopeAnnotation("Quicksort Performance", 15)
    @Test
    fun testQuicksortPerformance() {
        fun internal(iterationCount: Int) {
            val data: Array<Int?> = arrayOfNulls(iterationCount)
            for (x in data.indices) {
                data[x] = x
            }
            @Suppress("UNCHECKED_CAST")
            data as Array<Int>
            data.quickSort()
            for (x in data.indices) {
                assert(data[x] == x)
            }
            data.shuffle(secureRNG)
            data.quickSort()
            for (x in data.indices) {
                assert(data[x] == x)
                data[x] = 0
            }
            data.quickSort()
            for (x in data.indices) {
                assert(data[x] == 0)
            }
        }

        val small = measureTime { internal(1000) }
        val large = measureTime { internal(100000) }
        assert(large < (small * 400))
    }
}

