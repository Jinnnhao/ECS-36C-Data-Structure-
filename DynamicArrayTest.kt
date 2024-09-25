package edu.ucdavis.cs.ecs036c

import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import java.security.SecureRandom
import kotlin.random.asKotlinRandom
import kotlin.test.assertFailsWith
import edu.ucdavis.cs.ecs036c.testing.*
import java.util.concurrent.TimeUnit
import kotlin.reflect.KProperty
import kotlin.time.measureTime
import kotlin.reflect.jvm.javaField

/**
 * Randomness is often really useful for testing,
 * and this makes sure we have a guaranteed GOOD
 * random number generator
 */
val secureRNG = SecureRandom().asKotlinRandom()


class DynamicArrayTest  () {
    /**
     * You will need to write MANY more tests, but this is just a simple
     * example: it creates a LinkedList<String> of 3 entries,
     * and then calls the toString() function.  Since toString needs
     * iterator to work this actually tests a remarkable amount of your code!
     */
    @Test
    fun testInit(){
        val testArray = arrayOf("A", "B", "C")
        // The * operator here expands an array into the arguments
        // for a variable-argument function
        val data = toDynamicArray(*testArray)
        assert(data.toString() == "[A, B, C]")
    }

    @Test
    fun testResize()
    {
        val data = toDynamicArray("A", "B")

        assert(data.storage.size == 4)
        assert(data.privateSize == 2)

        data.append("C")
        assert(data.toString() == "[A, B, C]")

        data.append("D")
        assert(data.toString() == "[A, B, C, D]")

        data.resize()
        assert(data.storage.size == 8)
        data.resize()
        assert(data.storage.size == 16)

    }

    @Test
    fun testPrepend()
    {
        val data = DynamicArray<String?>()

        data.prepend("A")
        assert(data.toString() == "[A]")

        data.prepend("B")
        assert(data.toString() == "[B, A]")
        data.prepend("C")
        assert(data.toString() == "[C, B, A]")
        data.append("D")
        assert(data.toString() == "[C, B, A, D]")

        data.prepend("E")
        assert(data.toString() == "[E, C, B, A, D]")
    }

    @Test
    fun testRemoveAt()
    {
        val data = DynamicArray<String?>()

        data.prepend("A")
        assert(data.toString() == "[A]")

        data.prepend("B")
        assert(data.toString() == "[B, A]")
        data.prepend("C")
        assert(data.toString() == "[C, B, A]")
        data.append("D")
        assert(data.toString() == "[C, B, A, D]")

        data.removeAt(0)
        assert(data.toString() == "[B, A, D]")

        assert(data.removeAt(1) == "A")
        assert(data.toString() == "[B, D]")

        assertFailsWith<IndexOutOfBoundsException>()
        {
            data.removeAt(2)
        }
    }

    @Test
    fun testindexOf()
    {
        val data = toDynamicArray(0,1,2,3)

        assert(data.indexOf(0) == 0)
        assert(data.indexOf(3) == 3)
        assert(data.indexOf(2) == 2)

    }

    @Test
    fun testGet()
    {
        val data = toDynamicArray(0,1,2,3)
        assert(data.get(0) == 0)
        assert(data.get(3) == 3)

        assertFailsWith<IndexOutOfBoundsException>
        {
            data.get(-1)
        }

    }

    @Test
    fun testSet()
    {
        val data = toDynamicArray(0,1,2,3)
        data.set(0,1)
        data.set(2,1)
        data.set(3,1)
        assert(data.toString() == "[1, 1, 1, 1]")
    }

    @Test
    fun testInsertAt()
    {
        val data = toDynamicArray(1)

        data.insertAt(0,0)
        data.insertAt(2,2)
        data.insertAt(3,3)
        assert(data.toString() == "[0, 1, 2, 3]")

        assertFailsWith<IndexOutOfBoundsException>
        {
            data.insertAt(-1,0)
            data.insertAt(5,5)
        }
    }


    @Test
    fun testMap()
    {
        val data = toDynamicArray(0,1,2,3)

        val mapList = data.map { it.toString()} // {the body of the operator} converting Int to String
        Assertions.assertEquals("0", mapList[0])
        Assertions.assertEquals("1", mapList[1])
        Assertions.assertEquals("2", mapList[2])
        Assertions.assertEquals("3", mapList[3])
    }

