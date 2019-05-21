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
package org.jacoco.report.internal;

import java.io.IOException;

import org.jacoco.core.analysis.CoverageNodeImpl;
import org.jacoco.core.analysis.IBundleCoverage;
import org.jacoco.core.analysis.ICoverageNode.ElementType;
import org.jacoco.report.IReportGroupVisitor;
import org.jacoco.report.ISourceFileLocator;

/**
 * 内部基本访问者计算分层报告的组计数器摘要
 */
public abstract class AbstractGroupVisitor implements IReportGroupVisitor {

    /** 此组的覆盖节点到总计数器 */
    protected final CoverageNodeImpl total;

    private AbstractGroupVisitor lastChild;

    /**
     * 用给定的名称创建新组。
     *
     * @param name  内部创建的覆盖节点的名称
     */
    protected AbstractGroupVisitor(final String name) {
        total = new CoverageNodeImpl(ElementType.GROUP, name);
    }

    public final void visitBundle(final IBundleCoverage bundle, final ISourceFileLocator locator) throws IOException {
        finalizeLastChild();
        total.increment(bundle);
        handleBundle(bundle, locator);
    }

    /**
     * 调用以特定方式处理给定的包。
     *
     * @param bundle        分析包
     * @param locator       源定位器
     * @throws IOException  如果报告不能写
     */
    protected abstract void handleBundle(IBundleCoverage bundle, ISourceFileLocator locator) throws IOException;


    public final IReportGroupVisitor visitGroup(final String name)
            throws IOException {
        finalizeLastChild();
        lastChild = handleGroup(name);
        return lastChild;
    }

    /**
     * Called to handle a group with the given name in a specific way.
     *
     * @param name
     *            name of the group
     * @return created child group
     * @throws IOException
     *             if the report can't be written
     */
    protected abstract AbstractGroupVisitor handleGroup(final String name)
            throws IOException;

    /**
     * Must be called at the end of every group.
     *
     * @throws IOException
     *             if the report can't be written
     */
    public final void visitEnd() throws IOException {
        finalizeLastChild();
        handleEnd();
    }

    /**
     * Called to handle the end of this group in a specific way.
     *
     * @throws IOException
     *             if the report can't be written
     */
    protected abstract void handleEnd() throws IOException;

    private void finalizeLastChild() throws IOException {
        if (lastChild != null) {
            lastChild.visitEnd();
            total.increment(lastChild.total);
            lastChild = null;
        }
    }

}
