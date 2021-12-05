package org.briarheart.algorithm.search.util;

import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;

/**
 * @author Roman Chigvintsev
 */
public class SimpleBinarySearchTree<K extends Comparable<? super K>, V> implements BinarySearchTree<K, V> {
    protected Node<K, V> root;

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public int size() {
        return size(root);
    }

    @Override
    public int size(K lo, K hi) {
        if (lo == null)
            throw new IllegalArgumentException("Low key cannot be null");
        if (hi == null)
            throw new IllegalArgumentException("High key cannot null");

        if (lo.compareTo(hi) > 0)
            return 0;

        if (contains(hi))
            return rank(hi) - rank(lo) + 1;
        return rank(hi) - rank(lo);
    }

    @Override
    public int height() {
        return height(root);
    }

    @Override
    public boolean contains(K key) {
        if (key == null)
            throw new IllegalArgumentException("Key cannot be null");
        return get(key) != null;
    }

    @Override
    public V get(K key) {
        if (key == null)
            throw new IllegalArgumentException("Key cannot be null");
        return get(root, key);
    }

    @Override
    public void put(K key, V value) {
        if (key == null)
            throw new IllegalArgumentException("Key cannot be null");

        if (value == null) {
            remove(key);
            return;
        }

        root = put(root, key, value);
    }

    @Override
    public void remove(K key) {
        if (key == null)
            throw new IllegalArgumentException("Key cannot be null");
        root = remove(root, key);
    }

    @Override
    public void removeMin() {
        if (isEmpty())
            throw new NoSuchElementException("Tree is empty");
        root = removeMin(root);
    }

    @Override
    public void removeMax() {
        if (isEmpty())
            throw new NoSuchElementException("Tree is empty");
        root = removeMax(root);
    }

    @Override
    public K min() {
        if (isEmpty())
            throw new NoSuchElementException("Tree is empty");
        return min(root).key;
    }

    @Override
    public K max() {
        if (isEmpty())
            throw new NoSuchElementException("Tree is empty");
        return max(root).key;
    }

    @Override
    public K floor(K key) {
        if (key == null)
            throw new IllegalArgumentException("Key cannot be null");
        if (isEmpty())
            throw new NoSuchElementException("Tree is empty");
        Node<K, V> node = floor(root, key);
        return node == null ? null : node.key;
    }

    @Override
    public K ceiling(K key) {
        if (key == null)
            throw new IllegalArgumentException("Key cannot be null");
        if (isEmpty())
            throw new NoSuchElementException("Tree is empty");
        Node<K, V> node = ceiling(root, key);
        return node == null ? null : node.key;
    }

    @Override
    public K select(int k) {
        if (k < 0 || k >= size())
            throw new IndexOutOfBoundsException("Index of key must be positive and smaller than size of tree");
        return select(root, k).key;
    }

    @Override
    public int rank(K key) {
        if (key == null)
            throw new IllegalArgumentException("Key cannot be null");
        return rank(key, root);
    }

    @Override
    public Iterable<K> keys() {
        return keys(min(), max());
    }

    @Override
    public Iterable<K> keys(K lo, K hi) {
        if (lo == null)
            throw new IllegalArgumentException("Low key cannot be null");
        if (hi == null)
            throw new IllegalArgumentException("High key cannot null");

        Queue<K> queue = new LinkedList<>();
        keys(root, queue, lo, hi);
        return queue;
    }

    public Iterable<K> levelOrder() {
        Queue<K> keys = new LinkedList<>();
        Queue<Node<K, V>> nodes = new LinkedList<>();
        nodes.offer(root);

        while (!nodes.isEmpty()) {
            Node<K, V> node = nodes.poll();
            if (node == null)
                continue;

            keys.offer(node.key);
            nodes.offer(node.left);
            nodes.offer(node.right);
        }

        return keys;
    }

    protected int size(Node<K, V> node) {
        return node == null ? 0 : node.size;
    }

    protected int height(Node<K, V> node) {
        if (node == null)
            return -1;
        return Math.max(height(node.left), height(node.right)) + 1;
    }

