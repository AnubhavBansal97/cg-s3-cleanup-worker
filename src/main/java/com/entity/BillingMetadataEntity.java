package com.entity;

import javax.persistence.Entity;

import com.constants.ConfigConstants;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import com.vladmihalcea.hibernate.type.json.JsonNodeBinaryType;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.Column;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@TypeDef(name = "jsonb", typeClass = JsonNodeBinaryType.class)
@Table(name= ConfigConstants.BILLING_METADATA_TABLE)

public class BillingMetadataEntity {

        @Id
        @Column(name = "customer_hash",nullable = false)
        private String customerHash;

        @Column(name = "cloud", nullable = false)
        private Integer cloud;

        @Column(name = "account_uuid")
        private String accountUUID;

        @Type(type = "jsonb")
        @Column(name = "semi_enriched_files", columnDefinition = "jsonb")
        private JsonNode  semiEnrichedFiles;

        @Column(name="is_semi_ingested")
        private Boolean isSemiIngested;

        @Column(name="invocation_id")
        private String invocationId;

        @Column(name="month")
        private String month;


}
