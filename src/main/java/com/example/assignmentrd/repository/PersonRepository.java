package com.example.assignmentrd.repository;

import com.example.assignmentrd.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Long> {
}

