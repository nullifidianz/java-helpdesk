package com.nullifidianz.helpdesk.Service;

import com.nullifidianz.helpdesk.Exception.ResourceNotFoundException;
import com.nullifidianz.helpdesk.Model.User;
import com.nullifidianz.helpdesk.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;

    public User findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com id: " + id));
    }

    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email);
    }
}
