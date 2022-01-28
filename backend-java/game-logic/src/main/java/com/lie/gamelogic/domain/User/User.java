package com.lie.gamelogic.domain.User;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.socket.WebSocketSession;


@Data
@ApiModel(description = "connection 서버에서 user 정보를 저장하고 관리하는 모델입니다.")
public class User {

    @ApiModelProperty(notes ="행동의 종류입니다")
    private String actionType;
    @ApiModelProperty(notes = "방에 접속할 때 지정한 username 입니다")
    private final String username;
    @ApiModelProperty(notes = "현재 준비를 했는지 하지 않았는지 확인합니다")
    private Boolean ready;
    @ApiModelProperty(notes = "방정보를 알려줍니다")
    private String roomId;
    private Job job;



    public User (String actionType, final String username, String roomId){
        this.actionType = actionType;
        this.username = username;
        this.ready = false;
        this.roomId = roomId;
    }

    public void SettingJob(Job job){
        this.job = job;
    }
}
