package bearmaps.proj2ab;

import java.util.HashMap;
import java.util.NoSuchElementException;

/* This priority queue implementation has the following properties:
 *    1. Each item's priority needs to be provided when adding the item to the queue
 *    2. An item's priority can be changed after it is added to the queue
 *
 * The priority queue uses a binary min-heap to store nodes
 * Each node contains the item and its priority
 *
 * Definitions:
 * Binary min-heap: A binary tree that is complete and obeys the min-heap property
 * Min-heap: Every node is less than or equal to both of its children
 * Complete: Missing nodes are only at the bottom and all nodes are as far left as possible
 *
 * An example is shown below. Nodes are added as far left on the bottom level, such as when
 * adding 6. 2 is initially added to the bottom level but this violates the min-heap property,
 * therefore it bubbles to the top as its final position.
 *
 *              3                      3                      2
 *           /     \     add 6      /     \     add 2      /     \
 *          5      4    ------>    5      4    ------>    5      3
 *         /                      / \                    / \    /
 *        8                      8  6                   8  6   4
 *
 * This approach therefore efficiently sorts such that the lowest node, which represents
 * the highest priority, is always at the top of the tree and thus can be obtained quickly
 * */

/* Invariants
 *
 * */

public class ArrayHeapMinPQ<T> implements ExtrinsicMinPQ<T> {
    private Node<T>[] pq;
    private HashMap<T, Integer> item_map;
    private int capacity;
    private int size;
    private final double max_load_factor;
    private final double min_load_factor;

    private class Node<T> {
        private T item;
        private double priority;

        Node(T item, double p) {
            this.item = item;
            priority = p;
        }
    }

    public ArrayHeapMinPQ() {
        pq = new Node[16];
        item_map = new HashMap<>();
        capacity = 16;
        size = 0;
        max_load_factor = 0.75;
        min_load_factor = 0.25;
    }

    /* Adds an item with the given priority value. Throws an
     * IllegalArgumentException if item is already present.
     * You may assume that item is never null. */
    public void add(T item, double priority) {
        if (contains(item)) {
            throw new IllegalArgumentException("item already present");
        }

        pq[size] = new Node<>(item, priority);
        item_map.put(item, size);
        swim(size);
        size += 1;

        if ((double) size / capacity >= max_load_factor) {
            resize(2 * capacity);
        }
    }

    private void swim(int a) {
        if (pq[a].priority < pq[parent(a)].priority) {
            swap(a, parent(a));
            swim(parent(a));
        }
    }

    /* Returns the parent node index of the provided node */
    private int parent(int child) {
        return (child - 1) / 2;
    }

    /* Swaps the content */
    private void swap(int a, int b) {
        Node<T> temp = pq[a];
        pq[a] = pq[b];
        pq[b] = temp;
        item_map.put(pq[a].item, a);
        item_map.put(pq[b].item, b);
    }

    private void resize(int new_cap) {
        //int old_cap = capacity;
        capacity = new_cap;

        Node<T>[] temp = pq;
        pq = new Node[capacity];

        System.arraycopy(temp, 0, pq, 0, size);
    }

    /* Returns true if the PQ contains the given item. */
    public boolean contains(T item) {
        return item_map.containsKey(item);
    }

    /* Returns the minimum item. Throws NoSuchElementException if the PQ is empty. */
    public T getSmallest() {
        if (size == 0) {
            throw new NoSuchElementException();
        }

        return pq[0].item;
    }

    /* Removes and returns the minimum item. Throws NoSuchElementException if the PQ is empty. */
    public T removeSmallest() {
        if (size == 0) {
            throw new NoSuchElementException();
        }

        T smallest = pq[0].item;
        item_map.remove(smallest);

        if (size == 1) {
            pq[0] = null;
            size -= 1;
            return smallest;
        }

        pq[0] = pq[size - 1];
        pq[size - 1] = null;
        size -= 1;

        item_map.put(pq[0].item, 0);

        sink(0);

        if ((double) size / capacity <= min_load_factor && capacity > 16) {
            resize(capacity / 2);
        }

        return smallest;
    }

    private void sink(int a) {
        int left = a * 2 + 1;
        int right = (a + 1) * 2;

        if (pq[left] == null) {
            return;
        }

        if (pq[right] == null) {
            if (pq[left].priority < pq[a].priority) {
                swap(a, left);
            }
        } else if (pq[left].priority <= pq[right].priority) {
            if (pq[left].priority < pq[a].priority) {
                swap(a, left);
                if (left <= parent(size - 1)) {
                    sink(left);
                }
            }
        } else if (pq[right].priority < pq[left].priority) {
            if (pq[right].priority < pq[a].priority) {
                swap(a, right);
                if (right <= parent(size - 1)) {
                    sink(right);
                }
            }
        }
    }

    /* Returns the number of items in the PQ. */
    public int size() {
        return size;
    }

    /* Changes the priority of the given item. Throws NoSuchElementException if the item
     * doesn't exist. */
    public void changePriority(T item, double priority) {
        if (!contains(item)) {
            throw new NoSuchElementException("item not in priority queue");
        }

        int i = item_map.get(item);

        double old_priority = pq[i].priority;
        pq[i].priority = priority;

        if (size == 1) {
            return;
        }

        if (priority < old_priority && i != 0) {
            swim(i);
        } else if (priority > old_priority && i <= parent(size - 1)) {
            sink(i);
        }
    }
}
