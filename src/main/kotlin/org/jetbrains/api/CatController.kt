package org.jetbrains.api

import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull
import org.jetbrains.api.model.AllCatsResponse
import org.jetbrains.api.model.CatPairResponse
import org.jetbrains.api.model.CatResponse
import org.jetbrains.api.model.CreateCatRequest
import org.jetbrains.service.CatRecommenderIntegrationException
import org.jetbrains.service.CatService
import org.jetbrains.service.NewCat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Validated
@Controller
class CatController(
    private val catService: CatService
) {

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

    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/api/cat"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun addCat(
        @Valid @RequestBody
        createCatRequest: CreateCatRequest
    ): ResponseEntity<CatResponse> {
        val createdCat = catService.addCat(NewCat(createCatRequest.name, createCatRequest.breed))
        return ResponseEntity.ok(
            CatResponse(
                createdCat.id,
                createdCat.name,
                createdCat.breed
            )
        )
    }

    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/api/cats/pairs"],
        produces = ["application/json"]
    )
    fun generatePairs(
        @NotNull
        @Valid
        @RequestParam(value = "size", required = true, defaultValue = "100")
        size: Int
    ): ResponseEntity<List<CatPairResponse>> {
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

    @RequestMapping(
        method = [RequestMethod.GET],
        value = ["/api/cats"],
        produces = ["application/json"]
    )
    fun getAllCats(
        @Valid
        @RequestParam(value = "name", required = false)
        name: kotlin.String?
    ): ResponseEntity<AllCatsResponse> {
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
}
