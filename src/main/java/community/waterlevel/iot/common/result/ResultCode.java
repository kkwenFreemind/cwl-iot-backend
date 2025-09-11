package community.waterlevel.iot.common.result;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Enumeration of standardized response codes for API results.
 * <p>
 * Follows Alibaba Java Development Manual response code conventions:
 * <ul>
 * <li>00000: Success</li>
 * <li>A****: User-side errors</li>
 * <li>B****: System execution errors</li>
 * <li>C****: Third-party service errors</li>
 * </ul>
 * Each code is paired with a descriptive message.
 *
 * @author Ray.Hao
 * @since 2020/6/23
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 * 
 **/
@AllArgsConstructor
@NoArgsConstructor
public enum ResultCode implements IResultCode {

    /**
     * Operation completed successfully.
     */
    SUCCESS("00000", "OK"),

    /**
     * User-side error (primary macro code).
     */
    USER_ERROR("A0001", "User-side error"),

    /**
     * User registration error (secondary macro code).
     */
    USER_REGISTRATION_ERROR("A0100", "User registration error"),
    /**
     * User did not agree to the privacy agreement.
     */
    USER_NOT_AGREE_PRIVACY_AGREEMENT("A0101", "User did not agree to privacy agreement"),
    /**
     * Registration is restricted by country or region.
     */
    REGISTRATION_COUNTRY_OR_REGION_RESTRICTED("A0102", "Registration country or region restricted"),

    /**
     * Username verification failed.
     */
    USERNAME_VERIFICATION_FAILED("A0110", "Username verification failed"),
    /**
     * Username already exists.
     */
    USERNAME_ALREADY_EXISTS("A0111", "Username already exists"),
    /**
     * Username contains sensitive words.
     */
    USERNAME_CONTAINS_SENSITIVE_WORDS("A0112", "Username contains sensitive words"),
    /**
     * Username contains special characters.
     */
    USERNAME_CONTAINS_SPECIAL_CHARACTERS("A0113", "Username contains special characters"),

    /**
     * Password verification failed.
     */
    PASSWORD_VERIFICATION_FAILED("A0120", "Password verification failed"),
    /**
     * Password length is insufficient.
     */
    PASSWORD_LENGTH_NOT_ENOUGH("A0121", "Password length not enough"),
    /**
     * Password strength is insufficient.
     */
    PASSWORD_STRENGTH_NOT_ENOUGH("A0122", "Password strength not enough"),

    /**
     * Verification code input error.
     */
    VERIFICATION_CODE_INPUT_ERROR("A0130", "Verification code input error"),
    /**
     * SMS verification code input error.
     */
    SMS_VERIFICATION_CODE_INPUT_ERROR("A0131", "SMS verification code input error"),
    /**
     * Email verification code input error.
     */
    EMAIL_VERIFICATION_CODE_INPUT_ERROR("A0132", "Email verification code input error"),
    /**
     * Voice verification code input error.
     */
    VOICE_VERIFICATION_CODE_INPUT_ERROR("A0133", "Voice verification code input error"),

    /**
     * User certificate exception.
     */
    USER_CERTIFICATE_EXCEPTION("A0140", "User certificate exception"),
    /**
     * User certificate type not selected.
     */
    USER_CERTIFICATE_TYPE_NOT_SELECTED("A0141", "User certificate type not selected"),
    /**
     * Mainland ID number verification is illegal.
     */
    MAINLAND_ID_NUMBER_VERIFICATION_ILLEGAL("A0142", "Mainland ID number verification illegal"),

    /**
     * User basic information verification failed.
     */
    USER_BASIC_INFORMATION_VERIFICATION_FAILED("A0150", "User basic information verification failed"),
    /**
     * Phone format verification failed.
     */
    PHONE_FORMAT_VERIFICATION_FAILED("A0151", "Phone format verification failed"),
    /**
     * Address format verification failed.
     */
    ADDRESS_FORMAT_VERIFICATION_FAILED("A0152", "Address format verification failed"),
    /**
     * Email format verification failed.
     */
    EMAIL_FORMAT_VERIFICATION_FAILED("A0153", "Email format verification failed"),

