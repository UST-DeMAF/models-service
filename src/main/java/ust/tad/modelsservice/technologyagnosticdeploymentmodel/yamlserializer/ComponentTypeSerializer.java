package ust.tad.modelsservice.technologyagnosticdeploymentmodel.yamlserializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import ust.tad.modelsservice.technologyagnosticdeploymentmodel.entities.ComponentType;
import ust.tad.modelsservice.technologyagnosticdeploymentmodel.entities.Operation;
import ust.tad.modelsservice.technologyagnosticdeploymentmodel.entities.Property;

public class ComponentTypeSerializer extends StdSerializer<ComponentType> {

    public ComponentTypeSerializer() {
        this(null);
    }
  
    public ComponentTypeSerializer(Class<ComponentType> t) {
        super(t);
    }

    @Override
    public void serialize(ComponentType value, JsonGenerator gen, SerializerProvider provider) throws IOException {   
        gen.writeStartObject();
        gen.writeObjectFieldStart(value.getName());

        if(value.getParentType() != null) {
            gen.writeStringField("extends", value.getParentType().getName());
        } else {
            gen.writeStringField("extends", "-");
        }

        gen.writeStringField("description", value.getDescription());

        if (value.getProperties().isEmpty()) {            
            provider.defaultSerializeField("properties", value.getProperties(), gen);
        } else {
            gen.writeArrayFieldStart("properties");
            for (Property property : value.getProperties()) {
                gen.writeStartObject();   
                gen.writeObjectFieldStart(property.getKey());
                gen.writeStringField("type", property.getType().toString());
                gen.writeBooleanField("required", property.getRequired());
                if(property.getValue() != null) {
                    switch(property.getType()) {
                        case BOOLEAN:
                            gen.writeBooleanField("default_value", (Boolean) property.getValue());    
                            break;            
                        case DOUBLE:
                            gen.writeNumberField("default_value", (Double) property.getValue()); 
                            break;               
                        case INTEGER:
                            gen.writeNumberField("default_value", (Integer) property.getValue());
                            break;
                        case STRING:
                            gen.writeStringField("default_value", property.getValue().toString());
                            break;
                        default:
                            break;
                    }
                }                
                gen.writeEndObject();
                gen.writeEndObject();
            }
            gen.writeEndArray();
        }

        if (value.getOperations().isEmpty()) {
            provider.defaultSerializeField("operations", value.getOperations(), gen);
        } else {
            gen.writeArrayFieldStart("operations");
            for (Operation operation : value.getOperations()) {
                gen.writeStartObject();   
                gen.writeStringField(operation.getName(), "~");
                gen.writeEndObject();
            }
            gen.writeEndArray();
        }

        gen.writeEndObject();
        gen.writeEndObject();
    }
}
