package com.hasunemiku2015.minecraftnano.datastruct

/**
 * @author hasunemiku2015
 * @date 2022/09/11 19:59
 */
class SparseVector<T> (private val defaultValue: T) {
    private val dataMap = hashMapOf<Int, T>()
    private var last = -1

    constructor(vector: SparseVector<T>) : this(vector.defaultValue) {
        for ((key, value) in vector.dataMap) {
            this[key] = value
        }
    }

    fun append(data: T) {
        set(last + 1, data)
    }

    fun size(): Int {
        return last + 1
    }

    fun insert(i: Int, dat: T) {
        val list = ArrayList(this.asList())
        clear()
        list.add(i, dat)
        list.forEach {
            this.append(it)
        }
    }

    fun remove(i: Int) {
        val list = ArrayList(this.asList())
        clear()
        list.removeAt(i)
        list.forEach {
            this.append(it)
        }
    }

    fun contains(data: T): Boolean {
        if (last == -1) {
            return false
        }
        if (data == defaultValue) {
            for (i in 0..last) {
                if (!dataMap.containsKey(i)) {
                    return true
                }
            }
            return false
        } else {
            return dataMap.containsValue(data)
        }
    }

    private fun clear() {
        last = -1
        dataMap.clear()
    }

    fun asList(minSize: Int): List<T> {
        val out = arrayListOf<T>()
        if (last == -1) {
            return out
        }
        for (i in 0..last.coerceAtLeast(minSize)) {
            out.add(dataMap[i] ?: defaultValue)
        }
        return out
    }

    fun asList(): List<T> {
        return asList(0)
    }

    fun first(): T {
        return get(0)
    }

    fun last(): T {
        return get(last)
    }

    override fun toString(): String {
        return this.asList().toString()
    }

    operator fun get(position: Int): T {
        return dataMap[position] ?: defaultValue
    }

    operator fun set(position: Int, data: T) {
        if (position > last) {
            last = position
        }

        if (data != defaultValue) {
            dataMap[position] = data
        } else if (dataMap.containsKey(position)) {
            dataMap.remove(position)
        }
    }

    operator fun iterator(): Iterator<T> {
        return asList().iterator()
    }
}