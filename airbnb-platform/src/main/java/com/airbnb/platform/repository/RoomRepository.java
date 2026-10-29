package com.airbnb.platform.repository;

import com.airbnb.platform.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByPropertyId(Long propertyId);

    List<Room> findByPropertyIdAndAvailableTrue(Long propertyId);
}
