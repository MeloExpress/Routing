package br.com.MeloExpress.Routing.Repository;

import br.com.MeloExpress.Routing.Domain.Routing;
import br.com.MeloExpress.Routing.Domain.RoutingCollect;
import br.com.MeloExpress.Routing.Domain.RoutingEmployee;
import br.com.MeloExpress.Routing.Domain.RoutingFleet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoutingRepository extends JpaRepository<Routing, Long> {
    Optional<Routing> findByRoutingCode(UUID routingCode);
}


