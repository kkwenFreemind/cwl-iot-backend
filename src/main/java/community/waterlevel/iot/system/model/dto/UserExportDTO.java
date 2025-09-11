package community.waterlevel.iot.system.model.dto;

import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.format.DateTimeFormat;
import cn.idev.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * UserExportDTO is a data transfer object for exporting user data to Excel
 * files.
 * <p>
 * This class defines the fields and formatting for user export operations,
 * including username, nickname, department, gender, contact information, and
 * creation time.
 * Used in conjunction with EasyExcel for generating user reports in the IoT
 * backend.
 *
 * @author haoxr
 * @since 2022/4/11 8:46
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */

@Data
@ColumnWidth(20)
public class UserExportDTO {

    @ExcelProperty(value = "username")
    private String username;

    @ExcelProperty(value = "nickname")
    private String nickname;

    @ExcelProperty(value = "deptName")
    private String deptName;

    @ExcelProperty(value = "gender")
    private String gender;

    @ExcelProperty(value = "mobile")
    private String mobile;

    @ExcelProperty(value = "email")
    private String email;

    @ExcelProperty(value = "Create Time")
    @DateTimeFormat("yyyy/MM/dd HH:mm:ss")
    private LocalDateTime createTime;

}
