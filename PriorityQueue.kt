package edu.ucdavis.cs.ecs036c.homework7

/*
 * Class for a priority queue that supports the comparable trait
 * on elements.  It sets up to return the lowest value priority (a min heap),
 * if you want the opposite use a comparable object that is reversed.
 *
 * You could use this for implementing Dijkstra's in O(|V + E| log (V) ) time instead
 * of the default O(V^2) time.
 */
class PriorityQueue<T, P: Comparable<P>> {

    /*
     * Invariants that need to be maintained:
     *
     * priorityData must always be in heap order
     * locationData must map every data element to its
     * corresponding index in the priorityData, and
     * must not include any extraneous entries.
     *
     * You must NOT change these variable names and you MUST
     * maintain these invariants, as the autograder checks that
     * the internal structure is maintained.
     */
    val priorityData = mutableListOf<Pair<T, P>>()
    val locationData = mutableMapOf<T, Int>()

    /*
    * Size function is just the internal size of the priority queue...
    */
    val size : Int
        get() = priorityData.size



    /*
     * This is a secondary constructor that takes a series of
     * data/priority pairs.  It should put the pairs in the heap
     * and then call heapify/ensure the invariants are maintained
     */
    constructor (vararg init: Pair<T, P>) {
        var index = 0
        for (Pairs in init)
        {
            priorityData.add(Pairs)
            locationData[Pairs.first] = index
            index++
        }
        heapify()
    }

    /*
     * Heapify should ensure that the constraints are all updated.  This
     * is called by the secondary constructor.
     */
    fun heapify(){
        var startIndex = size - 1  // start with the end of the heap
        while((startIndex * 2 + 1) >= size)
        {
            // which means no left child in range, so the current node has no child
            startIndex --
        }
        // till the startIndex has at least one child
        for(i in startIndex downTo  0)
        {
            sink(i)
        }

    }

    /*
     * We support ranged-sink so that this could also be
     * used for heapsort, so sink without it just specifies
     * the range.
     */
    fun sink(i : Int) {
        sink(i, priorityData.size)
    }



    /*
     * The main sink function.  It accepts a range
     * argument, that by default is the full array, and
     * which considers that only indices < range are valid parts
     * of the heap.  This enables sink to be used for heapsort.
     */
    fun sink(i : Int, range: Int){
        val leftChildIndex = 2 * i + 1
        val rightChildIndex = 2 * i + 2

        if (leftChildIndex >= range)
        {
            return
        }

        if(rightChildIndex >= range)
        {
            // Only left child node
            if(priorityData[leftChildIndex].second < priorityData[i].second )
            {
                swap(i, leftChildIndex)
            }
            return
        }

        //Now if we get to here that means both children exist
        if(priorityData[leftChildIndex].second < priorityData[rightChildIndex].second && priorityData[leftChildIndex].second < priorityData[i].second)
        {
            swap(i, leftChildIndex)
            sink(leftChildIndex, range)
            return
        }

        if(priorityData[rightChildIndex].second < priorityData[leftChildIndex].second && priorityData[rightChildIndex].second < priorityData[i].second)
        {
            swap(i , rightChildIndex)
            sink(rightChildIndex,range)
            return
        }
    }



    /*
     *Swap function
     */
    fun swap(index:Int , child:Int)
    {
        // Temporarily save element at i
        val tmp = priorityData[index]

        // Perform swap in priorityData
        priorityData[index] = priorityData[child]
        priorityData[child] = tmp

        // update locationData with new index
        locationData[priorityData[index].first] = index
        locationData[priorityData[child].first] = child
    }





    /*
     * And the swim operation as well...
     */
    fun swim(i : Int) {
        if (i == 0)
        {
            return
        }
        var index = i

        while(index > 0)
        {
            val parentIndex = (index - 1) / 2
            if(priorityData[parentIndex].second > priorityData[i].second)
            {
                swap(parentIndex, i )
            }
            else {return }
            index = parentIndex
        }
    }


    /*
     * This pops off the data with the lowest priority.  It MUST
     * throw an exception if there is no data left.
     */
    fun pop() : T {
        if(priorityData.isEmpty())
        {
            throw NoSuchElementException()
        }
        val rootIndex = 0
        val lastIndex = size - 1
        swap(rootIndex, lastIndex)
        val result = priorityData.removeLast().first

        // recover the heap structure
        heapify()
        locationData.remove(result)

        return result
    }

    /*
     * And this function enables updating the priority of something in
     * the queue.  It should sink or swim the element as appropriate to update
     * its new priority.
     *
     * If the key doesn't exist it should create a new one
     */
    fun update(data: T, newPriority: P ) {
        // if data is not in locationData, should be null, then creat a new one
        if (!locationData.containsKey(data) )
        {
            priorityData.add(Pair(data, newPriority))
            locationData[data] = size-1
            heapify()
        }
        else
        {
            //if data is already exist, updating a newPriority
            priorityData[locationData[data]!!] = Pair(data, newPriority)
            heapify()
        }
    }

    /*
     * A convenient shortcut for update, allowing array assignment
     */
    operator fun set(data: T, newPriority: P) {
        update(data, newPriority)
    }

    /*
     * You don't need to implement this function but it is
     * strongly advised that you do so for testing purposes, to check
     * that all invariants are correct.
     */
    fun isValid() : Boolean {
        if(size == locationData.size) {
            var index = 0
            for (pair in priorityData) {
                if (locationData[pair.first] == index) {
                    if (index != 0) {
                        val parent = (index - 1) / 2
                        if (priorityData[parent].second > pair.second) {
                            return false
                        }
                    }
                }
                index++
            }
        }

        return true
    }

}
