package com.lie.gamelogic.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResultDto {
    String dead;
    FindDto winner;
    FindDto loser;
}
