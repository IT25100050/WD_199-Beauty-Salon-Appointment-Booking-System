package com.beauty_salon.beautysalon.repository;

import com.beauty_salon.beautysalon.enums.ServiceCategory;
import com.beauty_salon.beautysalon.model.Stylist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StylistRepository extends JpaRepository<Stylist, Long> {

    List<Stylist> findByActiveTrue();

    List<Stylist> findByActiveTrueAndCurrentlyBusyFalse();

    @Query("SELECT st FROM Stylist st JOIN st.categories c WHERE c = :category AND st.active = true")
    List<Stylist> findByCategoryAndActiveTrue(@Param("category") ServiceCategory category);

    @Query("SELECT st FROM Stylist st JOIN st.specializations s WHERE s.id = :serviceId AND st.active = true")
    List<Stylist> findByServiceIdAndActiveTrue(@Param("serviceId") Long serviceId);
}
