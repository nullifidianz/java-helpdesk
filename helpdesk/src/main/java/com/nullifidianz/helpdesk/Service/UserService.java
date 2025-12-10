package com.nullifidianz.helpdesk.Service;

import org.springframework.stereotype.Service;

import com.nullifidianz.helpdesk.Repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;

}
