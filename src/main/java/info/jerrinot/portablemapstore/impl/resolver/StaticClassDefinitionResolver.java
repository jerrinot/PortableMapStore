package info.jerrinot.portablemapstore.impl.resolver;

import com.hazelcast.nio.serialization.ClassDefinition;

import java.util.Set;

public final class StaticClassDefinitionResolver implements ClassDefinitionResolver {
    private final Set<ClassDefinition> staticDefinitions;

    public StaticClassDefinitionResolver(Set<ClassDefinition> staticDefinitions) {
        this.staticDefinitions = staticDefinitions;
    }

    @Override
    public ClassDefinition resolve(int factoryId, int classId, String tableName) {
        return staticDefinitions
                .stream()
                .filter(cd -> cd.getFactoryId() == factoryId && cd.getClassId() == classId)
                .findFirst()
                .orElse(null);
    }
}
