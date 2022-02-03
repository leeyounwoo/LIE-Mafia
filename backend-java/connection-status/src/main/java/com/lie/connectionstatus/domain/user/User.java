package com.lie.connectionstatus.domain.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "connection 서버에서 user 정보를 저장하고 관리하는 모델입니다.")
public class User {
    @ApiModelProperty(notes = "방에 접속할 때 지정한 username 입니다")
    private final String username;
    @ApiModelProperty(notes = "유저의 sessionId")
    private final String sessionId;
    @ApiModelProperty(notes = "현재 준비를 했는지 하지 않았는지 확인합니다")
    private Boolean ready;
    @ApiModelProperty(notes = "방장 권한/일반 플레이어 권한")
    private  Authority authority;

    public User (final String username, final String sessionId, Authority authority){
        this.username = username;
        this.ready = false;
        this.sessionId = sessionId;
        this.authority =  authority;
    }

    public void pressReady(){
        if(this.ready){
            this.ready = false;
            return;
        }
        this.ready = true;
    }
}