    /**
     * User login exception (secondary macro code).
     */
    USER_LOGIN_EXCEPTION("A0200", "User login exception"),
    /**
     * User account is frozen.
     */
    USER_ACCOUNT_FROZEN("A0201", "User account frozen"),
    /**
     * User account has been abolished.
     */
    USER_ACCOUNT_ABOLISHED("A0202", "User account abolished"),

    /**
     * Username or password error.
     */
    USER_PASSWORD_ERROR("A0210", "Username or password error"),
    /**
     * User input password error limit exceeded.
     */
    USER_INPUT_PASSWORD_ERROR_LIMIT_EXCEEDED("A0211", "User input password error limit exceeded"),
    /**
     * User does not exist.
     */
    USER_NOT_EXIST("A0212", "User does not exist"),

    /**
     * User identity verification failed.
     */
    USER_IDENTITY_VERIFICATION_FAILED("A0220", "User identity verification failed"),
    /**
     * User fingerprint recognition failed.
     */
    USER_FINGERPRINT_RECOGNITION_FAILED("A0221", "User fingerprint recognition failed"),
    /**
     * User face recognition failed.
     */
    USER_FACE_RECOGNITION_FAILED("A0222", "User face recognition failed"),
    /**
     * User not authorized for third-party login.
     */
    USER_NOT_AUTHORIZED_THIRD_PARTY_LOGIN("A0223", "User not authorized for third-party login"),

    /**
     * Access token is invalid or expired.
     */
    ACCESS_TOKEN_INVALID("A0230", "Access token invalid or expired"),
    /**
     * Refresh token is invalid or expired.
     */
    REFRESH_TOKEN_INVALID("A0231", "Refresh token invalid or expired"),

    /**
     * User verification code error.
     */
    USER_VERIFICATION_CODE_ERROR("A0240", "User verification code error"),
    /**
     * User verification code attempt limit exceeded.
     */
    USER_VERIFICATION_CODE_ATTEMPT_LIMIT_EXCEEDED("A0241", "User verification code attempt limit exceeded"),
    /**
     * User verification code expired.
     */
    USER_VERIFICATION_CODE_EXPIRED("A0242", "User verification code expired"),

    /**
     * Access permission exception (secondary macro code).
     */
    ACCESS_PERMISSION_EXCEPTION("A0300", "Access permission exception"),
    /**
     * Access unauthorized.
     */
    ACCESS_UNAUTHORIZED("A0301", "Access unauthorized"),
    /**
     * Authorization in progress.
     */
    AUTHORIZATION_IN_PROGRESS("A0302", "Authorization in progress"),
    /**
     * User authorization application rejected.
     */
    USER_AUTHORIZATION_APPLICATION_REJECTED("A0303", "User authorization application rejected"),

    /**
     * Access blocked due to object privacy settings.
     */
    ACCESS_OBJECT_PRIVACY_SETTINGS_BLOCKED("A0310", "Access blocked due to object privacy settings"),
    /**
     * Authorization expired.
     */
    AUTHORIZATION_EXPIRED("A0311", "Authorization expired"),
    /**
     * No permission to use API.
     */
    NO_PERMISSION_TO_USE_API("A0312", "No permission to use API"),

    /**
     * User access blocked.
     */
    USER_ACCESS_BLOCKED("A0320", "User access blocked"),
    /**
     * Blacklisted user.
     */
    BLACKLISTED_USER("A0321", "Blacklisted user"),
    /**
     * Account frozen.
     */
    ACCOUNT_FROZEN("A0322", "Account frozen"),
    /**
     * Illegal IP address.
     */
    ILLEGAL_IP_ADDRESS("A0323", "Illegal IP address"),
    /**
     * Gateway access restricted.
     */
    GATEWAY_ACCESS_RESTRICTED("A0324", "Gateway access restricted"),
    /**
     * Region blacklist.
     */
    REGION_BLACKLIST("A0325", "Region blacklist"),

    /**
     * Service arrears.
     */
    SERVICE_ARREARS("A0330", "Service arrears"),

    /**
     * User signature exception.
     */
    USER_SIGNATURE_EXCEPTION("A0340", "User signature exception"),
    /**
     * RSA signature error.
     */
    RSA_SIGNATURE_ERROR("A0341", "RSA signature error"),

