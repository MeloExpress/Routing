package br.com.MeloExpress.Routing.DTO;

public record CustomerDetailsFindDTO(
        Long customerId,
        String companyName,
        String phone,
        String responsible
) {
}
