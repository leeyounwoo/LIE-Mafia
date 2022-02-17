package com.lie.connectionstatus.port;

import com.lie.connectionstatus.dto.EventActionDto;
import com.lie.connectionstatus.dto.OutboundErrorDto;
import com.lie.connectionstatus.dto.OutboundMessageDto;
import org.springframework.stereotype.Service;


import java.io.IOException;

@Service
public interface SessionService {

    void sendMessageToClient(OutboundMessageDto outboundMessageDto);
    void sendErrorMessageToClient(OutboundErrorDto outboundErrorDto) throws IOException;

}
