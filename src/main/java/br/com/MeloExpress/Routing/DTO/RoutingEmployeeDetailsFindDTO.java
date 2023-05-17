package br.com.MeloExpress.Routing.DTO;

import java.util.UUID;

public record RoutingEmployeeDetailsFindDTO(
        UUID employeeCode,
        String name,
        String cpf
) {
}
