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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class AwsDemoService {

    private Logger logger = LoggerFactory.getLogger(AwsDemoService.class);

    @Value("${aws.appconfig.appname}")
    private String appId;

    @Value("${aws.appconfig.environment}")
    private String environmentId;

    @Value("${aws.appconfig.configprofile}")
    private String profileId;

    @Value("${appconfig.client.id}")
    private String clientId;

    private final AWSSimpleSystemsManagement awsSimpleSystemsManagement;

    private final AmazonCloudWatch cloudWatchClient;

    private String currentVersion;

    private double cloudwatchMetricValue;

    public AwsDemoService() {
        awsSimpleSystemsManagement = AWSSimpleSystemsManagementClient.builder().build();
        cloudWatchClient = AmazonCloudWatchClientBuilder.defaultClient();
        cloudwatchMetricValue = 1.0;
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

    public void setMetricValue(double metricValue) {
        cloudwatchMetricValue = metricValue;
    }

    @Scheduled(cron = "*/20 * * * * *")
    private void putMetricsToCW() {

        Dimension dimension = new Dimension()
                .withName("UNIQUE_PAGES")
                .withValue("URLS");

        MetricDatum datum = new MetricDatum()
                .withMetricName("PAGES_VISITED")
                .withUnit(StandardUnit.None)
                .withValue(cloudwatchMetricValue)
                .withDimensions(dimension);

        logger.info("sending metrics {}", datum);

        PutMetricDataRequest request = new PutMetricDataRequest()
                .withNamespace("SITE/TRAFFIC")
                .withMetricData(datum);

        cloudWatchClient.putMetricData(request);

    }

}
