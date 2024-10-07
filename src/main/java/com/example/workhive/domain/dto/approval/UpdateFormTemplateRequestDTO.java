package com.example.workhive.domain.dto.approval;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateFormTemplateRequestDTO {
    @NotNull
    @JsonProperty("formStructure")
    private String formStructure;
}