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

import org.jacoco.core.analysis.ISourceNode;
import org.jacoco.core.internal.flow.LabelInfo;
import org.objectweb.asm.Label;
import org.objectweb.asm.tree.AbstractInsnNode;

/**
 * 方法指令( Instruction )的状态生成器
 * 一个方法的所有指令必须按照它们的原始顺序加上附加信息，如行号。
 * 之后，可以使用getInstructions方法获得说明
 */
class InstructionsBuilder {

    /** 分析方法所属类别的探针阵列 */
    private final boolean[] probes;

    /** 属于随后添加的说明的行 */
    private int currentLine;

    /** 最后添加的指令 */
    private Instruction currentInsn;

    /** 方法的所有指令从ASM节点映射到相应的指令实例 */
    private final Map<AbstractInsnNode, Instruction> instructions;

    /** 标记后续说明的标签。 由于ASM问题#315745，每个指令可以有多个标签 */
    private final List<Label> currentLabel;

    /** 控制流中所有跳转的列表。 我们需要暂时存储，因为目标指令可能还不知道 */
    private final List<Jump> jumps;

    /**
     * 创建一个可用于分析单个方法的新构建器实例
     *
     * @param probes 用于确定每条指令覆盖状态的相应类的探测数组
     */
    InstructionsBuilder(final boolean[] probes) {

        // System.out.println("----------6.5--------" + "InstructionsBuilder # init( probes )");
        this.probes = probes;
        this.currentLine = ISourceNode.UNKNOWN_LINE; // 初始值为 -1
        this.currentInsn = null;
        this.instructions = new HashMap<AbstractInsnNode, Instruction>();
        this.currentLabel = new ArrayList<Label>(2);
        this.jumps = new ArrayList<Jump>();
    }

    /**
     * 设置当前 source 行。
     * 所有随后添加的指令都将分配给该行。
     * 如果没有设置行(例如，对于没有调试信息编译的类) {@link ISourceNode#UNKNOWN_LINE}被分配给指令
     */
    void setCurrentLine(final int line) {
        currentLine = line;
    }

    /**
     * 添加适用于随后添加的指令的标签。
     * 由于ASM内部结构，可以向指令中添加多个{@link Label}。
     */
    void addLabel(final Label label) {
        currentLabel.add(label);
        if (!LabelInfo.isSuccessor(label)) {
            noSuccessor();
        }
    }

    /**
     * 添加新指令。
     * 除非另有说明，否则默认情况下指令与前一条指令链接。
     */
    void addInstruction(final AbstractInsnNode node) {
        final Instruction insn = new Instruction(currentLine);
        final int labelCount = currentLabel.size();
        if (labelCount > 0) {
            for (int i = labelCount; --i >= 0;) {
                LabelInfo.setInstruction(currentLabel.get(i), insn);
            }
            currentLabel.clear();
        }
        if (currentInsn != null) {
            currentInsn.addBranch(insn, 0);
        }
        currentInsn = insn;
        instructions.put(node, insn);
    }

    /**
     * 声明下一条指令不会是当前指令的后继指令。
     * 这是无条件跳跃的情况，或者在技术上是探针插入之前的情况。
     */
    void noSuccessor() {
        currentInsn = null;
    }

    /**
     * 从最后添加的指令添加一个跳转。
     *
     * @param target 跳跃目标
     * @param branch 唯一的分支号码
     */
    void addJump(final Label target, final int branch) {
        jumps.add(new Jump(currentInsn, target, branch));
    }

    /**
     * 为最后一条指令添加新探针。
     *
     * @param probeId   探针阵列中的索引
     * @param branch    最后一条指令的唯一分支号
     */
    void addProbe(final int probeId, final int branch) {
        final boolean executed = probes != null && probes[probeId];
        currentInsn.addBranch(executed, branch);
    }

    /**
     * 返回此方法所有指令的状态。
     * 添加指令后，必须准确调用此方法一次。
     *
     * @return 将ASM指令节点映射到相应的{@link Instruction}实例
     */
    Map<AbstractInsnNode, Instruction> getInstructions() {
        // 跳线:
        for (final Jump j : jumps) {
            j.wire();
        }

        return instructions;
    }

    private static class Jump {

        private final Instruction source;
        private final Label target;
        private final int branch;

        Jump(final Instruction source, final Label target, final int branch) {
            this.source = source;
            this.target = target;
            this.branch = branch;
        }

        void wire() {
            source.addBranch(LabelInfo.getInstruction(target), branch);
        }

    }

    /**
     * 输出打印参数
     * @auther dudu
     * @return
     */
    @Override
    public String toString() {
        return "InstructionsBuilder{" +
                "probes=" + Arrays.toString(probes) +
                ", currentLine=" + currentLine +
                ", currentInsn=" + currentInsn +
                ", instructions=" + instructions +
                ", currentLabel=" + currentLabel +
                ", jumps=" + jumps +
                '}';
    }
}
