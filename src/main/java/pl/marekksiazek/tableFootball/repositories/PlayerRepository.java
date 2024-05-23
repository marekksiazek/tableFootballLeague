package pl.marekksiazek.tableFootball.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.marekksiazek.tableFootball.entity.Player;

import java.util.List;

public interface PlayerRepository extends JpaRepository<Player, Integer> {

    @Query(value = "SELECT * FROM players WHERE is_deleted = 0", nativeQuery = true)
    List<Player> findNotDeletedPlayer();
}
