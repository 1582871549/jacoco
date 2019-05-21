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
package org.jacoco.core.analysis;

/**
 * 单一方法的覆盖数据。此节点的名称是本地方法名称。
 */
public interface IMethodCoverage extends ISourceNode {

    /**
     * 返回方法的描述符。
     *
     * @return descriptor
     */
    String getDesc();

    /**
     * 如果已定义，则返回方法的通用签名。
     *
     * @return generic signature or <code>null</code>
     */
    String getSignature();

}
