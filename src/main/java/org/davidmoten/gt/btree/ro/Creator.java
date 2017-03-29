package org.davidmoten.gt.btree.ro;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.concurrent.Callable;
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
    private final Callable<File> fileFactory;

    public Creator(Serializer<Key> keySerializer, Serializer<Entry> entrySerializer, int maxPageSizeBytes,
            int nodeMaxChildren, Function<Entry, Key> keyMapper, Callable<File> fileFactory) {
        this.keySerializer = keySerializer;
        this.entrySerializer = entrySerializer;
        this.maxPageSizeBytes = maxPageSizeBytes;
        this.nodeMaxChildren = nodeMaxChildren;
        this.keyMapper = keyMapper;
        this.fileFactory = fileFactory;
    }

    public List<File> persist(Flowable<Entry> entries) {
        entries.map(entry -> new SerializedEntry<Entry>(entry, entrySerializer.serialize(entry)))
                .compose(Transformers.<SerializedEntry<Entry>>toListWhile( //
                        (list, t) -> {
                            int size = list.stream() //
                                    .map(entry -> entry.bytes.length) //
                                    .collect(Collectors.summingInt(x -> x));
                            return size + t.bytes.length <= maxPageSizeBytes || list.size() == 0;
                        })) //
                .map(list -> {
                    int size = list.stream() //
                            .map(entry -> entry.bytes.length) //
                            .collect(Collectors.summingInt(x -> x));
                    byte[] bytes = new byte[size];
                    int i = 0;
                    for (SerializedEntry<Entry> en : list) {
                        System.arraycopy(en.bytes, 0, bytes, i, en.bytes.length);
                        i += en.bytes.length;
                    }
                    File file = fileFactory.call();
                    try (FileOutputStream fos = new FileOutputStream(file)) {
                        fos.write(bytes);
                    }
                    int[] position = new int[1];
                    return list.stream().map(en -> {
                        Key key;
                        try {
                            key = keyMapper.apply(en.entry);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        KeyFilePosition<Key> kp = new KeyFilePosition<Key>(key, file.getName(), position[0]);
                        position[0] += en.bytes.length;
                        return kp;
                    }).collect(Collectors.toList());
                });
        return null;
    }

    private static final class KeyFilePosition<Key> {
        final Key key;
        final String filename;
        final int position;

        KeyFilePosition(Key key, String filename, int position) {
            this.key = key;
            this.filename = filename;
            this.position = position;
        }
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
