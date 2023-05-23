package br.com.MeloExpress.Routing.Repository;

import br.com.MeloExpress.Routing.Domain.RoutingFleet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoutingFleetRepository extends JpaRepository<RoutingFleet, Long> {
}
