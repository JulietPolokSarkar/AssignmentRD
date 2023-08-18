package com.example.assignmentrd.controller;

import com.example.assignmentrd.model.Person;
import com.example.assignmentrd.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/people")
public class PersonController {
    private final PersonRepository personRepository;

    @Autowired
    public PersonController(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @GetMapping
    public List<Person> getAllPeople() {
        return personRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> getPersonById(@PathVariable Long id) {
        Optional<Person> person = personRepository.findById(id);
        return person.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Person> createPersonWithImages(@RequestParam("portraitPhoto") MultipartFile portraitPhoto,
                                               @RequestParam("idDocument") MultipartFile idDocument,
                                               @ModelAttribute Person person) {
        // Upload portrait photo and set URL
        String portraitPhotoUrl = uploadImage(portraitPhoto);
        person.setPortraitPhotoUrl(portraitPhotoUrl);

        // Upload ID document and set URL
        String idDocumentUrl = uploadImage(idDocument);
        person.setIdDocumentUrl(idDocumentUrl);

        // Save the person to the database
        Person savedPerson = personRepository.save(person);

        // Return the saved person with HTTP status created
        return ResponseEntity.created(URI.create("/api/people/" + savedPerson.getId())).body(savedPerson);
    }
    private String uploadImage(MultipartFile image) {
        try {
            // Generate a unique filename for the image
            String fileName = UUID.randomUUID().toString() + "-" + image.getOriginalFilename();

            // Define the storage path (change this to your actual storage location)
            String storagePath = "./image/";

            // Create the full path for the image file
            Path imagePath = Paths.get(storagePath + fileName);

            // Save the image file to the storage location
            Files.copy(image.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);

            // Return the URL at which the image can be accessed
            return "./image/" + fileName; // Assuming your images are served from a URL like "/images/"
        } catch (IOException e) {
            // Handle the exception (e.g., log the error)
            e.printStackTrace();
            return null; // Return null to indicate failure
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<Person> updatePerson(@PathVariable Long id, @RequestBody Person updatedPerson) {
        if (!personRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        updatedPerson.setId(id);
        Person savedPerson = personRepository.save(updatedPerson);
        return ResponseEntity.ok(savedPerson);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePerson(@PathVariable Long id) {
        if (!personRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        personRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}