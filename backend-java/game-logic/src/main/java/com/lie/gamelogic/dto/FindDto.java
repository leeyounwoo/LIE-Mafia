package com.lie.gamelogic.dto;

import com.lie.gamelogic.domain.Job;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class FindDto {
    Job job;
    List<String> member;
}
