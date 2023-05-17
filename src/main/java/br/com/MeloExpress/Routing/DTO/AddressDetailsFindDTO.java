package br.com.MeloExpress.Routing.DTO;

public record AddressDetailsFindDTO(

        String zipCode,
        String street,
        String number,
        String complements,
        String district,
        String city,
        String state,
        String pointReference

) {
}
