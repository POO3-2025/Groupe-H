package be.helha.projets.projetdarktower.Exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    public void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    public void testHandleJsonParseError() {
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);
        Throwable cause = new Throwable("JSON mal formé");
        when(ex.getMostSpecificCause()).thenReturn(cause);

        ResponseEntity<String> response = exceptionHandler.handleJsonParseError(ex);

        assertThat(response.getStatusCodeValue()).isEqualTo(400);
        assertThat(response.getBody()).contains("Erreur de lecture JSON");
        assertThat(response.getBody()).contains("JSON mal formé");
    }

    @Test
    public void testHandleValidationError() {
        // Mock des erreurs de champ
        FieldError error1 = new FieldError("obj", "nom", "ne doit pas être vide");
        FieldError error2 = new FieldError("obj", "age", "doit être positif");

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(error1, error2));

        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<String> response = exceptionHandler.handleValidationError(ex);

        assertThat(response.getStatusCodeValue()).isEqualTo(400);
        assertThat(response.getBody()).contains("Erreur de validation");
        assertThat(response.getBody()).contains("Champ 'nom' : ne doit pas être vide");
        assertThat(response.getBody()).contains("Champ 'age' : doit être positif");
    }

    @Test
    public void testHandleGeneralError() {
        Exception ex = new Exception("Erreur inattendue");

        ResponseEntity<String> response = exceptionHandler.handleGeneralError(ex);

        assertThat(response.getStatusCodeValue()).isEqualTo(500);
        assertThat(response.getBody()).contains("Erreur interne");
        assertThat(response.getBody()).contains("Erreur inattendue");
    }
}
