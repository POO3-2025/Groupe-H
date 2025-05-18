package be.helha.projets.projetdarktower.Item;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ItemSelectionRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    public void displayTestName(TestInfo testInfo) {
        System.out.println("Exécution du test : " + testInfo.getDisplayName());
    }

    @Test
    @DisplayName("1: Test des getters et setters")
    void testGettersAndSetters() {
        ItemSelectionRequest request = new ItemSelectionRequest();
        request.setItemId("item123");
        request.setCibleId("cible456");

        assertEquals("item123", request.getItemId());
        assertEquals("cible456", request.getCibleId());
    }

    @Test
    @DisplayName("2: Validation - itemId ne doit pas être vide")
    void testValidationItemIdNotBlank() {
        ItemSelectionRequest request = new ItemSelectionRequest();
        request.setItemId("");  // Vide = invalide
        request.setCibleId("any");

        Set<ConstraintViolation<ItemSelectionRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());

        ConstraintViolation<ItemSelectionRequest> violation = violations.iterator().next();
        assertEquals("L'ID de l'item ne peut pas être vide", violation.getMessage());
        assertEquals("itemId", violation.getPropertyPath().toString());
    }

    @Test
    @DisplayName("3: Validation - itemId ne doit pas être null")
    void testValidationItemIdNull() {
        ItemSelectionRequest request = new ItemSelectionRequest();
        request.setItemId(null);  // Null = invalide

        Set<ConstraintViolation<ItemSelectionRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());

        ConstraintViolation<ItemSelectionRequest> violation = violations.iterator().next();
        assertEquals("L'ID de l'item ne peut pas être vide", violation.getMessage());
        assertEquals("itemId", violation.getPropertyPath().toString());
    }

    @Test
    @DisplayName("4: Validation - itemId valide")
    void testValidationItemIdValid() {
        ItemSelectionRequest request = new ItemSelectionRequest();
        request.setItemId("validId");
        // cibleId est optionnel, peut être null ou vide

        Set<ConstraintViolation<ItemSelectionRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }
}
