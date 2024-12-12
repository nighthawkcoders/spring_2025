package com.nighthawk.spring_portfolio.mvc.assignments;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Converter
public class QueueConverter implements AttributeConverter<Queue, String> {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Queue queue) {
        try {
            // If queue is null, initialize a new empty Queue and convert to JSON
            if (queue == null) {
                queue = new Queue();
            }
            return objectMapper.writeValueAsString(queue);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting Queue to JSON", e);
        }
    }

    @Override
    public Queue convertToEntityAttribute(String dbData) {
        try {
            // If dbData is null or empty, return a new empty Queue
            if (dbData == null || dbData.isEmpty()) {
                return new Queue();
            }
            return objectMapper.readValue(dbData, Queue.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting JSON to Queue", e);
        }
    }
}
