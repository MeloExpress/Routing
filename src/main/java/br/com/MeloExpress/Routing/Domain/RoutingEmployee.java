package br.com.MeloExpress.Routing.Domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "routingEmployeeId")
@Table(name = "routingEmployee")
@Entity(name = "routingEmployee")
public class RoutingEmployee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long routingEmployeeId;

    private UUID employeeCode;
}