    /**
     * User request parameter error (secondary macro code).
     */
    USER_REQUEST_PARAMETER_ERROR("A0400", "User request parameter error"),
    /**
     * Contains illegal or malicious redirect link.
     */
    CONTAINS_ILLEGAL_MALICIOUS_REDIRECT_LINK("A0401", "Contains illegal malicious redirect link"),
    /**
     * Invalid user input.
     */
    INVALID_USER_INPUT("A0402", "Invalid user input"),

    /**
     * Request required parameter is empty.
     */
    REQUEST_REQUIRED_PARAMETER_IS_EMPTY("A0410", "Request required parameter is empty"),

    /**
     * Request parameter value exceeds allowed range.
     */
    REQUEST_PARAMETER_VALUE_EXCEEDS_ALLOWED_RANGE("A0420", "Request parameter value exceeds allowed range"),
    /**
     * Parameter format mismatch.
     */
    PARAMETER_FORMAT_MISMATCH("A0421", "Parameter format mismatch"),

    /**
     * User input content is illegal.
     */
    USER_INPUT_CONTENT_ILLEGAL("A0430", "User input content illegal"),
    /**
     * Contains prohibited or sensitive words.
     */
    CONTAINS_PROHIBITED_SENSITIVE_WORDS("A0431", "Contains prohibited sensitive words"),

    /**
     * User operation exception.
     */
    USER_OPERATION_EXCEPTION("A0440", "User operation exception"),

    /**
     * User request service exception (secondary macro code).
     */
    USER_REQUEST_SERVICE_EXCEPTION("A0500", "User request service exception"),
    /**
     * Request limit exceeded.
     */
    REQUEST_LIMIT_EXCEEDED("A0501", "Request limit exceeded"),
    /**
     * Request concurrency limit exceeded.
     */
    REQUEST_CONCURRENCY_LIMIT_EXCEEDED("A0502", "Request concurrency limit exceeded"),
    /**
     * User operation, please wait.
     */
    USER_OPERATION_PLEASE_WAIT("A0503", "User operation, please wait"),
    /**
     * WebSocket connection exception.
     */
    WEBSOCKET_CONNECTION_EXCEPTION("A0504", "WebSocket connection exception"),
    /**
     * WebSocket connection disconnected.
     */
    WEBSOCKET_CONNECTION_DISCONNECTED("A0505", "WebSocket connection disconnected"),
    /**
     * User duplicate request, please try again later.
     */
    USER_DUPLICATE_REQUEST("A0506", "User duplicate request, please try again later"),

    /**
     * User resource exception (secondary macro code).
     */
    USER_RESOURCE_EXCEPTION("A0600", "User resource exception"),
    /**
     * Account balance insufficient.
     */
    ACCOUNT_BALANCE_INSUFFICIENT("A0601", "Account balance insufficient"),
    /**
     * User disk space insufficient.
     */
    USER_DISK_SPACE_INSUFFICIENT("A0602", "User disk space insufficient"),
    /**
     * User memory space insufficient.
     */
    USER_MEMORY_SPACE_INSUFFICIENT("A0603", "User memory space insufficient"),
    /**
     * User OSS capacity insufficient.
     */
    USER_OSS_CAPACITY_INSUFFICIENT("A0604", "User OSS capacity insufficient"),
    /**
     * User quota exhausted.
     */
    USER_QUOTA_EXHAUSTED("A0605", "User quota exhausted"),
    /**
     * User resource not found.
     */
    USER_RESOURCE_NOT_FOUND("A0606", "User resource not found"),

    /**
     * Upload file exception (secondary macro code).
     */
    UPLOAD_FILE_EXCEPTION("A0700", "Upload file exception"),
    /**
     * Upload file type mismatch.
     */
    UPLOAD_FILE_TYPE_MISMATCH("A0701", "Upload file type mismatch"),
    /**
     * Upload file too large.
     */
    UPLOAD_FILE_TOO_LARGE("A0702", "Upload file too large"),
    /**
     * Upload image too large.
     */
    UPLOAD_IMAGE_TOO_LARGE("A0703", "Upload image too large"),
    /**
     * Upload video too large.
     */
    UPLOAD_VIDEO_TOO_LARGE("A0704", "Upload video too large"),
    /**
     * Upload compressed file too large.
     */
    UPLOAD_COMPRESSED_FILE_TOO_LARGE("A0705", "Upload compressed file too large"),

