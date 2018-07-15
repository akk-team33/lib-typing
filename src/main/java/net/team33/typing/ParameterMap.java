package net.team33.typing;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import static java.util.Collections.unmodifiableList;

class ParameterMap extends AbstractMap<String, Generic<?>> {

    private final List<String> formal;
    private final List<Generic<?>> actual;

    ParameterMap(final List<String> formal, final List<Generic<?>> actual) {
        this.formal = unmodifiableList(new ArrayList<>(formal));
        this.actual = unmodifiableList(new ArrayList<>(actual));
    }

    @Override
    public final Set<Entry<String, Generic<?>>> entrySet() {
        return new EntrySet();
    }

    private class EntrySet extends AbstractSet<Entry<String, Generic<?>>> {

        @Override
        public final Iterator<Entry<String, Generic<?>>> iterator() {
            return new EntryIterator();
        }

        @Override
        public final int size() {
            return formal.size();
        }
    }

    private class EntryIterator implements Iterator<Entry<String, Generic<?>>> {

        private int index = 0;

        @Override
        public final boolean hasNext() {
            return index < formal.size();
        }

        @Override
        public final Entry<String, Generic<?>> next() {
            try {
                final Entry<String, Generic<?>> result = new SimpleImmutableEntry<String, Generic<?>>(
                        formal.get(index),
                        actual.get(index)
                );
                index += 1;
                return result;
            } catch (final IndexOutOfBoundsException caught) {
                final NoSuchElementException toThrow = new NoSuchElementException(caught.getMessage());
                toThrow.addSuppressed(caught);
                throw toThrow;
            }
        }
    }
}
