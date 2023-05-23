package br.com.MeloExpress.Routing.DTO;

import java.util.UUID;

public record RoutingResponseDTO(
        Long routingId,
        UUID routingCode,
        UUID routingEmployee,
        UUID routingFleet,
        UUID routingCollects

) {
}
