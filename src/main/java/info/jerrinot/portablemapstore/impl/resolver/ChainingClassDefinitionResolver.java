package info.jerrinot.portablemapstore.impl.resolver;

import com.hazelcast.core.HazelcastException;
import com.hazelcast.nio.serialization.ClassDefinition;

public final class ChainingClassDefinitionResolver implements ClassDefinitionResolver {
    private final ClassDefinitionResolver[] resolvers;

    public ChainingClassDefinitionResolver(ClassDefinitionResolver...resolvers) {
        this.resolvers = resolvers;
    }

    @Override
    public ClassDefinition resolve(int factoryId, int classId, String tableName) {
        for (var resolver : resolvers) {
            var cd = resolver.resolve(factoryId, classId, tableName);
            if (cd != null) {
                return cd;
            }
        }
        throw new HazelcastException("class definition not found");
    }
}
