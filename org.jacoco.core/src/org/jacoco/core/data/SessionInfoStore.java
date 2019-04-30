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

import static java.lang.Math.max;
import static java.lang.Math.min;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 用于收集和合并会话{@link SessionInfo}对象的容器
 */
public class SessionInfoStore implements ISessionInfoVisitor {

    private final List<SessionInfo> infos = new ArrayList<SessionInfo>();

    /**
     * 测试 infos 是否为空。
     *
     * @return 为空，则返回“true”
     */
    public boolean isEmpty() {
        return infos.isEmpty();
    }

    /**
     * 返回存储中当前包含的所有{@link SessionInfo}对象。
     * 信息对象按其自然顺序(即转储时间戳)排序。
     *
     * @return list of stored {@link SessionInfo} objects
     */
    public List<SessionInfo> getInfos() {
        final List<SessionInfo> copy = new ArrayList<SessionInfo>(infos);
        Collections.sort(copy);
        return copy;
    }

    /**
     * 返回具有给定id的新会话信息，该信息包含来自所有包含版本的合并版本。
     * 开始时间戳是所有包含会话的最小值，
     * 转储时间戳是所有包含会话的最大值。
     * 如果当前不包含会话，则两个时间戳都设置为 0
     *
     * @param id    合并会话信息的标识符
     *
     * @return new {@link SessionInfo} object
     */
    public SessionInfo getMerged(final String id) {
        if (infos.isEmpty()) {
            return new SessionInfo(id, 0, 0);
        }
        long start = Long.MAX_VALUE;
        long dump = Long.MIN_VALUE;
        for (final SessionInfo i : infos) {
            start = min(start, i.getStartTimeStamp());
            dump = max(dump, i.getDumpTimeStamp());
        }
        return new SessionInfo(id, start, dump);
    }

    /**
     * 将所有包含的{@link SessionInfo}对象写入给定访问者。
     * 信息对象按照转储时间戳的时间顺序发出。
     *
     * @param visitor   要将{@link SessionInfo}对象发送到的访问者
     */
    public void accept(final ISessionInfoVisitor visitor) {
        for (final SessionInfo i : getInfos()) {
            visitor.visitSessionInfo(i);
        }
    }

    // === ISessionInfoVisitor  的接口实现 ===

    public void visitSessionInfo(final SessionInfo info) {
        infos.add(info);
    }

}
