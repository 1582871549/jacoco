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
import org.jacoco.core.analysis.ILine;

/**
 * Implementation of {@link ILine}.
 */
public abstract class LineImpl implements ILine {

    /** 为其创建单例的最大指令计数器值 */
    private static final int SINGLETON_INS_LIMIT = 8;

    /** 为其创建单例的最大分支计数器值 */
    private static final int SINGLETON_BRA_LIMIT = 4;

    private static final LineImpl[][][][] SINGLETONS = new LineImpl[SINGLETON_INS_LIMIT + 1][][][];

    static {
        for (int i = 0; i <= SINGLETON_INS_LIMIT; i++) {
            SINGLETONS[i] = new LineImpl[SINGLETON_INS_LIMIT + 1][][];
            for (int j = 0; j <= SINGLETON_INS_LIMIT; j++) {
                SINGLETONS[i][j] = new LineImpl[SINGLETON_BRA_LIMIT + 1][];
                for (int k = 0; k <= SINGLETON_BRA_LIMIT; k++) {
                    SINGLETONS[i][j][k] = new LineImpl[SINGLETON_BRA_LIMIT + 1];
                    for (int l = 0; l <= SINGLETON_BRA_LIMIT; l++) {
                        SINGLETONS[i][j][k][l] = new Fix(i, j, k, l);
                    }
                }
            }
        }
    }

    /**
     * 没有说明或分支的空行。
     */
    public static final LineImpl EMPTY = SINGLETONS[0][0][0][0];

    private static LineImpl getInstance(final CounterImpl instructions,
                                        final CounterImpl branches) {
        final int im = instructions.getMissedCount();
        final int ic = instructions.getCoveredCount();
        final int bm = branches.getMissedCount();
        final int bc = branches.getCoveredCount();
        if (im <= SINGLETON_INS_LIMIT && ic <= SINGLETON_INS_LIMIT
                && bm <= SINGLETON_BRA_LIMIT && bc <= SINGLETON_BRA_LIMIT) {
            return SINGLETONS[im][ic][bm][bc];
        }
        return new Var(instructions, branches);
    }

    /**
     * 可变版本
     */
    private static final class Var extends LineImpl {
        Var(final CounterImpl instructions, final CounterImpl branches) {
            super(instructions, branches);
        }

        @Override
        public LineImpl increment(final ICounter instructions,
                                  final ICounter branches) {
            this.instructions = this.instructions.increment(instructions);
            this.branches = this.branches.increment(branches);
            return this;
        }
    }

    /**
     * 不可变版本
     */
    private static final class Fix extends LineImpl {
        public Fix(final int im, final int ic, final int bm, final int bc) {
            super(CounterImpl.getInstance(im, ic), CounterImpl.getInstance(bm,
                    bc));
        }

        @Override
        public LineImpl increment(final ICounter instructions,
                                  final ICounter branches) {
            return getInstance(this.instructions.increment(instructions),
                    this.branches.increment(branches));
        }
    }

    /** 指令计数器 */
    protected CounterImpl instructions;

    /** 分支计数器 */
    protected CounterImpl branches;

    private LineImpl(final CounterImpl instructions, final CounterImpl branches) {
        this.instructions = instructions;
        this.branches = branches;
    }

    /**
     * 将给定计数器添加到该行
     *
     * @param instructions  添加说明
     * @param branches      要添加的分支
     * @return instance with new counter values
     */
    public abstract LineImpl increment(final ICounter instructions, final ICounter branches);

    // === ILine implementation ===

    public int getStatus() {
        return instructions.getStatus() | branches.getStatus();
    }

    public ICounter getInstructionCounter() {
        return instructions;
    }

    public ICounter getBranchCounter() {
        return branches;
    }

    @Override
    public int hashCode() {
        return 23 * instructions.hashCode() ^ branches.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof ILine) {
            final ILine that = (ILine) obj;
            return this.instructions.equals(that.getInstructionCounter())
                    && this.branches.equals(that.getBranchCounter());
        }
        return false;
    }

    @Override
    public String toString() {
        return "LineImpl{" +
                "instructions=" + instructions +
                ", branches=" + branches +
                '}';
    }
}
