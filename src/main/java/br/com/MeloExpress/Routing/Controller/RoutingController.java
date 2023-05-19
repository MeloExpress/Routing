package br.com.MeloExpress.Routing.Controller;

import br.com.MeloExpress.Routing.DTO.RoutingAllDetailsToCreateDTO;
import br.com.MeloExpress.Routing.DTO.RoutingCreateDTO;
import br.com.MeloExpress.Routing.Service.RoutingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}

