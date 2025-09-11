package community.waterlevel.iot.shared.file.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import community.waterlevel.iot.common.exception.BusinessException;
import community.waterlevel.iot.common.result.ResultCode;
import community.waterlevel.iot.shared.file.model.FileInfo;
import community.waterlevel.iot.shared.file.service.FileService;
import io.minio.*;
import io.minio.http.Method;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;

/**
 * MinioFileService is an implementation of the
 * {@link community.waterlevel.iot.shared.file.service.FileService} interface
 * for managing file storage using MinIO object storage.
 * <p>
 * This service provides methods for uploading and deleting files in a MinIO
 * bucket, generating accessible file URLs, and handling bucket creation and
 * policy configuration. It is activated when the storage type is set to "minio"
 * in the configuration.
 * <p>
 * Designed for use in cloud-native or distributed deployments where scalable
 * object storage is required.
 *
 * @author Ray.Hao
 * @since 2023/6/2
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Component
@ConditionalOnProperty(value = "oss.type", havingValue = "minio")
@ConfigurationProperties(prefix = "oss.minio")
@RequiredArgsConstructor
@Data
@Slf4j
public class MinioFileService implements FileService {

    /**
     * Service endpoint for MinIO server
     */
    private String endpoint;
    /**
     * Access key for MinIO authentication
     */
    private String accessKey;
    /**
     * Secret key for MinIO authentication
     */
    private String secretKey;
    /**
     * Name of the MinIO bucket
     */
    private String bucketName;
    /**
     * Custom domain for file access (optional)
     */
    private String customDomain;

    private MinioClient minioClient;

    /**
     * Initializes the MinioClient after dependency injection is complete.
     */
    @PostConstruct
    public void init() {
        minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
        // createBucketIfAbsent(bucketName);
    }


    /**
     * Uploads a file to the MinIO server and returns file information.
     *
     * @param file  Multipart file object to upload
     * @return  FileInfo object containing file name and URL
     */
    @Override
    public FileInfo uploadFile(MultipartFile file) {

        // Create the bucket if it does not exist (recommended to do in init if MinIO is already set up)
        createBucketIfAbsent(bucketName);


        // Original file name
        String originalFilename = file.getOriginalFilename();

        // File extension
        String suffix = FileUtil.getSuffix(originalFilename);

        // Folder name based on current date
        String dateFolder = DateUtil.format(LocalDateTime.now(), "yyyyMMdd");

        // Unique file name
        String fileName = IdUtil.simpleUUID() + "." + suffix;


        // Use try-with-resources to auto-close the stream
        try (InputStream inputStream = file.getInputStream()) {
            // Upload file to MinIO
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(dateFolder + "/"+ fileName)
                    .contentType(file.getContentType())
                    .stream(inputStream, inputStream.available(), -1)
                    .build();
            minioClient.putObject(putObjectArgs);

            // Return file URL
            String fileUrl;
            // If custom domain is not configured
            if (StrUtil.isBlank(customDomain)) {
                // Get file URL from MinIO
                GetPresignedObjectUrlArgs getPresignedObjectUrlArgs = GetPresignedObjectUrlArgs.builder()
                        .bucket(bucketName)
                        .object(dateFolder + "/"+ fileName)
                        .method(Method.GET)
                        .build();

                fileUrl = minioClient.getPresignedObjectUrl(getPresignedObjectUrlArgs);
                fileUrl = fileUrl.substring(0, fileUrl.indexOf("?"));
            } else {
                // Use custom domain for file URL
                fileUrl = customDomain + "/"+ bucketName + "/"+ dateFolder + "/"+ fileName;
            }

            FileInfo fileInfo = new FileInfo();
            fileInfo.setName(originalFilename);
            fileInfo.setUrl(fileUrl);
            return fileInfo;
        } catch (Exception e) {
            log.error("File upload failed", e);
            throw new BusinessException(ResultCode.UPLOAD_FILE_EXCEPTION, e.getMessage());
        }
    }


    /**
     * Deletes a file from the MinIO server by its full path.
     *
     * @param filePath  Full file path to delete
     * @return  true if deletion is successful, false otherwise
     */
    @Override
    public boolean deleteFile(String filePath) {
        Assert.notBlank(filePath, "The path to delete the file cannot be empty");
        try {
            String fileName;
            if (StrUtil.isNotBlank(customDomain)) {
                fileName = filePath.substring(customDomain.length() + 1 + bucketName.length() + 1); // 两个/占了2个字符长度
            } else {
                fileName = filePath.substring(endpoint.length() + 1 + bucketName.length() + 1);
            }
            RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .build();

            minioClient.removeObject(removeObjectArgs);
            return true;
        } catch (Exception e) {
            log.error("Failed to delete file", e);
            throw new BusinessException(ResultCode.DELETE_FILE_EXCEPTION, e.getMessage());
        }
    }


    /**
     * PUBLIC桶策略
     * Returns the public bucket policy in JSON format for the specified bucket.
     * If not configured, newly created buckets are PRIVATE by default and files will be denied access (Access Denied).
     *
     * @param bucketName  Name of the bucket
     * @return  JSON string of the bucket policy
     */
    private static String publicBucketPolicy(String bucketName) {
        return "{\"Version\":\"2012-10-17\","
                + "\"Statement\":[{\"Effect\":\"Allow\","
                + "\"Principal\":{\"AWS\":[\"*\"]},"
                + "\"Action\":[\"s3:ListBucketMultipartUploads\",\"s3:GetBucketLocation\",\"s3:ListBucket\"],"
                + "\"Resource\":[\"arn:aws:s3:::" + bucketName + "\"]},"
                + "{\"Effect\":\"Allow\"," + "\"Principal\":{\"AWS\":[\"*\"]},"
                + "\"Action\":[\"s3:ListMultipartUploadParts\",\"s3:PutObject\",\"s3:AbortMultipartUpload\",\"s3:DeleteObject\",\"s3:GetObject\"],"
                + "\"Resource\":[\"arn:aws:s3:::" + bucketName + "/*\"]}]}";
    }

    /**
     * Creates a bucket if it does not already exist.
     *
     * @param bucketName  Name of the bucket to create
     */
    @SneakyThrows
    private void createBucketIfAbsent(String bucketName) {
        BucketExistsArgs bucketExistsArgs = BucketExistsArgs.builder().bucket(bucketName).build();
        if (!minioClient.bucketExists(bucketExistsArgs)) {
            MakeBucketArgs makeBucketArgs = MakeBucketArgs.builder().bucket(bucketName).build();

            minioClient.makeBucket(makeBucketArgs);

            // Set bucket access policy to PUBLIC. If not set, new buckets are PRIVATE by default and files will be denied access.
            SetBucketPolicyArgs setBucketPolicyArgs = SetBucketPolicyArgs
                    .builder()
                    .bucket(bucketName)
                    .config(publicBucketPolicy(bucketName))
                    .build();
            minioClient.setBucketPolicy(setBucketPolicyArgs);
        }
    }
}