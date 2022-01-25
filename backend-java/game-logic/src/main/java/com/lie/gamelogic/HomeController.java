package com.lie.gamelogic;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping
@Log4j2
public class HomeController {

    @GetMapping
    public ResponseEntity<LocalDateTime> test(){
        log.info("hi");
        return new ResponseEntity<>(LocalDateTime.now(), HttpStatus.OK);
    }
}
