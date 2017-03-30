package org.davidmoten.gt.btree.ro;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.github.davidmoten.rx2.flowable.Transformers;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.functions.Function;

public class Creator<Entry, Key> {

    private static final int FILE_NUM_SIZE = 4;
    private static final int FILE_POSITION_SIZE = 4;
    private final KeySerializer<Key> keySerializer;
    private final Serializer<Entry> entrySerializer;
    private final int maxPageSizeBytes;
    private final int nodeMaxChildren;
    private final Function<Entry, Key> keyMapper;
    private final AtomicInteger fileNumber = new AtomicInteger();
    private final File directory;
    private final String prefix;

    public Creator(KeySerializer<Key> keySerializer, Serializer<Entry> entrySerializer, int maxPageSizeBytes,
            int nodeMaxChildren, Function<Entry, Key> keyMapper, File directory, String prefix) {
        this.keySerializer = keySerializer;
        this.entrySerializer = entrySerializer;
        this.maxPageSizeBytes = maxPageSizeBytes;
        this.nodeMaxChildren = nodeMaxChildren;
        this.keyMapper = keyMapper;
        this.directory = directory;
        this.prefix = prefix;
    }

    /**
     * Returns the list of files holding the b-tree pages. The first file in the
     * list is the root of the b-tree.
     * 
     * @param entries
     * @return
     */
    public List<Integer> persist(Flowable<Entry> entries) {
        int keyRecordSize = keySerializer.size() + FILE_NUM_SIZE + FILE_POSITION_SIZE;
        int children = (int) Math.floor(Math.sqrt(maxPageSizeBytes / keyRecordSize));
        return entries.map(entry -> serialize(entry)) //
                .compose(splitIntoPages()) //
                .flatMapIterable(list -> toKeyFilePositions(list, writeToFile(list))) //
                .buffer(children * children) //
                .map(list -> {
                    int fileNum = fileNumber.incrementAndGet();
                    File file = new File(directory, prefix + fileNum);
                    try (OutputStream fos = new BufferedOutputStream(new FileOutputStream(file))) {
                        int j = 0;
                        for (int i = 0; i < list.size(); i += children) {
                            KeyFilePosition<Key> k = list.get(i);
                            keySerializer.serialize(k.key);
                            fos.write(keySerializer.serialize(k.key));
                            fos.write(intToByteArray(fileNum));
                            fos.write(intToByteArray((j + 1) * children * keyRecordSize));
                            j++;
                        }
                        for (int i = 0; i < list.size(); i += 1) {
                            KeyFilePosition<Key> k = list.get(i);
                            keySerializer.serialize(k.key);
                            fos.write(keySerializer.serialize(k.key));
                            fos.write(intToByteArray(k.fileNumber));
                            fos.write(intToByteArray(k.position));
                            j++;
                        }
                    }
                    return fileNum;
                }) //
                .toList() //
                .blockingGet();
    }

    private static final byte[] intToByteArray(int value) {
        return new byte[] { (byte) (value >>> 24), (byte) (value >>> 16), (byte) (value >>> 8), (byte) value };
    }

    private SerializedEntry<Entry> serialize(Entry entry) throws IOException {
        return new SerializedEntry<Entry>(entry, entrySerializer.serialize(entry));
    }

    private FlowableTransformer<SerializedEntry<Entry>, List<SerializedEntry<Entry>>> splitIntoPages() {
        return Transformers.<SerializedEntry<Entry>>toListWhile( //
                (list, t) -> {
                    int size = list.stream() //
                            .map(entry -> entry.bytes.length) //
                            .collect(Collectors.summingInt(x -> x));
                    return size + t.bytes.length <= maxPageSizeBytes || list.size() == 0;
                });
    }

    private List<KeyFilePosition<Key>> toKeyFilePositions(List<SerializedEntry<Entry>> list, int fileNumber) {
        int[] position = new int[1];
        return list.stream().map(en -> {
            Key key;
            try {
                key = keyMapper.apply(en.entry);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            KeyFilePosition<Key> kp = new KeyFilePosition<Key>(key, fileNumber, position[0]);
            position[0] += en.bytes.length;
            return kp;
        }).collect(Collectors.toList());
    }

    private int writeToFile(List<SerializedEntry<Entry>> list) throws Exception, IOException, FileNotFoundException {
        int size = list.stream() //
                .map(entry -> entry.bytes.length) //
                .collect(Collectors.summingInt(x -> x));
        byte[] bytes = new byte[size];
        int i = 0;
        for (SerializedEntry<Entry> en : list) {
            System.arraycopy(en.bytes, 0, bytes, i, en.bytes.length);
            i += en.bytes.length;
        }
        int fileNumber = this.fileNumber.incrementAndGet();
        File file = new File(directory, prefix + fileNumber);
        try (OutputStream fos = new BufferedOutputStream(new FileOutputStream(file))) {
            fos.write(bytes);
        }
        return fileNumber;
    }

    private static final class KeyFilePosition<Key> {
        final Key key;
        final int fileNumber;
        final int position;

        KeyFilePosition(Key key, int fileNumber, int position) {
            this.key = key;
            this.fileNumber = fileNumber;
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
