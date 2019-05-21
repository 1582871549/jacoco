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
package org.jacoco.core.data;

import org.jacoco.core.internal.data.CompactDataInput;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import static java.lang.String.format;

/**
 * Deserialization of execution data from binary streams.
 * 二进制流中执行数据的反序列化。
 */
public class ExecutionDataReader {

    /** 基础数据输入 */
    protected final CompactDataInput in;

    private ISessionInfoVisitor sessionInfoVisitor = null;

    private IExecutionDataVisitor executionDataVisitor = null;

    private boolean firstBlock = true;

    /**
     * Creates a new reader based on the given input stream input. Depending on
     * the nature of the underlying stream input should be buffered as most data
     * is read in single bytes.
     *
     * @param input
     *            input stream to read execution data from
     */
    public ExecutionDataReader(final InputStream input) {
        this.in = new CompactDataInput(input);
    }

    /**
     * Sets an listener for session information.
     *
     * @param visitor
     *            visitor to retrieve session info events
     */
    public void setSessionInfoVisitor(final ISessionInfoVisitor visitor) {
        this.sessionInfoVisitor = visitor;
    }

    /**
     * 为执行数据设置监听器
     *
     * @param visitor 访问者检索执行数据事件
     */
    public void setExecutionDataVisitor(final IExecutionDataVisitor visitor) {
        this.executionDataVisitor = visitor;
    }

    /**
     * Reads all data and reports it to the corresponding visitors. The stream
     * is read until its end or a command confirmation has been sent.
     *
     * @return <code>true</code> if additional data can be expected after a
     *         command has been executed. <code>false</code> if the end of the
     *         stream has been reached.
     * @throws IOException
     *             might be thrown by the underlying input stream
     * @throws IncompatibleExecDataVersionException
     *             incompatible data version from different JaCoCo release
     */
    public boolean read() throws IOException,
            IncompatibleExecDataVersionException {
        byte type;
        do {
            int i = in.read();
            if (i == -1) {
                return false; // EOF
            }
            type = (byte) i;
            if (firstBlock && type != ExecutionDataWriter.BLOCK_HEADER) {
                throw new IOException("Invalid execution data file.");
            }
            firstBlock = false;
        } while (readBlock(type));
        return true;
    }

    /**
     * Reads a block of data identified by the given id. Subclasses may
     * overwrite this method to support additional block types.
     *
     * @param blocktype
     *            block type
     * @return <code>true</code> if there are more blocks to read
     * @throws IOException
     *             might be thrown by the underlying input stream
     */
    protected boolean readBlock(final byte blocktype) throws IOException {
        switch (blocktype) {
            case ExecutionDataWriter.BLOCK_HEADER:
                readHeader();
                return true;
            case ExecutionDataWriter.BLOCK_SESSIONINFO:
                readSessionInfo();
                return true;
            case ExecutionDataWriter.BLOCK_EXECUTIONDATA:
                readExecutionData();
                return true;
            default:
                throw new IOException(format("Unknown block type %x.", Byte.valueOf(blocktype)));
        }
    }

    private void readHeader() throws IOException {
        if (in.readChar() != ExecutionDataWriter.MAGIC_NUMBER) {
            throw new IOException("Invalid execution data file.");
        }
        final char version = in.readChar();
        if (version != ExecutionDataWriter.FORMAT_VERSION) {
            throw new IncompatibleExecDataVersionException(version);
        }
    }

    private void readSessionInfo() throws IOException {
        if (sessionInfoVisitor == null) {
            throw new IOException("No session info visitor.");
        }
        final String id = in.readUTF();
        final long start = in.readLong();
        final long dump = in.readLong();
        sessionInfoVisitor.visitSessionInfo(new SessionInfo(id, start, dump));
    }

    private void readExecutionData() throws IOException {
        if (executionDataVisitor == null) {
            throw new IOException("No execution data visitor.");
        }
        final long id = in.readLong();
        final String name = in.readUTF();
        final boolean[] probes = in.readBooleanArray();

        executionDataVisitor.visitClassExecution(new ExecutionData(id, name, probes));
    }

}
