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
package org.jacoco.core.internal.instr;

import org.jacoco.core.internal.flow.ClassProbesVisitor;
import org.jacoco.core.internal.flow.MethodProbesVisitor;
import org.objectweb.asm.Opcodes;

/**
 * 内部类，用于记住一个类所需的探针总数
 */
class ProbeCounter extends ClassProbesVisitor {

    private int count;
    private boolean methods;

    ProbeCounter() {
        count = 0;
        methods = false;
    }

    /**
     * 该方法未被执行
     */
    @Override
    public MethodProbesVisitor visitMethod(final int access, final String name,
                                           final String desc, final String signature, final String[] exceptions) {

        if (!InstrSupport.CLINIT_NAME.equals(name) && (access & Opcodes.ACC_ABSTRACT) == 0) {
            methods = true;
        }
        return null;
    }

    /**
     * 该方法未被执行
     */
    @Override
    public void visitTotalProbeCount(final int count) {
        this.count = count;
    }

    int getCount() {
        return count;
    }

    /**
     * @return 如果类具有非抽象方法而不是静态初始值设定项，则为 true
     */
    boolean hasMethods() {
        return methods;
    }

}
