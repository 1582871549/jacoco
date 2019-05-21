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

import java.util.*;

import org.jacoco.core.analysis.IClassCoverage;
import org.jacoco.core.analysis.ICoveredMethodRecord;
import org.jacoco.core.analysis.IMethodCoverage;

/**
 * Implementation of {@link IClassCoverage} and {@link IClassCoverage}.
 */
public class ClassCoverageImpl extends SourceNodeImpl implements IClassCoverage, ICoveredMethodRecord {

    private final long id;
    private final boolean noMatch;
    private final Collection<IMethodCoverage> methods;
    private String signature;
    private String superName;
    private String[] interfaces;
    private String sourceFileName;

    /**
     * 当前类名
     */
    private String className;
    /**
     * 记录覆盖到的方法
     */
    private Map<String, String> coveredMethods;

    /**
     * 使用给定参数创建类覆盖数据对象。
     *
     * @param name      类的虚拟机名称
     * @param id        类id
     * @param noMatch   noMatch == true, 类id与执行数据不匹配
     */
    public ClassCoverageImpl(final String name, final long id, final boolean noMatch) {
        super(ElementType.CLASS, name);
        this.className = name;
        this.id = id;
        this.noMatch = noMatch;
        this.methods = new ArrayList<>();
        this.coveredMethods = new HashMap<>();
    }

    /**
     * 向此类添加方法。
     *
     * @param method    要添加的方法数据
     */
    public void addMethod(final IMethodCoverage method) {

        // System.out.println("----------12---------" + "ClassCoverageImpl # addMethod");
        // MethodCoverageImpl{desc='(Ljava/lang/String;)Ljava/util/List;', signature='(Ljava/lang/String;)Ljava/util/List<Ljava/lang/String;>;'}
        // SourceNodeImpl{lines=[LineImpl{instructions=Counter[2/0], branches=Counter[2/0]}, LineImpl{instructions=Counter[4/0], branches=Counter[0/0]}, LineImpl{instructions=Counter[2/0], branches=Counter[0/0]}, null, null, LineImpl{instructions=Counter[3/0], branches=Counter[2/0]}, LineImpl{instructions=Counter[4/0], branches=Counter[0/0]}, LineImpl{instructions=Counter[2/0], branches=Counter[0/0]}, null, null, LineImpl{instructions=Counter[4/0], branches=Counter[0/0]}, null, LineImpl{instructions=Counter[2/0], branches=Counter[0/0]}], offset=47}
        // listUser [METHOD]
        // ------------------------------------------------------------------------
        // MethodCoverageImpl{desc='()Ljava/lang/String;', signature='null'}
        // SourceNodeImpl{lines=[LineImpl{instructions=Counter[14/0], branches=Counter[0/0]}, LineImpl{instructions=Counter[2/0], branches=Counter[0/0]}], offset=34}
        // index [METHOD]
        // ------------------------------------------------------------------------
        // MethodCoverageImpl{desc='()Lcom/dudu/common/configuration/bean/MyProperties;', signature='null'}
        // SourceNodeImpl{lines=[LineImpl{instructions=Counter[0/6], branches=Counter[0/0]}], offset=49}
        // myProperties [METHOD]

        this.methods.add(method);
        increment(method);
        // 当至少包含一个方法时，类被视为已包含:
        if (methodCounter.getCoveredCount() > 0) {
            this.classCounter = CounterImpl.COUNTER_0_1;
        } else {
            this.classCounter = CounterImpl.COUNTER_1_0;
        }
    }

    /**
     * 设置类的虚拟机签名。
     *
     * @param signature 类别的虚拟机签名可能为null
     */
    public void setSignature(final String signature) {
        this.signature = signature;
    }

    /**
     * 设置超类的虚拟机名称。
     *
     * @param superName 超级类的虚拟机名称可以是null
     */
    public void setSuperName(final String superName) {
        this.superName = superName;
    }

    /**
     * 设置已实现/扩展接口的虚拟机名称。
     *
     * @param interfaces    已实现/扩展接口的虚拟机名称
     */
    public void setInterfaces(final String[] interfaces) {
        this.interfaces = interfaces;
    }

    /**
     * 为此类设置相应源文件的名称。
     *
     * @param sourceFileName    源文件的名称
     */
    public void setSourceFileName(final String sourceFileName) {
        this.sourceFileName = sourceFileName;
    }

    // === IClassCoverage implementation ===

    public long getId() {
        return id;
    }

    public boolean isNoMatch() {
        return noMatch;
    }

    public String getSignature() {
        return signature;
    }

    public String getSuperName() {
        return superName;
    }

    public String[] getInterfaceNames() {
        return interfaces;
    }

    public String getPackageName() {
        final int pos = getName().lastIndexOf('/');
        return pos == -1 ? "" : getName().substring(0, pos);
    }

    public String getSourceFileName() {
        return sourceFileName;
    }

    public Collection<IMethodCoverage> getMethods() {
        return methods;
    }

    // === ICoveredMethodRecord implementation ===

    @Override
    public Map<String, String> getCoveredMethods() {
        return coveredMethods;
    }

    @Override
    public String getClassName() {
        return className;
    }

    // === toString ===

    @Override
    public String toString() {
        return "ClassCoverageImpl{" +
                "id=" + id +
                ", noMatch=" + noMatch +
                ", signature='" + signature + '\'' +
                ", superName='" + superName + '\'' +
                ", className='" + className + '\'' +
                ", coveredMethods='" + coveredMethods + '\'' +
                ", interfaces=" + Arrays.toString(interfaces) +
                ", sourceFileName='" + sourceFileName + '\'' +
                ", branchCounter=" + branchCounter +
                ", instructionCounter=" + instructionCounter +
                ", lineCounter=" + lineCounter +
                ", complexityCounter=" + complexityCounter +
                ", methodCounter=" + methodCounter +
                ", classCounter=" + classCounter +
                "} ";
    }
}
