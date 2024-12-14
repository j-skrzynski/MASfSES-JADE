package org.example.util;

import java.util.*;

public class ListenableQueue<E> extends AbstractQueue<E> {
    private final Queue<E> _queue;
    private final List<QueueEventListener<E>> _listeners;

    public ListenableQueue(Queue<E> queue) {
        assert queue != null;
        _queue = queue;
        _listeners = new ArrayList<>();
    }

    public ListenableQueue<E> addListener(QueueEventListener<E> listener) {
        _listeners.add(listener);
        return this;
    }

    @Override
    public boolean offer(E element) {
        if (_queue.offer(element)) {
            _listeners.forEach(listener -> listener.onAdd(element));
            return true;
        }

        return false;
    }

    @Override
    public E poll() {
        E removedElement = _queue.poll();
        if (removedElement != null) {
            _listeners.forEach(listener -> listener.onRemove(removedElement));
        }

        return removedElement;
    }

    @Override
    public E peek() {
        return _queue.peek();
    }

    @Override
    public boolean isEmpty() {
        return _queue.isEmpty();
    }

    @Override
    public int size() {
        return _queue.size();
    }

    @Override
    public Iterator<E> iterator() {
        return _queue.iterator();
    }
}
