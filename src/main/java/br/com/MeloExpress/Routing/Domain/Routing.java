package br.com.MeloExpress.Routing.Domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "routingId")
@Table(name = "routing")
@Entity(name = "routing")
public class Routing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long routingId;

    private UUID routingCode;

    @OneToOne(targetEntity = RoutingEmployee.class)
    private UUID routingEmployee;

    @OneToOne(targetEntity = RoutingFleet.class)
    private UUID routingFleet;

    @OneToMany(mappedBy = "routing", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoutingCollect> routingCollect = new ArrayList<>();
}
