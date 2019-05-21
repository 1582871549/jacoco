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

import java.util.Collection;

/**
 * 包含方法的单个类的覆盖数据。
 * 此节点的名称是虚拟机符号中的完全限定类名(斜线分隔)。
 * 
 * @see IMethodCoverage
 */
public interface IClassCoverage extends ISourceNode {

    /**
     * 返回该类的标识符，即类定义的CRC64签名。
     *
     * @return class identifier
     */
    long getId();

    /**
     * 如果分析的类与提供的执行数据匹配，则返回。
     * 更确切地说，如果执行数据可用于具有相同限定名但具有不同类id的类。
     *
     * @return <code>true</code> if this class does not match to the provided
     *         execution data.
     */
    boolean isNoMatch();

    /**
     * 返回类的虚拟机签名。
     *
     * @return VM signature of the class (may be <code>null</code>)
     */
    String getSignature();

    /**
     * 返回超类的虚拟机名称。
     *
     * @return VM name of the super class (may be <code>null</code>, i.e.
     *         <code>java/lang/Object</code>)
     */
    String getSuperName();

    /**
     * 返回已实现/扩展接口的虚拟机名称。
     *
     * @return VM names of implemented/extended interfaces
     */
    String[] getInterfaceNames();

    /**
     * 返回此类所属包的虚拟机名称。
     *
     * @return VM name of the package
     */
    String getPackageName();

    /**
     * 返回相应源文件的可选名称。
     *
     * @return name of the corresponding source file
     */
    String getSourceFileName();

    /**
     * 返回此类中包含的方法。
     *
     * @return methods of this class
     */
    Collection<IMethodCoverage> getMethods();

}
