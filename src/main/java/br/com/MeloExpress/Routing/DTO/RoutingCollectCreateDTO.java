package br.com.MeloExpress.Routing.DTO;

import java.util.UUID;

public record RoutingCollectCreateDTO(
        long routingCollectId,
        UUID routingCollectCode
) {
}
