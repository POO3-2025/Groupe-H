package be.helha.projets.projetdarktower.Model;

public class LoginResponse {
    private Long userId;
    private String username;
    private String token;
    private int IsLoggedIn;

    public LoginResponse(Long userId, String username, String token ,int IsLoggedIn) {
        this.userId = userId;
        this.username = username;
        this.token = token;
        this.IsLoggedIn = IsLoggedIn;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getToken() {
        return token;
    }

    public int getIsLoggedIn() {
        return IsLoggedIn;
    }
}

