package org.jetbrains.repository

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.CrudRepository

@Table("cat_breed")
data class CatBreed(@Id var id: Long, val name: String, val description: String, val link: String)

interface CatBreedRepository : CrudRepository<CatBreed, Long>
