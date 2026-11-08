package br.com.springecommerce.iam.user.application.service;

import br.com.springecommerce.iam.user.application.dto.UserResponseDto;
import br.com.springecommerce.iam.user.application.mapper.UserMapper;
import br.com.springecommerce.iam.user.domain.exception.UserErrors;
import br.com.springecommerce.iam.user.domain.model.User;
import br.com.springecommerce.iam.user.infrastructure.persistence.UserRepository;
import br.com.springecommerce.shared.exception.base.ConflictException;
import br.com.springecommerce.shared.exception.base.ResourceNotFoundException;
import br.com.springecommerce.shared.pagination.PageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserQueryService {

    private final Logger logger = LoggerFactory.getLogger(UserQueryService.class);

    private final UserRepository repository;

    private final UserMapper mapper;

    private final PasswordEncoder passwordEncoder;

    public UserQueryService(
            UserRepository repository,
            UserMapper mapper,
            PasswordEncoder passwordEncoder
    ) {
        this.repository = repository;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
    }

    public PageResponse<UserResponseDto> findAll(Pageable pageable) {

        logger.debug("Fetching all users. pagination={}", pageable);

        var page = repository.findAll(pageable);

        var users = repository.findAllWithRolesByIdIn(
            page.getContent().stream().map(User::getId).toList()
        );

        var items = users.stream()
            .map(mapper::toResponse)
            .toList();

        return new PageResponse<>(
            items,
            page.getNumber(),
            page.getSize(),
            page.getTotalElements()
        );
    }

    public UserResponseDto findById(UUID id) {

        logger.debug("Fetching user. userId={}", id);

        User user = findByIdWithRoles(id);
        UserResponseDto response = mapper.toResponse(user);

        logger.info("Successfully retrieved user. userId={}", id);

        return response;
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

    public User getValidReference(UUID id) {
        logger.debug("Fetching user reference. id={}", id);

        validateEntity(id);

        return repository.getReferenceById(id);
    }

    private void validateEntity(UUID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(
                    UserErrors.Target.ID,
                    UserErrors.Code.USER_NOT_FOUND
            );
        }
    }
}