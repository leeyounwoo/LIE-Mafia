package com.lie.gamelogic.port;

import com.lie.gamelogic.domain.User.Job;
import com.lie.gamelogic.domain.User.User;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class GameServiceImpl implements GameService{
    @Override
    public void GetJobs(WebSocketSession session) throws IOException {

    }

    @Override
    public boolean GameStart(WebSocketSession session ,String roomId) {

        if(1==1) //
            return true;
        else return false;
    }

    @Override
    public void phaseturn(WebSocketSession session) {

    }

    @Override
    public boolean GameEnd(List<User> userList) {

        HashMap<String, User> participants = new HashMap<>();
        int citizenCount = 0;//시민 숫자
        int mapiaCount =0; //마피아 숫자
        for(User user : participants.values()){
            Job job = user.getJob();
            if(job.equals(Job.Citizen)) citizenCount++;
            else if(job.equals(Job.Doctor)) citizenCount++;
            else mapiaCount++;
        }

        if(mapiaCount >= citizenCount) return true;

        return false;
    }
}
