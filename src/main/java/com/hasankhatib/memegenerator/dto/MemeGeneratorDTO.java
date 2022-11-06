package com.hasankhatib.memegenerator.dto;

import lombok.Builder;
import lombok.Data;

import java.io.File;


@Data
@Builder
public class MemeGeneratorDTO {
  File memeFile;
}
