package com.example.assignmentrd.controller;

import com.example.assignmentrd.model.Person;
import com.example.assignmentrd.repository.PersonRepository;
import com.microsoft.azure.cognitiveservices.vision.faceapi.models.DetectedFace;
import com.microsoft.azure.cognitiveservices.vision.faceapi.models.VerifyResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

@RestController
@RequestMapping("/api/image-comparison")
public class ImageComparisonController {

    private final PersonRepository personRepository;
    private final String faceApiEndpoint;
    private final String faceApiKey;

    @Autowired
    public ImageComparisonController(
            PersonRepository personRepository,
            @Value("${azure.face-api.endpoint}") String faceApiEndpoint,
            @Value("${azure.face-api.subscription-key}") String faceApiKey) {
        this.personRepository = personRepository;
        this.faceApiEndpoint = faceApiEndpoint;
        this.faceApiKey = faceApiKey;
    }

    public static class ImageComparisonResult {
        private final double confidence;

        public ImageComparisonResult(double confidence) {
            this.confidence = confidence;
        }

        public double getConfidence() {
            return confidence;
        }
    }

    @PostMapping("/compare")
    public ResponseEntity<ImageComparisonResult> compareImages(
            @RequestParam("personId") Long personId,
            @RequestParam("idDocument") MultipartFile idDocument,
            @RequestParam("portraitPhoto") MultipartFile portraitPhoto) {
        try {
            Optional<Person> optionalPerson = personRepository.findById(personId);
            if (!optionalPerson.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            Person person = optionalPerson.get();

            double confidence = performImageComparison(idDocument.getInputStream(), portraitPhoto.getInputStream());

            ImageComparisonResult comparisonResult = new ImageComparisonResult(confidence);

            return ResponseEntity.ok(comparisonResult);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private double performImageComparison(InputStream idDocumentStream, InputStream portraitPhotoStream) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Ocp-Apim-Subscription-Key", faceApiKey);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("url", "URL_TO_YOUR_ID_DOCUMENT_IMAGE");
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            String faceDetectionUrl = faceApiEndpoint + "/detect?returnFaceId=true";
            ResponseEntity<DetectedFace[]> idDocumentResponse = restTemplate.exchange(
                    faceDetectionUrl, HttpMethod.POST, requestEntity, DetectedFace[].class);
            DetectedFace[] idDocumentFaces = idDocumentResponse.getBody();

            if (idDocumentFaces != null && idDocumentFaces.length > 0) {
                String idDocumentFaceId = String.valueOf(idDocumentFaces[0].faceId());

                body = new LinkedMultiValueMap<>();
                body.add("url", "URL_TO_YOUR_PORTRAIT_PHOTO_IMAGE");
                requestEntity = new HttpEntity<>(body, headers);

                ResponseEntity<DetectedFace[]> portraitPhotoResponse = restTemplate.exchange(
                        faceDetectionUrl, HttpMethod.POST, requestEntity, DetectedFace[].class);
                DetectedFace[] portraitPhotoFaces = portraitPhotoResponse.getBody();

                if (portraitPhotoFaces != null && portraitPhotoFaces.length > 0) {
                    String portraitPhotoFaceId = String.valueOf(portraitPhotoFaces[0].faceId());

                    String verifyUrl = faceApiEndpoint + "/verify";
                    body = new LinkedMultiValueMap<>();
                    body.add("faceId1", idDocumentFaceId);
                    body.add("faceId2", portraitPhotoFaceId);
                    requestEntity = new HttpEntity<>(body, headers);

                    ResponseEntity<VerifyResult> verifyResponse = restTemplate.exchange(
                            verifyUrl, HttpMethod.POST, requestEntity, VerifyResult.class);
                    VerifyResult verificationResult = verifyResponse.getBody();

                    return verificationResult.confidence();
                }
            }

            return 0.0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }

}
