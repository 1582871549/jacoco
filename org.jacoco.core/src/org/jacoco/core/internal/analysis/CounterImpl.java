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

/**
 * {@link ICounter}实现。实现工厂模式允许共享计数器实例。
 */
public abstract class CounterImpl implements ICounter {

    /**为其创建单例的最大计数器值*/
    private static final int SINGLETON_LIMIT = 30;

    private static final CounterImpl[][] SINGLETONS = new CounterImpl[SINGLETON_LIMIT + 1][];

    static {
        for (int i = 0; i <= SINGLETON_LIMIT; i++) {
            SINGLETONS[i] = new CounterImpl[SINGLETON_LIMIT + 1];
            for (int j = 0; j <= SINGLETON_LIMIT; j++) {
                SINGLETONS[i][j] = new Fix(i, j);
            }
        }
    }

    /** 计数器的常数为0/0. */
    public static final CounterImpl COUNTER_0_0 = SINGLETONS[0][0];

    /** 计数器的常数为1/0. */
    public static final CounterImpl COUNTER_1_0 = SINGLETONS[1][0];

    /** 计数器的常数为0/1. */
    public static final CounterImpl COUNTER_0_1 = SINGLETONS[0][1];

    /**
     * 计数器的可变版本, 后续每次都调用该方法
     */
    private static class Var extends CounterImpl {

        public Var(final int missed, final int covered) {
            super(missed, covered);
        }

        @Override
        public CounterImpl increment(final int missed, final int covered) {
            this.missed += missed;
            this.covered += covered;
            return this;
        }
    }

    /**
     * 计数器的不可变版本, 只在static方法中调用
     */
    private static class Fix extends CounterImpl {
        public Fix(final int missed, final int covered) {
            super(missed, covered);
        }

        @Override
        public CounterImpl increment(final int missed, final int covered) {
            return getInstance(this.missed + missed, this.covered + covered);
        }
    }

    /**
     * 方法来检索具有给定项数的计数器
     *
     * @param missed    遗漏项目数
     * @param covered   涵盖项目数
     * @return          计数器实例
     */
    public static CounterImpl getInstance(final int missed, final int covered) {
        if (missed <= SINGLETON_LIMIT && covered <= SINGLETON_LIMIT) {
            return SINGLETONS[missed][covered];
        } else {
            return new Var(missed, covered);
        }
    }

    /**
     * 方法检索给定计数器的克隆。
     *
     * @param counter   复制计数器
     * @return counter instance
     */
    public static CounterImpl getInstance(final ICounter counter) {
        return getInstance(counter.getMissedCount(), counter.getCoveredCount());
    }

    /** 错过的 items */
    protected int missed;

    /** 覆盖到的 items */
    protected int covered;

    /**
     * 用给定的数字创建一个新实例。
     *
     * @param missed    遗漏项目数
     * @param covered   涵盖项目数
     *
     */
    protected CounterImpl(final int missed, final int covered) {
        this.missed = missed;
        this.covered = covered;
    }

    /**
     * 返回一个计数器，其值按给定计数器的数字递增。
     * 是否修改该计数器实例或返回一个新实例取决于实现
     *
     * @param counter   附加项目总数和覆盖项目数
     *
     * @return counter instance with incremented values
     */
    public CounterImpl increment(final ICounter counter) {
        // System.out.println("----------14---------" + "CounterImpl # increment");
        // System.out.println("missedCount    " + counter.getMissedCount());
        // System.out.println("coveredCount   " + counter.getCoveredCount());
        return increment(counter.getMissedCount(), counter.getCoveredCount());
    }

    /**
     * 返回一个计数器，其值按给定的数字递增。是否修改该计数器实例或返回一个新实例取决于实现。
     *
     * @param missed    错过的项目数
     * @param covered   涵盖项目的数量
     * @return          具有递增值的计数器实例
     */
    public abstract CounterImpl increment(int missed, int covered);

    // === ICounter implementation ===

    public double getValue(final CounterValue value) {
        switch (value) {
            case TOTALCOUNT:
                return getTotalCount();
            case MISSEDCOUNT:
                return getMissedCount();
            case COVEREDCOUNT:
                return getCoveredCount();
            case MISSEDRATIO:
                return getMissedRatio();
            case COVEREDRATIO:
                return getCoveredRatio();
            default:
                throw new AssertionError(value);
        }
    }

    public int getTotalCount() {
        return missed + covered;
    }

    public int getCoveredCount() {
        return covered;
    }

    public int getMissedCount() {
        return missed;
    }

    public double getCoveredRatio() {
        return (double) covered / (missed + covered);
    }

    public double getMissedRatio() {
        return (double) missed / (missed + covered);
    }

    public int getStatus() {
        int status = covered > 0 ? FULLY_COVERED : EMPTY;
        if (missed > 0) {
            status |= NOT_COVERED;
        }
        return status;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof ICounter) {
            final ICounter that = (ICounter) obj;
            return this.missed == that.getMissedCount() && this.covered == that.getCoveredCount();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return missed ^ covered * 17;
    }

    @Override
    public String toString() {
        final StringBuilder b = new StringBuilder("Counter["); //$NON-NLS-1$
        b.append(getMissedCount());
        b.append('/').append(getCoveredCount());
        b.append(']');
        return b.toString();
    }

}
