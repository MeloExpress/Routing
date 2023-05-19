package br.com.MeloExpress.Routing.DTO;

import java.util.UUID;

public record RoutingFleetCreateDTO(
        Long routingFleetId,
        UUID fleetCode
) {
}
