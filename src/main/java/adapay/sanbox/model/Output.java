package adapay.sanbox.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Output {

    private Status status;
    private String content;

    public enum Status {
        DONE,
        COMPILE_FAILURE,
        EXCEPTION
    }
}