    /**
     * Delete file exception.
     */
    DELETE_FILE_EXCEPTION("A0710", "Delete file exception"),

    /**
     * User current version exception (secondary macro code).
     */
    USER_CURRENT_VERSION_EXCEPTION("A0800", "User current version exception"),
    /**
     * User installed version does not match system.
     */
    USER_INSTALLED_VERSION_NOT_MATCH_SYSTEM("A0801", "User installed version does not match system"),
    /**
     * User installed version is too low.
     */
    USER_INSTALLED_VERSION_TOO_LOW("A0802", "User installed version too low"),
    /**
     * User installed version is too high.
     */
    USER_INSTALLED_VERSION_TOO_HIGH("A0803", "User installed version too high"),
    /**
     * User installed version has expired.
     */
    USER_INSTALLED_VERSION_EXPIRED("A0804", "User installed version expired"),
    /**
     * User API request version does not match.
     */
    USER_API_REQUEST_VERSION_NOT_MATCH("A0805", "User API request version does not match"),
    /**
     * User API request version is too high.
     */
    USER_API_REQUEST_VERSION_TOO_HIGH("A0806", "User API request version too high"),
    /**
     * User API request version is too low.
     */
    USER_API_REQUEST_VERSION_TOO_LOW("A0807", "User API request version too low"),

    /**
     * User privacy not authorized (secondary macro code).
     */
    USER_PRIVACY_NOT_AUTHORIZED("A0900", "User privacy not authorized"),
    /**
     * User privacy not signed.
     */
    USER_PRIVACY_NOT_SIGNED("A0901", "User privacy not signed"),
    /**
     * User camera not authorized.
     */
    USER_CAMERA_NOT_AUTHORIZED("A0903", "User camera not authorized"),
    /**
     * User photo library not authorized.
     */
    USER_PHOTO_LIBRARY_NOT_AUTHORIZED("A0904", "User photo library not authorized"),
    /**
     * User file not authorized.
     */
    USER_FILE_NOT_AUTHORIZED("A0905", "User file not authorized"),
    /**
     * User location information not authorized.
     */
    USER_LOCATION_INFORMATION_NOT_AUTHORIZED("A0906", "User location information not authorized"),
    /**
     * User contacts not authorized.
     */
    USER_CONTACTS_NOT_AUTHORIZED("A0907", "User contacts not authorized"),

    /**
     * User device exception (secondary macro code).
     */
    USER_DEVICE_EXCEPTION("A1000", "User device exception"),
    /**
     * User camera exception.
     */
    USER_CAMERA_EXCEPTION("A1001", "User camera exception"),
    /**
     * User microphone exception.
     */
    USER_MICROPHONE_EXCEPTION("A1002", "User microphone exception"),
    /**
     * User earpiece exception.
     */
    USER_EARPIECE_EXCEPTION("A1003", "User earpiece exception"),
    /**
     * User speaker exception.
     */
    USER_SPEAKER_EXCEPTION("A1004", "User speaker exception"),
    /**
     * User GPS positioning exception.
     */
    USER_GPS_POSITIONING_EXCEPTION("A1005", "User GPS positioning exception"),

    /**
     * System execution error (primary macro code).
     */
    SYSTEM_ERROR("B0001", "System execution error"),

    /**
     * System execution timeout (secondary macro code).
     */
    SYSTEM_EXECUTION_TIMEOUT("B0100", "System execution timeout"),

    /**
     * System disaster recovery function triggered (secondary macro code).
     */
    SYSTEM_DISASTER_RECOVERY_FUNCTION_TRIGGERED("B0200", "System disaster recovery function triggered"),

    /**
     * System rate limiting.
     */
    SYSTEM_RATE_LIMITING("B0210", "System rate limiting"),

    /**
     * System function degradation.
     */
    SYSTEM_FUNCTION_DEGRADATION("B0220", "System function degradation"),

