package community.waterlevel.iot.shared.file.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import community.waterlevel.iot.common.result.Result;
import community.waterlevel.iot.shared.file.model.FileInfo;
import community.waterlevel.iot.shared.file.service.FileService;

/**
 * FileController is a REST controller that provides APIs for file management
 * operations such as file upload and deletion.
 * <p>
 * It exposes endpoints for uploading files via multipart requests and deleting
 * files by file path. The controller delegates file operations to the
 * {@link FileService} and returns standardized API responses.
 * <p>
 * Annotated for OpenAPI/Swagger documentation and designed for integration with
 * the IoT backend's file handling requirements.
 *
 * @author Ray.Hao
 * @since 2022/10/16
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Tag(name = "07.Document Center")
@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    /**
     * Handles file upload requests.
     * <p>
     * Accepts a multipart file and delegates the upload process to the
     * {@link FileService}.
     * </p>
     *
     * @param file the multipart file to be uploaded
     * @return a {@link Result} containing the uploaded file's information
     */
    @PostMapping
    @Operation(summary = "Upload a file")
    public Result<FileInfo> uploadFile(
            @Parameter(name = "file", description = "Form file object", required = true, in = ParameterIn.DEFAULT, schema = @Schema(name = "file", format = "binary")) @RequestPart(value = "file") MultipartFile file) {
        FileInfo fileInfo = fileService.uploadFile(file);
        return Result.success(fileInfo);
    }

    /**
     * Handles file deletion requests.
     * <p>
     * Deletes the file at the specified file path using the {@link FileService}.
     * </p>
     *
     * @param filePath the path of the file to be deleted
     * @return a {@link Result} indicating whether the deletion was successful
     */
    @DeleteMapping
    @Operation(summary = "Delete a file")
    @SneakyThrows
    public Result<?> deleteFile(
            @Parameter(description = "File path") @RequestParam String filePath) {
        boolean result = fileService.deleteFile(filePath);
        return Result.judge(result);
    }
}
