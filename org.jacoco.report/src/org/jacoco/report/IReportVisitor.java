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
package org.jacoco.report;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.jacoco.core.data.ExecutionData;
import org.jacoco.core.data.SessionInfo;

/**
 * Interface for all implementations to retrieve structured report data. Unlike
 * nested {@link IReportGroupVisitor} instances the root visitor accepts exactly one
 * bundle or group.
 *
 * 所有实现的接口，用于检索结构化报告数据。
 * 与嵌套的{@link IReportGroupVisitor}实例不同，根访问者只接受一个包或组。
 */
public interface IReportVisitor extends IReportGroupVisitor {

    /**
     * 用全局信息初始化报告。
     * 在调用任何其他方法之前，必须先调用此方法。
     *
     * @param sessionInfos 为该报表收集执行数据的按时间顺序排列的{@link SessionInfo}对象列表。
     * @param executionData 此报告考虑的所有{@link ExecutionData}对象的集合
     * @throws IOException in case of IO problems with the report writer
     */
    void visitInfo(List<SessionInfo> sessionInfos,
                   Collection<ExecutionData> executionData) throws IOException;

    /**
     * 必须在所有报告数据发出后调用。
     *
     * @throws IOException in case of IO problems with the report writer
     */
    void visitEnd() throws IOException;

}