    /**
     * System resource exception (secondary macro code).
     */
    SYSTEM_RESOURCE_EXCEPTION("B0300", "System resource exception"),
    /**
     * System resources exhausted.
     */
    SYSTEM_RESOURCE_EXHAUSTED("B0310", "System resources exhausted"),
    /**
     * System disk space exhausted.
     */
    SYSTEM_DISK_SPACE_EXHAUSTED("B0311", "System disk space exhausted"),
    /**
     * System memory exhausted.
     */
    SYSTEM_MEMORY_EXHAUSTED("B0312", "System memory exhausted"),
    /**
     * File handles exhausted.
     */
    FILE_HANDLE_EXHAUSTED("B0313", "File handles exhausted"),
    /**
     * System connection pool exhausted.
     */
    SYSTEM_CONNECTION_POOL_EXHAUSTED("B0314", "System connection pool exhausted"),
    /**
     * System thread pool exhausted.
     */
    SYSTEM_THREAD_POOL_EXHAUSTED("B0315", "System thread pool exhausted"),

    /**
     * System resource access exception.
     */
    SYSTEM_RESOURCE_ACCESS_EXCEPTION("B0320", "System resource access exception"),
    /**
     * System failed to read disk file.
     */
    SYSTEM_READ_DISK_FILE_FAILED("B0321", "System failed to read disk file"),

    /**
     * Third-party service error (primary macro code).
     */
    THIRD_PARTY_SERVICE_ERROR("C0001", "Third-party service error"),

    /**
     * Middleware service error (secondary macro code).
     */
    MIDDLEWARE_SERVICE_ERROR("C0100", "Middleware service error"),

    /**
     * RPC service error.
     */
    RPC_SERVICE_ERROR("C0110", "RPC service error"),
    /**
     * RPC service not found.
     */
    RPC_SERVICE_NOT_FOUND("C0111", "RPC service not found"),
    /**
     * RPC service not registered.
     */
    RPC_SERVICE_NOT_REGISTERED("C0112", "RPC service not registered"),
    /**
     * Interface does not exist.
     */
    INTERFACE_NOT_EXIST("C0113", "Interface does not exist"),

    /**
     * Message service error.
     */
    MESSAGE_SERVICE_ERROR("C0120", "Message service error"),
    /**
     * Message delivery error.
     */
    MESSAGE_DELIVERY_ERROR("C0121", "Message delivery error"),
    /**
     * Message consumption error.
     */
    MESSAGE_CONSUMPTION_ERROR("C0122", "Message consumption error"),
    /**
     * Message subscription error.
     */
    MESSAGE_SUBSCRIPTION_ERROR("C0123", "Message subscription error"),
    /**
     * Message group not found.
     */
    MESSAGE_GROUP_NOT_FOUND("C0124", "Message group not found"),

    /**
     * Cache service error.
     */
    CACHE_SERVICE_ERROR("C0130", "Cache service error"),
    /**
     * Key length exceeds allowed limit in cache service.
     */
    KEY_LENGTH_EXCEEDS_LIMIT("C0131", "Key length exceeds limit"),
    /**
     * Value length exceeds allowed limit in cache service.
     */
    VALUE_LENGTH_EXCEEDS_LIMIT("C0132", "Value length exceeds limit"),
    /**
     * Storage capacity is full in cache service.
     */
    STORAGE_CAPACITY_FULL("C0133", "Storage capacity full"),
    /**
     * Unsupported data format in cache service.
     */
    UNSUPPORTED_DATA_FORMAT("C0134", "Unsupported data format"),

    /**
     * Configuration service error.
     */
    CONFIGURATION_SERVICE_ERROR("C0140", "Configuration service error"),

    /**
     * Network resource service error.
     */
    NETWORK_RESOURCE_SERVICE_ERROR("C0150", "Network resource service error"),
    /**
     * VPN service error.
     */
    VPN_SERVICE_ERROR("C0151", "VPN service error"),
    /**
     * CDN service error.
     */
    CDN_SERVICE_ERROR("C0152", "CDN service error"),
    /**
     * Domain name resolution service error.
     */
    DOMAIN_NAME_RESOLUTION_SERVICE_ERROR("C0153", "Domain name resolution service error"),
    /**
     * Gateway service error.
     */
    GATEWAY_SERVICE_ERROR("C0154", "Gateway service error"),

