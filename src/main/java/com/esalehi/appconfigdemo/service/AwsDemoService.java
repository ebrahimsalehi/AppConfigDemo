package com.esalehi.appconfigdemo.service;

import com.amazonaws.services.appconfig.AmazonAppConfig;
import com.amazonaws.services.appconfig.AmazonAppConfigClientBuilder;
import com.amazonaws.services.appconfig.model.GetConfigurationRequest;
import com.amazonaws.services.appconfig.model.GetConfigurationResult;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClient;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class AwsDemoService {

    @Value("${aws.appconfig.appname}")
    private String appId;

    @Value("${aws.appconfig.environment}")
    private String environmentId;

    @Value("${aws.appconfig.configprofile}")
    private String profileId;

    @Value("${appconfig.client.id}")
    private String clientId;

    private AWSSimpleSystemsManagement awsSimpleSystemsManagement;

    private String currentVersion;

    public AwsDemoService() {
        awsSimpleSystemsManagement = AWSSimpleSystemsManagementClient.builder().build();
    }

    public String getConfiguration() {
        AmazonAppConfig client = AmazonAppConfigClientBuilder.standard().defaultClient();

        GetConfigurationRequest getConfigurationRequest = new GetConfigurationRequest();
        getConfigurationRequest.setApplication(getParameter(appId));
        getConfigurationRequest.setEnvironment(getParameter(environmentId));
        getConfigurationRequest.setConfiguration(getParameter(profileId));
        getConfigurationRequest.setClientConfigurationVersion(currentVersion);
        getConfigurationRequest.setClientId(clientId);
        GetConfigurationResult getConfigurationResult = client.getConfiguration(getConfigurationRequest);

        currentVersion = getConfigurationResult.getConfigurationVersion();

        return StandardCharsets.UTF_8.decode(getConfigurationResult.getContent()).toString();
    }

    public String getParameter(String name) {
        return awsSimpleSystemsManagement.getParameter(new GetParameterRequest().withName(name)).getParameter().getValue();
    }

}
