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
package org.jacoco.core.internal.analysis;

import org.jacoco.core.analysis.ICounter;
import org.jacoco.core.analysis.IMethodCoverage;

import java.util.Map;

/**
 * Implementation of {@link IMethodCoverage}.单一方法的覆盖数据。此节点的名称是本地方法名称。
 */
public class MethodCoverageImpl extends SourceNodeImpl implements IMethodCoverage {

    private final String desc;

    private final String signature;

    private final String methodName;

    /**
     * @auther dujianwei
     *
     * 是否记录覆盖方法, <code>true</code>
     * 记录覆盖方法到 {@link ClassCoverageImpl} 的 coveredMethods 属性中
     */
    private boolean flag;

    /**
     * @auther dujianwei
     *
     * 引用 {@link ClassCoverageImpl} 的 coveredMethods 属性地址到本类
     * 便于操作覆盖方法
     */
    private Map<String, String> coveredMethods;

    /**
     * 使用给定参数创建方法覆盖数据对象。
     *
     * @param name      方法的名称
     * @param desc      方法描述符
     * @param signature 方法签名
     */
    public MethodCoverageImpl(final String name, final String desc, final String signature) {
        super(ElementType.METHOD, name);
        this.methodName = name;
        this.desc = desc;
        this.signature = signature;
    }

    /**
     * 使用给定参数创建方法覆盖数据对象。
     *
     * @param name      方法的名称
     * @param desc      方法描述符
     * @param signature 方法签名
     */
    public MethodCoverageImpl(final String name, final String desc, final String signature, final ClassCoverageImpl coverage) {
        super(ElementType.METHOD, name);
        this.methodName = name;
        this.desc = desc;
        this.signature = signature;

        this.flag = true;
        this.coveredMethods = coverage.getCoveredMethods();
    }

    @Override
    public void increment(final ICounter instructions, final ICounter branches, final int line) {
        super.increment(instructions, branches, line);
        // 额外增加复杂性计数器
        if (branches.getTotalCount() > 1) {
            final int c = Math.max(0, branches.getCoveredCount() - 1);
            final int m = Math.max(0, branches.getTotalCount() - c - 1);
            this.complexityCounter = this.complexityCounter.increment(m, c);
        }
    }

    /**
     * 此方法必须在此方法覆盖节点的所有指令和分支递增后准确调用一次。
     */
    public void incrementMethodCounter() {

        // System.out.println("----------10.9-------" + "MethodCoverageImpl # incrementMethodCounter");
        // System.out.println(this.instructionCounter.getCoveredCount());
        // System.out.println(this.branchCounter);
        // System.out.println(this.instructionCounter);
        // System.out.println(this.lineCounter);
        // System.out.println(this.complexityCounter);
        // System.out.println(this.methodCounter);
        // System.out.println(this.classCounter);
        // System.out.println("=========================================");
        /*
         * 将指令计数器运算转换为方法计数器
         * 指令计数器为0时 说明该方法未被覆盖
         *
         * 1、因为每个方法中的每一行代码都对应着一个探针元素
         * 2、所以当单个方法中的指令计数等于0时, 说明该方法未被执行, 即未被覆盖
         */
        ICounter base;

        if (this.instructionCounter.getCoveredCount() == 0) {
            base = CounterImpl.COUNTER_1_0;
        } else {
            base = CounterImpl.COUNTER_0_1;

            // 通过flag来判断是否记录覆盖方法
            if (flag && coveredMethods != null) {

                // init = 构造函数, clinit = 静态方法块
                // if (!"<init>".equals(methodName) && !"<clinit>".equals(methodName)) {
                    // System.out.println(methodName);
                    // System.out.println(coverage.getClassName());
                    // coveredMethods.put(methodName, null);
                // }
                coveredMethods.put(methodName, null);
            }
        }
        // System.out.println(base);
        // System.out.println(flag);

        // System.out.println(base);
        // System.out.println(desc);
        // System.out.println(name);
        if (base == null) {
            throw new RuntimeException("覆盖率计数器 base 未正确赋值");
        }

        this.methodCounter = this.methodCounter.increment(base);
        this.complexityCounter = this.complexityCounter.increment(base);
    }

    // === IMethodCoverage implementation ===

    public String getDesc() {
        return desc;
    }

    public String getSignature() {
        return signature;
    }

    @Override
    public String toString() {
        return "MethodCoverageImpl{" +
                "desc='" + desc + '\'' +
                ", signature='" + signature + '\'' +
                ", methodName='" + methodName + '\'' +
                ", branchCounter=" + branchCounter +
                ", instructionCounter=" + instructionCounter +
                ", lineCounter=" + lineCounter +
                ", complexityCounter=" + complexityCounter +
                ", methodCounter=" + methodCounter +
                ", classCounter=" + classCounter +
                "} ";
    }
}
