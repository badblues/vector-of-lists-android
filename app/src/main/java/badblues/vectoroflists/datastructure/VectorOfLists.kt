package badblues.vectoroflists.datastructure

import kotlin.math.pow

class VectorOfLists<T : Comparable<T>> : Iterable<T> {
    private var lists: Array<Node<T>?>
    private var listCapacity: Int
    private var vectorSize: Int
    private var size: Int

    constructor(capacity: Int) {
        require(capacity > 0)
        listCapacity = capacity
        lists = arrayOfNulls(1)
        vectorSize = 1
        size = 0
    }

    fun get(index: Int): T {
        val n = getIndexOfList(index)
        val startIndex = getMaxSize(n)
        var current = lists[n]
        for (i in startIndex until index) {
            current = current?.next
        }
        return current?.data ?: throw IndexOutOfBoundsException("Index out of range")
    }

    fun size(): Int {
        return size
    }

    fun add(item: T): Boolean {
        val newNode = Node(item)
        val maxSize = getMaxSize(vectorSize)
        if (size == maxSize) {
            increaseVectorSize()
            lists[vectorSize - 1] = newNode
        } else {
            if (lists[vectorSize - 1] == null) {
                lists[vectorSize - 1] = newNode
            } else {
                var current = lists[vectorSize - 1]
                while (current?.next != null)
                    current = current.next
                current?.next = newNode
            }
        }
        size++
        return true
    }

    fun delete(index: Int): T {
        val n = getIndexOfList(index)
        val startIndex = getMaxSize(n)
        val data: T
        if (startIndex == index) {
            data = lists[n]?.data ?: throw IndexOutOfBoundsException("Index out of range")
            lists[n] = lists[n]?.next
        } else {
            var current = lists[n]
            val indexOfPrevious = index - 1
            for (i in startIndex until indexOfPrevious) {
                current = current?.next
            }
            data = current?.next?.data ?: throw IndexOutOfBoundsException("Index out of range")
            current.next = current.next?.next
        }
        shiftLeft(n)
        size--
        return data
    }

    fun insert(index: Int, item: T) {
        val newNode = Node(item)
        val n = getIndexOfList(index)
        val startIndex = getMaxSize(n)
        if (index == startIndex) {
            newNode.next = lists[n]
            lists[n] = newNode
        } else {
            var current = lists[n]
            val indexOfPrevious = index - 1
            for (i in startIndex until indexOfPrevious) {
                current = current?.next
            }
            newNode.next = current?.next
            current?.next = newNode
        }
        shiftRight(n)
        size++
    }

    fun sort() {
        combineLists()
        lists[0] = mergeSort(lists[0])
        uncombineLists()
    }

    fun forEach(action: (T) -> Unit) {
        for (i in 0 until vectorSize) {
            var current = lists[i]
            while (current != null) {
                action(current.data)
                current = current.next
            }
        }
    }

    override fun iterator(): Iterator<T> {
        return object : Iterator<T> {
            private var currentListIndex = 0
            private var currentNode: Node<T>? = lists[currentListIndex]

            override fun hasNext(): Boolean {
                while (currentNode == null && currentListIndex < vectorSize - 1) {
                    currentListIndex++
                    currentNode = lists[currentListIndex]
                }
                return currentNode != null
            }

            override fun next(): T {
                if (!hasNext()) {
                    throw NoSuchElementException()
                }
                val nextNode = currentNode
                currentNode = currentNode?.next
                return nextNode?.data ?: throw NoSuchElementException()
            }
        }
    }


    override fun toString(): String {
        val str = StringBuilder("[")
        for (i in 0 until vectorSize) {
            var current = lists[i]
            while (current != null) {
                str.append(current.data)
                if (current.next != null)
                    str.append(", ")
                current = current.next
            }
            if (i != vectorSize - 1)
                str.append("\n")
        }
        str.append("]")
        return str.toString()
    }

    fun getBaseCapacity(): Int {
        return listCapacity
    }

    fun getSize(): Int {
        return size
    }

