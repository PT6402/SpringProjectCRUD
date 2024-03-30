package demo.project_crud.DTO.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

import demo.project_crud.entities.User.Role;
import demo.project_crud.entities.User.TypeLogin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AuthResponse {

    @JsonProperty(value = "access_token")
    private String accessToken;

    @JsonProperty(value = "refresh_token")
    private String refreshToken;

    @JsonProperty(value = "fullname")
    private String fullName;

    private String email;
    private Role role;

    @JsonProperty(value = "type_login")
    private TypeLogin typeLogin;
}
