package utils.collection;

import exception.SkyeUtilsExceptionFactory;
import exception.SkyeUtilsExceptionType;
import org.apache.commons.lang3.ObjectUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description 由行列确定唯一值的table @Author Skye @Date 2022/11/25 23:35
 */
public class Table<Q, P, K> {

    // 对应存储的实际对象
    private final Map<Q, Map<P, K>> table = new HashMap<>();

    /**
     * 放入值
     *
     * @param row 对应的行
     * @param column 对应的列
     * @param value 对应位置的值
     */
    public void put(Q row, P column, K value) {
        Map<P, K> pkMap = table.get(row);
        if (ObjectUtils.isEmpty(pkMap)) {
            table.put(row, new HashMap<>(6));
        }
        table.get(row).put(column, value);
    }

    /**
     * 获得对应位置的值
     *
     * @param row 对应的行
     * @param column 对应的列
     * @return 对应位置的值
     */
    public K get(Q row, P column) {
        Map<P, K> pkMap = table.get(row);
        if (ObjectUtils.isEmpty(pkMap)) {
            return null;
        } else {
            return pkMap.get(column);
        }
    }

    /**
     * 移除对应位置的值
     *
     * @param row 行
     * @param column 列
     */
    public void remove(Q row, P column) {
        Map<P, K> pkMap = table.get(row);
        if (ObjectUtils.isEmpty(pkMap)) {
            throw SkyeUtilsExceptionFactory.createException(
                    SkyeUtilsExceptionType.TableRowNotExistException);
        } else {
            pkMap.remove(column);
        }
    }

    /**
     * 行是否存在
     *
     * @param row 行
     * @return 是否存在
     */
    public boolean rowIsExist(Q row) {
        return table.containsKey(row);
    }

    /**
     * 列是否存在
     *
     * @param column 列
     * @return 是否存在
     */
    public boolean columnIsExist(P column) {
        for (Map.Entry<Q, Map<P, K>> entry : table.entrySet()) {
            if (entry.getValue().containsKey(column)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获得该行所有数据
     *
     * @param row 行
     * @return 该行所有数据
     */
    public Map<P, K> getRow(Q row) {
        return table.get(row) == null ? new HashMap<>(6) : table.get(row);
    }

    /**
     * 获得该列所有数据
     *
     * @param column 列
     * @return 该列所有数据
     */
    public Map<Q, K> getColumn(P column) {
        Map<Q, K> map = new HashMap<>(6);
        for (Map.Entry<Q, Map<P, K>> entry : table.entrySet()) {
            Q row = entry.getKey();
            Map<P, K> pkMap = entry.getValue();
            if (pkMap.containsKey(column)) {
                map.put(row, pkMap.get(column));
            }
        }
        return map;
    }
}
