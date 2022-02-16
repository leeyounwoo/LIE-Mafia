package com.lie.gamelogic.port;

import com.lie.gamelogic.domain.ExecutionVote;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExecutionVoteRepository extends CrudRepository<ExecutionVote,String> {

}
