package community.waterlevel.iot.common.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Generic option model for dropdowns or selection components.
 * Supports value-label pairs, optional tag, and hierarchical child options for nested selections.
 * Commonly used in APIs and UI forms to represent selectable items.
 *
 * @param <T> the type of the option value
 *
 * @author haoxr
 * @since 2022/1/22
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 * 
 */
@Schema(description = "Dropdown options object")
@Data
@NoArgsConstructor
public class Option<T> {

    /**
     * Constructs an Option with the specified value and label.
     *
     * @param value the value of the option
     * @param label the label of the option
     */
    public Option(T value, String label) {
        this.value = value;
        this.label = label;
    }

    /**
     * Constructs an Option with the specified value, label, and child options.
     *
     * @param value    the value of the option
     * @param label    the label of the option
     * @param children the list of child options
     */
    public Option(T value, String label, List<Option<T>> children) {
        this.value = value;
        this.label = label;
        this.children = children;
    }

    /**
     * Constructs an Option with the specified value, label, and tag.
     *
     * @param value the value of the option
     * @param label the label of the option
     * @param tag   the tag type of the option
     */
    public Option(T value, String label, String tag) {
        this.value = value;
        this.label = label;
        this.tag = tag;
    }

    /**
     * The value of the option.
     */
    @Schema(description = "Option value")
    private T value;

    /**
     * The label of the option.
     */
    @Schema(description = "Option label")
    private String label;

    /**
     * The tag type of the option (for UI display or categorization).
     */
    @Schema(description = "Tag type")
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    private String tag;

    /**
     * The list of child options (for hierarchical dropdowns).
     */
    @Schema(description = "List of child options")
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    private List<Option<T>> children;

}