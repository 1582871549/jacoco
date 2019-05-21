/**
 * FileName: TestDemo
 * Author:   大橙子
 * Date:     2019/5/5 9:52
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package org.jacoco.dudu;

import java.util.*;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 * @author 大橙子
 * @create 2019/5/5
 * @since 1.0.0
 */
public class TestDemo {

    public static void main(String[] args) {

        // String s = "";
        // byte  b1[] = {0x02};
        // byte  b2[] = {0x01};
        // String str1 = new String(b1);
        // String str2 = new String(b2);
        //
        //
        // System.out.println(str1 + "\n"+ str2);


        Map<String, String> methodNames = Collections.EMPTY_MAP;

        if (methodNames == null || methodNames.isEmpty() || methodNames.containsKey("aaa")) {
            System.out.println("1");
        } else {
            System.out.println("2");
        }
    }
}
