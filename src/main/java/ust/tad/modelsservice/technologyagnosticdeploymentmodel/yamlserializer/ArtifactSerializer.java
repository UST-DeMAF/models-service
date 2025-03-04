package ust.tad.modelsservice.technologyagnosticdeploymentmodel.yamlserializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import ust.tad.modelsservice.technologyagnosticdeploymentmodel.entities.Artifact;

public class ArtifactSerializer extends StdSerializer<Artifact>{

    public ArtifactSerializer() {
        this(null);
    }

    public ArtifactSerializer(Class<Artifact> t) {
        super(t);
    }

    @Override
    public void serialize(Artifact value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeObjectFieldStart(value.getType());

        gen.writeStringField("name", value.getName());
        if (value.getFileURI() != null) {
            gen.writeStringField("fileURI", value.getFileURI().toString());
        } else {            
            gen.writeStringField("fileURI", "-");
        }

        gen.writeEndObject();
        gen.writeEndObject();        
    }
    
}
