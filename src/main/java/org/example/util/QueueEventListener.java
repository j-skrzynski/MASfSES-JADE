package org.example.util;

import java.util.EventListener;

public interface QueueEventListener<E> extends EventListener {
    public void onAdd(E element);

    public void onRemove(E element);
}
