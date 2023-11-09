package org.jetbrains.api

import org.jetbrains.generated.api.CatApi
import org.jetbrains.generated.api.model.AllCatsResponse
import org.jetbrains.generated.api.model.CatResponse
import org.jetbrains.service.CatService
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller

@Controller
class Controller(
    private val catService: CatService
) : CatApi {
    override fun getAllCats(): ResponseEntity<AllCatsResponse> {
        val cats = catService.getAllCats().map { CatResponse(it.name, it.breed) }
        return ResponseEntity.ok(AllCatsResponse(catService.countCats(), cats))
    }
}