    private fun writeObject(outputStream: java.io.ObjectOutputStream) {
        outputStream.writeInt(listCapacity)
        outputStream.writeInt(size)
        for (i in 0 until vectorSize) {
            var current = lists[i]
            while (current != null) {
                outputStream.writeObject(current.data)
                current = current.next
            }
        }
        outputStream.flush()
    }

    private fun readObject(inputStream: java.io.ObjectInputStream) {
        listCapacity = inputStream.readInt()
        val readSize = inputStream.readInt()
        lists = arrayOfNulls(1)
        vectorSize = 1
        for (i in 0 until readSize) {
            add(inputStream.readObject() as T)
        }
    }

    private fun mergeSort(head: Node<T>?): Node<T>? {
        if (head == null || head.next == null) {
            return head
        }

        val middle = getMiddle(head)
        val leftHalf = head
        val rightHalf = middle?.next
        middle?.next = null

        val left = mergeSort(leftHalf)
        val right = mergeSort(rightHalf)

        return merge(left, right)
    }

    private fun merge(left: Node<T>?, right: Node<T>?): Node<T>? {
        var result: Node<T>? = null
        if (left == null) {
            return right
        }
        if (right == null) {
            return left
        }

        if (left.data <= right.data) {
            result = left
            result.next = merge(left.next, right)
        } else {
            result = right
            result.next = merge(left, right.next)
        }

        return result
    }

    private fun getMiddle(head: Node<T>?): Node<T>? {
        if (head == null) {
            return head
        }

        var slowPtr = head
        var fastPtr = head

        while (fastPtr?.next != null && fastPtr.next?.next != null) {
            slowPtr = slowPtr?.next
            fastPtr = fastPtr.next?.next
        }

        return slowPtr
    }

    private fun combineLists() {
        for (i in 0 until vectorSize - 1) {
            var current = lists[i]
            while (current?.next != null)
                current = current.next
            current?.next = lists[i + 1]
        }
    }

    private fun uncombineLists() {
        for (i in 0 until vectorSize - 1) {
            val max = (2.0.pow(i.toDouble()).toInt() * listCapacity)
            var current = lists[i]
            for (j in 0 until max - 1)
                current = current?.next
            lists[i + 1] = current?.next
            current?.next = null
        }
    }

    private fun increaseVectorSize() {
        val newVector = arrayOfNulls<Node<T>>(vectorSize + 1)
        for (i in 0 until vectorSize) {
            newVector[i] = lists[i]
        }
        lists = newVector
        vectorSize++
    }

    private fun decreaseVectorSize() {
        val newVector = arrayOfNulls<Node<T>>(vectorSize - 1)
        for (i in 0 until vectorSize - 1) {
            newVector[i] = lists[i]
        }
        lists = newVector
        vectorSize--
    }

    private fun shiftLeft(n: Int) {
        for (i in n until vectorSize - 1) {
            var current = lists[i]
            while (current?.next != null) {
                current = current.next
            }
            current?.next = lists[i + 1]
            lists[i + 1] = current?.next?.next
            current?.next?.next = null
        }
        if (lists[vectorSize - 1] == null)
            decreaseVectorSize()
    }

    private fun shiftRight(n: Int) {
        for (i in n until vectorSize - 1) {
            var current = lists[i]
            while (current?.next?.next != null) { // current - previous to last element
                current = current.next
            }
            val remainder = current?.next
            remainder?.next = lists[i + 1]
            current?.next = null
            lists[i + 1] = remainder
        }
    }

    private fun getIndexOfList(itemIndex: Int): Int {
        require(itemIndex >= 0) { "Index cannot be negative" }
        require(itemIndex < size) { "Invalid index" }
        var n = 0
        while (true) {
            val maxSize = (2.0.pow(n.toDouble() + 1).toInt() - 1) * listCapacity
            if (maxSize > itemIndex)
                break
            n++
        }
        return n
    }

    private fun getMaxSize(vectorSize: Int): Int {
        return (2.0.pow(vectorSize.toDouble()) - 1).toInt() * listCapacity
    }
}
