package pl.marekksiazek.tableFootball.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.marekksiazek.tableFootball.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {
}
