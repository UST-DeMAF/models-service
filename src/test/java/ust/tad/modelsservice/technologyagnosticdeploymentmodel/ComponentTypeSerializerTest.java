package ust.tad.modelsservice.technologyagnosticdeploymentmodel;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import ust.tad.modelsservice.technologyagnosticdeploymentmodel.entities.ComponentType;
import ust.tad.modelsservice.technologyagnosticdeploymentmodel.entities.Operation;
import ust.tad.modelsservice.technologyagnosticdeploymentmodel.entities.Property;
import ust.tad.modelsservice.technologyagnosticdeploymentmodel.entities.PropertyType;
import ust.tad.modelsservice.technologyagnosticdeploymentmodel.yamlserializer.YamlObjectMapper;

@SpringBootTest
public class ComponentTypeSerializerTest {
    
    @Test
    public void serializeBaseComponentType_correct() throws JsonProcessingException {
        String expected = 
            "---\n"
            + "BaseType:\n"
            + "  extends: \"-\"\n"
            + "  description: \"The default component type from which all other component types derive.\"\n"
            + "  properties: []\n"
            + "  operations: []\n";

        ComponentType baseType = new ComponentType("BaseType", "This is the base type", List.of(), List.of(), null);
    
        ObjectMapper mapper = YamlObjectMapper.createYamlObjectMapper();
        String yaml = mapper.writeValueAsString(baseType);
    
        assertEquals(expected, yaml);
    }

    @Test
    public void serializeComponentTypes_correct() throws JsonProcessingException {
        String expected = 
            "---\n"
            + "TypeOne:\n"
            + "  extends: \"BaseType\"\n"
            + "  description: \"type one\"\n"
            + "  properties:\n"
            + "    - testProperty:\n"
            + "        type: \"STRING\"\n"
            + "        required: true\n"
            + "        default_value: \"testValue\"\n"
            + "  operations:\n"
            + "    - Create: \"~\"\n";

        Property property = assertDoesNotThrow(() -> new Property("testProperty", PropertyType.STRING, true, "testValue", null));
        Operation operationDef = new Operation("Create", null, null);

        ComponentType baseType = new ComponentType("BaseType", "This is the base type", List.of(), List.of(), null);
        ComponentType componentTypeOne = new ComponentType("TypeOne", "type one", List.of(property), List.of(operationDef), baseType);

        ObjectMapper mapper = YamlObjectMapper.createYamlObjectMapper();
        String yaml = mapper.writeValueAsString(componentTypeOne);

        assertEquals(expected, yaml);
    }
    

    @Test
    public void serializeTypeSystemComponentType_descriptionsOfTypeSystem() throws JsonProcessingException {
        String expected =
            "---\n"
            + "Storage:\n"
            + "  extends: \"BaseType\"\n"
            // Long strings are wrapped with an escaped line break, as everywhere in the YAML output
            + "  description: \"A storage volume that is attached to a component and holds the component's\\\n"
            + "    \\ data persistently.\"\n"
            + "  properties:\n"
            + "    - storage_size:\n"
            + "        type: \"STRING\"\n"
            + "        description: \"The capacity of the storage volume.\"\n"
            + "        required: true\n"
            + "        default_value: \"\"\n"
            + "  operations: []\n";

        Property storageSize = assertDoesNotThrow(() -> new Property("storage_size", PropertyType.STRING, true, "", null));
        ComponentType baseType = new ComponentType("BaseType", null, List.of(), List.of(), null);
        ComponentType storage = new ComponentType("Storage", null, List.of(storageSize), List.of(), baseType);

        ObjectMapper mapper = YamlObjectMapper.createYamlObjectMapper();
        String yaml = mapper.writeValueAsString(storage);

        assertEquals(expected, yaml);
    }

    @Test
    public void serializeConcreteComponentType_ownDescription() throws JsonProcessingException {
        String expected =
            "---\n"
            + "postgres-DatabaseSystem:\n"
            + "  extends: \"DatabaseSystem\"\n"
            + "  description: null\n"
            + "  properties: []\n"
            + "  operations: []\n";

        ComponentType databaseSystem = new ComponentType("DatabaseSystem", null, List.of(), List.of(), null);
        ComponentType postgres = new ComponentType("postgres-DatabaseSystem", null, List.of(), List.of(), databaseSystem);

        ObjectMapper mapper = YamlObjectMapper.createYamlObjectMapper();
        String yaml = mapper.writeValueAsString(postgres);

        assertEquals(expected, yaml);
    }
}
