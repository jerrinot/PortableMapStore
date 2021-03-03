package info.jerrinot.portablemapstore.test.domain;

import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableFactory;

public class PersonFactory implements PortableFactory {
    @Override
    public Portable create(int classId) {
        return switch (classId) {
            case 1 -> new Person();
            default -> throw new IllegalStateException("Unexpected value: " + classId);
        };
    }
}
