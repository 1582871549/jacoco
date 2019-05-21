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

import java.util.Collection;

import org.jacoco.core.internal.analysis.CounterImpl;

/**
 * 覆盖数据节点的基本实现。
 */
public class CoverageNodeImpl implements ICoverageNode {

    private final ElementType elementType;

    private final String name;

    /** 分支计数器. */
    protected CounterImpl branchCounter;

    /** 指令计数器. */
    protected CounterImpl instructionCounter;

    /** 行计数器 */
    protected CounterImpl lineCounter;

    /** 圈计数器. */
    protected CounterImpl complexityCounter;

    /** 方法计数器 */
    protected CounterImpl methodCounter;

    /** 类计数器. */
    protected CounterImpl classCounter;

    /**
     * 创建新的覆盖数据节点, 初始化各项覆盖率指标
     *
     * @param elementType   此实例表示的元素的类型
     * @param name          此节点的名称
     */
    public CoverageNodeImpl(final ElementType elementType, final String name) {
        this.elementType = elementType;
        this.name = name;
        this.branchCounter = CounterImpl.COUNTER_0_0;
        this.instructionCounter = CounterImpl.COUNTER_0_0;
        this.complexityCounter = CounterImpl.COUNTER_0_0;
        this.methodCounter = CounterImpl.COUNTER_0_0;
        this.classCounter = CounterImpl.COUNTER_0_0;
        this.lineCounter = CounterImpl.COUNTER_0_0;
    }

    /**
     * 用另一个元素给定的值递增计数器
     *
     * @param child     要添加的计数器
     */
    public void increment(final ICoverageNode child) {
        instructionCounter = instructionCounter.increment(child.getInstructionCounter());
        branchCounter = branchCounter.increment(child.getBranchCounter());
        lineCounter = lineCounter.increment(child.getLineCounter());
        complexityCounter = complexityCounter.increment(child.getComplexityCounter());
        methodCounter = methodCounter.increment(child.getMethodCounter());
        classCounter = classCounter.increment(child.getClassCounter());
    }

    /**
     * 用元素集合给定的值递增计数器
     *
     * @param children  节点列表，哪些计数器将被添加到该节点
     */
    public void increment(final Collection<? extends ICoverageNode> children) {
        for (final ICoverageNode child : children) {
            increment(child);
        }
    }

    // === ICoverageDataNode ===

    public ElementType getElementType() {
        return elementType;
    }

    public String getName() {
        return name;
    }

    public ICounter getInstructionCounter() {
        return instructionCounter;
    }

    public ICounter getBranchCounter() {
        return branchCounter;
    }

    public ICounter getLineCounter() {
        return lineCounter;
    }

    public ICounter getComplexityCounter() {
        return complexityCounter;
    }

    public ICounter getMethodCounter() {
        return methodCounter;
    }

    public ICounter getClassCounter() {
        return classCounter;
    }

    public ICounter getCounter(final CounterEntity entity) {
        switch (entity) {
            case INSTRUCTION:
                return getInstructionCounter();
            case BRANCH:
                return getBranchCounter();
            case LINE:
                return getLineCounter();
            case COMPLEXITY:
                return getComplexityCounter();
            case METHOD:
                return getMethodCounter();
            case CLASS:
                return getClassCounter();
        }
        throw new AssertionError(entity);
    }

    public boolean containsCode() {
        return getInstructionCounter().getTotalCount() != 0;
    }

    /**
     * 只调用一次, 在最后一个类解析完之后
     * @return
     */
    public ICoverageNode getPlainCopy() {
        final CoverageNodeImpl copy = new CoverageNodeImpl(elementType, name);
        copy.instructionCounter = CounterImpl.getInstance(instructionCounter);
        copy.branchCounter = CounterImpl.getInstance(branchCounter);
        copy.lineCounter = CounterImpl.getInstance(lineCounter);
        copy.complexityCounter = CounterImpl.getInstance(complexityCounter);
        copy.methodCounter = CounterImpl.getInstance(methodCounter);
        copy.classCounter = CounterImpl.getInstance(classCounter);
        return copy;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(name).append(" [").append(elementType).append("]");
        return sb.toString();
    }

}
