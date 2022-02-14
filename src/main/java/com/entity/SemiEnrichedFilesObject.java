package com.entity;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vladmihalcea.hibernate.type.json.JsonNodeBinaryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Entity;
import java.util.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@TypeDef(name = "jsonb", typeClass = JsonNodeBinaryType.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SemiEnrichedFilesObject {

    @JsonAlias(value = "other_files_from_other_months")
    private  Map<String,Map<String,OtherFilesFromOtherMonthsObject>> otherFilesFromOtherMonths;

    @JsonAlias(value = "actual_files_for_current_month")
    private  Map<String,List<String> > actualFilesForCurrentMonth;
}
