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
package org.jacoco.core.analysis;

/**
 * 单个源行的指令和分支覆盖由该接口描述。
 */
public interface ILine {

    /**
     * 返回该行的指令计数器
     *
     * @return instruction counter
     */
    ICounter getInstructionCounter();

    /**
     * 返回该行的分支计数器
     *
     * @return branches counter
     */
    ICounter getBranchCounter();

    /**
     * 返回从构造计数器和分支计数器计算的该行的覆盖状态
     *
     * @see ICounter#EMPTY              空的
     * @see ICounter#NOT_COVERED        未覆盖
     * @see ICounter#PARTLY_COVERED     部分覆盖
     * @see ICounter#FULLY_COVERED      完全覆盖
     *
     * @return status of this line
     */
    int getStatus();

}
