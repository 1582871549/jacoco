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
package org.jacoco.report.internal.html.page;

import java.io.IOException;

import org.jacoco.core.analysis.IBundleCoverage;
import org.jacoco.core.analysis.ICoverageNode;
import org.jacoco.core.analysis.IPackageCoverage;
import org.jacoco.report.ISourceFileLocator;
import org.jacoco.report.internal.ReportOutputFolder;
import org.jacoco.report.internal.html.HTMLElement;
import org.jacoco.report.internal.html.IHTMLReportContext;

/**
 * 显示捆绑包覆盖信息的页面。该页面包含一个包含包的所有包的表格。
 */
public class BundlePage extends TablePage<ICoverageNode> {

    private final ISourceFileLocator locator;

    private IBundleCoverage bundle;

    /**
     * 在给定上下文中创建新访问者
     *
     * @param bundle    捆绑包的覆盖日期
     * @param parent    可选分层父级
     * @param locator   源定位器
     * @param folder    此捆绑包的基本文件夹
     * @param context   设置上下文
     */
    public BundlePage(final IBundleCoverage bundle, final ReportPage parent,
                      final ISourceFileLocator locator, final ReportOutputFolder folder,
                      final IHTMLReportContext context) {
        super(bundle.getPlainCopy(), parent, folder, context);
        this.bundle = bundle;
        this.locator = locator;
    }

    @Override
    public void render() throws IOException {
        renderPackages();
        super.render();
        // 不要将捆绑结构保存在内存中
        bundle = null;
    }

    private void renderPackages() throws IOException {
        for (final IPackageCoverage p : bundle.getPackages()) {
            if (!p.containsCode()) {
                continue;
            }
            final String packagename = p.getName();
            final String foldername = packagename.length() == 0 ? "default" : packagename.replace('/', '.');
            final PackagePage page = new PackagePage(p, this, locator, folder.subFolder(foldername), context);
            page.render();
            addItem(page);
        }
    }

    @Override
    protected String getOnload() {
        return "initialSort(['breadcrumb', 'coveragetable'])";
    }

    @Override
    protected String getFileName() {
        return "index.html";
    }

    @Override
    protected void content(HTMLElement body) throws IOException {
        if (bundle.getPackages().isEmpty()) {
            body.p().text("No class files specified.");
        } else if (!bundle.containsCode()) {
            body.p().text("None of the analyzed classes contain code relevant for code coverage.");
        } else {
            super.content(body);
        }
    }

}
