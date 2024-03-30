package demo.project_crud.services;

import java.io.IOException;
import java.util.Date;
import java.util.Random;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import demo.project_crud.DTO.auth.AuthResponse;
import demo.project_crud.DTO.auth.AuthenticateRequest;
import demo.project_crud.DTO.auth.RegisterRequest;
import demo.project_crud.DTO.auth.ResetPasswordRequest;
import demo.project_crud.entities.User.Role;
import demo.project_crud.entities.User.Token;
import demo.project_crud.entities.User.User;
import demo.project_crud.repository.TokenRepo;
import demo.project_crud.repository.UserRepo;
import demo.project_crud.util.ObjectsValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UserRepo userRepo;
    private final JwtService jwtService;
    private final TokenRepo tokenRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final ObjectsValidator<User> validator;

    private final MailService mailService;

    @Async
    public ResponseEntity<?> register(RegisterRequest regisReq) {
        User user = new User();
        user.setFullname(regisReq.getFullname());
        user.setEmail(regisReq.getEmail());
        user.setRole(Role.USER);
        user.setTypeLogin(regisReq.getTypeLogin());
        if (regisReq.getTypeLogin().toString().equals("GOOGLE")) {
            if (isValidAccessTokenGG(regisReq.getPassword())) {
                user.setPassword(regisReq.getPassword());
            } else {
                user.setPassword(null);
            }
        } else {
            user.setPassword(passwordEncoder.encode(regisReq.getPassword()));
        }
        var violations = validator.validate(user);
        if (!violations.isEmpty()) {
            return new ResponseEntity<>(violations, HttpStatus.BAD_REQUEST);
        }
        var savedUser = userRepo.save(user);
        var accessToken = jwtService.generateAccessToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        saveTokenUser(savedUser, accessToken);
        var response = AuthResponse.builder()
                .fullName(regisReq.getFullname())
                .email(user.getEmail())
                .typeLogin(regisReq.getTypeLogin())
                .role(user.getRole())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
        return ResponseEntity.ok(response);
    }

    @Async
    public ResponseEntity<?> authenticate(AuthenticateRequest authReq) {

        if (authReq.getTypeLogin().toString().equals("GOOGLE")) {
            if (!isValidAccessTokenGG(authReq.getPassword())) {
                return new ResponseEntity<>("invalid token", HttpStatus.BAD_REQUEST);
            }
        } else {
            try {
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(authReq.getEmail(), authReq.getPassword())
                );
            } catch (AuthenticationException e) {
                log.info(e.getMessage());
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }

        var user = userRepo.findByEmail(authReq.getEmail()).orElseThrow();
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveTokenUser(user, accessToken);
        var response = AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .fullName(user.getFullname())
                .email(user.getEmail())
                .role(user.getRole())
                .typeLogin(user.getTypeLogin())
                .build();

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String refreshToken;
        String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return new ResponseEntity<>("header Authorization or Bearer not found ", HttpStatus.BAD_REQUEST);
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            var user = this.userRepo.findByEmail(userEmail).orElseThrow();
            if (jwtService.inValidToken(refreshToken, user)) {
                String accessToken = jwtService.generateAccessToken(user);
                revokeAllUserTokens(user);
                saveTokenUser(user, accessToken);
                return ResponseEntity.ok(accessToken);
            }
        }

        return new ResponseEntity<>("token invalid", HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<?> findTypeLogin(String email) {
        var user = userRepo.findByEmail(email).orElse(null);
        if (user == null) {
            return new ResponseEntity<>("user not found", HttpStatus.NOT_FOUND);
        } else {
            return ResponseEntity.ok(user.getTypeLogin());
        }
    }

    @Async
    public boolean isValidAccessTokenGG(String access_token_google) {
        RestTemplate restTemplate = new RestTemplate();
        String urlGGCheck = "https://www.googleapis.com/oauth2/v3/userinfo";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + access_token_google);
        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
        @SuppressWarnings("null")
        ResponseEntity<String> response = restTemplate.exchange(urlGGCheck, HttpMethod.GET, entity, String.class);
        return response.getStatusCode().toString().contains("OK");
    }

    public ResponseEntity<?> reloadPage(HttpServletRequest request, HttpServletResponse response) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String refreshToken;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return new ResponseEntity<>("header Authorization or Bearer not found ", HttpStatus.BAD_REQUEST);
        }
        refreshToken = authHeader.substring(7);
        if (jwtService.extractExpiration(refreshToken).before(new Date())) {
            return new ResponseEntity<>("token expired", HttpStatus.BAD_REQUEST);
        }

        String email = jwtService.extractUsername(refreshToken);
        User user = userRepo.findByEmail(email).orElse(null);
        if (user == null) {
            return new ResponseEntity<>("user not found", HttpStatus.BAD_REQUEST);
        }

        var validUserTokens = tokenRepo.findAllValidTokensByUser(user.getId());
        if (validUserTokens.isEmpty()) {
            return new ResponseEntity<>("is logouted", HttpStatus.BAD_REQUEST);
        }

        String accessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveTokenUser(user, accessToken);

        var responseUser = AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken)
                .fullName(user.getFullname())
                .email(user.getEmail())
                .role(user.getRole())
                .typeLogin(user.getTypeLogin())
                .build();
        return ResponseEntity.ok(responseUser);

    }

    public ResponseEntity<?> forgotPassword(@NonNull String email) {

        try {
            var user = userRepo.findByEmail(email).orElse(null);
            if (user == null) {
                return new ResponseEntity<>("user not found", HttpStatus.BAD_REQUEST);
            }

            Random random = new Random();
            int randomNumber = random.nextInt(10000);
            var checkSendMail = mailService.sendMail(email, "reset password",
                    "this is code to reset password: " + String.format("%04d", randomNumber));
            if (!checkSendMail) {
                return new ResponseEntity<>("mail send fail!", HttpStatus.BAD_REQUEST);
            } else {
                String accessToken = jwtService.generateAccessToken(user);
                saveTokenUserReset(user, accessToken + "|" + randomNumber);
                return ResponseEntity.ok(accessToken);
            }

        } catch (Exception e) {
            return new ResponseEntity<>("mail send fail!", HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> resetPasswordUser(ResetPasswordRequest resetPasswordReq) {
        if (jwtService.extractExpiration(resetPasswordReq.getAccessToken()).before(new Date())) {
            return new ResponseEntity<>("token expired", HttpStatus.BAD_REQUEST);
        }
        String email = jwtService.extractUsername(resetPasswordReq.getAccessToken());
        if (!email.equals(resetPasswordReq.getEmail())) {
            return new ResponseEntity<>("username not match", HttpStatus.BAD_REQUEST);
        }

        User user = userRepo.findByEmail(email).orElse(null);
        if (user == null) {
            return new ResponseEntity<>("user not found", HttpStatus.BAD_REQUEST);
        }

        Token checkToken = tokenRepo.findByToken(resetPasswordReq.getAccessToken() + "|" + resetPasswordReq.getCode()).orElse(null);
        if (checkToken == null) {
            return new ResponseEntity<>("token invalid", HttpStatus.BAD_REQUEST);
        }

        user.setPassword(passwordEncoder.encode(resetPasswordReq.getNewPassword()));
        userRepo.save(user);
        return ResponseEntity.ok("reset password success!");
    }

    private void saveTokenUser(User savedUser, String jwt) {
        Token token = new Token();
        token.setUser(savedUser);
        token.setToken(jwt);
        token.setRevoked(false);
        token.setExpired(false);
        token.setResetPassword(false);
        tokenRepo.save(token);
    }

    private void saveTokenUserReset(User savedUser, String jwt) {
        Token token = new Token();
        token.setUser(savedUser);
        token.setToken(jwt);
        token.setRevoked(false);
        token.setExpired(false);
        token.setResetPassword(true);
        tokenRepo.save(token);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepo.findAllValidTokensByUser(user.getId());
        if (validUserTokens.isEmpty()) {
            return;
        }
        validUserTokens.forEach(o -> {
            o.setExpired(true);
            o.setRevoked(true);
        });
        tokenRepo.saveAll(validUserTokens);
    }

}
