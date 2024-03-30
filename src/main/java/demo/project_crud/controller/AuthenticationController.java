package demo.project_crud.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import demo.project_crud.DTO.auth.AuthenticateRequest;
import demo.project_crud.DTO.auth.RegisterRequest;
import demo.project_crud.DTO.auth.ResetPasswordRequest;
import demo.project_crud.services.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authService;

    @PostMapping("register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("authenticate")
    public ResponseEntity<?> authenticate(@RequestBody @Valid AuthenticateRequest request) {
        return authService.authenticate(request);
    }

    @GetMapping("refresh-token")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        return authService.refreshToken(request, response);
    }

    @GetMapping("check-type-login/{email}")
    public ResponseEntity<?> checkTypeLogin(@PathVariable String email) {
        return authService.findTypeLogin(email);
    }

    @GetMapping("reload-page")
    public ResponseEntity<?> reloadPage(HttpServletRequest request, HttpServletResponse response) {
        return authService.reloadPage(request, response);
    }

    @GetMapping("forgot-password/{email}")
    public ResponseEntity<?> forgotPassword(@PathVariable @NonNull String email) {
        return authService.forgotPassword(email);
    }

    @PostMapping("reset-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ResetPasswordRequest request) {
        return authService.resetPasswordUser(request);
    }

    public record refreshTokenRequest(String refreshToken) {

    }

}
