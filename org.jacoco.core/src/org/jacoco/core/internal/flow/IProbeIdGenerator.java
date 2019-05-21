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
package org.jacoco.core.internal.flow;

/**
 * 内部接口，用于创建类中唯一的探针id。
 */
public interface IProbeIdGenerator {

    /**
     * 返回下一个探针id。(是唯一的)
     *
     * @return unique probe id
     */
    int nextId();

}
