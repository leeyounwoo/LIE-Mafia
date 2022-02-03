package com.lie.connectionstatus.adapter;

import com.lie.connectionstatus.domain.room.Room;
import com.lie.connectionstatus.port.ConnectionService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/room")
@RequiredArgsConstructor
public class ConnectionController {

    private final ConnectionService connectionService;

    @GetMapping(path = "/{room-id}")
    @ApiOperation(value = "방 정보 유효 체크", notes = "접속하려는 방이 존재하는 지 체크")
    public ResponseEntity<Room> checkIfRoomExists(@PathVariable(value = "room-id") String roomId){
        Room room;
        try{
            room = connectionService.checkIfRoomExists(roomId);
        } catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(room, HttpStatus.OK);
    }

    @GetMapping(path = "/{room-id}/username/{username}")
    @ApiOperation(value = "방 정보와 유저명 유효체크", notes = "접속 시도하는 방에 중복되는 유저명이 존재하는 지 체크" )
    public ResponseEntity<Boolean> checkIfUsernameExistsInRoom(@PathVariable(value = "room-id") String roomId,
                                              @PathVariable(value = "username") String username){
        try{
            if(connectionService.checkIfUsernameExistsInRoom(roomId,username)){
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
