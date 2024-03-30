package demo.project_crud.DTO.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

import demo.project_crud.entities.User.TypeLogin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    private String fullname;
    private String email;
    private String password;
    @JsonProperty(value = "type_login")
    private TypeLogin typeLogin;
}
