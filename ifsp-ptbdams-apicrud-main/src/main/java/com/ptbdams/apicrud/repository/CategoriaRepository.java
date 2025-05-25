package com.ptbdams.apicrud.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ptbdams.apicrud.model.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}