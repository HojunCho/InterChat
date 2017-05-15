package com.network_project.interchat.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Concurrent한 Set을 지원하기 위해 제작한 Concurrent Hash Set.
 * 내부적으로는 이미 존재하는 ConcurrentHashMap을 이용하였다.
 * @param <T> 요소의 타입
 */
public class ConcurrentHashSet<T> implements Set<T> {
	private Map<T, Boolean> map = new ConcurrentHashMap<T, Boolean>();

	@Override
	public boolean add(T e) {
		return map.putIfAbsent(e, Boolean.TRUE) == null;
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		for (T e : c)
            add(e);
		return true;
	}

	@Override
	public void clear() {
		map.clear();
	}

	@Override
	public boolean contains(Object o) {
		return map.containsKey(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object o : c) {
            if (!contains(o))
                return false;
        }
        return true;
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {
            private Iterator<Map.Entry<T, Boolean>> iterator = map.entrySet().iterator();

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public T next() {
                return iterator.next().getKey();
            }

            @Override
            public void remove() {
            	throw new UnsupportedOperationException();
            }
        };
	}

	@Override
	public boolean remove(Object o) {
		Boolean ret = map.remove(o);
		return ret != null && ret;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		for (Object o : c)
            remove(o);
		return true;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return false;
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public Object[] toArray() {
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings("hiding")
	@Override
	public <T> T[] toArray(T[] a) {
		throw new UnsupportedOperationException();
	}
}
