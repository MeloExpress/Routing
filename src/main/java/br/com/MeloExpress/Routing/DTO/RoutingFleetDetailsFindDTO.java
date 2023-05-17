package br.com.MeloExpress.Routing.DTO;

import java.util.UUID;

public record RoutingFleetDetailsFindDTO(
        Long fleetNumber,
        UUID fleetCode,
        String fleetPlate
) {
}
