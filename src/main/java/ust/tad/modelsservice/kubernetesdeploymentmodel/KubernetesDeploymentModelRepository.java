package ust.tad.modelsservice.kubernetesdeploymentmodel;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ust.tad.modelsservice.kubernetesdeploymentmodel.entities.KubernetesDeploymentModel;

import java.util.List;
import java.util.UUID;

@Repository
public interface KubernetesDeploymentModelRepository extends MongoRepository<KubernetesDeploymentModel, UUID> {
    List<KubernetesDeploymentModel> findByTransformationProcessId(UUID transformationProcessId);
}
