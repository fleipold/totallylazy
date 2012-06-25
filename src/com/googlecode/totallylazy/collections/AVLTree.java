package com.googlecode.totallylazy.collections;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Predicate;
import com.googlecode.totallylazy.Unchecked;
import com.googlecode.totallylazy.comparators.Comparators;

import java.util.Comparator;
import java.util.NoSuchElementException;

import static com.googlecode.totallylazy.Unchecked.cast;

public interface AVLTree<K, V> extends TreeMap<K, V> {
    int height();

    int balance();

    @Override
    AVLTree<K, V> cons(Pair<K, V> head);

    @Override
    AVLTree<K, V> tail() throws NoSuchElementException;

    @Override
    AVLTree<K, V> put(K key, V value);

    @Override
    AVLTree<K, V> remove(K key);

    @Override
    AVLTree<K, V> filterKeys(Predicate<? super K> predicate);

    @Override
    AVLTree<K, V> filterValues(Predicate<? super V> predicate);

    @Override
    <NewV> AVLTree<K, NewV> mapValues(Callable1<? super V, ? extends NewV> transformer);

    @Override
    Pair<AVLTree<K, V>, Pair<K, V>> removeFirst();

    @Override
    Pair<AVLTree<K, V>, Pair<K, V>> removeLast();

    @Override
    AVLTree<K, V> left();

    @Override
    AVLTree<K, V> left(TreeMap<K, V> newLeft);

    @Override
    AVLTree<K, V> right();

    @Override
    AVLTree<K, V> right(TreeMap<K, V> newRight);

    enum constructors implements TreeFactory {
        factory;

        @Override
        public <K, V> AVLTree<K, V> create(Comparator<K> comparator) {
            return new Empty<K, V>(comparator);
        }

        @Override
        public <K, V> AVLTree<K, V> create(Comparator<K> comparator, K key, V value) {
            return create(comparator, key, value, this.<K, V>create(comparator), this.<K, V>create(comparator));
        }

        @Override
        public <K, V> AVLTree<K, V> create(Comparator<K> comparator, K key, V value, TreeMap<K, V> left, TreeMap<K, V> right) {
            return methods.balance(new Node<K, V>(comparator, key, value, Unchecked.<AVLTree<K, V>>cast(left), Unchecked.<AVLTree<K, V>>cast(right)));
        }

        public static <K extends Comparable<? super K>, V> AVLTree<K, V> avlTree(K key, V value) {
            return factory.create(Comparators.<K>ascending(), key, value);
        }
    }

    class methods {
        // http://upload.wikimedia.org/wikipedia/commons/thumb/f/f5/AVL_Tree_Rebalancing.svg/350px-AVL_Tree_Rebalancing.svg.png
        private static <K, V> AVLTree<K, V> balance(AVLTree<K, V> node) {
            if (node.balance() == -2) {
                return balanceRight(node);
            }
            if (node.balance() == 2) {
                return balanceLeft(node);
            }
            return node;
        }

        private static <K, V> AVLTree<K, V> balanceLeft(AVLTree<K, V> node) {
            if (node.left().balance() == -1) {
                return balanceLeftRight(node);
            }
            if (node.left().balance() == 1) {
                return balanceLeftLeft(node);
            }
            return node;
        }

        private static <K, V> AVLTree<K, V> balanceRight(AVLTree<K, V> node) {
            if (node.right().balance() == 1) {
                return balanceRightLeft(node);
            }
            if (node.right().balance() == -1) {
                return balanceRightRight(node);
            }
            return node;
        }

        private static <K, V> AVLTree<K, V> balanceLeftLeft(AVLTree<K, V> parent) {
            AVLTree<K, V> c = parent.left().right();
            AVLTree<K, V> five = parent.left(c);
            return parent.left().right(five);
        }

        private static <K, V> AVLTree<K, V> balanceLeftRight(AVLTree<K, V> parent) {
            AVLTree<K, V> b = parent.left().right().left();
            AVLTree<K, V> three = parent.left().right(b);
            AVLTree<K, V> four = parent.left().right().left(three);
            return balanceLeftLeft(parent.left(four));
        }

        private static <K, V> AVLTree<K, V> balanceRightRight(AVLTree<K, V> parent) {
            AVLTree<K, V> b = parent.right().left();
            AVLTree<K, V> three = parent.right(b);
            return parent.right().left(three);
        }

        private static <K, V> AVLTree<K, V> balanceRightLeft(AVLTree<K, V> parent) {
            AVLTree<K, V> c = parent.right().left().right();
            AVLTree<K, V> five = parent.right().left(c);
            AVLTree<K, V> four = parent.right().left().right(five);
            return balanceRightRight(parent.right(four));
        }
    }

    final class Empty<K, V> extends AbstractEmptyTreeMap<K, V, AVLTree<K, V>> implements AVLTree<K, V> {
        public Empty(Comparator<K> comparator) {
            super(comparator, AVLTree.constructors.factory);
        }

        @Override
        public int height() {
            return 0;
        }

        @Override
        public int balance() {
            return 0;
        }

        @Override
        public <NewV> AVLTree<K, NewV> mapValues(Callable1<? super V, ? extends NewV> transformer) {
            return cast(this);
        }
    }

    final class Node<K, V> extends AbstractTreeMap<K, V, AVLTree<K, V>> implements AVLTree<K, V> {
        private final int height;

        private Node(Comparator<K> comparator, K key, V value, AVLTree<K, V> left, AVLTree<K, V> right) {
            super(comparator, key, value, left, right, AVLTree.constructors.factory);
            height = Math.max(left.height(), right.height()) + 1;
        }

        @Override
        public int height() {
            return height;
        }

        @Override
        public int balance() {
            return left().height() - right().height();
        }

        @Override
        public <NewV> AVLTree<K, NewV> mapValues(Callable1<? super V, ? extends NewV> transformer) {
            return cast(TreeMap.methods.mapValues(transformer, factory, this));
        }
    }
}
