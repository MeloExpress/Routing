package br.com.MeloExpress.Routing.Domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "routingFleetId")
@Table(name = "routingFleet")
@Entity(name = "routingFleet")
public class RoutingFleet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long routingFleetId;

    private UUID fleetCode;
}
