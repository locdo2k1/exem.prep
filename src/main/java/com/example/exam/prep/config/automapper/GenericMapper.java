package com.example.exam.prep.config.automapper;
import org.modelmapper.ModelMapper;

public class GenericMapper {
    private static ModelMapper modelMapper = new ModelMapper();

    public static <T, U> T toEntity(U source, T destination) {
        modelMapper.map(source, destination);
        return destination;
    }

    public static <T, U> T toEntity(U source, Class<T> destinationClass) {
        return modelMapper.map(source, destinationClass);
    }
}