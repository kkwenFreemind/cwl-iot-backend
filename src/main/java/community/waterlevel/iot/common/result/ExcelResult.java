package community.waterlevel.iot.common.result;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Response structure for Excel import/export operations.
 * Contains result code, counts of valid and invalid records, and error
 * messages.
 * Used to communicate the outcome of Excel data processing to the client.
 *
 * @author Theo
 * @since 2025/1/14 11:46:08
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Data
public class ExcelResult {

    /**
     * Response code indicating the result of the Excel operation.
     * Typically corresponds to a predefined result code such as success or failure.
     */
    private String code;

    /**
     * The number of valid records processed during the Excel operation.
     */
    private Integer validCount;

    /**
     * The number of invalid records encountered during the Excel operation.
     */
    private Integer invalidCount;

    /**
     * A list of messages providing details about the processing results, such as
     * errors or warnings.
     */
    private List<String> messageList;

    /**
     * Default constructor initializing the result as a successful operation with
     * zero valid and invalid counts,
     * and an empty message list.
     */
    public ExcelResult() {
        this.code = ResultCode.SUCCESS.getCode();
        this.validCount = 0;
        this.invalidCount = 0;
        this.messageList = new ArrayList<>();
    }
}