    protected V get(Node<K, V> node, K key) {
        while (node != null) {
            int cmp = key.compareTo(node.key);
            if (cmp < 0)
                node = node.left;
            else if (cmp > 0)
                node = node.right;
            else
                return node.value;
        }
        return null;
    }

    protected Node<K, V> put(Node<K, V> node, K key, V value) {
        if (node == null)
            return new Node<>(key, value);
        int cmp = key.compareTo(node.key);
        if (cmp < 0)
            node.left = put(node.left, key, value);
        else if (cmp > 0)
            node.right = put(node.right, key, value);
        else
            node.value = value;
        updateSize(node);
        return node;
    }

    protected Node<K, V> remove(Node<K, V> node, K key) {
        if (node == null)
            return null;
        int cmp = key.compareTo(node.key);
        if (cmp < 0)
            node.left = remove(node.left, key);
        else if (cmp > 0)
            node.right = remove(node.right, key);
        else {
            if (node.right == null)
                return node.left;
            if (node.left == null)
                return node.right;
            Node<K, V> t = node;
            node = min(t.right);
            node.right = removeMin(t.right);
            node.left = t.left;
        }
        updateSize(node);
        return node;
    }

    protected Node<K, V> removeMin(Node<K, V> node) {
        if (node.left == null)
            return node.right;
        node.left = removeMin(node.left);
        updateSize(node);
        return node;
    }

    protected Node<K, V> removeMax(Node<K, V> node) {
        if (node.right == null)
            return node.left;
        node.right = removeMax(node.right);
        updateSize(node);
        return node;
    }

    protected Node<K, V> min(Node<K, V> node) {
        if (node.left == null)
            return node;
        return min(node.left);
    }

    protected Node<K, V> max(Node<K, V> node) {
        if (node.right == null)
            return node;
        return max(node.right);
    }

    protected Node<K, V> floor(Node<K, V> node, K key) {
        if (node == null)
            return null;
        int cmp = key.compareTo(node.key);
        if (cmp == 0)
            return node;
        if (cmp < 0)
            return floor(node.left, key);
        Node<K, V> result = floor(node.right, key);
        if (result != null)
            return result;
        return node;
    }

    protected Node<K, V> ceiling(Node<K, V> node, K key) {
        if (node == null)
            return null;
        int cmp = key.compareTo(node.key);
        if (cmp == 0)
            return node;
        if (cmp < 0) {
            Node<K, V> result = ceiling(node.left, key);
            if (result != null)
                return result;
            return node;
        }
        return ceiling(node.right, key);
    }

    protected Node<K, V> select(Node<K, V> node, int k) {
        int lSize = size(node.left);
        if (lSize > k)
            return select(node.left, k);
        if (lSize < k)
            return select(node.right, k - lSize - 1);
        return node;
    }

    protected int rank(K key, Node<K, V> node) {
        if (node == null)
            return 0;
        int cmp = key.compareTo(node.key);
        if (cmp < 0)
            return rank(key, node.left);
        if (cmp > 0)
            return size(node.left) + rank(key, node.right) + 1;
        return size(node.left);
    }

    protected void keys(Node<K, V> node, Queue<K> queue, K lo, K hi) {
        if (node == null)
            return;
        int loCmp = lo.compareTo(node.key);
        int hiCmp = hi.compareTo(node.key);
        if (loCmp < 0)
            keys(node.left, queue, lo, hi);
        if (loCmp <= 0 && hiCmp >= 0)
            queue.offer(node.key);
        if (hiCmp > 0)
            keys(node.right, queue, lo, hi);
    }

    protected void updateSize(Node<K, V> node) {
        node.size = size(node.left) + size(node.right) + 1;
    }

    protected static class Node<K, V> {
        protected K key;
        protected V value;
        protected Node<K, V> left;
        protected Node<K, V> right;
        protected int size;

        public Node(K key, V value) {
            this(key, value, 1);
        }

        public Node(K key, V value, int size) {
            this.key = key;
            this.value = value;
            this.size = size;
        }
    }
}
