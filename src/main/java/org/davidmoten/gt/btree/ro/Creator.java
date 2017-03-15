package org.davidmoten.gt.btree.ro;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import com.github.davidmoten.rx2.flowable.Transformers;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;

public class Creator<Entry, Key> {

    private final Serializer<Key> keySerializer;
    private final Serializer<Entry> entrySerializer;
    private final int maxPageSizeBytes;
    private final int nodeMaxChildren;
    private final Function<Entry, Key> keyMapper;

    public Creator(Serializer<Key> keySerializer, Serializer<Entry> entrySerializer,
            int maxPageSizeBytes, int nodeMaxChildren, Function<Entry, Key> keyMapper) {
        this.keySerializer = keySerializer;
        this.entrySerializer = entrySerializer;
        this.maxPageSizeBytes = maxPageSizeBytes;
        this.nodeMaxChildren = nodeMaxChildren;
        this.keyMapper = keyMapper;
    }

    public List<File> persist(Flowable<Entry> entries) {
        entries.map(entry -> new SerializedEntry<Entry>(entry, entrySerializer.serialize(entry)))
                .compose(Transformers.<SerializedEntry<Entry>> toListWhile( //
                        (list, t) -> {
                            int size = list.stream().map(entry -> entry.bytes.length)
                                    .collect(Collectors.summingInt(x -> x));
                            return size + t.bytes.length <= maxPageSizeBytes;
                        }));
        return null;
    }

    private static class SerializedEntry<Entry> {
        final Entry entry;
        final byte[] bytes;

        SerializedEntry(Entry entry, byte[] bytes) {
            super();
            this.entry = entry;
            this.bytes = bytes;
        }

    }

}
