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
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.JSRInlinerAdapter;

import java.util.Arrays;

/**
 * 该类修复了Java字节代码的两个潜在问题
 *
 * 通过内联自Java 6以来就不推荐使用的子程序来删除JSR/RET指令。
 * RET语句使控制流分析变得复杂，因为跳转目标没有明确给出。
 * 如果代码属性行号和局部变量名指向一些工具创建的无效偏移量，则删除它们。
 * 当用ASM类文件写出这种无效标签时，不再进行验证。
 */
class MethodSanitizer extends JSRInlinerAdapter {

    MethodSanitizer(final MethodVisitor mv, final int access,
                    final String name, final String desc, final String signature, final String[] exceptions) {

        super(InstrSupport.ASM_API_VERSION, mv, access, name, desc, signature, exceptions);
    }

    @Override
    public void visitLocalVariable(final String name, final String desc,
                                   final String signature, final Label start, final Label end,
                                   final int index) {

        // 这里我们依赖于树应用编程接口对信息字段的使用。如果在信息字段包含对标签代码的引用之前标签已经被正确使用，则为空。
        if (start.info != null && end.info != null) {
            super.visitLocalVariable(name, desc, signature, start, end, index);
        }
    }

    @Override
    public void visitLineNumber(final int line, final Label start) {
        // 这里，我们依赖于树应用编程接口对信息字段的使用。如果在信息字段包含对标签代码的引用之前标签已经被正确使用，则为空
        if (start.info != null) {
            super.visitLineNumber(line, start);
        }
    }

    @Override
    public String toString() {
        return "MethodSanitizer super super MethodNode{" +
                "access=" + access +
                ", name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", signature='" + signature + '\'' +
                ", exceptions=" + exceptions +
                ", parameters=" + parameters +
                ", visibleAnnotations=" + visibleAnnotations +
                ", invisibleAnnotations=" + invisibleAnnotations +
                ", visibleTypeAnnotations=" + visibleTypeAnnotations +
                ", invisibleTypeAnnotations=" + invisibleTypeAnnotations +
                ", attrs=" + attrs +
                ", annotationDefault=" + annotationDefault +
                ", visibleAnnotableParameterCount=" + visibleAnnotableParameterCount +
                ", visibleParameterAnnotations=" + Arrays.toString(visibleParameterAnnotations) +
                ", invisibleAnnotableParameterCount=" + invisibleAnnotableParameterCount +
                ", invisibleParameterAnnotations=" + Arrays.toString(invisibleParameterAnnotations) +
                ", instructions=" + instructions +
                ", tryCatchBlocks=" + tryCatchBlocks +
                ", maxStack=" + maxStack +
                ", maxLocals=" + maxLocals +
                ", localVariables=" + localVariables +
                ", visibleLocalVariableAnnotations=" + visibleLocalVariableAnnotations +
                ", invisibleLocalVariableAnnotations=" + invisibleLocalVariableAnnotations +
                "} ";
    }
}
