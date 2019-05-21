package org.jacoco.core.analysis;

import java.util.Map;

/**
 * jacoco 用例关联所需的接口
 *
 * @author dujianwei
 */
public interface ICoveredMethodRecord {

    /**
     * 获取当前类中所有覆盖到的方法名
     *
     * @return 返回一个map
     */
    Map<String, String> getCoveredMethods();

    /**
     * 获取当前类名
     *
     * @return 返回一个当前类名
     */
    String getClassName();
}
