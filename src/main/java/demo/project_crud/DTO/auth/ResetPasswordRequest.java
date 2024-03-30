package demo.project_crud.DTO.auth;

import org.springframework.lang.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResetPasswordRequest {

    @NonNull
    @Email
    private String email;

    @NonNull
    @JsonProperty(value = "access_token")
    private String accessToken;

    @NonNull
    private String code;
    
    @NonNull
    @JsonProperty(value = "new_password")
    private String newPassword;
}
