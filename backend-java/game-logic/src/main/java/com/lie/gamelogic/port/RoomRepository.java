package com.lie.gamelogic.port;

import com.lie.gamelogic.domain.Room;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends CrudRepository<Room,String> {
}
