package com.example.minierp.application.user;

import com.example.minierp.domain.common.exceptions.NotFoundException;
import com.example.minierp.domain.user.Role;
import com.example.minierp.domain.user.User;
import com.example.minierp.domain.user.UserRepository;
import com.example.minierp.interfaces.rest.users.UpdateUserRequest;
import com.example.minierp.interfaces.rest.users.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    @Transactional(readOnly = true)
    public Page<UserResponse> findAll(Pageable pageable) {
        Page<User> page = repository.findAll().stream() // optional: could implement paging in repo
//                .filter(p -> p.getDeletedAt() == null)
                .collect(java.util.stream.Collectors.collectingAndThen(java.util.stream.Collectors.toList(),
                        list -> new org.springframework.data.domain.PageImpl<>(list, pageable, list.size())));
        return page.map(this::mapToResponse);
    }

    @Transactional
    public UserResponse update(Long id, UpdateUserRequest request) {
        User user = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(id, "کاربر "));

        user.setUsername(request.username());
        user.setPassword(request.password());
        user.setRole(Role.valueOf(request.role().toUpperCase()));
        user.setActive(request.active());


        User saved = repository.save(user);
        return mapToResponse(saved);
    }

    private UserResponse mapToResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getRole().name(),
                user.getActive()
        );
    }

    @Transactional(readOnly = true)
    public UserResponse findById(Long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(id, "کاربر "));
        return mapToResponse(user);
    }
}
