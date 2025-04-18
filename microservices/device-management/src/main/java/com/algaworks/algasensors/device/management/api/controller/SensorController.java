package com.algaworks.algasensors.device.management.api.controller;


import com.algaworks.algasensors.device.management.api.model.SensorInput;
import com.algaworks.algasensors.device.management.api.model.SensorOutput;
import com.algaworks.algasensors.device.management.common.IdGenerator;
import com.algaworks.algasensors.device.management.domain.model.Sensor;
import com.algaworks.algasensors.device.management.domain.model.SensorId;
import com.algaworks.algasensors.device.management.domain.repository.SensorRepository;
import io.hypersistence.tsid.TSID;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/sensors")
@RequiredArgsConstructor
public class SensorController {

    private final SensorRepository sensorRepository;


    @GetMapping
    public Page<SensorOutput> search(Pageable pageable){
        Page<Sensor> sensors = sensorRepository.findAll(pageable);
        return sensors.map(this::convertToModel);
    }

    @GetMapping("{sensorId}")
    public SensorOutput getOne(@PathVariable TSID sensorId){
        Sensor sensor = sensorRepository.findById(new SensorId(sensorId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return convertToModel(sensor);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SensorOutput create(@RequestBody SensorInput input) {
        Sensor sensor = Sensor.builder()
                .id(new SensorId(IdGenerator.generateTSID()))
                .name(input.getName())
                .ip(input.getIp())
                .location(input.getLocation())
                .protocol(input.getProtocol())
                .model(input.getModel())
                .enabled(false)
                .build();
        sensor = sensorRepository.saveAndFlush(sensor);

        return convertToModel(sensor);
    }

    @PutMapping("{sensorId}")
    public SensorOutput update(@PathVariable TSID sensorId, @Valid @RequestBody SensorInput input) {
        // Verificar se o sensor existe
        Sensor existingSensor = sensorRepository.findById(new SensorId(sensorId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Sensor não encontrado com ID: " + sensorId));

        // Atualizar todas as propriedades do sensor
        existingSensor.setName(input.getName());
        existingSensor.setIp(input.getIp());
        existingSensor.setLocation(input.getLocation());
        existingSensor.setProtocol(input.getProtocol());
        existingSensor.setModel(input.getModel());

        // Salvar as alterações
        Sensor updatedSensor = sensorRepository.saveAndFlush(existingSensor);

        // Retornar o sensor atualizado
        return convertToModel(updatedSensor);
    }

    @DeleteMapping("{sensorId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable TSID sensorId) {
        // Verificar se o sensor existe
        SensorId id = new SensorId(sensorId);
        if (!sensorRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Sensor não encontrado com ID: " + sensorId);
        }

        // Remover o sensor
        sensorRepository.deleteById(id);
    }

    /**
     * Ativa um sensor específico
     *
     * @param sensorId ID do sensor a ser ativado
     */
    @PatchMapping("{sensorId}/enable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void enableSensor(@PathVariable TSID sensorId) {
        Sensor sensor = sensorRepository.findById(new SensorId(sensorId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Sensor não encontrado com ID: " + sensorId));

        sensor.setEnabled(true);
        sensorRepository.saveAndFlush(sensor);
    }

    /**
     * Desativa um sensor específico
     *
     * @param sensorId ID do sensor a ser desativado
     */
    @PatchMapping("{sensorId}/disable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void disableSensor(@PathVariable TSID sensorId) {
        Sensor sensor = sensorRepository.findById(new SensorId(sensorId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Sensor não encontrado com ID: " + sensorId));

        sensor.setEnabled(false);
        sensorRepository.saveAndFlush(sensor);
    }


    private SensorOutput convertToModel(Sensor sensor) {
        return SensorOutput.builder()
                .id(sensor.getId().getValue())
                .name(sensor.getName())
                .ip(sensor.getIp())
                .location(sensor.getLocation())
                .protocol(sensor.getProtocol())
                .model(sensor.getModel())
                .enabled(sensor.getEnabled())
                .build();
    }

}
