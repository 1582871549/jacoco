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
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodNode;

import java.util.List;

/**
 * 方法访问者收集类中{@link Label}的流相关信息。
 * 它计算“多目标”和“后继”属性，这些属性随后可以通过{@link LabelInfo}获得。
 */
public final class LabelFlowAnalyzer extends MethodVisitor {

    /**
     * 用控制流信息标记方法的所有标签。
     *
     * @param method = MethodSanitizer的匿名类   标记标签的方法
     */
    public static void markLabels(final MethodNode method) {

        // System.out.println("----------8----------" + "LabelFlowAnalyzer # markLabels");
        // System.out.println("access          " + method.access);
        // System.out.println("name            " + method.name);
        // System.out.println("desc            " + method.desc);
        // System.out.println("signature       " + method.signature);
        // System.out.println("exceptions      " + method.exceptions);
        // System.out.println("parameters      " + method.parameters);
        // System.out.println("tryCatchBlocks  " + method.tryCatchBlocks.size());
        // System.out.println("instructions    " + method.instructions);

        // 我们不使用accept()方法，因为ASM会在每次调用accept()后重置标签
        final MethodVisitor lfa = new LabelFlowAnalyzer();
        for (int i = method.tryCatchBlocks.size(); --i >= 0;) {
            method.tryCatchBlocks.get(i).accept(lfa);
        }
        method.instructions.accept(lfa);
    }

    /**
     * <code>true</code> if the current instruction is a potential successor of
     * the previous instruction. Accessible for testing.
     */
    boolean successor = false;

    /**
     * <code>true</code> for the very first instruction only. Accessible for
     * testing.
     */
    boolean first = true;

    /**
     * Label instance of the last line start.
     */
    Label lineStart = null;

    /**
     * Create new instance.
     */
    public LabelFlowAnalyzer() {
        super(InstrSupport.ASM_API_VERSION);
    }

    @Override
    public void visitTryCatchBlock(final Label start, final Label end,
                                   final Label handler, final String type) {
        // Enforce probe at the beginning of the block. Assuming the start of
        // the block already is successor of some other code, adding a target
        // makes the start a multitarget. However, if the start of the block
        // also is the start of the method, no probe will be added.
        LabelInfo.setTarget(start);

        // Mark exception handler as possible target of the block
        LabelInfo.setTarget(handler);
    }

    @Override
    public void visitJumpInsn(final int opcode, final Label label) {
        LabelInfo.setTarget(label);
        if (opcode == Opcodes.JSR) {
            throw new AssertionError("Subroutines not supported.");
        }
        successor = opcode != Opcodes.GOTO;
        first = false;
    }

    @Override
    public void visitLabel(final Label label) {
        if (first) {
            LabelInfo.setTarget(label);
        }
        if (successor) {
            LabelInfo.setSuccessor(label);
        }
    }

    @Override
    public void visitLineNumber(final int line, final Label start) {
        lineStart = start;
    }

    @Override
    public void visitTableSwitchInsn(final int min, final int max,
                                     final Label dflt, final Label... labels) {
        visitSwitchInsn(dflt, labels);
    }

    @Override
    public void visitLookupSwitchInsn(final Label dflt, final int[] keys,
                                      final Label[] labels) {
        visitSwitchInsn(dflt, labels);
    }

    private void visitSwitchInsn(final Label dflt, final Label[] labels) {
        LabelInfo.resetDone(dflt);
        LabelInfo.resetDone(labels);
        setTargetIfNotDone(dflt);
        for (final Label l : labels) {
            setTargetIfNotDone(l);
        }
        successor = false;
        first = false;
    }

    private static void setTargetIfNotDone(final Label label) {
        if (!LabelInfo.isDone(label)) {
            LabelInfo.setTarget(label);
            LabelInfo.setDone(label);
        }
    }

    @Override
    public void visitInsn(final int opcode) {
        switch (opcode) {
            case Opcodes.RET:
                throw new AssertionError("Subroutines not supported.");
            case Opcodes.IRETURN:
            case Opcodes.LRETURN:
            case Opcodes.FRETURN:
            case Opcodes.DRETURN:
            case Opcodes.ARETURN:
            case Opcodes.RETURN:
            case Opcodes.ATHROW:
                successor = false;
                break;
            default:
                successor = true;
                break;
        }
        first = false;
    }

    @Override
    public void visitIntInsn(final int opcode, final int operand) {
        successor = true;
        first = false;
    }

    @Override
    public void visitVarInsn(final int opcode, final int var) {
        successor = true;
        first = false;
    }

    @Override
    public void visitTypeInsn(final int opcode, final String type) {
        successor = true;
        first = false;
    }

    @Override
    public void visitFieldInsn(final int opcode, final String owner,
                               final String name, final String desc) {
        successor = true;
        first = false;
    }

    @Override
    public void visitMethodInsn(final int opcode, final String owner,
                                final String name, final String desc, final boolean itf) {
        successor = true;
        first = false;
        markMethodInvocationLine();
    }

    @Override
    public void visitInvokeDynamicInsn(final String name, final String desc,
                                       final Handle bsm, final Object... bsmArgs) {
        successor = true;
        first = false;
        markMethodInvocationLine();
    }

    private void markMethodInvocationLine() {
        if (lineStart != null) {
            LabelInfo.setMethodInvocationLine(lineStart);
        }
    }

    @Override
    public void visitLdcInsn(final Object cst) {
        successor = true;
        first = false;
    }

    @Override
    public void visitIincInsn(final int var, final int increment) {
        successor = true;
        first = false;
    }

    @Override
    public void visitMultiANewArrayInsn(final String desc, final int dims) {
        successor = true;
        first = false;
    }

}
