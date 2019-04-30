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
package org.jacoco.core.tools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.jacoco.core.data.ExecutionDataReader;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.data.ExecutionDataWriter;
import org.jacoco.core.data.SessionInfoStore;

/**
 * 装载Exec数据, 为了方便使用
 * 将 *.exec文件放入{@link ExecutionDataStore}和{@link SessionInfoStore}中。
 */
public class ExecFileLoader {

    private final SessionInfoStore sessionInfos;
    private final ExecutionDataStore executionData;

    /**
     * 新实例，用于组合来自多个文件的会话信息和执行数据。
     */
    public ExecFileLoader() {
        sessionInfos = new SessionInfoStore();
        executionData = new ExecutionDataStore();
    }

    /**
     * 从给定的输入流中读取所有数据。 并装载到{@link ExecutionDataReader}类中。
     *
     * @param stream        要从中读取数据的流
     * @throws IOException  in case of problems while reading from the stream
     */
    public void load(final InputStream stream) throws IOException {
        final BufferedInputStream buffer = new BufferedInputStream(stream);
        final ExecutionDataReader reader = new ExecutionDataReader(buffer);

        reader.setExecutionDataVisitor(executionData);
        reader.setSessionInfoVisitor(sessionInfos);
        reader.read();
    }

    /**
     * 从给定的输入流中读取所有数据。
     *
     * @param file 要从中读取数据的文件
     * @throws IOException  in case of problems while reading from the stream
     */
    public void load(final File file) throws IOException {
        final InputStream stream = new FileInputStream(file);
        try {
            load(stream);
        } finally {
            stream.close();
        }
    }

    /**
     * 将当前内容保存到给定的输出流中。
     *
     * @param stream        要保存内容的流
     * @throws IOException  in case of problems while writing to the stream
     */
    public void save(final OutputStream stream) throws IOException {
        final ExecutionDataWriter dataWriter = new ExecutionDataWriter(stream);
        sessionInfos.accept(dataWriter);
        executionData.accept(dataWriter);
    }

    /**
     * 将当前内容保存到给定文件中。
     * 父目录是根据需要创建的。
     * 还获得了文件系统锁，以避免并发写访问。
     *
     * @param file          保存内容的文件
     * @param append        <code>true</code>时内容应该被追加，否则文件将被覆盖。
     * @throws IOException  in case of problems while writing to the stream
     */
    public void save(final File file, final boolean append) throws IOException {
        final File folder = file.getParentFile();
        if (folder != null) {
            folder.mkdirs();
        }
        final FileOutputStream fileStream = new FileOutputStream(file, append);
        // 避免来自其他进程的并发写入:
        fileStream.getChannel().lock();
        final OutputStream bufferedStream = new BufferedOutputStream(fileStream);
        try {
            save(bufferedStream);
        } finally {
            bufferedStream.close();
        }
    }

    /**
     * 返回包含所有已加载会话的会话信息存储。
     *
     * @return session info store
     */
    public SessionInfoStore getSessionInfoStore() {
        return sessionInfos;
    }

    /**
     * 返回包含所有已加载class数据的执行数据存储。
     *
     * @return execution data store
     */
    public ExecutionDataStore getExecutionDataStore() {
        return executionData;
    }

}
