package com.lie.connectionstatus.adapter;

import com.lie.connectionstatus.domain.room.Room;
import com.lie.connectionstatus.port.ConnectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

public class MessageConsumer {
    // press ready -> connectionservice


    @RestController
    @RequestMapping(path = "/room")
    @RequiredArgsConstructor
    public static class ConnectionController {

        private final ConnectionService connectionService;

        @GetMapping(path = "/{room-id}")
        public ResponseEntity<Room> checkIfRoomExists(@PathVariable(value = "room-id") String roomId){
            Room room;
            try{
                room = connectionService.checkIfRommExists(roomId);
            } catch (Exception e){
                e.printStackTrace();
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(room, HttpStatus.OK);
        }

        @GetMapping(path = "/{room-id}/username/{username}")
        public ResponseEntity<Boolean> checkIfUsernameExistsInRoom(@PathVariable(value = "room-id") String roomId,
                                                  @PathVariable(value = "username") String username){
            if(connectionService.checkIfUsernameExistsInRoom(roomId,username)){
               return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            return new ResponseEntity<>(HttpStatus.OK);
        }
    }
}
