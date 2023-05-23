package br.com.MeloExpress.Routing.DTO;

import br.com.MeloExpress.Routing.Domain.RoutingCollect;

import java.util.List;
import java.util.UUID;

public record RoutingCreateDTO(

        UUID routingCode,
        UUID routingEmployeeCode,
        UUID routingFleetCode,
        List<RoutingCollect> routingCollects

) {
    public List<RoutingCollect> getRoutingCollects() {
        return routingCollects;
    }
}
