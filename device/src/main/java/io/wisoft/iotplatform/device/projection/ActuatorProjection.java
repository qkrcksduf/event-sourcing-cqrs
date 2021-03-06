package io.wisoft.iotplatform.device.projection;

import device.ValidationStatus;
import io.wisoft.iotplatform.device.domain.Actuator;
import io.wisoft.iotplatform.device.domain.ActuatorAggregate;
import io.wisoft.iotplatform.device.domain.ActuatorValidationCheckCommand;
import io.wisoft.iotplatform.device.repository.ActuatorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.Timestamp;
import org.springframework.stereotype.Component;
import sensing.AutoControlDetectedEvent;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor

public class ActuatorProjection {

  private final CommandGateway gateway;
  private final ActuatorRepository repository;

  @EventHandler
  public void checkExistActuator(AutoControlDetectedEvent event, @Timestamp Instant instant) {
    log.debug("projecting {}, timestamp {}", event, instant);
    ValidationStatus actuatorValidation;
    String description;

    Optional<Actuator> findActuator = repository.findById(event.getActuatorId());
    System.out.println("test1");

    try {
      Actuator actuator = findActuator.orElseGet(null);
      actuatorValidation = ValidationStatus.VALID;
      description = "모든 유효성 검사를 통과했으므로 동작 가능합니다.";

      System.out.println(actuatorValidation);

      gateway.send(new ActuatorValidationCheckCommand(
          UUID.randomUUID(),
          actuator.getId(),
          actuator.getDevice().getId(),
          actuator.getModelId(),
          actuator.getName(),
          actuator.getDevice().getName(),
          actuatorValidation,
          description,
          event.getActuatingValue()));
    } catch (NullPointerException e) {
      actuatorValidation = ValidationStatus.INVALID;
      description = "actuator가 존재하지 않으므로 제어할 수 없습니다.";

      gateway.send(new ActuatorValidationCheckCommand(
          UUID.randomUUID(),
          event.getActuatorId(),
          actuatorValidation,
          event.getActuatingValue(),
          description));
    }


//    if (actuator == null) {
//      actuatorValidation = ValidationStatus.INVALID;
//      description = "actuator가 존재하지 않으므로 제어할 수 없습니다.";
//
//      gateway.send(new ActuatorValidationCheckCommand(
//          UUID.randomUUID(),
//          event.getActuatorId(),
//          actuatorValidation,
//          event.getActuatingValue(),
//          description));
//
//
//    } else {
//      actuatorValidation = ValidationStatus.VALID;
//      description = "모든 유효성 검사를 통과했으므로 동작 가능합니다.";
//
//      System.out.println(actuatorValidation);
//
//      gateway.send(new ActuatorValidationCheckCommand(
//          UUID.randomUUID(),
//          actuator.getId(),
//          actuator.getDevice().getId(),
//          actuator.getModelId(),
//          actuator.getName(),
//          actuator.getDevice().getName(),
//          actuatorValidation,
//          description,
//          event.getActuatingValue()));
//    }
  }

}
