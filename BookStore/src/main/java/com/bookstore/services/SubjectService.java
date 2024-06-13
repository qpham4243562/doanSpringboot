package com.bookstore.services;

import com.bookstore.entity.SubjectEntity;
import com.bookstore.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubjectService {

    @Autowired
    private SubjectRepository subjectRepository;

    public List<SubjectEntity> getAllSubjects() {
        return subjectRepository.findAll();
    }
    public SubjectEntity getSubjectById(Long id) {
        Optional<SubjectEntity> subjectEntity = subjectRepository.findById(id);
        return subjectEntity.orElse(null); // or handle as appropriate, e.g., throw an exception
    }

}
