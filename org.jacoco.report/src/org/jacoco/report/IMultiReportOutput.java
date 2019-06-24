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
import java.io.OutputStream;

/**
 * Interface to emit multiple binary files.
 *
 * 接口来发出多个二进制文件。
 */
public interface IMultiReportOutput {

    /**
     * Creates a file at the given local path. The returned {@link OutputStream}
     * has to be closed before the next document is created.
     *
     * 在给定的本地路径创建文件。在创建下一个文档之前，必须关闭返回的{@link OutputStream}。
     *
     * @param path 新文档的本地路径
     * @return 输出内容
     * @throws IOException 如果创建失败
     */
    OutputStream createFile(String path) throws IOException;

    /**
     * Closes the underlying resource container.
     *
     * 关闭基础资源容器
     *
     * @throws IOException 如果关闭失败
     */
    void close() throws IOException;

}
