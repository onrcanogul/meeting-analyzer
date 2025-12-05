package com.example.demo.starter.presentation.controller.v1;

import com.example.demo.starter.application.dto.integration.IntegrationTokenDto;
import com.example.demo.starter.application.dto.integration.RepositoryDto;
import com.example.demo.starter.application.service.integration.token.IntegrationService;
import com.example.demo.starter.domain.enumeration.ProviderType;
import com.example.demo.starter.infrastructure.util.response.NoContent;
import com.example.demo.starter.infrastructure.util.response.ServiceResponse;
import com.example.demo.starter.presentation.controller.base.BaseController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/integration")
public class IntegrationController extends BaseController {
    private final IntegrationService service;

    public IntegrationController(IntegrationService service) {
        this.service = service;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ServiceResponse<List<IntegrationTokenDto>>> get(@PathVariable UUID userId) {
        return controllerResponse(service.getByUser(userId));
    }

    @GetMapping("repositories")
    public ResponseEntity<ServiceResponse<List<RepositoryDto>>> get() {
        return controllerResponse(service.getRepositoriesForMeeting());
    }

    @PostMapping("/connect")
    public ResponseEntity<ServiceResponse<NoContent>> connect(
            @RequestParam ProviderType provider,
            @RequestParam String token,
            @RequestParam(required = false) String meta) {
        return controllerResponse(service.connectUser(provider, token, meta));
    }

    @DeleteMapping
    public ResponseEntity<ServiceResponse<NoContent>> delete(@RequestParam UUID userId, @RequestParam ProviderType providerType) {
        return controllerResponse(service.delete(userId, providerType));
    }
}
