package ust.tad.modelsservice.technologyagnosticdeploymentmodel.yamlserializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

/**
 * The descriptions of the component and relation types of the DeMAF type system and of their
 * property definitions, read from the copies of the type definitions in the resources
 * (typeDefinitions/). Types and properties are identified by their names.
 */
public final class TypeSystem {

    public static final String COMPONENT_TYPES = "component_types";
    public static final String RELATION_TYPES = "relation_types";

    private static final Map<String, Map<String, Map<String, Object>>> TYPES = new HashMap<>();

    static {
        load("typeDefinitions/edmmTypes.yaml", COMPONENT_TYPES);
        load("typeDefinitions/relationTypes.yaml", RELATION_TYPES);
    }

    private TypeSystem() {
    }

    /**
     * The description of a type of the type system.
     *
     * @param kind COMPONENT_TYPES or RELATION_TYPES
     * @param typeName the name of the type
     * @return the description, or empty if the type system has no such type or no description
     */
    public static Optional<String> description(String kind, String typeName) {
        return Optional.ofNullable(definition(kind, typeName).get("description")).map(Object::toString);
    }

    /**
     * The description of a property definition of a type of the type system.
     *
     * @param kind COMPONENT_TYPES or RELATION_TYPES
     * @param typeName the name of the type
     * @param key the key of the property
     * @return the description, or empty if the type has no such property or no description
     */
    @SuppressWarnings("unchecked")
    public static Optional<String> propertyDescription(String kind, String typeName, String key) {
        Object properties = definition(kind, typeName).get("properties");
        if (properties instanceof List) {
            for (Object entry : (List<Object>) properties) {
                if (entry instanceof Map && ((Map<String, Object>) entry).get(key) instanceof Map) {
                    Object description = ((Map<String, Map<String, Object>>) entry).get(key).get("description");
                    return Optional.ofNullable(description).map(Object::toString);
                }
            }
        }
        return Optional.empty();
    }

    private static Map<String, Object> definition(String kind, String typeName) {
        return TYPES.getOrDefault(kind, Map.of()).getOrDefault(typeName, Map.of());
    }

    @SuppressWarnings("unchecked")
    private static void load(String resource, String kind) {
        try (InputStream stream = TypeSystem.class.getClassLoader().getResourceAsStream(resource)) {
            if (stream == null) {
                throw new IllegalStateException("Type definitions not found: " + resource);
            }
            Map<String, Object> file = new ObjectMapper(new YAMLFactory()).readValue(stream, Map.class);
            Map<String, Map<String, Object>> types = new HashMap<>();
            for (Object entry : (List<Object>) file.getOrDefault(kind, List.of())) {
                for (Map.Entry<String, Object> type : ((Map<String, Object>) entry).entrySet()) {
                    types.put(type.getKey(), (Map<String, Object>) type.getValue());
                }
            }
            TYPES.put(kind, types);
        } catch (IOException e) {
            throw new UncheckedIOException("Type definitions cannot be read: " + resource, e);
        }
    }
}
