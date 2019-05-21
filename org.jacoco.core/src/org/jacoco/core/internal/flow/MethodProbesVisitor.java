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
import org.objectweb.asm.tree.MethodNode;

/**
 * 一个{@link MethodVisitor}，带有获取探针插入信息的附加方法。
 */
public abstract class MethodProbesVisitor extends MethodVisitor {

    /**
     * 没有委托访问者的新访问者实例。
     */
    public MethodProbesVisitor() {
        this(null);
    }

    /**
     * 委托给给定访问者的新访问者实例。
     *
     * @param mv 链中可选的下一个访问者
     */
    public MethodProbesVisitor(final MethodVisitor mv) {
        super(InstrSupport.ASM_API_VERSION, mv);
    }

    /**
     * 访问应该插入当前位置的无条件探针。
     *
     * @param probeId 要插入的探针的id
     */
    @SuppressWarnings("unused")
    public void visitProbe(final int probeId) {
    }

    /**
     * 访问跳转指令。
     * 具有给定id的探针应该以只有在跳转到给定标签时才执行的方式插入。
     *
     * @param opcode
     *            the opcode of the type instruction to be visited. This opcode
     *            is either IFEQ, IFNE, IFLT, IFGE, IFGT, IFLE, IF_ICMPEQ,
     *            IF_ICMPNE, IF_ICMPLT, IF_ICMPGE, IF_ICMPGT, IF_ICMPLE,
     *            IF_ACMPEQ, IF_ACMPNE, GOTO, IFNULL or IFNONNULL.
     * @param label
     *            the operand of the instruction to be visited. This operand is
     *            a label that designates the instruction to which the jump
     *            instruction may jump.
     * @param probeId
     *            id of the probe
     * @param frame
     *            stackmap frame status after the execution of the jump
     *            instruction. The instance is only valid with the call of this
     *            method.
     * @see MethodVisitor#visitJumpInsn(int, Label)
     */
    @SuppressWarnings("unused")
    public void visitJumpInsnWithProbe(final int opcode, final Label label,
                                       final int probeId, final IFrame frame) {
    }

    /**
     * Visits a zero operand instruction with a probe. This event is used only
     * for instructions that terminate the method. Therefore the probe must be
     * inserted before the actual instruction.
     *
     * @param opcode
     *            the opcode of the instruction to be visited. This opcode is
     *            either IRETURN, LRETURN, FRETURN, DRETURN, ARETURN, RETURN or
     *            ATHROW.
     * @param probeId
     *            id of the probe
     * @see MethodVisitor#visitInsn(int)
     */
    @SuppressWarnings("unused")
    public void visitInsnWithProbe(final int opcode, final int probeId) {
    }

    /**
     * Visits a TABLESWITCH instruction with optional probes for each target
     * label. Implementations can be optimized based on the fact that the same
     * target labels will always have the same probe id within a call to this
     * method. The probe id for each label can be obtained with
     * {@link LabelInfo#getProbeId(Label)}.
     *
     * @param min
     *            the minimum key value.
     * @param max
     *            the maximum key value.
     * @param dflt
     *            beginning of the default handler block.
     * @param labels
     *            beginnings of the handler blocks. <code>labels[i]</code> is
     *            the beginning of the handler block for the
     *            <code>min + i</code> key.
     * @param frame
     *            stackmap frame status after the execution of the switch
     *            instruction. The instance is only valid with the call of this
     *            method.
     * @see MethodVisitor#visitTableSwitchInsn(int, int, Label, Label[])
     */
    @SuppressWarnings("unused")
    public void visitTableSwitchInsnWithProbes(final int min, final int max,
                                               final Label dflt, final Label[] labels, final IFrame frame) {
    }

    /**
     * Visits a LOOKUPSWITCH instruction with optional probes for each target
     * label. Implementations can be optimized based on the fact that the same
     * target labels will always have the same probe id within a call to this
     * method. The probe id for each label can be obtained with
     * {@link LabelInfo#getProbeId(Label)}.
     *
     * @param dflt
     *            beginning of the default handler block.
     * @param keys
     *            the values of the keys.
     * @param labels
     *            beginnings of the handler blocks. <code>labels[i]</code> is
     *            the beginning of the handler block for the
     *            <code>keys[i]</code> key.
     * @param frame
     *            stackmap frame status after the execution of the switch
     *            instruction. The instance is only valid with the call of this
     *            method.
     * @see MethodVisitor#visitLookupSwitchInsn(Label, int[], Label[])
     */
    @SuppressWarnings("unused")
    public void visitLookupSwitchInsnWithProbes(final Label dflt,
                                                final int[] keys, final Label[] labels, final IFrame frame) {
    }

    /**
     * 此方法可以被覆盖，以挂钩到将此方法的指令作为 visitX()  事件发出的过程中。
     *
     * @param methodNode    要发出的内容
     * @param methodVisitor 请注意，这不一定是这个访问者实例，而是一些计算探针的包装器。
     */
    public void accept(final MethodNode methodNode, final MethodVisitor methodVisitor) {
        methodNode.accept(methodVisitor);
    }

}
