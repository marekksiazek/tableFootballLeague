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
import org.springframework.web.server.ResponseStatusException;
import pl.marekksiazek.tableFootball.entity.User;
import pl.marekksiazek.tableFootball.repositories.RoleRepository;
import pl.marekksiazek.tableFootball.repositories.UserRepository;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Tag(name="User", description = "User API")
@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;


    @GetMapping("/users")
    @Operation(
            summary = "Fetch all users",
            description = "Fetch all users entities and their data from data source"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success operation", content = @Content(mediaType =
                    "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "403", description = "You don't have access to the endpoint", content = @Content),
            @ApiResponse(responseCode = "404", description = "Page not found", content = @Content)
    })
    public ResponseEntity<List<User>> findAllUsers(){
        List users = userRepository.findNotDeletedUser();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/{id}")
    @Operation(
            summary = "Fetch one user",
            description = "Fetch user entities and his data from data source"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success operation", content = @Content(mediaType =
                    "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "403", description = "You don't have access to the endpoint", content = @Content),
            @ApiResponse(responseCode = "404", description = "Page not found", content = @Content)
    })
    public ResponseEntity<User> getUserById(@Parameter(description = "Id of user to be searched") @PathVariable Integer id){
        Optional<User> orderOptional = userRepository.findById(id);
        return orderOptional.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping(value = "/users/{role_id}", consumes = "application/json;charset=UTF-8")
    @Transactional
    @Operation(
            summary = "Create one user",
            description = "Create user entities and add his data from data source"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created task", content = @Content(mediaType =
                    "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "403", description = "You don't have access to the endpoint", content = @Content),
            @ApiResponse(responseCode = "404", description = "Page not found", content = @Content)
    })
    public ResponseEntity<User> cretateUser(@Parameter(description = "User entity")@PathVariable(value = "role_id") Integer role_id,
                                            @RequestBody User user){

        User savedUser = roleRepository.findById(role_id).map(role -> {
            user.setRole(role);
            return userRepository.save(user);
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return ResponseEntity.created(URI.create("/users/" + savedUser.getId())).body(savedUser);
    }

    @PutMapping("/users/{id}")
    @Transactional
    @Operation(
            summary = "Update one user",
            description = "Update user entities and change his data from data source"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated user", content = @Content(mediaType =
                    "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "403", description = "You don't have access to the endpoint", content = @Content),
            @ApiResponse(responseCode = "404", description = "Page not found", content = @Content)
    })
    public ResponseEntity<User> updateUser(@Parameter (description = "User entity") @RequestBody User newUser,
                                           @Parameter(description = "User id to update") @PathVariable Integer id){

        boolean oldUser = userRepository.findById(id)
                .map(user -> {
                    user.setName(newUser.getName());
                    user.setSurname(newUser.getSurname());
                    user.setEmail(newUser.getEmail());
                    user.setPhone(newUser.getPhone());
                    userRepository.save(user);
                    return ResponseEntity.ok(user).equals(HttpStatus.CREATED);
                })
                .orElseGet(() -> {
                    newUser.setId(id);
                    userRepository.save(newUser);
                    return ResponseEntity.ok(newUser).equals(HttpStatus.CREATED);
                });

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/users/{id}")
    @Transactional
    @Operation(
            summary = "Delete one user",
            description = "Delete user entities and change it to inactive"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deleted user", content = @Content),
            @ApiResponse(responseCode = "403", description = "You don't have access to the endpoint", content = @Content),
            @ApiResponse(responseCode = "404", description = "Page not found", content = @Content)
    })
    public ResponseEntity<User> deleteUser(@Parameter (description = "User entity") @RequestBody User deletedUser,
                                           @Parameter(description = "User id to delete") @PathVariable Integer id){
        boolean oldUser = userRepository.findById(id)
                .map(user -> {
                    user.setIsDeleted(1);
                    userRepository.save(user);
                    return ResponseEntity.ok(user).equals(HttpStatus.OK);
                })
                .orElseGet(() -> {
                    deletedUser.setId(id);
                    userRepository.save(deletedUser);
                    return ResponseEntity.ok(deletedUser).equals(HttpStatus.NO_CONTENT);
                });

        return ResponseEntity.ok().build();
    }
}
