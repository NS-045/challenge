package com.db.awmd.challenge.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TransferDto {
    @NotNull
    private String accountFromId;
    @NotNull
    private String accountToId;
    @NotNull
    private BigDecimal amount;
}
