package community.waterlevel.iot.system.model.dto;

import cn.idev.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * UserImportDTO is a data transfer object for importing user data from Excel
 * files.
 * <p>
 * This class defines the fields expected in the import template, including
 * username, nickname, gender, contact information, roles, and department code.
 * Used in conjunction with EasyExcel for batch user import operations in the
 * IoT backend.
 *
 * @author Ray.Hao
 * @since 2022/4/10
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Data
public class UserImportDTO {

    @ExcelProperty(value = "username")
    private String username;

    @ExcelProperty(value = "nickname")
    private String nickname;

    @ExcelProperty(value = "genderLabel")
    private String genderLabel;

    @ExcelProperty(value = "mobile")
    private String mobile;

    @ExcelProperty(value = "email")
    private String email;

    @ExcelProperty("roleCodes")
    private String roleCodes;

    @ExcelProperty("deptCode")
    private String deptCode;

}
