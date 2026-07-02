package com.handymanhub.dto.response;

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// WHAT CHANGED:
//   Added userId  — frontend reads this from the response on login/register
//                   and stores it. No more "find customer by email" API call.
//   Added refreshToken — the raw UUID token for the /refresh flow.
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

public class AuthResponseDto {

    private String token;        // Access token (JWT, short-lived ~15 min)
    private String refreshToken; // Refresh token (UUID, long-lived ~7 days)
    private Long userId;         // User's database ID — new!
    private String name;
    private String email;
    private String role;

    private AuthResponseDto() {}

    public String getToken()        { return token; }
    public String getRefreshToken() { return refreshToken; }
    public Long getUserId()         { return userId; }
    public String getName()         { return name; }
    public String getEmail()        { return email; }
    public String getRole()         { return role; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final AuthResponseDto dto = new AuthResponseDto();
        public Builder token(String v)        { dto.token = v; return this; }
        public Builder refreshToken(String v) { dto.refreshToken = v; return this; }
        public Builder userId(Long v)         { dto.userId = v; return this; }
        public Builder name(String v)         { dto.name = v; return this; }
        public Builder email(String v)        { dto.email = v; return this; }
        public Builder role(String v)         { dto.role = v; return this; }
        public AuthResponseDto build()        { return dto; }
    }
}