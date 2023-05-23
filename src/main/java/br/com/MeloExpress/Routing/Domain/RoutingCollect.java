package br.com.MeloExpress.Routing.Domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "routingCollectId")
@Table(name = "routingCollect")
@Entity(name = "routingCollect")
public class RoutingCollect {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long routingCollectId;

    private UUID routingCollectCode;

    public RoutingCollect(String routingCollectCode) {
        this.routingCollectCode = UUID.fromString(routingCollectCode);
    }

}
