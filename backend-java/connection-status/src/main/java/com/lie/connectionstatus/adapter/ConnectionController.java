package com.lie.connectionstatus.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/room")
@RequiredArgsConstructor
public class ConnectionController {

    @GetMapping(path = "/{room-id}")
    public String checkIfRoomExists(@PathVariable(value = "room-id") String roomId){
        return roomId;
    }

    @GetMapping(path = "/{room-id}/username/{username}")
    public String checkIfUsernameExistsInRoom(@PathVariable(value = "room-id") String roomId,
                                              @PathVariable(value = "username") String username){
        return roomId;
    }
}
