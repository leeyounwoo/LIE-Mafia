package com.lie.gamelogic.dto;

import com.lie.gamelogic.domain.Job;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class GameEndDto {
    Job Winner;
    Job Loser;
    List<String> WinnerList;
    List<String> LoserList ;
}
