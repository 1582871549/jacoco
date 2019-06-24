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
 * Coverage data of a Java package containing classes and source files. The name
 * of this node is the package name in VM notation (slash separated). The name
 * of the default package is the empty string.
 *
 * 包含类和源文件的Java包的覆盖数据。
 * 此节点的名称是虚拟机符号中的包名(斜线分隔)。
 * 默认包的名称是空字符串。
 *
 * @see IClassCoverage
 * @see ISourceFileCoverage
 */
public interface IPackageCoverage extends ICoverageNode {

    /**
     * Returns all classes contained in this package.
     * 返回包中包含的所有类。
     *
     * @return all classes
     */
    Collection<IClassCoverage> getClasses();

    /**
     * Returns all source files in this package.
     * 返回此包中的所有源文件
     *
     * @return all source files
     */
    Collection<ISourceFileCoverage> getSourceFiles();

}
