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
package org.jacoco.core.internal.flow;

import org.jacoco.core.internal.instr.InstrSupport;
import org.objectweb.asm.ClassVisitor;

/**
 * 一个{@link ClassVisitor}，带有获取每个方法的探针插入信息的附加方法
 */
public abstract class ClassProbesVisitor extends ClassVisitor {

    /**
     * 没有委托访问者的新访问者实例。
     */
    public ClassProbesVisitor() {
        this(null);
    }

    /**
     * 委托给给定访问者的新访问者实例。
     *
     * @param cv 链中可选的下一个访问者
     */
    public ClassProbesVisitor(final ClassVisitor cv) {
        super(InstrSupport.ASM_API_VERSION, cv);
    }

    /**
     * 当访问一个方法时，我们需要一个{@link MethodProbesVisitor }来处理该方法的探测。
     */
    @Override
    public abstract MethodProbesVisitor visitMethod(
            int access, String name, String desc, String signature, String[] exceptions);

    /**
     * 报告遇到的探测器总数。
     * 对于类，此方法在{@link ClassVisitor#visitEnd()}之前调用。
     * 对于接口，在发出第一个方法(静态初始值设定项)之前调用此方法。
     *
     * @param count 探针总数
     */
    public abstract void visitTotalProbeCount(int count);

    @Override
    public String toString() {
        return "ClassVisitor{" +
                "api=" + api +
                ", cv=" + cv +
                "} ";
    }
}
