package ust.tad.modelsservice.kubernetesdeploymentmodel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ust.tad.modelsservice.kubernetesdeploymentmodel.entities.KubernetesDeploymentModel;

import java.util.UUID;

@RestController
@RequestMapping("kubernetes")
public class KubernetesDeploymentModelController {
    private static final Logger LOG =
            LoggerFactory.getLogger(KubernetesDeploymentModelController.class);

    @Autowired
    private KubernetesDeploymentModelService kubernetesDeploymentModelService;

    /**
     * Create or update a kubernetes deployment model.
     *
     * @param kubernetesDeploymentModel the kubernetes model to create or update.
     * @return the created or updated kubernetes deployment model.
     */
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<KubernetesDeploymentModel> createOrUpdateKubernetesDeploymentModel(
            @RequestBody KubernetesDeploymentModel kubernetesDeploymentModel) {
        LOG.info("Updating kubernetes deployment model");
        try {
            KubernetesDeploymentModel model =
                    kubernetesDeploymentModelService.createOrUpdateKubernetesDeploymentModel(kubernetesDeploymentModel);
            return new ResponseEntity<>(model, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Update of kubernetes deployment model failed", e);
        }
    }

    /**
     * Retrieves a kubernetes deployment model, identified by the transformationProcessId.
     * If no kubernetes deployment model exists, initializes a new empty model.
     *
     * @param transformationProcessId the identifier of the kubernetes deployment model.
     * @return the kubernetes deployment model with HttpStatus.OK.
     */
    @GetMapping("/{transformationProcessId}")
    public ResponseEntity<KubernetesDeploymentModel> getByTransformationProcessId(
            @PathVariable UUID transformationProcessId) {
        LOG.info("Sending kubernetes deployment model");
        try {
            return new ResponseEntity<>(kubernetesDeploymentModelService.
                    getOrCreateKubernetesDeploymentModelByTransformationProcessId(transformationProcessId),
                    HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }
}
