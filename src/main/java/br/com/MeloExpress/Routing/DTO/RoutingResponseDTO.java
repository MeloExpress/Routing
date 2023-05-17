package br.com.MeloExpress.Routing.DTO;

import br.com.MeloExpress.Routing.Domain.RoutingCollect;

import java.util.List;
import java.util.UUID;

public record RoutingResponseDTO(
        Long routingId,
        UUID routingCode,
        UUID routingEmployee,
        UUID routingFleet,
        List<RoutingCollect> routingCollects

) {
}
