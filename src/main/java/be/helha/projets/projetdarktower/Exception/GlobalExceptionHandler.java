package be.helha.projets.projetdarktower.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import java.util.stream.Collectors;

/**
 * Gestionnaire global des exceptions pour l'application Spring.
 *
 * <p>Intercepte les exceptions fréquentes comme les erreurs JSON mal formé,
 * les erreurs de validation, et les erreurs non gérées, pour fournir des réponses HTTP appropriées.</p>
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Gestionnaire d'erreur pour les JSON mal formés ou illisibles.
     *
     * @param ex Exception déclenchée lors de la lecture JSON incorrecte.
     * @return ResponseEntity avec code 400 et message détaillant l'erreur.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleJsonParseError(HttpMessageNotReadableException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Erreur de lecture JSON : " + ex.getMostSpecificCause().getMessage());
    }

    /**
     * Gestionnaire d'erreur pour les erreurs de validation des requêtes HTTP.
     *
     * @param ex Exception déclenchée lors de la validation avec @Valid.
     * @return ResponseEntity avec code 400 et liste des champs invalides et leurs messages.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationError(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> "Champ '" + error.getField() + "' : " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Erreur de validation : " + message);
    }

    /**
     * Gestionnaire d'erreur général pour toute exception non interceptée précédemment.
     *
     * @param ex Exception non gérée.
     * @return ResponseEntity avec code 500 et message d'erreur interne.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralError(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erreur interne : " + ex.getMessage());
    }
}
