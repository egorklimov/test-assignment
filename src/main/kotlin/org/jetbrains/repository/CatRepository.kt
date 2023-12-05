package org.jetbrains.repository

import org.springframework.data.annotation.Id
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.CrudRepository

@Table("cat")
data class Cat(
    @Id
    var id: Long,
    @Column("breed_id")
    val breedId: Long,
    val name: String,
    @Column("favorite_spot")
    val favoriteSpot: String
)

interface CatRepository : CrudRepository<Cat, Long> {
    @Query("SELECT COUNT(id) FROM CAT")
    fun countAll(): Long

    fun findAllByName(name: String): List<Cat>

    @Query("SELECT * FROM CAT ORDER BY ID DESC LIMIT :limit")
    fun findAllWithLimit(limit: Int): List<Cat>
}
