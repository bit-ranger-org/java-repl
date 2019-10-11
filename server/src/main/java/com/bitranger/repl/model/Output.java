package com.bitranger.repl.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author bin.zhang
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Output {

    private Status status;
    private List<String> output;
    private List<String> error;

    public enum Status {
        SUCCESS,
        COMPILE_FAILURE,
        EXCEPTION
    }
}
