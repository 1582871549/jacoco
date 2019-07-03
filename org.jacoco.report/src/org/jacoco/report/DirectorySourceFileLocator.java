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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Locator for source files that picks source files from a given directory in
 * the file system.
 *
 * 源文件定位器，从文件系统中的给定目录中选择源文件。
 */
public class DirectorySourceFileLocator extends InputStreamSourceFileLocator {

    private final File directory;

    /**
     * Creates a new locator that searches for source files in the given directory.
     * 创建在给定目录中搜索源文件的新定位器。
     *
     * @param directory 搜索源文件的目录
     * @param encoding 源文件的编码，平台默认编码为 <code>null</code>
     * @param tabWidth 源文件中以空格数表示的制表符宽度
     */
    public DirectorySourceFileLocator(final File directory,
                                      final String encoding, final int tabWidth) {
        super(encoding, tabWidth);
        this.directory = directory;
    }

    @Override
    protected InputStream getSourceStream(final String path) throws IOException {
        final File file = new File(directory, path);
        if (file.exists()) {
            return new FileInputStream(file);
        } else {
            return null;
        }
    }

}
