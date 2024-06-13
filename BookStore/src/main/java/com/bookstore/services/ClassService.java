package com.bookstore.services;

import com.bookstore.entity.ClassEntity;
import com.bookstore.repository.ClassRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClassService {

    @Autowired
    private ClassRepository classRepository;

    public List<ClassEntity> getAllClasses() {
        return classRepository.findAll();
    }

    public ClassEntity getClassById(Long id) {
        Optional<ClassEntity> classEntity = classRepository.findById(id);
        return classEntity.orElse(null); // or handle as appropriate, e.g., throw an exception
    }

}
