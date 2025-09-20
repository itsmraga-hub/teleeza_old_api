package com.teleeza.wallet.teleeza.location.repository;

import com.teleeza.wallet.teleeza.location.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    @Query(value = "select name from locations",nativeQuery = true)
    List<String> getCountyNames();
}
