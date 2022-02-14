package com.entity;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vladmihalcea.hibernate.type.json.JsonNodeBinaryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.TypeDef;

import java.util.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@TypeDef(name = "jsonb", typeClass = JsonNodeBinaryType.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OtherFilesFromOtherMonthsObject {

    @JsonAlias(value = "workflow_id")
    private String workflowId;

    @JsonAlias(value = "semi_enriched_files")
    private List<String> semiEnrichedFiles;

}
