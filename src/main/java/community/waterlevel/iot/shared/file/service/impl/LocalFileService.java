package community.waterlevel.iot.shared.file.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import community.waterlevel.iot.shared.file.model.FileInfo;
import community.waterlevel.iot.shared.file.service.FileService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.time.LocalDateTime;

/**
 * LocalFileService is an implementation of the
 * {@link community.waterlevel.iot.shared.file.service.FileService} interface
 * for managing file storage on the local filesystem.
 * <p>
 * This service provides methods for uploading and deleting files using the
 * server's local disk, organizing files by date, and generating accessible file
 * URLs. It is activated when the storage type is set to "local" in the
 * configuration.
 * <p>
 * Designed for use in development or on-premises deployments where cloud or
 * remote object storage is not required.
 *
 * @author Theo
 * @since 2024-12-09 17:11
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Data
@Slf4j
@Component
@ConditionalOnProperty(value = "oss.type", havingValue = "local")
@ConfigurationProperties(prefix = "oss.local")
@RequiredArgsConstructor
public class LocalFileService implements FileService {

    /**
     * The root directory path for storing files locally.
     */
    @Value("${oss.local.storage-path}")
    private String storagePath;

    /**
     * Uploads a file to the local file system.
     * <p>
     * The file is saved in a date-based subdirectory with a unique UUID-based
     * filename.
     * </p>
     *
     * @param file the multipart file to upload
     * @return a {@link FileInfo} object containing the uploaded file's metadata
     * @throws RuntimeException if the file upload fails
     */
    @Override
    public FileInfo uploadFile(MultipartFile file) {

        String originalFilename = file.getOriginalFilename();

        String suffix = FileUtil.getSuffix(originalFilename);

        String fileName = IdUtil.simpleUUID() + "." + suffix;

        String folder = DateUtil.format(LocalDateTime.now(), DatePattern.PURE_DATE_PATTERN);
        String filePrefix = storagePath.endsWith(File.separator) ? storagePath : storagePath + File.separator;

        try (InputStream inputStream = file.getInputStream()) {

            FileUtil.writeFromStream(inputStream, filePrefix + folder + File.separator + fileName);
        } catch (Exception e) {
            log.error("File upload failed", e);
            throw new RuntimeException("File upload failed");
        }
        String fileUrl = File.separator + folder + File.separator + fileName;
        FileInfo fileInfo = new FileInfo();
        fileInfo.setName(originalFilename);
        fileInfo.setUrl(fileUrl);
        return fileInfo;
    }

    /**
     * Deletes a file from the local file system.
     * <p>
     * Only files (not directories) can be deleted. Returns {@code false} if the
     * path is empty, null, or a directory.
     * </p>
     *
     * @param filePath the full URL or relative path of the file to delete
     * @return {@code true} if the file was deleted successfully; {@code false}
     *         otherwise
     */
    @Override
    public boolean deleteFile(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return false;
        }

        if (FileUtil.isDirectory(storagePath + filePath)) {
            return false;
        }
        return FileUtil.del(storagePath + filePath);
    }
}
