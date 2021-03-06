package com.demosproject.springboot.carsbackend.jpa.domain.services;


import com.demosproject.springboot.carsbackend.jpa.domain.model.Race;
import com.demosproject.springboot.carsbackend.jpa.domain.model.User;
import com.demosproject.springboot.carsbackend.jpa.dto.RaceDto;
import com.demosproject.springboot.carsbackend.jpa.dto.RaceFullDto;
import com.demosproject.springboot.carsbackend.jpa.domain.repositories.RaceRepositoryJPA;
import com.demosproject.springboot.carsbackend.jpa.domain.repositories.UserRepositoryJPA;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Data
@RequiredArgsConstructor
@Service
public class RaceServiceJPA {

  private final ModelMapper modelMapper;

  private final RaceRepositoryJPA raceRepositoryJPA;

  private final UserRepositoryJPA userRepositoryJPA;

  /**
   * Retrieve a list of races from database and return them using {@link RaceDto} representation.
   *
   * @return a list of Races using DTO representation.
   */
  public List<RaceDto> getRaces() {
    List<Race> races = this.raceRepositoryJPA.findAll();
    return races.stream().map(race -> modelMapper.map(race, RaceDto.class))
        .collect(Collectors.toList());
  }

  /**
   * Retrieve a list of races of a given user from database and return them using {@link RaceDto}
   * representation.
   *
   * @return a list of Races using DTO representation.
   */
  public List<RaceDto> getUserRaces(long userId) {
    List<Race> races = this.raceRepositoryJPA.findByUsers_Id(userId);
    return races.stream().map(race -> modelMapper.map(race, RaceDto.class))
        .collect(Collectors.toList());
  }

  /**
   * Retrieve a race with the given id from the database and return it using its DTO
   * representation.
   *
   * @param id the race id.
   * @return the race DTO.
   */
  public RaceFullDto getRaceByID(Long id) {
    Optional<Race> raceOptional = this.raceRepositoryJPA.findById(id);

    if (raceOptional.isPresent()) {
      return modelMapper.map(raceOptional.get(), RaceFullDto.class);
    } else {
      throw new NoSuchElementException(String.format("Race with id %d does not exist", id));
    }
  }

  /**
   * Save a new race.
   *
   * @param raceDto the race DTO.
   */
  public RaceDto saveRace(RaceDto raceDto, String currentUsername) {
    Race race = modelMapper.map(raceDto, Race.class);
    //There will exist always an authenticated user
    User currentUser = this.userRepositoryJPA.findByUsername(currentUsername).get();
    race.getUsers().add(currentUser);
    race = this.raceRepositoryJPA.save(race);
    return modelMapper.map(race, RaceDto.class);
  }

  /**
   * Deletes a race with the given id.
   *
   * @param raceId the Race id.
   */
  public void deleteRace(long raceId) {
    this.raceRepositoryJPA.deleteById(raceId);
  }

  /**
   * Adds a user to a race.
   *
   * @param raceId the race id.
   * @param userId the id of the user that wants to join the race.
   * @param username the current user username.
   */
  public void addUserToRace(long raceId, long userId,  String username) {
    Optional<User> optionalUser = this.userRepositoryJPA.findByUsername(username);

    if (!optionalUser.isPresent()) {
      throw new NoSuchElementException(String.format("User with username %s does not exist", username));
    } else {
      User retrievedUser = optionalUser.get();
      if(userId != retrievedUser.getId()){
        throw new IllegalStateException("Current user does not match with the provided User id");
      }
      Optional<Race> raceOptional = this.raceRepositoryJPA.findById(raceId);
      if (raceOptional.isPresent()) {
        Race race = raceOptional.get();
        race.addUser(optionalUser.get());
        this.raceRepositoryJPA.save(race);
      } else {
        throw new NoSuchElementException(String.format("Race with id %d does not exist", raceId));
      }
    }
  }

  private long getCurrentUserId() {
    User authenticatedUser = (User) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    return authenticatedUser.getId();
  }
}