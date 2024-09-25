package edu.ucdavis.cs.ecs036c

import kotlin.random.Random

/*
 * This is declaring an "Extension Function", basically we are
 * creating a NEW method for Array<T> items.  this will
 * refer to the Array<T> it is called on.
 *
 * We allow an optional comparison function, and this does NOT NEED TO BE
 * a stable sort.
 */
fun <T: Comparable<T>> Array<T>.selectionSort(reverse: Boolean = false) : Array<T>{
    if(reverse){
        return this.selectionSortWith(object: Comparator<T> {
            override fun compare(a: T, b: T): Int {
                return b.compareTo(a)
            }})
    }
    return this.selectionSortWith(object: Comparator<T> {
        override fun compare(a: T, b: T): Int {
            return a.compareTo(b)
        }})
}

fun <T: Comparable<T>> Array<T>.selectionSortWith(comp: Comparator<in T>) : Array<T> {
    for(i in 0..<size){
        var min = this[i]
        var minAt = i
        for(j in i..<size){
            val compare = comp.compare(this[j], min)
            if(compare < 0){
                min = this[j]
                minAt = j
            }
        }
        this[minAt] = this[i]
        this[i] = min
    }
    return this
}

fun <T: Comparable<T>> Array<T>.quickSort(reverse: Boolean = false) : Array<T>{
    if(reverse){
        return this.quickSortWith(object: Comparator<T> {
            override fun compare(a: T, b: T): Int {
                return b.compareTo(a)
            }})
    }
    return this.quickSortWith(object: Comparator<T> {
        override fun compare(a: T, b: T): Int {
            return a.compareTo(b)
        }})
}


/*
 * Here is the QuickSort function you need to implement
 */
fun <T> Array<T>.quickSortWith( comp: Comparator<in T>) : Array<T> {
    fun partition(low: Int, high: Int): Int {
        val pivot = this[high]
        var i = low - 1
        for (j in low until high) {
            if (comp.compare(this[j], pivot) <= 0) {
                i++
                val temp = this[i]
                this[i] = this[j]
                this[j] = temp
            }
        }
        val temp = this[i+1]
        this[i+1] = this[high]
        this[high] = temp
        return i+1
    }

    fun quickSortInternal(low: Int, high: Int) {
        if (low < high) {
            val pi = partition(low, high)
            quickSortInternal(low, pi-1)
            quickSortInternal(pi+1, high)
        }
    }

    quickSortInternal(0, this.lastIndex)
    return this
}

