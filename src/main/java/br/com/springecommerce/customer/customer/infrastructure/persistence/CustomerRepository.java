package br.com.springecommerce.customer.customer.infrastructure.persistence;

import br.com.springecommerce.customer.customer.domain.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    boolean existsByCpf(String cpf);

    Optional<Customer> findByUserId(UUID id);
}
