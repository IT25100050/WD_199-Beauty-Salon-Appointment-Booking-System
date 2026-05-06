package com.beauty_salon.beautysalon.repository;

import com.beauty_salon.beautysalon.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    Optional<Client> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    List<Client> findByVipTrue();

    List<Client> findByActiveTrue();

    @Query("SELECT c FROM Client c WHERE LOWER(c.firstName) LIKE LOWER(CONCAT('%',:q,'%')) " +
            "OR LOWER(c.lastName) LIKE LOWER(CONCAT('%',:q,'%')) " +
            "OR LOWER(c.email) LIKE LOWER(CONCAT('%',:q,'%'))")
    List<Client> search(@Param("q") String query);
}
