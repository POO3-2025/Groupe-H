package be.helha.projets.projetdarktower.Controller;

import be.helha.projets.projetdarktower.Model.LoginRequest;
import be.helha.projets.projetdarktower.Model.LoginResponse;
import be.helha.projets.projetdarktower.Model.User;
import be.helha.projets.projetdarktower.Security.JwtUtil;
import be.helha.projets.projetdarktower.Service.UserService;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @BeforeEach
    public void displayTestName(TestInfo testInfo) {
        System.out.println("Exécution du test : " + testInfo.getDisplayName());
    }

    // ======= REGISTER TESTS =======

    @Test
    @DisplayName("1: Échec de l'inscription si utilisateur existe déjà")
    public void testRegister_UserAlreadyExists() {
        User user = new User();
        user.setUsername("testuser");

        when(userService.userExists("testuser")).thenReturn(true);

        ResponseEntity<String> response = authController.register(user);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Nom d'utilisateur existe déjà", response.getBody());
    }

    @Test
    @DisplayName("2: Inscription réussie d'un nouvel utilisateur")
    public void testRegister_Success() {
        User user = new User();
        user.setUsername("newuser");

        when(userService.userExists("newuser")).thenReturn(false);

        ResponseEntity<String> response = authController.register(user);

        verify(userService).register(user);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Utilisateur enregistré avec succès !", response.getBody());
    }

    // ======= LOGIN TESTS =======

    @Test
    @DisplayName("3: Échec connexion - utilisateur non trouvé")
    public void testAuthenticateUser_UserNotFound() {
        LoginRequest request = new LoginRequest("unknown", "password");

        when(userService.findByUsername("unknown")).thenReturn(null);

        ResponseEntity<?> response = authController.authenticateUser(request);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Utilisateur inexistant", response.getBody());
    }

    @Test
    @DisplayName("4: Échec connexion - mauvais mot de passe")
    public void testAuthenticateUser_WrongPassword() {
        LoginRequest request = new LoginRequest("user", "wrongpass");
        User user = new User();
        user.setUsername("user");

        when(userService.findByUsername("user")).thenReturn(user);
        when(userService.isPasswordCorrect(user, "wrongpass")).thenReturn(false);

        ResponseEntity<?> response = authController.authenticateUser(request);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Mot de passe incorrect", response.getBody());
    }

    @Test
    @DisplayName("5: Connexion réussie avec JWT")
    public void testAuthenticateUser_Success() {
        LoginRequest request = new LoginRequest("user", "pass");
        User user = new User();
        user.setId(1L);
        user.setUsername("user");
        user.setIsLoggedIn(1);

        when(userService.findByUsername("user")).thenReturn(user);
        when(userService.isPasswordCorrect(user, "pass")).thenReturn(true);
        when(jwtUtil.generateToken("user")).thenReturn("fake-jwt-token");

        ResponseEntity<?> response = authController.authenticateUser(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        LoginResponse loginResponse = (LoginResponse) response.getBody();
        assertNotNull(loginResponse);
        assertEquals(1, loginResponse.getUserId());
        assertEquals("user", loginResponse.getUsername());
        assertEquals("fake-jwt-token", loginResponse.getToken());
        assertEquals(1, loginResponse.getIsLoggedIn());
    }

    // ======= UPDATE IS LOGGED TEST =======

    @Test
    @DisplayName("6: Mise à jour réussie du statut isLogged")
    public void testUpdateIsLogged_Success() {
        doNothing().when(userService).updateIsLogged(1, 1);

        ResponseEntity<String> response = authController.updateIsLogged(1, 1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Statut de connexion mis à jour avec succès.", response.getBody());
    }

    @Test
    @DisplayName("7: Échec mise à jour statut isLogged avec exception")
    public void testUpdateIsLogged_Failure() {
        doThrow(new RuntimeException("DB error")).when(userService).updateIsLogged(1, 1);

        ResponseEntity<String> response = authController.updateIsLogged(1, 1);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().contains("Erreur lors de la mise à jour"));
    }
}
