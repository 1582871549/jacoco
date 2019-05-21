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

import org.jacoco.core.analysis.CoverageNodeImpl;
import org.jacoco.core.analysis.ICounter;
import org.jacoco.core.analysis.ILine;
import org.jacoco.core.analysis.ISourceNode;

import java.util.Arrays;

/**
 * Implementation of {@link ISourceNode}.
 */
public class SourceNodeImpl extends CoverageNodeImpl implements ISourceNode {

    private LineImpl[] lines;

    /** {@link #lines}中的第一行数字  */
    private int offset;

    /**
     * 创建新的源节点实现实例。
     *
     * @param elementType   元素类型
     * @param name          元素的名称
     */
    public SourceNodeImpl(final ElementType elementType, final String name) {
        super(elementType, name);
        lines = null;
        offset = UNKNOWN_LINE;
    }

    /**
     * 确保内部缓冲区能够从第一行到最后一行。
     * 虽然缓冲器也自动增加，
     * 这种方法允许在预先知道总范围的情况下进行优化。
     *
     * {@link ISourceNode#UNKNOWN_LINE} = -1
     *
     * @param first     第一行 or {@link ISourceNode#UNKNOWN_LINE}
     * @param last      最后一行 or {@link ISourceNode#UNKNOWN_LINE}
     */
    public void ensureCapacity(final int first, final int last) {
        if (first == UNKNOWN_LINE || last == UNKNOWN_LINE) {
            return;
        }
        if (lines == null) {
            offset = first;
            lines = new LineImpl[last - first + 1];
        } else {
            final int newFirst = Math.min(getFirstLine(), first);
            final int newLast = Math.max(getLastLine(), last);
            final int newLength = newLast - newFirst + 1;
            if (newLength > lines.length) {
                final LineImpl[] newLines = new LineImpl[newLength];
                System.arraycopy(lines, 0, newLines, offset - newFirst, lines.length);
                offset = newFirst;
                lines = newLines;
            }
        }
    }

    /**
     * 用给定子代的值递增所有计数器。
     * 递增行计数器时，假设子级引用相同的源文件
     *
     * ClassCoverageImpl # addMethod # method = child = MethodCoverageImpl
     *
     * @param child     要添加的子节点
     */
    public void increment(final ISourceNode child) {

        // System.out.println("----------13---------" + "SourceNodeImpl # increment");
        // System.out.println("instructionCounter  " + child.getInstructionCounter());
        // System.out.println("branchCounter       " + child.getBranchCounter());
        // System.out.println("complexityCounter   " + child.getComplexityCounter());
        // System.out.println("methodCounter       " + child.getMethodCounter());
        // System.out.println("classCounter        " + child.getClassCounter());
        // System.out.println("firstLine           " + child.getFirstLine());

        // 以下属性为 CoverageNodeImpl 字段, child = MethodCoverageImpl
        instructionCounter = instructionCounter.increment(child.getInstructionCounter());
        branchCounter = branchCounter.increment(child.getBranchCounter());
        complexityCounter = complexityCounter.increment(child.getComplexityCounter());
        methodCounter = methodCounter.increment(child.getMethodCounter());
        classCounter = classCounter.increment(child.getClassCounter());
        final int firstLine = child.getFirstLine();
        if (firstLine != UNKNOWN_LINE) {
            final int lastLine = child.getLastLine();
            ensureCapacity(firstLine, lastLine);
            for (int i = firstLine; i <= lastLine; i++) {
                final ILine line = child.getLine(i);
                incrementLine(line.getInstructionCounter(), line.getBranchCounter(), i);
            }
        }
    }

    /**
     * 将指令和分支增加给定的计数器值。
     * 如果指定了可选行号，则指令和分支将被添加到给定行。
     * 行计数器相应地递增。
     *
     * @param instructions
     *            instructions to add
     * @param branches
     *            branches to add
     * @param line
     *            optional line number or {@link ISourceNode#UNKNOWN_LINE}
     */
    public void increment(final ICounter instructions, final ICounter branches,
                          final int line) {
        if (line != UNKNOWN_LINE) {
            incrementLine(instructions, branches, line);
        }
        instructionCounter = instructionCounter.increment(instructions);
        branchCounter = branchCounter.increment(branches);
    }

    private void incrementLine(final ICounter instructions,
                               final ICounter branches, final int line) {
        ensureCapacity(line, line);
        final LineImpl l = getLine(line);
        final int oldTotal = l.getInstructionCounter().getTotalCount();
        final int oldCovered = l.getInstructionCounter().getCoveredCount();
        lines[line - offset] = l.increment(instructions, branches);

        // Increment line counter:
        if (instructions.getTotalCount() > 0) {
            if (instructions.getCoveredCount() == 0) {
                if (oldTotal == 0) {
                    lineCounter = lineCounter
                            .increment(CounterImpl.COUNTER_1_0);
                }
            } else {
                if (oldTotal == 0) {
                    lineCounter = lineCounter
                            .increment(CounterImpl.COUNTER_0_1);
                } else {
                    if (oldCovered == 0) {
                        lineCounter = lineCounter.increment(-1, +1);
                    }
                }
            }
        }
    }

    // === ISourceNode implementation ===

    public int getFirstLine() {
        return offset;
    }

    public int getLastLine() {
        return lines == null ? UNKNOWN_LINE : (offset + lines.length - 1);
    }

    public LineImpl getLine(final int nr) {
        if (lines == null || nr < getFirstLine() || nr > getLastLine()) {
            return LineImpl.EMPTY;
        }
        final LineImpl line = lines[nr - offset];
        return line == null ? LineImpl.EMPTY : line;
    }

    @Override
    public String toString() {
        return "SourceNodeImpl{" +
                "lines=" + Arrays.toString(lines) +
                ", offset=" + offset +
                "} ";
    }
}
