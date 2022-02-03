package com.lie.gamelogic.port;

import com.lie.gamelogic.domain.room.Room;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

//spring data redis 연동 진행
@Repository
public interface RoomRepository extends CrudRepository<Room, String> {
    public Room findByRoomId(String roomId);
}
