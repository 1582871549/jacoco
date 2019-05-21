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
package org.jacoco.core.internal.instr;

import org.jacoco.core.internal.flow.ClassProbesVisitor;
import org.jacoco.core.internal.flow.MethodProbesVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

/**
 * 为覆盖跟踪检测一个类的适配器。
 */
public class ClassInstrumenter extends ClassProbesVisitor {

    private final IProbeArrayStrategy probeArrayStrategy;

    private String className;

    /**
     * 该方法未被执行
     *
     * 向给定的类访问者发出此类的检测版本
     *
     * @param probeArrayStrategy    该策略将用于访问探针阵列
     * @param cv                    访问者链中的下一个委托将接收检测类
     */
    public ClassInstrumenter(final IProbeArrayStrategy probeArrayStrategy,
                             final ClassVisitor cv) {
        super(cv);
        this.probeArrayStrategy = probeArrayStrategy;
    }

    /**
     * 该方法未被执行
     */
    @Override
    public void visit(final int version, final int access, final String name,
                      final String signature, final String superName,
                      final String[] interfaces) {
        this.className = name;
        super.visit(version, access, name, signature, superName, interfaces);
    }

    /**
     * 该方法未被执行
     */
    @Override
    public FieldVisitor visitField(final int access, final String name,
                                   final String desc, final String signature, final Object value) {
        return super.visitField(access, name, desc, signature, value);
    }

    /**
     * 该方法未被执行
     *
     * 当访问一个方法时，我们需要一个{@link MethodProbesVisitor }来处理该方法的探测。
     */
    @Override
    public MethodProbesVisitor visitMethod(final int access, final String name,
                                           final String desc, final String signature, final String[] exceptions) {

        InstrSupport.assertNotInstrumented(name, className);

        final MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);

        if (mv == null) {
            return null;
        }
        final MethodVisitor frameEliminator = new DuplicateFrameEliminator(mv);
        final ProbeInserter probeVariableInserter = new ProbeInserter(access, name, desc, frameEliminator, probeArrayStrategy);
        return new MethodInstrumenter(probeVariableInserter, probeVariableInserter);
    }

    /**
     * 该方法未被执行
     *
     * 报告遇到的探测器总数。
     * 对于类，此方法在{@link ClassVisitor#visitEnd()}之前调用。
     * 对于接口，在发出第一个方法(静态初始值设定项)之前调用此方法。
     *
     * @param count 探针总数
     */
    @Override
    public void visitTotalProbeCount(final int count) {
        probeArrayStrategy.addMembers(cv, count);
    }

}
