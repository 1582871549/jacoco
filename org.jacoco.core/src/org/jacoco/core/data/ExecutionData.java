/*******************************************************************************
 * Copyright (c) 2009, 2019 Mountainminds GmbH & Co. KG and Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marc R. Hoffmann - initial API and implementation
 *    
 *******************************************************************************/
package org.jacoco.core.data;

import static java.lang.String.format;

import java.util.Arrays;

/**
 * 单个Java类的执行数据。虽然实例是不可变的，但是必须注意#boolean[] #类型的探测数据数组，它可以被修改。
 */
public final class ExecutionData {

    private final long id;

    private final String name;

    private final boolean[] probes;

    /**
     * 使用给定的探测数据创建一个新的{@link ExecutionData}对象。
     *
     * @param id        class identifier  类标识符
     * @param name      VM name           虚拟机名称
     * @param probes    probe data        探针数组
     */
    public ExecutionData(final long id, final String name,
                         final boolean[] probes) {
        this.id = id;
        this.name = name;
        this.probes = probes;
    }

    /**
     * 使用给定的探针数组长度创建一个新的{@link ExecutionData}对象。所有探针都设置为false。
     *
     * @param id            类标识符
     * @param name          虚拟机名称
     * @param probeCount    探针数组长度
     */
    public ExecutionData(final long id, final String name, final int probeCount) {
        this.id = id;
        this.name = name;
        this.probes = new boolean[probeCount];
    }

    /**
     * 返回此类的唯一标识符。标识符是原始类文件定义的CRC64校验和。
     *
     * @return class identifier
     */
    public long getId() {
        return id;
    }

    /**
     * class的虚拟机名称
     *
     * @return VM name
     */
    public String getName() {
        return name;
    }

    /**
     * 返回执行数据探针,  value = true 则表示执行了相应的探测。
     *
     * @return probe data
     */
    public boolean[] getProbes() {
        return probes;
    }

    /**
     * 将所有探针数组设置为 false
     */
    public void reset() {
        Arrays.fill(probes, false);
    }

    /**
     * 检查是否有探针被执行
     *
     * @return 如果被执行则返回 true
     */
    public boolean hasHits() {
        for (final boolean p : probes) {
            if (p) {
                return true;
            }
        }
        return false;
    }

    /**
     * 将给定的执行数据合并到当前对象的探测数据中。
     * 如果执行了该探针或相应的其他探针，则该对象中的探针条目被标记为已执行(true)。
     * 另一个对象的探针阵列不会被修改。
     *
     * @param other     要合并的执行数据
     */
    public void merge(final ExecutionData other) {
        merge(other, true);
    }

    /**
     * 将给定的执行数据合并到该对象的探测数据中。
     * 如果执行了相应的另一个探测。该对象中的探测器设置为 flag 值 .
     * 另一个对象的探针阵列不会被修改。
     *
     * @param other     要合并的执行数据
     * @param flag      合并模式  flag == true, 则叠加两个数组
     *                           flag == false, 则两个数组相减
     */
    public void merge(final ExecutionData other, final boolean flag) {

        /* 校验两个对象是否为同一个class */
        assertCompatibility(other.getId(), other.getName(), other.getProbes().length);

        final boolean[] otherData = other.getProbes();

        for (int i = 0; i < probes.length; i++) {

            /* 如何执行数组中 探针为true, 则将当前对象的数组探针也置为true */
            if (otherData[i]) {
                probes[i] = flag;
            }
        }
    }

    /**
     * 断言此执行数据对象与给定参数兼容。 该检查的目的是检测极不可能的类id冲突。
     *
     * @param id                        其他 classId 必须相同
     * @param name                      其他名称      必须等于此名称
     * @param probeCount                探头数据长度   必须与此数据相同
     * @throws IllegalStateException  如果给定参数与此实例不匹配 则抛出异常
     */
    public void assertCompatibility(final long id, final String name, final int probeCount) throws IllegalStateException {
        if (this.id != id) {
            throw new IllegalStateException(format("Different ids (%016x and %016x).", Long.valueOf(this.id), Long.valueOf(id)));
        }
        if (!this.name.equals(name)) {
            throw new IllegalStateException(format("Different class names %s and %s for id %016x.", this.name, name, Long.valueOf(id)));
        }
        if (this.probes.length != probeCount) {
            throw new IllegalStateException(format("Incompatible execution data for class %s with id %016x.", name, Long.valueOf(id)));
        }
    }

    /**
     * 该toString方法会将 id 转换成 16进制 输出
     *
     * @return String
     */
    @Override
    public String toString() {
        return String.format("ExecutionData[name=%s, id=%016x]", name, Long.valueOf(id));
    }

}
