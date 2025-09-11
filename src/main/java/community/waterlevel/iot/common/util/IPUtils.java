package community.waterlevel.iot.common.util;

import cn.hutool.core.util.StrUtil;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.xdb.Searcher;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Utility class for IP address operations.
 * <p>
 * Provides methods to obtain the client's real IP address and its corresponding
 * geographic location.
 * <ul>
 * <li>Handles scenarios with reverse proxies (e.g., Nginx), where
 * {@code request.getRemoteAddr()} may not return the real client IP.</li>
 * <li>If multiple reverse proxies are used, the {@code X-Forwarded-For} header
 * may contain a list of IPs; the first non-unknown valid IP is considered the
 * real client IP.</li>
 * <li>Supports IP-to-region lookup using the ip2region database.</li>
 * </ul>
 * 
 * <b>Note:</b> The ip2region database is primarily designed for IP-to-region
 * mapping within China.
 * It provides detailed region information for Chinese IP addresses (province,
 * city, ISP, etc.),
 * but for IPs outside China, the data is often less detailed or may only
 * indicate "foreign" or "overseas."
 * If you need accurate global IP geolocation, consider integrating a more
 * comprehensive international IP database or service.
 *
 * @author Ray
 * @since 2.10.0
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Slf4j
@Component
public class IPUtils {

    private static final String DB_PATH = "/data/ip2region.xdb";
    private static Searcher searcher;

    /**
     * Initializes the ip2region Searcher instance from the resource database file.
     * <p>
     * Loads the ip2region.xdb file from the classpath, copies it to a temporary
     * file, and initializes the Searcher for IP-to-region lookups.
     * Logs an error if initialization fails.
     */
    @PostConstruct
    public void init() {
        try {
            InputStream inputStream = getClass().getResourceAsStream(DB_PATH);
            if (inputStream == null) {
                throw new FileNotFoundException("Resource not found: " + DB_PATH);
            }

            Path tempDbPath = Files.createTempFile("ip2region", ".xdb");
            Files.copy(inputStream, tempDbPath, StandardCopyOption.REPLACE_EXISTING);

            searcher = Searcher.newWithFileOnly(tempDbPath.toString());
        } catch (Exception e) {
            log.error("IpRegionUtil initialization ERROR, {}", e.getMessage());
        }
    }

    /**
     * Retrieves the real client IP address from the given HTTP request.
     * <p>
     * Handles multiple reverse proxy headers to extract the first valid,
     * non-unknown IP address.
     * If the request is from localhost, attempts to resolve the actual local IP
     * address.
     *
     * @param request the {@link HttpServletRequest} object
     * @return the client's real IP address, or an empty string if unavailable
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ip = null;
        try {
            if (request == null) {
                return "";
            }
            ip = request.getHeader("x-forwarded-for");
            if (checkIp(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (checkIp(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (checkIp(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
            }
            if (checkIp(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }
            if (checkIp(ip)) {
                ip = request.getRemoteAddr();
                if ("127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)) {
                    // 根据网卡取本机配置的IP
                    ip = getLocalAddr();
                }
            }
        } catch (Exception e) {
            log.error("IPUtils ERROR, {}", e.getMessage());
        }

        // 使用代理，则获取第一个IP地址
        if (StrUtil.isNotBlank(ip) && ip.indexOf(",") > 0) {
            ip = ip.substring(0, ip.indexOf(","));
        }

        return ip;
    }

    private static boolean checkIp(String ip) {
        String unknown = "unknown";
        return StrUtil.isEmpty(ip) || unknown.equalsIgnoreCase(ip);
    }

    /**
     * Retrieves the local machine's IP address.
     *
     * @return the local IP address, or {@code null} if it cannot be determined
     */
    private static String getLocalAddr() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.error("InetAddress.getLocalHost()-error, {}", e.getMessage());
        }
        return null;
    }

    /**
     * Gets the geographic region information for the specified IP address using the
     * ip2region database.
     *
     * @param ip the IP address to look up
     * @return the geographic region information, or {@code null} if not found or on
     *         error
     */
    public static String getRegion(String ip) {
        if (searcher == null) {
            log.error("Searcher is not initialized");
            return null;
        }

        try {
            return searcher.search(ip);
        } catch (Exception e) {
            log.error("IpRegionUtil ERROR, {}", e.getMessage());
            return null;
        }
    }
}
