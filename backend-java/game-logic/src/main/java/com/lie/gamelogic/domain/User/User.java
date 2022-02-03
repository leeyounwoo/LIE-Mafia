package com.lie.gamelogic.domain.User;

import com.lie.gamelogic.domain.Vote.Vote;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.socket.WebSocketSession;


@Data
@ApiModel(description = "connection 서버에서 user 정보를 저장하고 관리하는 모델입니다.")
public class User {


    @ApiModelProperty(notes = "방에 접속할 때 지정한 username 입니다")
    private final String username;
    @ApiModelProperty(notes = "방정보를 알려줍니다")
    private Job job;
    //뽑은 사람명 저장
    private String select;
    private boolean ready;
    //생존여부 체크
    //false 면 생존
    //true 면 사망
    private boolean dead;


    public User (final String username){
        this.username = username;
    }

}
