package com.sgi.inventorysystem.controllers;

import com.sgi.inventorysystem.models.User;
import com.sgi.inventorysystem.repositories.UserRepository;
import com.sgi.inventorysystem.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth") // ✅ ahora sin /api, porque el context-path ya lo agrega
@CrossOrigin(
        origins = {"https://app.cdmprime.com", "http://localhost:3000"},
        allowedHeaders = {"Content-Type", "Authorization", "Accept", "Origin"},
        methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.OPTIONS},
        allowCredentials = "true"
)
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // POST /api/auth/login
    @PostMapping(
            value = "/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Map<String, Object> login(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();

        try {
            String username = request.get("username");
            String password = request.get("password");

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            User user = userRepository.findByUsername(username);
            String token = jwtUtil.generateToken(username, user.getRole());

            response.put("token", token);
            response.put("role", user.getRole());
            response.put("success", true);
        } catch (AuthenticationException e) {
            response.put("success", false);
            response.put("error", "Invalid username or password");
        }

        return response;
    }

    // POST /api/auth/register
    @PostMapping(
            value = "/register",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Map<String, Object> register(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();

        String username = request.get("username");
        String password = request.get("password");

        if (userRepository.findByUsername(username) != null) {
            response.put("success", false);
            response.put("error", "Username already exists");
            return response;
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("USER"); // default role

        userRepository.save(user);

        response.put("success", true);
        return response;
    }

    // GET /api/auth/check-username?username=foo
    @GetMapping(value = "/check-username", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Boolean> checkUsername(@RequestParam String username) {
        boolean exists = userRepository.findByUsername(username) != null;
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return response;
    }
}