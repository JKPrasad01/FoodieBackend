package com.example.FoodApp.config;

import com.example.FoodApp.dto.*;
import com.example.FoodApp.service.Service.UserService;
import com.example.FoodApp.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth/user")
@CrossOrigin(
        origins = "http://localhost:5173",
        allowCredentials = "true" // critical: allows cookies in CORS
)
@RequiredArgsConstructor
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtil jwtUtil;

    // REGISTER USER
    @Transactional
    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@Valid @RequestBody SignupRequest signupRequest) {
        logger.info("successfully register {}",signupRequest.getUsername());
        UserDTO userDTO = userService.registerUser(signupRequest);
        return ResponseEntity.ok(userDTO);
    }

    // LOGIN USER
    @PostMapping("/login-user")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            UserDetails userDetails = (UserDetails) auth.getPrincipal();

            // Generate tokens
            String jwtToken = jwtUtil.generateJwtToken(userDetails);
            String refreshToken = jwtUtil.generateRefreshToken(userDetails);

            // Add cookies
            Cookie accessCookie = new Cookie("jwt", jwtToken);
            accessCookie.setHttpOnly(true);
            accessCookie.setSecure(false);
            accessCookie.setPath("/");
            accessCookie.setMaxAge(15 * 60);

            Cookie refreshCookie = new Cookie("refresh-token", refreshToken);
            refreshCookie.setHttpOnly(true);
            refreshCookie.setSecure(false);
            refreshCookie.setPath("/");
            refreshCookie.setMaxAge(7 * 24 * 60 * 60);

            response.addCookie(accessCookie);
            response.addCookie(refreshCookie);

            // Build response for frontend
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("message", "Logged In");

            responseBody.put("token",jwtToken);

            return ResponseEntity.ok(responseBody);

        } catch (Exception ex) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid username or password"));
        }
    }



    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        UserUpdateDTO response = userService.getUserByUsername(userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    //  LOGOUT USER (clears cookie)
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {


        Cookie cookie = new Cookie("jwt", "");
                cookie.setHttpOnly(true);
                cookie.setSecure(false);
                cookie.setPath("/");
                cookie.setMaxAge(0);

        Cookie refreshCookie = new Cookie("refresh-token","");
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(false);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(0);

        response.addCookie(cookie);
        response.addCookie(refreshCookie);
        return ResponseEntity.ok("Logged out successfully");
    }
}
