package io.wisoft.eventsourcing.query.exception;

import lombok.Builder;
import lombok.Getter;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class BindingErrorResponse {

  private final String title;
  private final String message;
  private List<FieldErrorInfo> errors;

  public void setErrors(final List<FieldError> errors) {
    this.errors = errors
        .stream()
        .map(FieldErrorInfo::new)
        .collect(Collectors.toList());
  }

  @Getter
  static class FieldErrorInfo {
    private final String field;
    private final String message;

    private FieldErrorInfo(FieldError fieldError) {
      this.field = fieldError.getField();
      this.message = fieldError.getDefaultMessage();
    }
  }
}
