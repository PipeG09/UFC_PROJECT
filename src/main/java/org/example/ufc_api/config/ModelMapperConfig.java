package org.example.ufc_api.config;
import org.example.ufc_api.dto.UsuarioDto;
import org.example.ufc_api.model.Usuario;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mm = new ModelMapper();
        mm.typeMap(Usuario.class, UsuarioDto.class)
                .addMappings(m -> m.map(Usuario::getRol, UsuarioDto::setRol));

        return mm;
    }
}