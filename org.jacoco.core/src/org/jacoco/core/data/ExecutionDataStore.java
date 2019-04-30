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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 内存中用于执行数据的数据存储。数据可以通过其{@link IExecutionDataVisitor}界面添加。
 * 如果为同一个类多次提供执行数据，则数据被合并，即标记一个探针
 */
public final class ExecutionDataStore implements IExecutionDataVisitor {

    private final Map<Long, ExecutionData> entries = new HashMap<>();

    private final Set<String> names = new HashSet<>();

    /**
     *
     * 将给定的{@link ExecutionData}对象添加到存储中。
     * 如果已经存在具有相同类id的执行数据，
     * 这个结构与给定的结构合并。
     *
     * @param data      要添加或合并的执行数据
     * @throws IllegalStateException 如果给定的{@link ExecutionData}对象与已包含的相应对象不兼容, 则抛出异常
     *
     * @see ExecutionData#assertCompatibility(long, String, int)
     */
    public void put(final ExecutionData data) throws IllegalStateException {
        final Long id = Long.valueOf(data.getId());
        final ExecutionData entry = entries.get(id);

        /* map中没有则添加, 有则进行合并 */
        if (entry == null) {
            entries.put(id, data);
            names.add(data.getName());
        } else {
            entry.merge(data);
        }
    }

    /**
     * 从存储中减去给定{@link ExecutionData}对象中的探针。
     * 也就是说，对于给定数据对象中的所有设置探针，该存储中的相应探针将被取消设置。
     * 如果没有给定数据对象id为的执行数据，此操作将无效。
     *
     * @param data      要减去的执行数据
     * @throws IllegalStateException 如果给定的{@link ExecutionData}对象与已包含的相应对象不兼容
     *
     * @see ExecutionData#assertCompatibility(long, String, int)
     */
    public void subtract(final ExecutionData data) throws IllegalStateException {
        final Long id = Long.valueOf(data.getId());
        final ExecutionData entry = entries.get(id);
        if (entry != null) {
            entry.merge(data, false);
        }
    }

    /**
     * 从该存储中减去给定执行数据存储中的所有探针。
     *
     * @param store     要减去的执行数据存储
     *
     * @see #subtract(ExecutionData)
     */
    public void subtract(final ExecutionDataStore store) {
        for (final ExecutionData data : store.getContents()) {
            subtract(data);
        }
    }

    /**
     * 如果存在于此存储中，则返回具有给定id的{@link ExecutionData}条目。
     *
     * @param id    class id
     *
     * @return execution data or <code>null</code>
     */
    public ExecutionData get(final long id) {
        return entries.get(Long.valueOf(id));
    }

    /**
     * 检查具有给定名称的类的执行数据是否包含在存储中
     *
     * @param name      对应的虚拟机名称
     *
     * @return 如果至少包含一个类，则为true
     */
    public boolean contains(final String name) {
        return names.contains(name);
    }

    /**
     * 返回具有给定标识符的类的覆盖率数据。 如果给定id下没有可用数据，则会创建一个新条目。
     *
     * @param id            类标识符
     * @param name          虚拟机名称
     * @param probeCount    探针数组长度
     *
     * @return execution data
     */
    public ExecutionData get(final Long id, final String name, final int probeCount) {
        ExecutionData entry = entries.get(id);
        if (entry == null) {
            entry = new ExecutionData(id.longValue(), name, probeCount);
            entries.put(id, entry);
            names.add(name);
        } else {
            entry.assertCompatibility(id.longValue(), name, probeCount);
        }
        return entry;
    }

    /**
     * 重置所有执行数据探测器，即将其标记为未执行。
     * 执行数据对象本身不会被删除。
     */
    public void reset() {
        for (final ExecutionData executionData : this.entries.values()) {
            executionData.reset();
        }
    }

    /**
     * 返回表示存储当前内容的集合。
     *
     * @return current contents
     */
    public Collection<ExecutionData> getContents() {
        return new ArrayList<ExecutionData>(entries.values());
    }

    /**
     * 将存储的内容写入给定的访问者界面
     *
     * @param visitor   将内容写入的接口
     */
    public void accept(final IExecutionDataVisitor visitor) {
        for (final ExecutionData data : getContents()) {
            visitor.visitClassExecution(data);
        }
    }

    // === IExecutionDataVisitor 的接口实现 ===

    public void visitClassExecution(final ExecutionData data) {
        put(data);
    }
}
