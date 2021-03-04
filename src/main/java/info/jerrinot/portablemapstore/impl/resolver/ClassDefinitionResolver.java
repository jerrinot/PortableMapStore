package info.jerrinot.portablemapstore.impl.resolver;

import com.hazelcast.nio.serialization.ClassDefinition;

public interface ClassDefinitionResolver {
    ClassDefinition resolve(int factoryId, int classId, String tableName);
}
