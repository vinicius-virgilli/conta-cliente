package org.viniciusvirgilli.exception.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class CamposComProblemasDto {
    @JsonProperty("Campo(s) com problema(s):")
    private List<String> campos;
}
