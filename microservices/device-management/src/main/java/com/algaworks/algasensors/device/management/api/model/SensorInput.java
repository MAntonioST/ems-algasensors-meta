package com.algaworks.algasensors.device.management.api.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SensorInput {

    @NotBlank(message = "O nome do sensor é obrigatório")
    private String name;

    @NotBlank(message = "O IP do sensor é obrigatório")
    private String ip;

    @NotBlank(message = "A localização do sensor é obrigatória")
    private String location;

    @NotBlank(message = "O protocolo do sensor é obrigatório")
    private String protocol;

    @NotBlank(message = "O modelo do sensor é obrigatório")
    private String model;
}
