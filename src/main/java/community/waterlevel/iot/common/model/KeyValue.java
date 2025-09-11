package community.waterlevel.iot.common.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Simple key-value pair model.
 * Commonly used for representing options, configuration entries, or generic
 * mappings in APIs and UI components.
 *
 * @author haoxr
 * @since 2024/5/25
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Schema(description = "Key-value pairs")
@Data
@NoArgsConstructor
public class KeyValue {

    /**
     * Constructs a KeyValue object with the specified key and value.
     *
     * @param key   the key of the option
     * @param value the label or value of the option
     */
    public KeyValue(String key, String value) {
        this.key = key;
        this.value = value;
    }

    /**
     * The key of the option.
     */
    @Schema(description = "Option key")
    private String key;

    /**
     * The label or value of the option.
     */
    @Schema(description = "Option label")
    private String value;

}