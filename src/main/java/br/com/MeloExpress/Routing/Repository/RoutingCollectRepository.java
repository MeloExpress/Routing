package br.com.MeloExpress.Routing.Repository;

import br.com.MeloExpress.Routing.Domain.RoutingCollect;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoutingCollectRepository extends JpaRepository<RoutingCollect, Long> {
}
