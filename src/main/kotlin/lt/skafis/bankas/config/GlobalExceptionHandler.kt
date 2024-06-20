package lt.skafis.bankas.config

import org.apache.logging.log4j.util.InternalException
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
        log.trace(ex.stackTraceToString())
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body<String>("Resource not found: " + ex.message)
    }

    @ExceptionHandler(InternalException::class)
    fun handleInternalException(ex: InternalException): ResponseEntity<String> {
        log.error("Internal exception occurred: " + ex.message)
        log.trace(ex.stackTraceToString())
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body<String>("Internal exception occurred: " + ex.message)
    }

    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception): ResponseEntity<String> {
        log.error("Exception occurred: " + ex.message)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body<String>("Error occurred, try again. If the issue remains, contact the system admin. Error message: " + ex.message)
    }

}