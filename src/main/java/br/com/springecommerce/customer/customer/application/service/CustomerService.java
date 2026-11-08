package br.com.springecommerce.customer.customer.application.service;

import br.com.springecommerce.customer.customer.application.dto.CustomerCreateDto;
import br.com.springecommerce.customer.customer.application.dto.CustomerCreateFromUser;
import br.com.springecommerce.customer.customer.application.dto.CustomerResponseDto;
import br.com.springecommerce.customer.customer.application.dto.CustomerUpdateDto;
import br.com.springecommerce.customer.customer.application.mapper.CustomerMapper;
import br.com.springecommerce.customer.customer.domain.exception.CustomerErrors;
import br.com.springecommerce.customer.customer.domain.model.Customer;
import br.com.springecommerce.customer.customer.infrastructure.persistence.CustomerRepository;
import br.com.springecommerce.iam.user.application.service.UserQueryService;
import br.com.springecommerce.iam.user.domain.model.User;
import br.com.springecommerce.shared.exception.base.ConflictException;
import br.com.springecommerce.shared.exception.base.ResourceNotFoundException;
import br.com.springecommerce.shared.pagination.PageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CustomerService {

    private final Logger logger = LoggerFactory.getLogger(CustomerService.class);

    private final CustomerRepository repository;

    private final CustomerMapper mapper;

    private final UserQueryService userService;

    public CustomerService(
            CustomerRepository repository,
            CustomerMapper mapper,
            UserQueryService userService
    ) {
        this.repository = repository;
        this.mapper = mapper;
        this.userService = userService;
    }

    public PageResponse<CustomerResponseDto> findAll(Pageable pageable) {

        logger.debug("Fetching all Customers. page={}", pageable);

        Page<Customer> page = repository.findAll(pageable);

        List<CustomerResponseDto> items = page.stream()
                .map(mapper::toResponse)
                .toList();

        return new PageResponse<>(
                items,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements()
        );
    }

    public CustomerResponseDto findById(UUID id) {

        logger.debug("Starting findById. customerId={}", id);

        Customer customer = findEntityById(id);
        CustomerResponseDto response = mapper.toResponse(customer);

        logger.info("Customer retrieved successfully. customerId={}", id);

        return response;
    }

    public CustomerResponseDto me(UUID userId) {

        logger.debug("Fetching authenticated customer profile. customerId={}", userId);

        Customer customer = findEntityById(userId);

        CustomerResponseDto response = mapper.toResponse(customer);

        logger.info("Customer profile retrieved successfully. customerId={}", userId);

        return response;
    }

    @Transactional
    public CustomerResponseDto create(UUID userId, CustomerCreateDto dto) {

        logger.debug("Creating customer. userId={}", userId);

        validateCustomerUniqueness(userId, dto.cpf());

        User user = userService.getValidReference(userId);

        Customer customer = mapper.toEntity(dto);
        customer.assignUser(user);

        Customer savedCustomer = repository.save(customer);

        logger.info("Customer created successfully. customerId={}", savedCustomer.getId());

        return mapper.toResponse(savedCustomer);
    }

    @Transactional
    public void createFromUser(UUID userId, CustomerCreateFromUser dto) {

        validateCustomerUniqueness(userId, dto.cpf());

        User user = userService.getValidReference(userId);

        Customer customer = mapper.toEntityFromUser(dto);
        customer.assignUser(user);

        repository.save(customer);
    }

    @Transactional
    public CustomerResponseDto update(UUID id, CustomerUpdateDto dto) {

        logger.debug("Updating customer. customerId={}", id);

        Customer customer = findEntityById(id);
        applyUpdate(dto, customer);
        Customer updatedCustomer = repository.save(customer);

        logger.info("Customer updated successfully. customerId={}", customer.getId());

        return mapper.toResponse(updatedCustomer);
    }

    private void applyUpdate(CustomerUpdateDto dto, Customer customer) {
        customer.setName(dto.name());
        customer.setPhone(dto.phone());
        customer.setBirthDate(dto.birthDate());
    }

    @Transactional
    public void delete(UUID id) {

        logger.debug("Deleting customer. customerId={}", id);

        Customer customer = findEntityById(id);

        logger.info("Customer deleted successfully. customerId={}", id);

        repository.delete(customer);
    }

    private Customer findEntityById(UUID id) {
        logger.debug("Fetching customer. customerId={}", id);

        return repository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Customer not found. customerId={}", id);
                    return new ResourceNotFoundException(
                            CustomerErrors.Target.ID,
                            CustomerErrors.Code.CUSTOMER_NOT_FOUND);
                });
    }

    public Customer findCustomer(UUID id) {
        logger.debug("Fetching customer by userId={}", id);

        return repository.findById(id).orElse(null);
    }

    private void validateCustomerUniqueness(UUID userId, String cpf) {

        if (repository.existsById(userId)) {
            logger.warn("Customer already exists. userId={}", userId);

            throw new ConflictException(
                    CustomerErrors.Target.CUSTOMER,
                    CustomerErrors.Code.CUSTOMER_ALREADY_EXISTS
            );
        }

        if (repository.existsByCpf(cpf)) {
            logger.warn("Cpf already registered. Cpf={}", cpf);

            throw new ConflictException(
                    CustomerErrors.Target.CUSTOMER,
                    CustomerErrors.Code.CUSTOMER_CPF_ALREADY_EXISTS
            );
        }
    }
}
