package com.example.assignmentrd.configuration;

import com.azure.ai.formrecognizer.FormRecognizerClient;
import com.azure.ai.formrecognizer.FormRecognizerClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class AzureConfig {

    @Value("${azure.form-recognizer.endpoint}")
    private String formRecognizerEndpoint;

    @Value("${azure.form-recognizer.api-key}")
    private String formRecognizerApiKey;

    @Bean
    public FormRecognizerClient formRecognizerClient() {
        return new FormRecognizerClientBuilder()
                .endpoint(formRecognizerEndpoint)
                .credential(new AzureKeyCredential(formRecognizerApiKey))
                .buildClient();
    }
}
