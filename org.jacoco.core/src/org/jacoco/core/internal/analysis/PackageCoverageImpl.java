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
package org.jacoco.core.internal.analysis;

import java.util.Collection;

import org.jacoco.core.analysis.CoverageNodeImpl;
import org.jacoco.core.analysis.IClassCoverage;
import org.jacoco.core.analysis.IPackageCoverage;
import org.jacoco.core.analysis.ISourceFileCoverage;

/**
 * Implementation of {@link IPackageCoverage}.
 */
public class PackageCoverageImpl extends CoverageNodeImpl implements IPackageCoverage {

    private final Collection<IClassCoverage> classes;

    private final Collection<ISourceFileCoverage> sourceFiles;

    /**
     * Creates package node instance for a package with the given name.
     * 为具有给定名称的包创建包节点实例
     *
     * @param name 包的虚拟机名称
     * @param classes 此包中所有类的集合
     * @param sourceFiles 此包中所有源文件的集合
     */
    public PackageCoverageImpl(final String name,
                               final Collection<IClassCoverage> classes,
                               final Collection<ISourceFileCoverage> sourceFiles) {
        super(ElementType.PACKAGE, name);
        this.classes = classes;
        this.sourceFiles = sourceFiles;
        increment(sourceFiles);
        for (final IClassCoverage c : classes) {
            // We need to add only classes without a source file reference.
            // Classes associated with a source file are already included in the
            // SourceFileCoverage objects.
            // 我们只需要添加没有源文件引用的类。
            // 与源文件相关联的类已经包含在源文件覆盖对象中。
            if (c.getSourceFileName() == null) {
                increment(c);
            }
        }
    }

    // === IPackageCoverage implementation ===

    public Collection<IClassCoverage> getClasses() {
        return classes;
    }

    public Collection<ISourceFileCoverage> getSourceFiles() {
        return sourceFiles;
    }

}
