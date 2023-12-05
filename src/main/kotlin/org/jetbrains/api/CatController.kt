package org.jetbrains.api

import jakarta.servlet.http.HttpServletRequest
import org.jetbrains.generated.api.CatApi
import org.jetbrains.generated.api.model.AllCatsResponse
import org.jetbrains.generated.api.model.CatPairResponse
import org.jetbrains.generated.api.model.CatResponse
import org.jetbrains.generated.api.model.CreateCatRequest
import org.jetbrains.service.CatRecommenderIntegrationException
import org.jetbrains.service.CatService
import org.jetbrains.service.NewCat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Controller
class CatController(
    private val catService: CatService
) : CatApi {

    data class ApiErrorResponse(
        val timestamp: String,
        val status: Int,
        val error: String,
        val path: String,
        val message: String
    )

    @ExceptionHandler(value = [CatRecommenderIntegrationException::class])
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun onIntegrationException(
        ex: CatRecommenderIntegrationException,
        request: HttpServletRequest
    ): ResponseEntity<ApiErrorResponse> =
        ResponseEntity.internalServerError()
            .body(
                ApiErrorResponse(
                    LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase,
                    request.requestURL.toString(),
                    ex.localizedMessage
                )
            )

    @ExceptionHandler(value = [RuntimeException::class])
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun onRuntimeException(ex: RuntimeException, request: HttpServletRequest): ResponseEntity<ApiErrorResponse> =
        ResponseEntity.badRequest()
            .body(
                ApiErrorResponse(
                    LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    HttpStatus.BAD_REQUEST.value(),
                    HttpStatus.BAD_REQUEST.reasonPhrase,
                    request.requestURL.toString(),
                    ex.localizedMessage
                )
            )

    override fun generatePairs(size: Int): ResponseEntity<List<CatPairResponse>> {
        return ResponseEntity.ok(
            catService.generatePairs(size).map { (first, second) ->
                CatPairResponse(
                    CatResponse(
                        first.id,
                        first.name,
                        first.breed
                    ),
                    CatResponse(
                        second.id,
                        second.name,
                        second.breed
                    )
                )
            }
        )
    }

    override fun getAllCats(name: String?): ResponseEntity<AllCatsResponse> {
        val cats = Optional.ofNullable(name)
            .map { catService.findCatsByName(it) }
            .orElseGet { catService.getAllCats() }

        return ResponseEntity.ok(
            AllCatsResponse(
                catService.countCats(),
                cats.map { CatResponse(it.id, it.name, it.breed) }
            )
        )
    }

    override fun addCat(createCatRequest: CreateCatRequest): ResponseEntity<CatResponse> {
        val createdCat = catService.addCat(NewCat(createCatRequest.name, createCatRequest.breed))
        return ResponseEntity.ok(
            CatResponse(
                createdCat.id,
                createdCat.name,
                createdCat.breed
            )
        )
    }
}
