package ust.tad.modelsservice.technologyagnosticdeploymentmodel;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import ust.tad.modelsservice.technologyagnosticdeploymentmodel.entities.Property;
import ust.tad.modelsservice.technologyagnosticdeploymentmodel.entities.PropertyType;
import ust.tad.modelsservice.technologyagnosticdeploymentmodel.entities.RelationType;
import ust.tad.modelsservice.technologyagnosticdeploymentmodel.yamlserializer.YamlObjectMapper;

@SpringBootTest
public class RelationTypeSerializerTest {

    @Test
    public void serializeTypeSystemRelationType_descriptionsOfTypeSystem() throws JsonProcessingException {
        String expected =
            "---\n"
            + "AttachesTo:\n"
            + "  extends: \"DependsOn\"\n"
            // Long strings are wrapped with an escaped line break, as everywhere in the YAML output
            + "  description: \"The source component provides storage that is attached to the target\\\n"
            + "    \\ component at the path given by `location`.\"\n"
            + "  properties:\n"
            + "    - location:\n"
            + "        type: \"STRING\"\n"
            + "        description: \"The path at which the storage is available to the component.\"\n"
            + "        required: true\n"
            + "        default_value: \"\"\n"
            + "  operations: []\n";

        Property location = assertDoesNotThrow(() -> new Property("location", PropertyType.STRING, true, "", null));
        RelationType dependsOn = new RelationType("DependsOn", null, List.of(), List.of(), null);
        RelationType attachesTo = new RelationType("AttachesTo", null, List.of(location), List.of(), dependsOn);

        ObjectMapper mapper = YamlObjectMapper.createYamlObjectMapper();
        String yaml = mapper.writeValueAsString(attachesTo);

        assertEquals(expected, yaml);
    }

    @Test
    public void serializeOtherRelationType_ownDescription() throws JsonProcessingException {
        String expected =
            "---\n"
            + "MyRelation:\n"
            + "  extends: \"DependsOn\"\n"
            + "  description: \"my relation\"\n"
            + "  properties: []\n"
            + "  operations: []\n";

        RelationType dependsOn = new RelationType("DependsOn", null, List.of(), List.of(), null);
        RelationType myRelation = new RelationType("MyRelation", "my relation", List.of(), List.of(), dependsOn);

        ObjectMapper mapper = YamlObjectMapper.createYamlObjectMapper();
        String yaml = mapper.writeValueAsString(myRelation);

        assertEquals(expected, yaml);
    }
}
