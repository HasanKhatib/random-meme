package com.hasankhatib.randommeme.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Data;

@Data
public class DadJokeResponseDTO {
  private JsonNode body;
  private boolean success;
  public DadJokeResponseDTO(){}
}
