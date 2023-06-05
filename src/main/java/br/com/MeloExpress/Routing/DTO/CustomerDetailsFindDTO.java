package br.com.MeloExpress.Routing.DTO;

import java.util.UUID;

public record CustomerDetailsFindDTO(
        Long customerId,
        UUID customerCode,
        String companyName,
        String phone,
        String email,
        String responsible
) {
}
