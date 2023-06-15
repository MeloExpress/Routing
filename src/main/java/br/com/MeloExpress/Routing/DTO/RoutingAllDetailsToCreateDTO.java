package br.com.MeloExpress.Routing.DTO;

import br.com.MeloExpress.Routing.Domain.Reason;

import java.util.List;
import java.util.UUID;

public record RoutingAllDetailsToCreateDTO(
        Reason reason,
        Long routingId,
        UUID routingCode,
        RoutingEmployeeDetailsFindDTO routingEmployeeDetails,
        RoutingFleetDetailsFindDTO routingFleetDetails,
        List <RoutingCollectDetailsFindDTO> routingCollects

) {}