    /**
     * Third-party system execution timeout (secondary macro code).
     */
    THIRD_PARTY_SYSTEM_EXECUTION_TIMEOUT("C0200", "Third-party system execution timeout"),

    /**
     * RPC execution timeout.
     */
    RPC_EXECUTION_TIMEOUT("C0210", "RPC execution timeout"),

    /**
     * Message delivery timeout.
     */
    MESSAGE_DELIVERY_TIMEOUT("C0220", "Message delivery timeout"),

    /**
     * Cache service timeout.
     */
    CACHE_SERVICE_TIMEOUT("C0230", "Cache service timeout"),

    /**
     * Configuration service timeout.
     */
    CONFIGURATION_SERVICE_TIMEOUT("C0240", "Configuration service timeout"),

    /**
     * Database service timeout.
     */
    DATABASE_SERVICE_TIMEOUT("C0250", "Database service timeout"),

    /**
     * Database service error (secondary macro code).
     */
    DATABASE_SERVICE_ERROR("C0300", "Database service error"),

    /**
     * Table does not exist in the database.
     */
    TABLE_NOT_EXIST("C0311", "Table does not exist"),
    /**
     * Column does not exist in the database table.
     */
    COLUMN_NOT_EXIST("C0312", "Column does not exist"),

    /**
     * Multiple columns with the same name exist in a multi-table association.
     */
    MULTIPLE_SAME_NAME_COLUMNS_IN_MULTI_TABLE_ASSOCIATION("C0321",
            "Multiple same-name columns in multi-table association"),

    /**
     * Database deadlock detected.
     */
    DATABASE_DEADLOCK("C0331", "Database deadlock"),

    /**
     * Primary key conflict in the database.
     */
    PRIMARY_KEY_CONFLICT("C0341", "Primary key conflict"),

    /**
     * Database access denied (e.g., write operations are disabled in demo
     * environments).
     */
    DATABASE_ACCESS_DENIED("C0351",
            "Database access denied: write operations are disabled in demo environment. Please deploy locally or enable Mock mode for testing."),

    /**
     * Third-party disaster recovery system triggered (secondary macro code).
     */
    THIRD_PARTY_DISASTER_RECOVERY_SYSTEM_TRIGGERED("C0400", "Third-party disaster recovery system triggered"),
    /**
     * Third-party system rate limiting.
     */
    THIRD_PARTY_SYSTEM_RATE_LIMITING("C0401", "Third-party system rate limiting"),
    /**
     * Third-party function degradation.
     */
    THIRD_PARTY_FUNCTION_DEGRADATION("C0402", "Third-party function degradation"),

    /**
     * Notification service error (secondary macro code).
     */
    NOTIFICATION_SERVICE_ERROR("C0500", "Notification service error"),
    /**
     * SMS reminder service failed.
     */
    SMS_REMINDER_SERVICE_FAILED("C0501", "SMS reminder service failed"),
    /**
     * Voice reminder service failed.
     */
    VOICE_REMINDER_SERVICE_FAILED("C0502", "Voice reminder service failed"),
    /**
     * Email reminder service failed.
     */
    EMAIL_REMINDER_SERVICE_FAILED("C0503", "Email reminder service failed");

    /**
     * Returns the response code string.
     *
     * @return the response code
     */
    @Override
    public String getCode() {
        return code;
    }

    /**
     * Returns the descriptive message for the response code.
     *
     * @return the response message
     */
    @Override
    public String getMsg() {
        return msg;
    }

    /**
     * The response code string, e.g., "00000", "A0001".
     */
    private String code;

    /**
     * The descriptive message for the response code.
     */
    private String msg;

    /**
     * Returns a JSON-style string representation of the response code and message.
     *
     * @return a string in JSON format
     */
    @Override
    public String toString() {
        return "{" +
                "\"code\":\"" + code + '\"' +
                ", \"msg\":\"" + msg + '\"' +
                '}';
    }

    /**
     * Returns the corresponding {@code ResultCode} for the given code string.
     * If no match is found, returns {@link #SYSTEM_ERROR} by default.
     *
     * @param code the response code string
     * @return the matching ResultCode, or SYSTEM_ERROR if not found
     */
    public static ResultCode getValue(String code) {
        for (ResultCode value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return SYSTEM_ERROR; // Default to system execution error
    }
}