package br.com.springecommerce.sales.order.infrastructure.specification;

import br.com.springecommerce.sales.order.domain.model.Order;
import br.com.springecommerce.sales.order.presentation.filter.OrderCustomerFilter;
import br.com.springecommerce.sales.order.presentation.filter.OrderManagerFilter;
import org.springframework.data.jpa.domain.Specification;

public class OrderSpecificationFactory {

    private OrderSpecificationFactory() {}

    public static Specification<Order> publicView(OrderCustomerFilter filter) {
        Specification<Order> spec = Specification.where(null);

        spec = spec.and(OrderSpecifications.status(filter.status()));

        spec = spec.and(OrderSpecifications.minTotal(filter.minTotal()));
        spec = spec.and(OrderSpecifications.maxTotal(filter.maxTotal()));

        spec = spec.and(OrderSpecifications.createdFrom(filter.createdFrom()));
        spec = spec.and(OrderSpecifications.createdTo(filter.createdTo()));

        return spec;
    }

    public static Specification<Order> managerView(OrderManagerFilter filter) {
        Specification<Order> spec = Specification.where(null);

        // spec = spec.and(OrderSpecifications.search(filter.search()));

        spec = spec.and(OrderSpecifications.customerId(filter.customerId()));

        spec = spec.and(OrderSpecifications.status(filter.status()));

        spec = spec.and(OrderSpecifications.minTotal(filter.minTotal()));
        spec = spec.and(OrderSpecifications.maxTotal(filter.maxTotal()));

        spec = spec.and(OrderSpecifications.createdFrom(filter.createdFrom()));
        spec = spec.and(OrderSpecifications.createdTo(filter.createdTo()));

        spec = spec.and(OrderSpecifications.updatedFrom(filter.updatedFrom()));
        spec = spec.and(OrderSpecifications.updatedTo(filter.updatedTo()));

        return spec;
    }
}