package com.login.view.controller;

import com.login.services.UserService;
import com.login.shared.UserDto;
import com.login.view.model.UserRequest;
import com.login.view.model.UserResponse;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/login")
public class UserController {

    private final UserService service;
    private final PasswordEncoder encoder;

    private final ModelMapper mapper = new ModelMapper();

    public UserController(UserService service, PasswordEncoder encoder) {
        this.service = service;
        this.encoder = encoder;
    }

    @GetMapping(value = "/getAllUsers")
    public ResponseEntity<List<UserResponse>> getAllUsers() {

        List<UserDto> dto = service.findAllUsers();
        List<UserResponse> users = dto.stream().map(userDto -> mapper.map(userDto, UserResponse.class)).collect(Collectors.toList());

        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable String id) {

        Optional<UserDto> dtoOpt = service.findUserById(id);

        if (dtoOpt.isPresent()) {
            UserResponse user = mapper.map(dtoOpt.get(), UserResponse.class);
            return new ResponseEntity<>(user, HttpStatus.FOUND);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/getUserByUsername/{username}")
    public ResponseEntity<UserResponse> getUserByUsername(@PathVariable String username) {

        Optional<UserDto> dtoOpt = service.findUserByUsername(username);

        if (dtoOpt.isPresent()) {
            UserResponse user = mapper.map(dtoOpt.get(), UserResponse.class);
            return new ResponseEntity<>(user, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/validatePassword")
    public ResponseEntity<Boolean> validatePassword(@RequestParam String username,
                                                    @RequestParam String password) {

        Optional<UserDto> optUser = service.findUserByUsername(username);

        if (optUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
        }

        UserDto user = optUser.get();
        Boolean valid = encoder.matches(password, user.getPassword());

        HttpStatus status = (valid) ? HttpStatus.OK : HttpStatus.UNAUTHORIZED;

        return ResponseEntity.status(status).body(valid);
    }

    @PostMapping(value = "/registerUser")
    public ResponseEntity<UserResponse> registerUser(@RequestBody @Valid UserRequest userRequest) {

        UserDto userDto = mapper.map(userRequest, UserDto.class);
        UserDto registeredUser = service.registerUser(userDto);

        return new ResponseEntity<>(mapper.map(registeredUser, UserResponse.class), HttpStatus.CREATED);
    }

    @PutMapping(value = "/updateUser/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable String id,
                                                   @RequestBody @Valid UserRequest userRequest) {

        HttpStatus status = HttpStatus.OK;
        List<UserDto> users = service.findAllUsers();

        for (UserDto u : users) {

            if (!Objects.equals(id, u.getId())) {
                status = HttpStatus.NOT_FOUND;
            } else {
                status = HttpStatus.ACCEPTED;

                UserDto userDto = mapper.map(userRequest, UserDto.class);
                UserDto updatedUser = service.updateUser(id, userDto);

                return new ResponseEntity<>(mapper.map(updatedUser, UserResponse.class), status);
            }
        }

        return new ResponseEntity<>(status);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable String id) {

        HttpStatus status = HttpStatus.OK;
        List<UserDto> users = service.findAllUsers();

        for (UserDto u : users) {

            if (!Objects.equals(id, u.getId())) {
                status = HttpStatus.NOT_FOUND;
            } else {
                status = HttpStatus.FOUND;
                service.deleteUserById(id);
            }
        }

        return new ResponseEntity<>(status);
    }

    @DeleteMapping(value = "/deleteAllUsers")
    public ResponseEntity<String> DeleteAll() {

        service.deleteAllUsers();

        return new ResponseEntity<>("all users deleted", HttpStatus.OK);
    }
}