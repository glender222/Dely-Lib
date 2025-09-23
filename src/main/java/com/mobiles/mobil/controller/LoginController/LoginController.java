package com.mobiles.mobil.controller.LoginController;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mobiles.mobil.model.Dto.LoginRequest;
import com.mobiles.mobil.model.Dto.LoginResponse;
import com.mobiles.mobil.model.Dto.RegistroClienteRequest;
import com.mobiles.mobil.service.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Auth", description = "Autenticación basada en sesión UUID")
@RestController
@RequestMapping("/api/v1")
public class LoginController {
 private final AuthService authService;

    public LoginController(AuthService authService) {
        this.authService = authService;
    }

    /** Registro de cliente */
    @Operation(summary = "Registrar cliente")
    @ApiResponse(responseCode = "201", description = "Cliente registrado")
    @PostMapping("/register")
    public ResponseEntity<Long> register(@Valid @RequestBody RegistroClienteRequest req) {
        Long id = authService.registrarCliente(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    /** Login */
    @Operation(
        summary = "Login y creación de sesión",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Login exitoso",
                content = @Content(schema = @Schema(implementation = LoginResponse.class)),
                headers = {
                    @Header(name = LoginHeaders.SESSION_HEADER, description = "ID de sesión (también en body)")
                }
            )
        }
    )
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        LoginResponse res = authService.login(req);
        return ResponseEntity.ok()
                .header(LoginHeaders.SESSION_HEADER, res.getSessionId())
                .body(res);
    }

    /** Logout */
    @Operation(summary = "Cerrar sesión")
    @ApiResponse(responseCode = "204", description = "Sesión cerrada")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader(LoginHeaders.SESSION_HEADER) String sessionId) {
        authService.logout(sessionId);
        return ResponseEntity.noContent().build();
    }

    /** Perfil de sesión */
    @Operation(summary = "Obtener datos de la sesión actual")
    @ApiResponse(responseCode = "200", description = "Datos de sesión")
    @GetMapping("/me")
    public ResponseEntity<LoginResponse> me(@RequestHeader(LoginHeaders.SESSION_HEADER) String sessionId) {
        var u = authService.validar(sessionId);
        LoginResponse res = new LoginResponse();
        res.setSessionId(sessionId);
        res.setUserId(u.getIdUsuario());
        res.setNombre(u.getNombreCompleto());
        res.setRol(u.getRol());
        return ResponseEntity.ok(res);
    }
}
