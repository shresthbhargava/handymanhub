package com.handymanhub.dto.response;

public class AuthResponseDto {

    private String token;
    private String name;
    private String email;
    private String role;

    private AuthResponseDto() {}

    public String getToken() { return token; }
    public String getName()  { return name; }
    public String getEmail() { return email; }
    public String getRole()  { return role; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final AuthResponseDto dto = new AuthResponseDto();
        public Builder token(String v) { dto.token = v; return this; }
        public Builder name(String v)  { dto.name = v; return this; }
        public Builder email(String v) { dto.email = v; return this; }
        public Builder role(String v)  { dto.role = v; return this; }
        public AuthResponseDto build() { return dto; }
    }
}