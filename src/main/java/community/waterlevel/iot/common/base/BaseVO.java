package community.waterlevel.iot.common.base;

import lombok.Data;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

/**
 * Base class for view objects (VO) used in API responses or UI data transfer.
 * Provides a common parent for all VO classes to ensure consistency and serialization support.

 *
 * @author haoxr
 * @since 2022/10/22
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 * 
 */
@Data
@ToString
public class BaseVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
}
