package pl.projekt.orderService;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.projekt.orderService.entities.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}
