package com.kurtcan.sepaggregatorservice.shared.cache.hazelcast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.nio.serialization.ByteArraySerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomHazelcastSerializer implements ByteArraySerializer<Object> {

    @Qualifier("hazelcastObjectMapper")
    private final ObjectMapper hazelcastObjectMapper;

    public CustomHazelcastSerializer(@Qualifier("hazelcastObjectMapper") ObjectMapper hazelcastObjectMapper) {
        this.hazelcastObjectMapper = hazelcastObjectMapper;
    }

    @Override
    public byte[] write(Object object) throws IOException {
        return hazelcastObjectMapper.writeValueAsBytes(object);
    }

    @Override
    public Object read(byte[] buffer) throws IOException {
        return hazelcastObjectMapper.readValue(buffer, Object.class);
    }

    @Override
    public int getTypeId() {
        return 1; // Unique ID for this serializer
    }

    @Override
    public void destroy() {
        // No-op
    }
}