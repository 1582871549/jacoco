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
package org.jacoco.core.internal.analysis.filter;

import java.util.Set;

import org.objectweb.asm.tree.AbstractInsnNode;

/**
 * 过滤器用来标记过滤项目的接口
 */
public interface IFilterOutput {

    /**
     * 标记在计算覆盖率时应该忽略的指令序列
     *
     * @param fromInclusive 应忽略的第一条指令，包括
     * @param toInclusive   应忽略的位于“代码”之后的最后一条指令，包括
     */
    void ignore(AbstractInsnNode fromInclusive, AbstractInsnNode toInclusive);

    /**
     * 标记在计算覆盖率期间应该合并的两条指令
     *
     * @param i1    第一条指令
     * @param i2    第二条指令
     */
    void merge(AbstractInsnNode i1, AbstractInsnNode i2);

    /**
     * 标记在计算覆盖率时应该替换其输出分支的指令
     *
     * @param source        指示哪些分支应该被替换
     * @param newTargets    分行的新目标
     */
    void replaceBranches(AbstractInsnNode source, Set<AbstractInsnNode> newTargets);

}
