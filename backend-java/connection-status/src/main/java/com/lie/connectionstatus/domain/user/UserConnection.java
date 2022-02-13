package com.lie.connectionstatus.domain.user;

import com.google.gson.JsonObject;
import com.lie.connectionstatus.adapter.MessageProducer;
import com.lie.connectionstatus.port.MessageInterface;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kurento.client.*;
import org.kurento.jsonrpc.JsonUtils;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Data
@Slf4j
public class UserConnection implements Closeable {
    private String username;
    private String roomId;

    private final String sessionId;

    private final MediaPipeline mediaPipeline;
    private final WebRtcEndpoint outgoingMedia;
    private final ConcurrentMap<String, WebRtcEndpoint> incomingMedia = new ConcurrentHashMap<>();
    private final MessageInterface messageInterface;

    public UserConnection(final String username, final String roomId,
                          final MediaPipeline mediaPipeline, final String sessionId, final MessageInterface messageInterface){

        this.mediaPipeline = mediaPipeline;
        this.username = username;
        this.sessionId = sessionId;
        this.roomId = roomId;
        this.messageInterface = messageInterface;

        this.outgoingMedia = new WebRtcEndpoint.Builder(mediaPipeline).build();
        this.outgoingMedia.addIceCandidateFoundListener(new EventListener<IceCandidateFoundEvent>() {

            @Override
            public void onEvent(IceCandidateFoundEvent event) {
                JsonObject response = new JsonObject();
                response.addProperty("id", "iceCandidate");
                response.addProperty("name", username);
                response.add("candidate", JsonUtils.toJsonObject(event.getCandidate()));
                synchronized (messageInterface) {
                    messageInterface.broadCastToClient("client.response", sessionId,response.toString());
                }

            }
        });
    }
    public void receiveVideoFrom(UserConnection sender, String sdpOffer) throws IOException{
        log.info("USER {} : connecting with {} ", this.username, sender.getUsername());

        log.debug("USER {} : SdpOffer for {} is {}", this.username, sender.getUsername(), sdpOffer);

        final String ipSdpAnswer = this.getEndpointForUser(sender).processOffer(sdpOffer);
        final JsonObject scParams = new JsonObject();
        scParams.addProperty("id", "receiveVideoAnswer");
        //username
        scParams.addProperty("name", sender.getUsername());
        scParams.addProperty("sdpAnswer", ipSdpAnswer);

        log.debug("USER {}: SdpAnswer for {} is {}", this.username, sender.getUsername(), ipSdpAnswer);

        this.sendMessage(scParams);
        log.debug("gather candidates");
        this.getEndpointForUser(sender).gatherCandidates();
    }
    
    private WebRtcEndpoint getEndpointForUser(final UserConnection sender){
        if (sender.getUsername().equals(username)){
            log.debug("PARTICIPANT {}: cofiguring loopback", this.username);
            return outgoingMedia;
        }
        log.info("PARTICIPANT {}: receiving video from {}", this.username, sender.getUsername());
        
        WebRtcEndpoint incoming = incomingMedia.get(sender.getUsername());
        if (incoming == null){
            log.debug("PARTICIPANT {}: creating new endpoint for {}", this.username, sender.getUsername());
            incoming = new WebRtcEndpoint.Builder(mediaPipeline).build();

            incoming.addIceCandidateFoundListener(new EventListener<IceCandidateFoundEvent>() {

                @Override
                public void onEvent(IceCandidateFoundEvent event) {
                    JsonObject response = new JsonObject();
                    response.addProperty("id", "iceCandidate");
                    response.addProperty("name", sender.getUsername());
                    response.add("candidate", JsonUtils.toJsonObject(event.getCandidate()));
                    synchronized (messageInterface) {
                        messageInterface.broadCastToClient("client.response", sessionId, response.toString());
                    }
                }
            });
            incomingMedia.put(sender.getUsername(), incoming);
        }

        log.debug("PARTICIPANT {}: obtained endpoint for {}", this.username, sender.getUsername());
        sender.getOutgoingMedia().connect(incoming);

        return incoming;
    }
    public void sendMessage(JsonObject message) throws IOException {
        log.debug("USER {}: Sending message {}", username, message);
        synchronized (messageInterface) {
            messageInterface.broadCastToClient("client.response", sessionId, message.toString());
        }
    }

    public void cancelVideoFrom(final UserConnection sender) {
        this.cancelVideoFrom(sender.getUsername());
    }


    public void cancelVideoFrom(final String senderName) {
        log.debug("PARTICIPANT {}: canceling video reception from {}", this.username, senderName);
        final WebRtcEndpoint incoming = incomingMedia.remove(senderName);

        log.debug("PARTICIPANT {}: removing endpoint for {}", this.username, senderName);
        incoming.release(new Continuation<Void>() {
            @Override
            public void onSuccess(Void result) throws Exception {
                log.trace("PARTICIPANT {}: Released successfully incoming EP for {}",
                        UserConnection.this.username, senderName);
            }

            @Override
            public void onError(Throwable cause) throws Exception {
                log.warn("PARTICIPANT {}: Could not release incoming EP for {}", UserConnection.this.username,
                        senderName);
            }
        });
    }

    public void addCandidate(IceCandidate candidate, String username) {
        if (this.username.compareTo(username) == 0) {
            log.info("Adding "+ username + " to candidates");

            outgoingMedia.addIceCandidate(candidate);
        } else {
            WebRtcEndpoint webRtc = incomingMedia.get(username);
            if (webRtc != null) {
                webRtc.addIceCandidate(candidate);
            }
        }
    }

    @Override
    public void close() throws IOException {
        log.debug("PARTICIPANT {}: Releasing resources", this.username);
        for (final String remoteParticipantName : incomingMedia.keySet()) {

            log.trace("PARTICIPANT {}: Released incoming EP for {}", this.username, remoteParticipantName);

            final WebRtcEndpoint ep = this.incomingMedia.get(remoteParticipantName);

            ep.release(new Continuation<Void>() {

                @Override
                public void onSuccess(Void result) throws Exception {
                    log.trace("PARTICIPANT {}: Released successfully incoming EP for {}",
                            UserConnection.this.username, remoteParticipantName);
                }

                @Override
                public void onError(Throwable cause) throws Exception {
                    log.warn("PARTICIPANT {}: Could not release incoming EP for {}", UserConnection.this.username,
                            remoteParticipantName);
                }
            });
        }

        outgoingMedia.release(new Continuation<Void>() {

            @Override
            public void onSuccess(Void result) throws Exception {
                log.trace("PARTICIPANT {}: Released outgoing EP", UserConnection.this.username);
            }

            @Override
            public void onError(Throwable cause) throws Exception {
                log.warn("USER {}: Could not release outgoing EP", UserConnection.this.username);
            }
        });
    }
}
