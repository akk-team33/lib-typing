package de.team33.libs.typing.v3;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

class ParameterMap extends AbstractMap<String, Type<?>> {

    static final ParameterMap EMPTY = new ParameterMap();

    private final List<String> formal;
    private final List<Type<?>> actual;

    ParameterMap(final List<String> formal, final List<Type<?>> actual) {
        if (formal.size() == actual.size()) {
            this.formal = unmodifiableList(new ArrayList<>(formal));
            this.actual = unmodifiableList(new ArrayList<>(actual));
        } else {
            throw new IllegalArgumentException(String.format(
                    "formal and actual must match in size but was%n\tformal: %s%n\tactual: %s", formal, actual));
        }
    }

    private ParameterMap() {
        this.formal = emptyList();
        this.actual = emptyList();
    }

    final List<String> getFormal() {
        // noinspection AssignmentOrReturnOfFieldWithMutableType
        return formal;
    }

    final List<Type<?>> getActual() {
        // noinspection AssignmentOrReturnOfFieldWithMutableType
        return actual;
    }

    @Override
    public final Set<Entry<String, Type<?>>> entrySet() {
        return new EntrySet();
    }

    private class EntrySet extends AbstractSet<Entry<String, Type<?>>> {

        @Override
        public final Iterator<Entry<String, Type<?>>> iterator() {
            return new EntryIterator();
        }

        @Override
        public final int size() {
            return formal.size();
        }
    }

    private class EntryIterator implements Iterator<Entry<String, Type<?>>> {

        private int index = 0;

        @Override
        public final boolean hasNext() {
            return index < formal.size();
        }

        @Override
        public final Entry<String, Type<?>> next() {
            try {
                final Entry<String, Type<?>> result = new SimpleImmutableEntry<>(
                        formal.get(index),
                        actual.get(index)
                );
                index += 1;
                return result;
            } catch (final IndexOutOfBoundsException caught) {
                throw new NoSuchElementException(caught.getMessage());
            }
        }
    }
}
