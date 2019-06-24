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
package org.jacoco.report.internal.html;

import java.util.Locale;

import org.jacoco.report.ILanguageNames;
import org.jacoco.report.internal.html.index.IIndexUpdate;
import org.jacoco.report.internal.html.resources.Resources;
import org.jacoco.report.internal.html.table.Table;

/**
 * Context and configuration information during creation of a HTML report.
 * 创建超文本标记语言报告期间的上下文和配置信息。
 */
public interface IHTMLReportContext {

    /**
     * Returns the static resources used in this report.
     * 返回此报告中使用的静态资源。
     *
     * @return static resources
     */
    Resources getResources();

    /**
     * Returns the language names call-back used in this report.
     * 返回此报告中使用的语言名称。
     *
     * @return language names
     */
    ILanguageNames getLanguageNames();

    /**
     * Returns a table for rendering coverage nodes.
     * 返回用于呈现覆盖节点的表。
     *
     * @return table for rendering
     */
    Table getTable();

    /**
     * Returns a string of textual information to include in every page footer.
     * 返回要包含在每个页脚中的文本信息字符串。
     *
     * @return footer text or empty string
     */
    String getFooterText();

    /**
     * Returns the link to the sessions page.
     * 返回会话页面的链接。
     *
     * @return sessions page link
     */
    ILinkable getSessionsPage();

    /**
     * Returns the encoding of the generated HTML documents.
     * 返回生成的HTML文档的编码
     *
     * @return encoding for generated HTML documents
     */
    String getOutputEncoding();

    /**
     * Returns the service for index updates.
     * 返回索引更新服务。
     *
     * @return sevice for indes updates
     */
    IIndexUpdate getIndexUpdate();

    /**
     * Returns the locale used to format numbers and dates.
     * 返回用于格式化数字和日期的区域设置。
     *
     * @return locale for numbers and dates
     */
    Locale getLocale();

}
