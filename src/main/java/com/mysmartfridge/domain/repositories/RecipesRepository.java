package com.mysmartfridge.domain.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.mysmartfridge.domain.Recipe;

@Repository
public interface RecipesRepository extends CrudRepository<Recipe, Long> {
}