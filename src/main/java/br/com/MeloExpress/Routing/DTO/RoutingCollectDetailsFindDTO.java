package br.com.MeloExpress.Routing.DTO;

import java.util.UUID;

public record RoutingCollectDetailsFindDTO(
        Long collectId,
        CustomerDetailsFindDTO customerData,
        AddressDetailsFindDTO addressData


) {
}
