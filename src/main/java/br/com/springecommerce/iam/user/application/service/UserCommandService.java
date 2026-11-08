package br.com.springecommerce.iam.user.application.service;

import br.com.springecommerce.customer.customer.application.dto.CustomerCreateFromUser;
import br.com.springecommerce.customer.customer.application.service.CustomerService;
import br.com.springecommerce.iam.role.application.service.RoleService;
import br.com.springecommerce.iam.role.domain.code.RoleCode;
import br.com.springecommerce.iam.role.domain.model.Role;
import br.com.springecommerce.iam.user.application.dto.UserCreateDto;
import br.com.springecommerce.iam.user.application.dto.UserEmployeeCreateDto;
import br.com.springecommerce.iam.user.application.dto.UserResponseDto;
import br.com.springecommerce.iam.user.application.dto.UserUpdateRoleDto;
import br.com.springecommerce.iam.user.application.mapper.UserMapper;
import br.com.springecommerce.iam.user.domain.exception.UserErrors;
import br.com.springecommerce.iam.user.domain.model.User;
import br.com.springecommerce.iam.user.infrastructure.persistence.UserRepository;
import br.com.springecommerce.shared.exception.base.ConflictException;
import br.com.springecommerce.shared.exception.base.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class UserCommandService {

    private final Logger logger = LoggerFactory.getLogger(UserCommandService.class);

    private final UserRepository repository;

    private final RoleService roleService;

    private final CustomerService customerService;

    private final UserMapper mapper;

    private final PasswordEncoder passwordEncoder;

    public UserCommandService(
            UserRepository repository,
            RoleService roleService,
            CustomerService customerService,
            UserMapper mapper,
            PasswordEncoder passwordEncoder
    ) {
        this.repository = repository;
        this.roleService = roleService;
        this.customerService = customerService;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponseDto create(UserCreateDto dto) {

        logger.debug("Creating new user and its customer. data={}", dto);

        validateUniquenessByEmail(dto.email());

        User user = mapper.toEntity(dto);

        Role defaultRole = roleService.getByTitle(RoleCode.ROLE_CUSTOMER.name());

        user.assignRole(defaultRole);

        setEncodedPassword(user, dto.password());

        User savedUser = repository.save(user);

        CustomerCreateFromUser customerData = new CustomerCreateFromUser(
                dto.name(), dto.cpf(), null, null
        );

        customerService.createFromUser(user.getId(), customerData);

        logger.info("User and customer created successfully. userId={}", savedUser.getId());

        return mapper.toResponse(savedUser);
    }

    @Transactional
    public UserResponseDto createEmployee(UserEmployeeCreateDto dto) {

        logger.debug("Creating new user employee domain. data={}", dto);

        validateUniquenessByEmail(dto.email());

        User user = mapper.toEmployeeEntity(dto);

        List<Role> roles = roleService.findAllByIdIn(dto.roleIds());

        user.assignRoles(roles);

        setEncodedPassword(user, dto.password());

        User savedUser = repository.save(user);

        logger.info("User employee created successfully. userId={}, roles={}",
                savedUser.getId(), dto.roleIds());

        return mapper.toResponse(savedUser);
    }

    @Transactional
    public void delete(UUID id) {

        logger.debug("Deleting user. userId={}", id);

        User entity = findEntityById(id);

        logger.info("User deleted successfully. userId={}", id);

        repository.delete(entity);
    }

    @Transactional
    public void deactivate(UUID id) {

        logger.debug("Deactivating user. userId={}", id);

        repository.deactivate(id);

        logger.info("User deactivated successfully. userId={}", id);
    }

    @Transactional
    public void activate(UUID id) {

        logger.debug("Activating user. userId={}", id);

        repository.activate(id);

        logger.info("User activated successfully. userId={}", id);
    }

    @Transactional
    public void assignRoleToUser(UserUpdateRoleDto dto) {
        logger.debug("Assigning role to user. userId={}, roleId={}", dto.userId(), dto.roleId());

        User user =  findByIdWithRoles(dto.userId());

        Role role = roleService.getEntityById(dto.roleId());

        user.assignRole(role);

        logger.info("Role Assigned to user successfully. userId={}, roleId={}", dto.userId(), dto.roleId());
    }

    @Transactional
    public void removeRoleFromUser(UserUpdateRoleDto dto) {
        logger.debug("Removing role fom user. userId={}, roleId={}", dto.userId(), dto.roleId());

        User user =  findByIdWithRoles(dto.userId());

        user.removeRole(dto.roleId());

        logger.info("Role removed from User successfully. userId={}, roleId={}", dto.userId(), dto.roleId());
    }

    private User findEntityById(UUID id) {
        return repository.findById(id)
            .orElseThrow(() -> {
                logger.warn("User not found. userId={}", id);

                return new ResourceNotFoundException(
                        UserErrors.Target.ID,
                        UserErrors.Code.USER_NOT_FOUND);
            });
    }

    private User findByIdWithRoles(UUID id) {

        logger.debug("Fetching user with roles. userId={}", id);

        return repository.findByIdWithRoles(id)
            .orElseThrow(() -> {
                logger.warn("User not found. userId={}", id);
                return new ResourceNotFoundException(
                        UserErrors.Target.ID,
                        UserErrors.Code.USER_NOT_FOUND);
            });
    }

    private void validateUniquenessByEmail(String email) {
        if (repository.existsByEmail(email))
            throw new ConflictException(
                    UserErrors.Target.EMAIL,
                    UserErrors.Code.USER_EMAIL_ALREADY_EXISTS);
    }

    private void setEncodedPassword(User user, String password) {
        user.setPasswordHash(passwordEncoder.encode(password));
    }
}