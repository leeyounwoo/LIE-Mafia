package com.lie.connectionstatus.port;

import com.lie.connectionstatus.adapter.MessageProducer;
import com.lie.connectionstatus.domain.user.User;
import com.lie.connectionstatus.domain.user.UserConnection;
import com.lie.connectionstatus.domain.user.UserConnectionManager;
import com.lie.connectionstatus.dto.EventActionDto;
import com.lie.connectionstatus.dto.OutboundErrorDto;
import com.lie.connectionstatus.dto.OutboundMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class SessionServiceImpl implements SessionService{
    private final UserConnectionManager userConnectionManager;
    private final MessageProducer messageProducer;


    @Override
    public void sendMessageToClient(OutboundMessageDto outboundMessageDto) {
        messageProducer.sendToParticipants(outboundMessageDto.getReceivers().stream()
                .map(receiver -> userConnectionManager.getBySession(receiver))
                .collect(Collectors.toList()), outboundMessageDto.getMessage());
    }

    @Override
    public void sendErrorMessageToClient(OutboundErrorDto outboundErrorDto) throws IOException {
        UserConnection clientSession = userConnectionManager.getBySession(outboundErrorDto.getSessionId());
        clientSession.close();
    }
}

