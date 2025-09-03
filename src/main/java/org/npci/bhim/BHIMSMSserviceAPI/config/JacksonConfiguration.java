//package org.npci.bhim.BHIMSMSserviceAPI.config;
//
//import com.fasterxml.jackson.databind.MapperFeature;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.SerializationFeature;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//@Slf4j
//public class JacksonConfiguration {
//
//    @Bean
//    public ObjectMapper objectMapper() {
//
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.registerModule(new JavaTimeModule());
//        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Optional: forces ISO format
//        mapper.disable(MapperFeature.REQUIRE_HANDLERS_FOR_JAVA8_TIMES);
//        return mapper;
//    }
//}
