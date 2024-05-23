package pl.marekksiazek.tableFootball.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import pl.marekksiazek.tableFootball.entity.Player;
import pl.marekksiazek.tableFootball.repositories.PlayerRepository;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Tag(name="Player", description = "Player API")
@RestController
@RequestMapping("/api")
public class PlayerController {

    @Autowired
    PlayerRepository playerRepository;


    @GetMapping("/players")
    @Operation(
            summary = "Fetch all players",
            description = "Fetch all players entities and their data from data source"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success operation", content = @Content(mediaType =
                    "application/json", schema = @Schema(implementation = Player.class))),
            @ApiResponse(responseCode = "403", description = "You don't have access to the endpoint", content = @Content),
            @ApiResponse(responseCode = "404", description = "Page not found", content = @Content)
    })
    public ResponseEntity<List<Player>> findAllPlayers(){
        List players = playerRepository.findNotDeletedPlayer();

        return ResponseEntity.ok(players);
    }

    @GetMapping("/players/{id}")
    @Operation(
            summary = "Fetch one player",
            description = "Fetch player entities and his data from data source"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success operation", content = @Content(mediaType =
                    "application/json", schema = @Schema(implementation = Player.class))),
            @ApiResponse(responseCode = "403", description = "You don't have access to the endpoint", content = @Content),
            @ApiResponse(responseCode = "404", description = "Page not found", content = @Content)
    })
    public ResponseEntity<Player> findPlayerById(@Parameter(description = "Id of player to be searched") @PathVariable int id) {
        Optional<Player> player = playerRepository.findById(id);

        return player.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/players")
    @Transactional
    @Operation(
            summary = "Create one player",
            description = "Create player entities and add his data from data source"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created task", content = @Content(mediaType =
                    "application/json", schema = @Schema(implementation = Player.class))),
            @ApiResponse(responseCode = "403", description = "You don't have access to the endpoint", content = @Content),
            @ApiResponse(responseCode = "404", description = "Page not found", content = @Content)
    })
    public ResponseEntity<Player> createPlayer(@Parameter(description = "Player entity") @RequestBody Player player){
        Player savePlayer = playerRepository.save(player);
        return ResponseEntity.created(URI.create("/players/" + savePlayer.getPlayerId())).body(savePlayer);
    }

    @PutMapping("/players/{id}")
    @Transactional
    @Operation(
            summary = "Update one player",
            description = "Update player entities and change his data from data source"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated player", content = @Content(mediaType =
                    "application/json", schema = @Schema(implementation = Player.class))),
            @ApiResponse(responseCode = "403", description = "You don't have access to the endpoint", content = @Content),
            @ApiResponse(responseCode = "404", description = "Page not found", content = @Content)
    })
    public ResponseEntity<Player> updatePlayer(@Parameter (description = "Player entity") @RequestBody Player newPlayer
            ,
                                               @Parameter(description = "Player id to update") @PathVariable Integer id){
        boolean oldPlayer = playerRepository.findById(id)
                .map(player -> {
                    player.setName(newPlayer.getName());
                    player.setSurname(newPlayer.getSurname());
                    player.setDepartment(newPlayer.getDepartment());
                    player.setEmail(newPlayer.getEmail());
                    player.setPhone(newPlayer.getPhone());
                    return ResponseEntity.ok(player).equals(HttpStatus.CREATED);
                })
                .orElseGet(() -> {
                    newPlayer.setPlayerId(id);
                    playerRepository.save(newPlayer);
                    return ResponseEntity.ok(newPlayer).equals(HttpStatus.CREATED);
                });
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/players/{id}")
    @Transactional
    @Operation(
            summary = "Delete one player",
            description = "Delete player entities and change it to inactive"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deleted player", content = @Content),
            @ApiResponse(responseCode = "403", description = "You don't have access to the endpoint", content = @Content),
            @ApiResponse(responseCode = "404", description = "Page not found", content = @Content)
    })
    public ResponseEntity<Player> deletePlayer(@Parameter (description = "Player entity") @RequestBody Player deletedPlayer,
                                               @Parameter(description = "Player id to delete") @PathVariable Integer id) {
        boolean playerToDelete = playerRepository.findById(id)
                .map(player -> {
                    player.setIsDeleted(1);
                    playerRepository.save(player);
                    return ResponseEntity.ok(player).equals(HttpStatus.NO_CONTENT);
                })
                .orElseGet(() -> {
                    deletedPlayer.setPlayerId(id);
                    playerRepository.save(deletedPlayer);
                    return ResponseEntity.ok(deletedPlayer).equals(HttpStatus.NO_CONTENT);
                });
        return ResponseEntity.noContent().build();
    }

}
