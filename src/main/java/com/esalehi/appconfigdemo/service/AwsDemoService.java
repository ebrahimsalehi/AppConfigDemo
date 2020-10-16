package com.esalehi.appconfigdemo.service;

import com.amazonaws.services.appconfig.AmazonAppConfig;
import com.amazonaws.services.appconfig.AmazonAppConfigClientBuilder;
import com.amazonaws.services.appconfig.model.GetConfigurationRequest;
import com.amazonaws.services.appconfig.model.GetConfigurationResult;
import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.MetricDatum;
import com.amazonaws.services.cloudwatch.model.PutMetricDataRequest;
import com.amazonaws.services.cloudwatch.model.PutMetricDataResult;
import com.amazonaws.services.cloudwatch.model.StandardUnit;
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

        return "CurrentVersion: " + currentVersion + "<br>" + StandardCharsets.UTF_8.decode(getConfigurationResult.getContent()).toString();
    }

    public String getParameter(String name) {
        return awsSimpleSystemsManagement.getParameter(new GetParameterRequest().withName(name)).getParameter().getValue();
    }

    public void putMetricsToCW(Double value) {
        final AmazonCloudWatch cw =
                AmazonCloudWatchClientBuilder.defaultClient();

        Dimension dimension = new Dimension()
                .withName("UNIQUE_PAGES")
                .withValue("URLS");

        System.out.println("sending metrics ");

        MetricDatum datum = new MetricDatum()
                .withMetricName("PAGES_VISITED")
                .withUnit(StandardUnit.None)
                .withValue(value)
                .withDimensions(dimension);

        PutMetricDataRequest request = new PutMetricDataRequest()
                .withNamespace("SITE/TRAFFIC")
                .withMetricData(datum);

        PutMetricDataResult response = cw.putMetricData(request);
        System.out.println(response);


    }


}
