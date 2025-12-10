package com.nullifidianz.helpdesk.Repository;

import com.nullifidianz.helpdesk.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
