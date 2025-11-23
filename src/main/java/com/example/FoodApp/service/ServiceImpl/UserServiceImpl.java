package com.example.FoodApp.service.ServiceImpl;


import com.example.FoodApp.Exception.RoleNotFoundException;
import com.example.FoodApp.Exception.UserNotFoundException;
import com.example.FoodApp.dto.LoginResponse;
import com.example.FoodApp.dto.SignupRequest;
import com.example.FoodApp.dto.UserDTO;
import com.example.FoodApp.dto.UserUpdateDTO;
import com.example.FoodApp.entity.Role;
import com.example.FoodApp.entity.User;
import com.example.FoodApp.repository.RoleRepository;
import com.example.FoodApp.repository.UserRepository;
import com.example.FoodApp.service.Service.UserService;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger=  LoggerFactory.getLogger(UserServiceImpl.class);




    @Override
    public LoginResponse logInUser(String username, String password){

        User user = userRepository.findByUsername(username).orElseThrow(()->new UserNotFoundException(username+" and "+password));

        System.out.println(password);
        if(!passwordEncoder.matches(password,user.getPassword())){
            throw new BadCredentialsException("Invalid Password");
        }

        return LoginResponse.builder()
                .userId(user.getUserId())
                .username(username)
                .roles(user.getRoles().stream()
                        .map(Role::getRole)
                        .collect(Collectors.toSet()))
                .build();
    }

    @Override
    public UserUpdateDTO getUserById(Long userId){
            User user = userRepository.findById(userId).orElseThrow(()->new UserNotFoundException("User not found "+ userId));
            return modelMapper.map(user,UserUpdateDTO.class);
    }

    public List<UserDTO> findAll(){
       List<User> list = userRepository.findAll();
       return list.stream().map(user -> modelMapper.map(user, UserDTO.class)).toList();
    }

    @Override
    public UserUpdateDTO updateUser(String username, UserUpdateDTO userDTO) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found " + username));

        if (userDTO.getUsername() != null) user.setUsername(userDTO.getUsername());
        if (userDTO.getUserEmail() != null) user.setUserEmail(userDTO.getUserEmail());
        if (userDTO.getContactNumber() != null) user.setContactNumber(userDTO.getContactNumber());
        if (userDTO.getAddress() != null) user.setAddress(userDTO.getAddress());
        if (userDTO.getBio() != null) user.setBio(userDTO.getBio());
        if (userDTO.getUserProfile() != null) user.setUserProfile(userDTO.getUserProfile());

        User updated = userRepository.save(user);
        return modelMapper.map(updated, UserUpdateDTO.class);
    }

    @Override
    public String deleteUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(()->new UserNotFoundException("No User existed to Delete"+ userId));
        userRepository.delete(user);
        return "Deleted SuccessFully";
    }


    @Override
    public UserUpdateDTO getUserByUsername(String username){
       User user = userRepository.findByUsername(username).orElseThrow(()->new UserNotFoundException("User Details not found "+username));
        System.out.println("user"+user);
       return modelMapper.map(user,UserUpdateDTO.class);
    }


    public UserDTO registerUser(SignupRequest signupRequest){
        logger.info("Starting registration process for username: '{}' :" , signupRequest.getUsername());

        try{
            // validate user name
            if(userRepository.existsByUsername(signupRequest.getUsername())){
                logger.warn("Registration failed : UserName '{}' is Already taken",signupRequest.getUsername());
            }

            //validate user email
            if (userRepository.existsByUserEmail(signupRequest.getEmail())){
                logger.warn("Registration failed : Email '{}' is Already existed",signupRequest.getEmail());
            }


            // create user
            User user=mapToUser(signupRequest);

            User saved = userRepository.save(user);

            logger.info("User registration successfully : ID:{},username:{} ,email : {}",saved.getUserId(),saved.getUsername(),saved.getUserEmail());
            return modelMapper.map(saved,UserDTO.class);
        }
        catch (Exception e){
            logger.error("Error during registration ",e);
            throw e;
        }
    }


    private User mapToUser(SignupRequest signupRequest){
        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setUserEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));

        Role role = roleRepository.findByRole("USER")
                .orElseThrow(() -> new RoleNotFoundException("role not found : USER"));

        user.setRoles(Set.of(role));
        return user;
    }


    @Transactional(readOnly = true)
    public boolean isUsernameAvailable(String userName){
        return userRepository.existsByUsername(userName);
    }
}
