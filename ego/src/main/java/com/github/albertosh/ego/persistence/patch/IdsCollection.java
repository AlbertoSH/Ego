package com.github.albertosh.ego.persistence.patch;

import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 * Utility class. It delegates every task on an inner {@see List} Used for patch operations when a
 * collection of ids is required
 */
public class IdsCollection implements Collection<ObjectId> {

    private final List<ObjectId> delegate = new ArrayList<>();

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return delegate.contains(o);
    }

    @Override
    public Iterator<ObjectId> iterator() {
        return delegate.iterator();
    }

    @Override
    public Object[] toArray() {
        return delegate.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return delegate.toArray(a);
    }

    @Override
    public boolean add(ObjectId objectId) {
        return delegate.add(objectId);
    }

    @Override
    public boolean remove(Object o) {
        return delegate.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return delegate.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends ObjectId> c) {
        return delegate.addAll(c);
    }

    public boolean addAll(int index, Collection<? extends ObjectId> c) {
        return delegate.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return delegate.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return delegate.retainAll(c);
    }

    public void replaceAll(UnaryOperator<ObjectId> operator) {
        delegate.replaceAll(operator);
    }

    public void sort(Comparator<? super ObjectId> c) {
        delegate.sort(c);
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public boolean equals(Object o) {
        return delegate.equals(o);
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    public ObjectId get(int index) {
        return delegate.get(index);
    }

    public ObjectId set(int index, ObjectId element) {
        return delegate.set(index, element);
    }

    public void add(int index, ObjectId element) {
        delegate.add(index, element);
    }

    public ObjectId remove(int index) {
        return delegate.remove(index);
    }

    public int indexOf(Object o) {
        return delegate.indexOf(o);
    }

    public int lastIndexOf(Object o) {
        return delegate.lastIndexOf(o);
    }

    public ListIterator<ObjectId> listIterator() {
        return delegate.listIterator();
    }

    public ListIterator<ObjectId> listIterator(int index) {
        return delegate.listIterator(index);
    }

    public List<ObjectId> subList(int fromIndex, int toIndex) {
        return delegate.subList(fromIndex, toIndex);
    }

    @Override
    public Spliterator<ObjectId> spliterator() {
        return delegate.spliterator();
    }

    @Override
    public boolean removeIf(Predicate<? super ObjectId> filter) {
        return delegate.removeIf(filter);
    }

    @Override
    public Stream<ObjectId> stream() {
        return delegate.stream();
    }

    @Override
    public Stream<ObjectId> parallelStream() {
        return delegate.parallelStream();
    }

    @Override
    public void forEach(Consumer<? super ObjectId> action) {
        delegate.forEach(action);
    }
}
