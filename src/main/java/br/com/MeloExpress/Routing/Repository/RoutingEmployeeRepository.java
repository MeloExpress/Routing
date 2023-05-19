package br.com.MeloExpress.Routing.Repository;

import br.com.MeloExpress.Routing.Domain.RoutingEmployee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoutingEmployeeRepository extends JpaRepository<RoutingEmployee, Long> {
}
