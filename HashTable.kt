package edu.ucdavis.cs.ecs036c

import kotlin.math.absoluteValue
import kotlin.system.exitProcess

class HashTable<K, V>(var initialCapacity: Int = 8) {
    data class HashTableEntry<K, V>(val key: K, var value: V, var deleted : Boolean = false);
    // The number of elements in the storage that exist, whether or not they are marked deleted
    internal var occupied = 0

    // The number of non-deleted elements.
    internal var privateSize = 0

    // And the internal storage array
    internal var storage: Array<HashTableEntry<K, V>?> = arrayOfNulls(initialCapacity)

    val size: Int
        get() = privateSize

    // An iterator of key/value pairs, done by using a sequence and calling yield
    // on each pair that is in the table and VALID
    operator fun iterator() : Iterator<Pair<K, V>> =
        sequence<Pair<K, V>> {
            for (pair in storage)
            {
               if(pair != null && !pair.deleted)
               {
                   yield(Pair(pair.key, pair.value))
               }
            }
    }.iterator()

    override fun toString() : String = this.iterator().asSequence().joinToString(prefix="{", postfix="}",
        limit = 200) { "[${it.first}/${it.second}]" }


    // Internal resize function.  It should copy all the
    // valid entries but ignore the deleted entries.
    private fun resize(){
        val oldStorage = storage
        occupied = 0
        privateSize = 0
        storage = arrayOfNulls(oldStorage.size * 2)
        for (item in oldStorage)
        {
            // copying each valid item to the new storage
            if(item != null && !item.deleted)
            {
                // this[index] internally and automatically use the storage in the HashTable class
                // this is the meaning of setup internal array in the class
                // that makes the array become a part of HashTable class itself
                // So, since it sets a internal array storage thus this[index]==storage[index]
                this[item.key] = item.value
            }
        }
    }

    operator fun contains(key: K): Boolean {
        var hash = key.hashCode().absoluteValue
        var index = hash % storage.size

        // if the node != null, search for the key
        while (storage[index] != null) {
            if (!storage[index]!!.deleted && storage[index]?.key == key) {
                return true
            }
            index = (index+1) % storage.size
        } // until it find a empty node, exit the loop
        return  false
    }

    // Get returns null if the key doesn't exist
    operator fun get(key: K): V? {
        val hash = key.hashCode().absoluteValue
        var index = hash % storage.size

        while (storage[index] != null) {
            if (!storage[index]!!.deleted && storage[index]?.key == key) {
                return storage[index]?.value
            }
            index = (index+1) % storage.size
        } // until it find a empty node, exit the loop
        return  null
    }

    // IF the key exists just update the corresponding data.
    // If the key doesn't exist, find a spot to insert it.
    // If you need to insert into a NEW entry, resize if
    // the occupancy (active & deleted entries) is >75%
    operator fun set(key: K, value: V) {
        val checkContain = contains(key)
        val hash = key.hashCode().absoluteValue
        var index = hash % storage.size

        while (storage[index] != null)
        {
            //if the key matches in the index and the item is not deleted and the key is contained
            //then juts update the value
            if(storage[index]!!.key == key && !storage[index]!!.deleted && checkContain)
            {
                storage[index]!!.value = value
                return
            }
            //if the item in the index is deleted and the key is not contained
            else if(storage[index]!!.deleted && !checkContain)
            {
                storage[index] = HashTableEntry(key, value)
                privateSize++
                return
            }
            // none of these cases applied. Open addressing
            index = (index + 1) % storage.size
        }
        //do a resize check before inserting a new entry
        if(occupied > storage.size * 0.75)
        {
            resize()
            set(key,value)
            return
        }
        //Insert the entry if the entry in this index is empty
        if (storage[index] == null)
        {
            storage[index] = HashTableEntry(key, value)
            privateSize++
            occupied++
        }

    }

    // If the key doesn't exist remove does nothing
    fun remove(key: K) {
        var index = (key.hashCode().absoluteValue) % storage.size
        while (storage[index] != null) {
            if (storage[index]?.key == key) {
                storage[index]!!.deleted = true
                privateSize--
                return
            }
            index = (index + 1) % storage.size
        } // until it find a empty node, exit the loop

    }
}