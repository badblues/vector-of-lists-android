package badblues.vectoroflists.datastructure

public class Node<T : Comparable<T>>(val data: T){
    var next: Node<T>? = null
}