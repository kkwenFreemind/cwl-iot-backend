package community.waterlevel.iot.shared.file.service;

import org.springframework.web.multipart.MultipartFile;
import community.waterlevel.iot.shared.file.model.FileInfo;

/**
 * FileService defines the abstraction for file storage operations in the
 * system.
 * <p>
 * Implementations of this interface provide methods for uploading and deleting
 * files, supporting integration with various object storage solutions.
 * Used by controllers and other components to manage file persistence and
 * retrieval.
 *
 * @author haoxr
 * @since 2022/11/19
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
public interface FileService {

    /**
     * Uploads a file to the object storage.
     *
     * @param file Multipart file object to upload
     * @return FileInfo object containing file details
     */
    FileInfo uploadFile(MultipartFile file);

    /**
     * Deletes a file from the object storage by its full URL.
     *
     * @param filePath Full URL of the file to delete
     * @return true if deletion is successful, false otherwise
     */
    boolean deleteFile(String filePath);

}
