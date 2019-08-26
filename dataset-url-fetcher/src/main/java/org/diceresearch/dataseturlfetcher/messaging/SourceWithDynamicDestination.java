package org.diceresearch.dataseturlfetcher.messaging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.binding.BinderAwareChannelResolver;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

@EnableBinding
@Service
public class SourceWithDynamicDestination {

    private final BinderAwareChannelResolver resolver;

    @Autowired
    public SourceWithDynamicDestination(BinderAwareChannelResolver resolver) {
        this.resolver = resolver;
    }

    public void sendMessage(byte[] body, String target) {
        resolver.resolveDestination(target)
                .send(MessageBuilder.withPayload(body).build());
    }
}
