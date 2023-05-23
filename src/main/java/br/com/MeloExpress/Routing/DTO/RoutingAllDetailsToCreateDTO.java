package br.com.MeloExpress.Routing.DTO;

import java.util.List;

public record RoutingAllDetailsToCreateDTO(
        Long routingId,
        RoutingEmployeeDetailsFindDTO routingEmployeeDetails,
        RoutingFleetDetailsFindDTO routingFleetDetails,
        List <RoutingCollectDetailsFindDTO> routingCollects

) {}
