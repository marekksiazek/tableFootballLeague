package pl.marekksiazek.tableFootball.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.marekksiazek.tableFootball.entity.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {

    @Query(value = "SELECT * FROM XXXXX WHERE is_deleted = 0", nativeQuery = true)
    List<User> findNotDeletedUser();
}
