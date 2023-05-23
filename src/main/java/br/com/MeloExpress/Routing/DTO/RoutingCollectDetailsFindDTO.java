package br.com.MeloExpress.Routing.DTO;

import java.util.UUID;

public record RoutingCollectDetailsFindDTO(
        Long collectId,
        UUID collectCode,
        String startTime,
        String endTime,
        String collectState,
        CustomerDetailsFindDTO customerData,
        AddressDetailsFindDTO addressData


) {
}
