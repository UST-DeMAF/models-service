package ust.tad.modelsservice.kubernetesdeploymentmodel;

public class ModelNotFoundException extends Exception{
    public ModelNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
