/*******************************************************************************
 * Copyright (c) 2009, 2019 Mountainminds GmbH & Co. KG and Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Evgeny Mandrikov - initial API and implementation
 *    
 *******************************************************************************/
package org.jacoco.core.internal.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jacoco.core.analysis.ISourceFileCoverage;
import org.jacoco.core.analysis.ISourceNode;
import org.jacoco.core.internal.analysis.filter.IFilterOutput;
import org.objectweb.asm.tree.AbstractInsnNode;

/**
 * 计算单个方法的过滤覆盖率。
 * 在计算覆盖率结果之前，此类的实例可以首先用作{@link IFilterOutput}。
 */
class MethodCoverageCalculator implements IFilterOutput {

    // 说明
    private final Map<AbstractInsnNode, Instruction> instructions;
    // 忽略
    private final Set<AbstractInsnNode> ignored;

    /**
     * 应该合并成不相交集合的指令。来自一组指令的覆盖信息将被合并到该组的代表性指令中。
     * 每个这样的集合被表示为一个单独的链表:
     * 除了一个元素之外的每个元素引用同一集合中的另一个元素，
     * 没有引用的元素是这个集合的代表。
     * 该映射存储集合(键)元素的引用(值)。
     */
    private final Map<AbstractInsnNode, AbstractInsnNode> merged;
    // 替换
    private final Map<AbstractInsnNode, Set<AbstractInsnNode>> replacements;

    MethodCoverageCalculator(final Map<AbstractInsnNode, Instruction> instructions) {
        this.instructions = instructions;
        this.ignored = new HashSet<>();
        this.merged = new HashMap<>();
        this.replacements = new HashMap<>();
    }

    /**
     * 应用所有指定的过滤命令并计算结果覆盖率。
     *
     * @param coverage      结果被添加到这个覆盖节点
     */
    void calculate(final MethodCoverageImpl coverage) {

        // System.out.println("----------10.5-------" + "MethodCoverageCalculator # calculate");
        // System.out.println(coverage);
        // System.out.println(instructions);

        applyMerges();
        applyReplacements();
        ensureCapacity(coverage);

        for (final Entry<AbstractInsnNode, Instruction> entry : instructions.entrySet()) {
            if (!ignored.contains(entry.getKey())) {
                final Instruction instruction = entry.getValue();
                coverage.increment(instruction.getInstructionCounter(),
                        instruction.getBranchCounter(), instruction.getLine());
            }
        }

        // System.out.println("----------10.8-------" + "MethodCoverageCalculator # calculate");
        coverage.incrementMethodCounter();
    }

    private void applyMerges() {
        // 合并到 representative:
        for (final Entry<AbstractInsnNode, AbstractInsnNode> entry : merged.entrySet()) {
            final AbstractInsnNode node = entry.getKey();
            final Instruction instruction = instructions.get(node);
            final AbstractInsnNode representativeNode = findRepresentative(node);
            ignored.add(node);
            instructions.put(representativeNode, instructions.get(representativeNode).merge(instruction));
            entry.setValue(representativeNode);
        }

        // 从 representative 处获取合并值
        for (final Entry<AbstractInsnNode, AbstractInsnNode> entry : merged.entrySet()) {
            instructions.put(entry.getKey(), instructions.get(entry.getValue()));
        }
    }

    private void applyReplacements() {
        for (final Entry<AbstractInsnNode, Set<AbstractInsnNode>> entry : replacements.entrySet()) {
            final Set<AbstractInsnNode> replacements = entry.getValue();
            final List<Instruction> newBranches = new ArrayList<Instruction>(replacements.size());
            for (final AbstractInsnNode b : replacements) {
                newBranches.add(instructions.get(b));
            }
            final AbstractInsnNode node = entry.getKey();
            instructions.put(node, instructions.get(node).replaceBranches(newBranches));
        }
    }

    /**
     * 保证容量
     * @param coverage
     */
    private void ensureCapacity(final MethodCoverageImpl coverage) {
        // 确定线路范围
        int firstLine = ISourceFileCoverage.UNKNOWN_LINE;
        int lastLine = ISourceFileCoverage.UNKNOWN_LINE;
        for (final Entry<AbstractInsnNode, Instruction> entry : instructions.entrySet()) {

            if (!ignored.contains(entry.getKey())) {
                final int line = entry.getValue().getLine();

                if (line != ISourceNode.UNKNOWN_LINE) {
                    if (firstLine > line || lastLine == ISourceNode.UNKNOWN_LINE) {
                        firstLine = line;
                    }
                    if (lastLine < line) {
                        lastLine = line;
                    }
                }
            }
        }

        // 性能优化以避免线阵列的增量增加:
        coverage.ensureCapacity(firstLine, lastLine);
    }

    private AbstractInsnNode findRepresentative(AbstractInsnNode i) {
        AbstractInsnNode r;
        while ((r = merged.get(i)) != null) {
            i = r;
        }
        return i;
    }

    // === IFilterOutput API ===

    public void ignore(final AbstractInsnNode fromInclusive, final AbstractInsnNode toInclusive) {

        for (AbstractInsnNode i = fromInclusive; i != toInclusive; i = i.getNext()) {
            ignored.add(i);
        }
        ignored.add(toInclusive);
    }

    public void merge(AbstractInsnNode i1, AbstractInsnNode i2) {
        i1 = findRepresentative(i1);
        i2 = findRepresentative(i2);
        if (i1 != i2) {
            merged.put(i2, i1);
        }
    }

    public void replaceBranches(final AbstractInsnNode source, final Set<AbstractInsnNode> newTargets) {
        replacements.put(source, newTargets);
    }

}
