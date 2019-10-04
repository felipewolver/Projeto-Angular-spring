package com.algamoneyfel.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.algamoneyfel.api.model.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

}
