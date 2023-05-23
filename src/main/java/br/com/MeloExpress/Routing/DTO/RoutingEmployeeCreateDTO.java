package br.com.MeloExpress.Routing.DTO;

import java.util.UUID;

public record RoutingEmployeeCreateDTO(
        Long routingEmployeeId,
        UUID employeeCode
) {
}
