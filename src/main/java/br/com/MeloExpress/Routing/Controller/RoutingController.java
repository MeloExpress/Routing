package br.com.MeloExpress.Routing.Controller;

import br.com.MeloExpress.Routing.DTO.RoutingAllDetailsToCreateDTO;
import br.com.MeloExpress.Routing.DTO.RoutingCollectDetailsFindDTO;
import br.com.MeloExpress.Routing.DTO.RoutingCreateDTO;
import br.com.MeloExpress.Routing.Domain.RoutingCollect;
import br.com.MeloExpress.Routing.Service.RoutingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/routing")
public class RoutingController {

    private final RoutingService routingService;
    private final String employeeApiUrl;
    private final String fleetApiUrl;
    private final String collectApiUrl;

    @Autowired
    public RoutingController(RoutingService routingService,
                             @Value("${employee.api.url}") String employeeApiUrl,
                             @Value("${fleet.api.url}") String fleetApiUrl,
                             @Value("${collect.api.url}") String collectApiUrl) {
        this.routingService = routingService;
        this.employeeApiUrl = employeeApiUrl;
        this.fleetApiUrl = fleetApiUrl;
        this.collectApiUrl = collectApiUrl;
    }

    @PostMapping("/create")
    public RoutingAllDetailsToCreateDTO createRouting(@RequestBody RoutingCreateDTO routingCreateDTO) {
        return routingService.createRouting(routingCreateDTO, employeeApiUrl, fleetApiUrl, collectApiUrl);
    }

    @PostMapping("/{routingCode}/collects")
    public RoutingAllDetailsToCreateDTO addCollectsToRouting(
            @PathVariable UUID routingCode,
            @RequestBody List<RoutingCollect> newCollects
    ) {
        return routingService.addCollectsToRouting(routingCode, newCollects, collectApiUrl);
    }

    @GetMapping("/collects")
    public ResponseEntity<List<RoutingCollectDetailsFindDTO>> getAllCollectsByRouting(
            @RequestParam("routingCode") UUID routingCode,
            @Value("${collect.api.url}") String collectApiUrl
    ) {
        List<RoutingCollectDetailsFindDTO> collects = routingService.getAllCollectsByRouting(routingCode, collectApiUrl);
        return ResponseEntity.ok(collects);
    }

}

