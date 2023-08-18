package com.example.assignmentrd.controller;

import com.azure.ai.formrecognizer.FormRecognizerClient;
import com.azure.ai.formrecognizer.models.*;
import com.example.assignmentrd.model.Person;
import com.example.assignmentrd.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/validation")
public class DataValidationController {

    private final FormRecognizerClient formRecognizerClient;
    private final PersonRepository personRepository;

    @Autowired
    public DataValidationController(FormRecognizerClient formRecognizerClient, PersonRepository personRepository) {
        this.formRecognizerClient = formRecognizerClient;
        this.personRepository = personRepository;
    }

    private static final String modelId = "your_custom_form_model_id_here";


    @PostMapping("/validate")
    public ResponseEntity<String> validateData(@RequestParam("personId") Long personId,
                                               @RequestParam("idDocument") MultipartFile idDocument) {
        try {
            Optional<Person> optionalPerson = personRepository.findById(personId);
            if (!optionalPerson.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            Person person = optionalPerson.get();
            String extractedData = extractDataFromDocument(idDocument);

            // Compare the extracted data with user-provided data and create a validation result message
            String validationResultMessage = validateAgainstExtractedData(person, extractedData);

            return ResponseEntity.ok(validationResultMessage);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private String extractDataFromDocument(MultipartFile idDocument) throws IOException {
        try (InputStream documentStream = idDocument.getInputStream()) {
            List<RecognizedForm> recognizedForms = formRecognizerClient.beginRecognizeCustomForms(modelId, documentStream,
                    idDocument.getSize()).getFinalResult();

            if (recognizedForms.size() > 0) {
                Map<String, FormField> fields = recognizedForms.get(0).getFields();

                // Extract data from fields
                String firstName = fields.get("firstName").getValueData().getText();
                String lastName = fields.get("lastName").getValueData().getText();
                String dateOfBirth = fields.get("dateOfBirth").getValueData().getText();
                String placeOfBirth = fields.get("placeOfBirth").getValueData().getText();
                String nationality = fields.get("nationality").getValueData().getText();
                String gender = fields.get("gender").getValueData().getText();
                String address = fields.get("address").getValueData().getText();
                String email = fields.get("email").getValueData().getText();
                String phoneNumber = fields.get("phoneNumber").getValueData().getText();
                // Extract other fields similarly

                // Construct the extracted data string
                String extractedData = "First Name: " + firstName + "\n"
                        + "Last Name: " + lastName + "\n"
                        + "Date of Birth: " + dateOfBirth + "\n"
                        + "Place of Birth: " + placeOfBirth + "\n"
                        + "Nationality: " + nationality + "\n"
                        + "Gender: " + gender + "\n"
                        + "Address: " + address + "\n"
                        + "Email: " + email + "\n"
                        + "Phone Number: " + phoneNumber + "\n";
                // Add other fields to the extracted data

                return extractedData;
            } else {
                return "No recognized forms found.";
            }
        } catch (FormRecognizerException e) {
            e.printStackTrace();
            return "Error extracting data from the document.";
        }
    }


    private String validateAgainstExtractedData(Person person, String extractedData) {
        // Implement your validation logic here
        // Compare the extracted data with person's information and highlight any discrepancies
        // Create a validation result message based on the comparison

        StringBuilder validationResultMessage = new StringBuilder("Validation Result:\n");

        if (!extractedData.contains(person.getFirstName())) {
            validationResultMessage.append("First Name does not match the document.\n");
        }

        if (!extractedData.contains(person.getLastName())) {
            validationResultMessage.append("Last Name does not match the document.\n");
        }

        if (!extractedData.contains(person.getDateOfBirth().toString())) {
            validationResultMessage.append("Date of Birth does not match the document.\n");
        }

        if (!extractedData.contains(person.getPlaceOfBirth())) {
            validationResultMessage.append("Place of Birth does not match the document.\n");
        }

        if (!extractedData.contains(person.getNationality())) {
            validationResultMessage.append("Nationality does not match the document.\n");
        }

        if (!extractedData.contains(person.getGender())) {
            validationResultMessage.append("Gender does not match the document.\n");
        }

        if (!extractedData.contains(person.getAddress())) {
            validationResultMessage.append("Address does not match the document.\n");
        }

        if (!extractedData.contains(person.getEmail())) {
            validationResultMessage.append("Email does not match the document.\n");
        }

        if (!extractedData.contains(person.getPhoneNumber())) {
            validationResultMessage.append("Phone Number does not match the document.\n");
        }

        if (validationResultMessage.isEmpty()) {
            validationResultMessage.append("Validation Successful: Data matches the document.");
        } else {
            validationResultMessage.append("Validation Failed: Data does not match the document.");
        }

        return validationResultMessage.toString();
    }
}
