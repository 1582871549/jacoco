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
package org.jacoco.core.analysis;

/**
 * 具有不同覆盖计数器的分层覆盖数据节点的接口
 */
public interface ICoverageNode {

    /**
     * 由{@link ICoverageNode}实例表示的Java元素的类型。
     */
    enum ElementType {

        /** Method */
        METHOD,

        /** Class */
        CLASS,

        /** Source File */
        SOURCEFILE,

        /** Java 包 */
        PACKAGE,

        /** 捆绑包 */
        BUNDLE,

        /** 包的逻辑组 */
        GROUP,

    }

    /**
     * JaCoCo支持不同的计数器类型
     */
    enum CounterEntity {

        /** 指令计数器 */
        INSTRUCTION,

        /** 分支计数器 */
        BRANCH,

        /** 行计数器 */
        LINE,

        /** 圈复杂度计数器 */
        COMPLEXITY,

        /** 方法计数器 */
        METHOD,

        /** class计数器 */
        CLASS
    }

    /**
     * Returns the type of element represented by this node.
     *
     * @return type of this node
     */
    ElementType getElementType();

    /**
     * Returns the name of this node.
     *
     * @return name of this node
     */
    String getName();

    /**
     * Returns the counter for byte code instructions.
     *
     * @return counter for instructions
     */
    ICounter getInstructionCounter();

    /**
     * Returns the counter for branches.
     *
     * @return counter for branches
     */
    ICounter getBranchCounter();

    /**
     * Returns the counter for lines.
     *
     * @return counter for lines
     */
    ICounter getLineCounter();

    /**
     * Returns the counter for cyclomatic complexity.
     *
     * @return counter for complexity
     */
    ICounter getComplexityCounter();

    /**
     * Returns the counter for methods.
     *
     * @return counter for methods
     */
    ICounter getMethodCounter();

    /**
     * Returns the counter for classes.
     *
     * @return counter for classes
     */
    ICounter getClassCounter();

    /**
     * 对计数器的一般访问
     *
     * @param entity
     *            entity we're we want to have the counter for
     * @return counter for the given entity
     */
    ICounter getCounter(CounterEntity entity);

    /**
     * 检查此节点是否包含与代码覆盖率相关的代码。
     *
     * @return <code>true</code> 如果包含, 则为true
     */
    boolean containsCode();

    /**
     * 创建此节点的纯拷贝。
     * 虽然{@link ICoverageNode}实现可能包含大量数据结构，
     * 但此方法返回的副本仅会减少到计数器。
     * 这有助于在处理大型结构时节省内存。
     *
     * @return copy with counters only
     */
    ICoverageNode getPlainCopy();

}
