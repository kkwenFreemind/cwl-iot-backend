package community.waterlevel.iot.shared.file.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


/**
 * FileInfo is a data model representing metadata for a file managed by the system.
 * <p>
 * It contains properties for the file's name and its accessible URL, and is used as a response object for file upload and retrieval operations.
 * Annotated for OpenAPI/Swagger documentation and designed for use in file-related APIs.
 *
 * @author Ray.Hao
 * @since 1.0.0
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Schema(description = "File Object")
@Data
public class FileInfo {

    @Schema(description = "File name")
    private String name;

    @Schema(description = "File URL")
    private String url;

}
