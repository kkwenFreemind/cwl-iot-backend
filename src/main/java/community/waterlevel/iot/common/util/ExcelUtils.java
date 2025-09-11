package community.waterlevel.iot.common.util;

import cn.idev.excel.EasyExcel;
import cn.idev.excel.event.AnalysisEventListener;

import java.io.InputStream;

/**
 * Utility class for Excel import operations.
 * Provides methods to read Excel files and process data using EasyExcel and
 * custom event listeners.
 *
 * @author haoxr
 * @since 2023/03/01
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
public class ExcelUtils {

    /**
     * Imports data from an Excel file input stream and processes each row using the
     * provided listener.
     * <p>
     * Utilizes EasyExcel to read the specified sheet and map rows to the given
     * class type.
     *
     * @param is       the input stream of the Excel file
     * @param clazz    the class type to map each row to
     * @param listener the event listener to handle row analysis and processing
     * @param <T>      the type of the data model
     */
    public static <T> void importExcel(InputStream is, Class clazz, AnalysisEventListener<T> listener) {
        EasyExcel.read(is, clazz, listener).sheet().doRead();
    }
}
