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
 * 覆盖节点的接口，这些节点具有单独的源代码行，如方法、类和源文件。
 */
public interface ISourceNode extends ICoverageNode {

    /** 放置未知行的支架(无调试信息) */
    int UNKNOWN_LINE = -1;

    /**
     * 第一行覆盖信息的数量。如果不包含任何行，该方法返回-1。
     *
     * @return number of the first line or {@link #UNKNOWN_LINE}
     */
    int getFirstLine();

    /**
     * 最后一行覆盖信息的数量。如果不包含任何行，该方法返回-1。
     *
     * @return number of the last line or {@link #UNKNOWN_LINE}
     */
    int getLastLine();

    /**
     * 返回给定行的行信息。
     *
     * @param nr    感兴趣的行号
     * @return line information
     */
    ILine getLine(int nr);

}
