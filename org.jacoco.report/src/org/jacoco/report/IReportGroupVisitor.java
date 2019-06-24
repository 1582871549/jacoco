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

import org.jacoco.core.analysis.IBundleCoverage;

/**
 * Output-Interface for hierarchical report structures. To allow sequential
 * processing and save memory the group structure has to be traversed in a
 * "deep first" fashion. The interface is implemented by the report formatters
 * and can be used to emit coverage report structures.
 *
 * 输出-分层报告结构的接口。
 * 为了允许顺序处理和节省内存，必须以“深度优先”的方式遍历组结构。
 * 该接口由报告格式化程序实现，可用于发出覆盖报告结构。
 *
 * The following constraints apply in using {@link IReportGroupVisitor} instances:
 * 以下约束适用于使用{@link IReportGroupVisitor}实例
 *
 * <ul>
 * <li>A visitor instance can be used to either submit bundles (
 * {@link #visitBundle(IBundleCoverage, ISourceFileLocator)}) or groups
 * {@link #visitGroup(String)}). Bundles and groups are not allowed for the same
 * visitor.</li>
 * <li>When creating nested groups with {@link #visitGroup(String)} the
 * hierarchy has to be processed in a "deep first" manner.</li>
 * </ul>
 *
 * <ul >
 * <li >
 *      访问者实例可以用来提交包 {@link #visitBundle(IBundleCoverage, ISourceFileLocator)})
 *      或组{@link #visitGroup(String)}). 不允许同一个访问者使用包和组
 * </li >
 * <li >
 *     使用{@link #visitGroup(String)}创建嵌套组时, 必须以“深度优先”的方式处理层次结构。
 * </li >
 * </ul >
 */
public interface IReportGroupVisitor {

    /**
     * Called to add a bundle to the the report.
     * 调用以将包添加到报告中。
     *
     * @param bundle 要包含在报告中的包
     * @param locator 此捆绑包的源定位器
     * @throws IOException in case of IO problems with the report writer
     */
    void visitBundle(IBundleCoverage bundle, ISourceFileLocator locator) throws IOException;

    /**
     * Called to add a new group to the report. The returned
     * {@link IReportGroupVisitor} instance can be used to add nested bundles or
     * groups. The content of the group has to be completed before this or any
     * parent visitor can be used again ("deep first").
     *
     * 调用以将新组添加到报告中。返回的{@link IReportGroupVisitor}实例可用于添加嵌套包或组。
     * 必须先完成群组内容，然后才能再次使用该访问者或任何父访问者(“深度优先”)。
     *
     * @param name 群组名称
     * @return 群组内容的访问者
     * @throws IOException in case of IO problems with the report writer
     */
    IReportGroupVisitor visitGroup(String name) throws IOException;

}
