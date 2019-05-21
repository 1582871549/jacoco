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

/**
 * 描述作为执行数据源的会话的数据对象。{@link SessionInfo}实例可以通过{@link Comparable}界面按转储日期排序。
 */
public class SessionInfo implements Comparable<SessionInfo> {

    private final String id;

    private final long start;

    private final long dump;

    /**
     * 用给定的数据创建不可变的会话信息。
     *
     * @param id        任意会话标识符，不能为 null
     * @param start     执行数据记录时基于epoc的时间戳
     * @param dump      收集执行数据时基于epoc的时间戳
     */
    public SessionInfo(final String id, final long start, final long dump) {
        if (id == null) {
            throw new IllegalArgumentException();
        }
        this.id = id;
        this.start = start;
        this.dump = dump;
    }

    /**
     * @return 会话标识符
     */
    public String getId() {
        return id;
    }

    /**
     * @return 执行数据记录时基于epoc的时间戳
     */
    public long getStartTimeStamp() {
        return start;
    }

    /**
     * @return 收集执行数据时基于epoc的时间戳
     */
    public long getDumpTimeStamp() {
        return dump;
    }

    public int compareTo(final SessionInfo other) {
        if (this.dump < other.dump) {
            return -1;
        }
        if (this.dump > other.dump) {
            return +1;
        }
        return 0;
    }

    @Override
    public String toString() {
        return "SessionInfo[" + id + "]";
    }
}
