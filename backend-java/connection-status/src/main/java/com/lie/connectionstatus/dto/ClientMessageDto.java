package com.lie.connectionstatus.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "클라이언트에서 전달하는 message convention 바탕의 DTO 입니다")
public class ClientMessageDto {
    @ApiModelProperty(notes = "event의 종류를 말합니다. connection/game/chat 셋 중 한 가지를 선택합니다")
    private String eventType;
    @ApiModelProperty(notes = "action의 종류를 말합니다. connection의 경우 create/join 중 한 가지를 선택합니다")
    private String actionType;
    @ApiModelProperty(notes = "요청하는 방 정보를 전달합니다.")
    private String roomId;
    @ApiModelProperty(notes = "지정한 username을 전달합니다.")
    private String username;
}
