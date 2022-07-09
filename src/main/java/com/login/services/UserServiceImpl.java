package com.login.services;

import com.login.model.User;
import com.login.repository.UserRepository;
import com.login.shared.UserDto;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final PasswordEncoder encoder;

    private final ModelMapper mapper = new ModelMapper();

    public UserServiceImpl(UserRepository repository, PasswordEncoder encoder) {
        this.repository = repository;
        this.encoder = encoder;
    }

    @Override
    public List<UserDto> findAllUsers() {

        List<User> users = repository.findAll();

        return users.stream()
                .map(user -> mapper.map(user, UserDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UserDto> findUserById(String id) {

        Optional<User> userOpt = repository.findById(id);

        if (userOpt.isPresent()) {
            UserDto userDto = mapper.map(userOpt.get(), UserDto.class);
            return Optional.of(userDto);
        }

        return Optional.empty();
    }

    @Override
    public Optional<UserDto> findUserByUsername(String username) {

        Optional<User> userOpt = repository.findByUsername(username);

        if (userOpt.isPresent()) {
            UserDto userDto = mapper.map(userOpt.get(), UserDto.class);
            return Optional.of(userDto);
        }

        return Optional.empty();
    }

    @Override
    public UserDto registerUser(UserDto userDto) {

        User user = mapper.map(userDto, User.class);
        user.setPassword(encoder.encode(user.getPassword()));
        User registeredUser = repository.insert(user);

        return mapper.map(registeredUser, UserDto.class);
    }

    @Override
    public UserDto updateUser(String id, UserDto userDto) {

        User user = mapper.map(userDto, User.class);
        user.setId(id);
        user.setPassword(encoder.encode(user.getPassword()));
        User updatedUser = repository.save(user);

        return mapper.map(updatedUser, UserDto.class);
    }

    @Override
    public void deleteUserById(String id) {
        repository.deleteById(id);
    }

    @Override
    public void deleteAllUsers() {
        repository.deleteAll();
    }

}
