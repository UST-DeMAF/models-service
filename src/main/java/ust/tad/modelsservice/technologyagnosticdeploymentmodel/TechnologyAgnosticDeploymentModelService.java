package ust.tad.modelsservice.technologyagnosticdeploymentmodel;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ust.tad.modelsservice.technologyagnosticdeploymentmodel.entities.*;
import ust.tad.modelsservice.technologyagnosticdeploymentmodel.exceptions.EntityNotFoundException;
import ust.tad.modelsservice.technologyagnosticdeploymentmodel.exceptions.InvalidPropertyValueException;
import ust.tad.modelsservice.technologyagnosticdeploymentmodel.repositories.ComponentRepository;
import ust.tad.modelsservice.technologyagnosticdeploymentmodel.repositories.TechnologyAgnosticDeploymentModelRepository;
import ust.tad.modelsservice.technologyagnosticdeploymentmodel.repositories.ComponentTypeRepository;
import ust.tad.modelsservice.technologyagnosticdeploymentmodel.repositories.RelationTypeRepository;
import ust.tad.modelsservice.technologyagnosticdeploymentmodel.yamlserializer.*;

@Service
public class TechnologyAgnosticDeploymentModelService {

    @Autowired
    private TechnologyAgnosticDeploymentModelRepository deploymentModelRepository;

    @Autowired
    private ComponentTypeRepository componentTypeRepository;

    @Autowired
    private RelationTypeRepository relationTypeRepository;

    @Autowired
    private ComponentRepository componentRepository;

    @Value("${tadm.output.directory}")
    private String outputPath;

    /**
     * Initialize a technology-agnostic deployment model which only holds the base component types.
     * Creates an entity of type TechnologyAgnosticDeploymentModel and saves it in the models database.
     * 
     * @param transformationProcessId
     * @return the initialized technology-agnostic deployment model.
     * @throws InvalidPropertyValueException 
     */
    public TechnologyAgnosticDeploymentModel initializeTechnologyAgnosticDeploymentModel(UUID transformationProcessId) {
        List<ComponentType> componentTypes = saveComponentTypes(createBaseComponentTypes());
        List<RelationType> relationTypes = saveRelationTypes(createBaseRelationTypes());

        return deploymentModelRepository.save(
            new TechnologyAgnosticDeploymentModel(
                transformationProcessId,
                new ArrayList<>(), 
                new ArrayList<>(), 
                new ArrayList<>(), 
                componentTypes, 
                relationTypes));
    }

    /**
     * Updates a given technology-agnostic deployment model with new information.
     * 
     * @param tadm
     * @return the updated AnnotatedDeploymentModel.
     */
    public TechnologyAgnosticDeploymentModel updateTechnologyAgnosticDeploymentModel(TechnologyAgnosticDeploymentModel tadm) {
        saveComponentTypes(tadm.getComponentTypes());
        saveRelationTypes(tadm.getRelationTypes());
        saveComponents(tadm.getComponents());
        return deploymentModelRepository.save(tadm);
    }

    /**
     * Export a technology-agnostic deployment model, identified by the transformationProcessId.
     * The output directory is specified by the environment variable tadm.output.directory in the application.properties file.
     * 
     * @param transformationProcessId
     * @return the absolute path and file name containing the exported technology-agnostic deployment model.
     * @throws IOException if there was an error with the creation of the output file.
     * @throws Exception if there is no technology-agnostic deployment model with the given transformationProcessId.
     */
    public String exportTechnologyAgnosticDeploymentModel(UUID transformationProcessId) throws EntityNotFoundException, IOException {
        ObjectMapper mapper = YamlObjectMapper.createYamlObjectMapper();
        File outputFile = Path.of(outputPath,transformationProcessId.toString()+".yaml").toFile();
        TechnologyAgnosticDeploymentModel tadm = getTechnologyAgnosticDeploymentModelByTransformationProcessId(transformationProcessId);
        mapper.writeValue(outputFile, tadm);
        return outputFile.getAbsolutePath();
    }

    /**
     * Retrieves a technology-agnostic deployment model, identified by the transformationProcessId.
     * 
     * @param transformationProcessId
     * @return the technology-agnostic deployment model.
     * @throws EntityNotFoundException if there is no technology-agnostic deployment model with the given transformationProcessId.
     */
    public TechnologyAgnosticDeploymentModel getTechnologyAgnosticDeploymentModelByTransformationProcessId(UUID transformationProcessId) throws EntityNotFoundException {
        List<TechnologyAgnosticDeploymentModel> tadms = deploymentModelRepository.findByTransformationProcessId(transformationProcessId);
        if(tadms.isEmpty()) {
            throw new EntityNotFoundException(
                String.format("Could not find technology-agnostic deployment model with the transformation process id '%s'",
                transformationProcessId));
        } else {
            return tadms.get(0);
        }
    }


    private List<RelationType> createBaseRelationTypes() {
        RelationType dependsOn = new RelationType("DependsOn", "generic relation type", new ArrayList<>(), new ArrayList<>(), null);
        RelationType hostedOn = new RelationType("HostedOn", "hosted on relation", new ArrayList<>(), new ArrayList<>(), dependsOn);        
        RelationType connectsTo = new RelationType("ConnectsTo", "connects to relation", new ArrayList<>(), new ArrayList<>(), dependsOn);
        RelationType attachesTo = new RelationType("AttachesTo", "attaches to relation", new ArrayList<>(), new ArrayList<>(), dependsOn);
        List<RelationType> relationTypes = new ArrayList<>();
        relationTypes.add(dependsOn);
        relationTypes.add(hostedOn);
        relationTypes.add(connectsTo);
        relationTypes.add(attachesTo);
        return relationTypes;
    }

    private List<ComponentType> createBaseComponentTypes() {        
        ComponentType baseType = new ComponentType("BaseType", "This is the base type", new ArrayList<>(), new ArrayList<>(), null);
        List<ComponentType> componentTypes = new ArrayList<>();
        componentTypes.add(baseType);
        return componentTypes;
    }    

    private List<ComponentType> saveComponentTypes(List<ComponentType> componentTypes) {
        return componentTypeRepository.saveAll(componentTypes);
    }

    private List<RelationType> saveRelationTypes(List<RelationType> relationTypes) {
        return relationTypeRepository.saveAll(relationTypes);
    }

    private List<Component> saveComponents(List<Component> components) {        
        return componentRepository.saveAll(components);
    }
    
}