    @Test
    fun testMapInplace()
    {
        val arr = arrayOf(0,1,2,3)
        val testList = toDynamicArray(*arr)

        testList.mapInPlace {it*2 } // testList = [0,2,4,6]

        Assertions.assertEquals(0, testList[0])
        Assertions.assertEquals(2, testList[1])
        Assertions.assertEquals(4, testList[2])
        Assertions.assertEquals(6, testList[3])

        testList.mapInPlace {it*3 } // testList = [0,6,12,18]
        Assertions.assertEquals(0, testList[0])
        Assertions.assertEquals(6, testList[1])
        Assertions.assertEquals(12, testList[2])
        Assertions.assertEquals(18, testList[3])

    }

    @Test
    fun testFilter()
    {
        val arr = arrayOf(-2,-1,0,1,2)
        val testList = toDynamicArray(*arr)

        val newList1 = testList.filter { it >= 0 } // newList1 = [0,1,2]
        val newList2 = testList.filter { it < 0 } // newList2 = [-2,-1]
        Assertions.assertEquals(0, newList1[0])
        Assertions.assertEquals(1, newList1[1])
        Assertions.assertEquals(2, newList1[2])

        Assertions.assertEquals(-2, newList2[0])
        Assertions.assertEquals(-1, newList2[1])

    }


    @Test
    fun testFilterInPlace()
    {
        val arr = arrayOf(-1,0,1,2)
        val testList = toDynamicArray(*arr)

        testList.filterInPlace { it >= 0 } // testList = [0,1,2]
        Assertions.assertEquals(0, testList[0])
        Assertions.assertEquals(1, testList[1])
        Assertions.assertEquals(2, testList[2])

        testList.filterInPlace { it > 0 }   // testList = [1,2]
        Assertions.assertEquals(1, testList[0])
        Assertions.assertEquals(2, testList[1])

    }

    @Test
    fun testInitResize(){
        val testArray = arrayOf("A", "B", "C", "D", "E", "F")
        // The * operator here expands an array into the arguments
        // for a variable-argument function
        val data = toDynamicArray(*testArray)
        assert(data.toString() == "[A, B, C, D, E, F]")
    }


    /**
     * Similarly, we give you this test as well.
     */
    @Test
    fun testBasicInit(){
        val testArray = arrayOf(0,1,2,3,4,5)
        testArray.shuffle(random = secureRNG)
        val data = toDynamicArray(*testArray)
        // Useful kotlin shortcut:  Kotlin's native Arrays
        // (and also our DynamicArray) has a dynamic
        // field
        for(x in data.indices){
            assert(testArray[x] == data[x])
        }
        assert(testArray.size == data.size)
        assertFailsWith<IndexOutOfBoundsException>() {
            data[-1]
        }
        assertFailsWith<IndexOutOfBoundsException>() {
            data[data.size]
        }
    }

    @Test
    fun testBasicNullableData(){
        val data = DynamicArray<Int?>()
        for(x in 0..<5){
            data.append(x)
        }
        for(x in 5..<10){
            data.append(null)
        }
        for(x in 0..<10){
            if(x < 5){
                assert(data[x] == x)
            } else {
                assert(data[x] == null)
            }
        }
    }

    /*
     * An example of a timing test, ensuring that append() is constant time.
     * You will want other timing tests as well.
     */
    @Test
    @Timeout(5, unit = TimeUnit.SECONDS)
    fun testTiming(){
        fun internal(iterationCount : Int){
            val data: DynamicArray<Int> = toDynamicArray()
            for(x in 0..<iterationCount){
                assert(data.size == x)
                data.append(x)
                assert(data[x] == x)
            }
        }
        val small = measureTime{internal(1000)}
        val large = measureTime{internal(100000)}
        // We have it be 200 rather than 100 to account for measurement errors
        // in timing, as this is something that can routinely occur.
        assert(large < (small * 200))
    }


    /*
     * This is how we check to make sure you don't add any more
     * fields to the class and that the types are right.
     *
     * We are making this test public so you can both see how it
     * works (it is a fair bit of interesting meta-programming,
     * that is, programming to manipulate programming)
     * and to make sure that you don't add any more fields
     * to your class.
     */
    @Test
    @GradescopeAnnotation("Test Introspection", maxScore = 0)
    fun testIntrospection(){
        val fields = DynamicArray::class.members
        val allowedFields = mapOf("privateSize" to "int",
            "size" to null,
            "start" to "int",
            "storage" to "java.lang.Object[]",
            "indices" to null
        )
        for (item in fields){
            if (item is KProperty){
                if (item.name !in allowedFields){
                    println("Unknown additional varibale: "+ item.name)
                    assert(false)
                }
                val javaType = item.javaField?.type?.getTypeName()
                if(javaType != allowedFields[item.name]){
                    println("Declared " + item.name + " as incorrect type " +
                    javaType)
                    assert(false)
                }
            }
        }
    }
}