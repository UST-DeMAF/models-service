package ust.tad.modelsservice.kubernetesdeploymentmodel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ust.tad.modelsservice.kubernetesdeploymentmodel.entities.KubernetesDeploymentModel;

import java.util.List;
import java.util.UUID;

@Service
public class KubernetesDeploymentModelService {
    @Autowired
    KubernetesDeploymentModelRepository kubernetesDeploymentModelRepository;

    public KubernetesDeploymentModel createOrUpdateKubernetesDeploymentModel(
            KubernetesDeploymentModel kubernetesDeploymentModel) {
        return kubernetesDeploymentModelRepository.save(kubernetesDeploymentModel);
    }

    public KubernetesDeploymentModel getOrCreateKubernetesDeploymentModelByTransformationProcessId(
            UUID transformationProcessId) {
        List<KubernetesDeploymentModel> models =
                kubernetesDeploymentModelRepository.findByTransformationProcessId(transformationProcessId);
        if (models.isEmpty()) {
            KubernetesDeploymentModel modelToCreate = new KubernetesDeploymentModel();
            modelToCreate.setTransformationProcessId(transformationProcessId);
            return createOrUpdateKubernetesDeploymentModel(modelToCreate);
        } else {
            return models.get(0);
        }
    }
}
