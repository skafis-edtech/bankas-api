package lt.skafis.bankas.config

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.webjars.NotFoundException

@ControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFoundException(ex: NotFoundException): ResponseEntity<String> {
        log.error("Resource not found: " + ex.message)
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body<String>("Resource not found: " + ex.message)
    }

    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception): ResponseEntity<String> {
        log.error("Exception occurred: " + ex.message)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body<String>("Exception occurred: " + ex.message)
    }

}