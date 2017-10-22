package com.db.awmd.challenge.dto;

import com.db.awmd.challenge.validation.SecondValidation;
import com.db.awmd.challenge.validation.resource.annotation.ValidAccount;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.GroupSequence;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

@Data
@GroupSequence({TransferDTO.class, SecondValidation.class})
public class TransferDTO {

    @Valid
    @NotNull
    @NotEmpty
    @ValidAccount
    private String originatingAccountId;

    @Valid
    @NotNull
    @NotEmpty
    @ValidAccount
    private String beneficiaryAccountId;

    @Valid
    @NotNull
    @NotEmpty
    private String transferAmount;

    @JsonCreator
    public TransferDTO(
            @JsonProperty("originatingAccountId") String originatingAccountId,
            @JsonProperty("beneficiaryAccountId") String beneficiaryAccountId,
            @JsonProperty("transferAmount") String transferAmount) {
        this.originatingAccountId = originatingAccountId;
        this.beneficiaryAccountId = beneficiaryAccountId;
        this.transferAmount = transferAmount;
    }

    public BigDecimal getTransferAmoutEuqivalent() {
        return new BigDecimal(this.getTransferAmount());
    }
}
