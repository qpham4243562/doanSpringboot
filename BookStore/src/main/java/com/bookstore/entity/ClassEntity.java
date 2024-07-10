package com.bookstore.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "class")
public class ClassEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Class name cannot be blank")
    @Size(max = 100, message = "Class name must be less than 100 characters")
    @Column(name = "name")
    private String name;

    public ClassEntity() {
    }


    public ClassEntity(Long id) {
        this.id = id;
    }
}