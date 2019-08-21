package adapay.sanbox.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author bin.zhang
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Snippet {

    private Language language;

    public enum Language {
        JAVA
    }
}
