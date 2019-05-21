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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jacoco.core.analysis.CoverageNodeImpl;
import org.jacoco.core.analysis.IBundleCoverage;
import org.jacoco.core.analysis.IClassCoverage;
import org.jacoco.core.analysis.IPackageCoverage;
import org.jacoco.core.analysis.ISourceFileCoverage;

/**
 * 包的覆盖数据。包将包的集合分组
 * Implementation of {@link IBundleCoverage}.
 */
public class BundleCoverageImpl extends CoverageNodeImpl implements
        IBundleCoverage {

    private final Collection<IPackageCoverage> packages;

    /**
     * 创建具有给定名称的包的新实例
     *
     * @param name      这个包的名称
     * @param packages  这个包中包含的所有包的集合
     */
    public BundleCoverageImpl(final String name, final Collection<IPackageCoverage> packages) {
        super(ElementType.BUNDLE, name);
        this.packages = packages;
        increment(packages);
    }

    /**
     * 创建具有给定名称的包的新实例。
     * 包是根据给定的类和源文件计算的。
     *
     * @param name          这个包的名称
     * @param classes       这个包中的所有类
     * @param sourcefiles   这个包中的所有源文件
     */
    public BundleCoverageImpl(final String name,
                              final Collection<IClassCoverage> classes,
                              final Collection<ISourceFileCoverage> sourcefiles) {
        this(name, groupByPackage(classes, sourcefiles));
    }

    private static Collection<IPackageCoverage> groupByPackage(
            final Collection<IClassCoverage> classes,
            final Collection<ISourceFileCoverage> sourcefiles) {
        final Map<String, Collection<IClassCoverage>> classesByPackage = new HashMap<String, Collection<IClassCoverage>>();
        for (final IClassCoverage c : classes) {
            addByName(classesByPackage, c.getPackageName(), c);
        }

        final Map<String, Collection<ISourceFileCoverage>> sourceFilesByPackage = new HashMap<String, Collection<ISourceFileCoverage>>();
        for (final ISourceFileCoverage s : sourcefiles) {
            addByName(sourceFilesByPackage, s.getPackageName(), s);
        }

        final Set<String> packageNames = new HashSet<String>();
        packageNames.addAll(classesByPackage.keySet());
        packageNames.addAll(sourceFilesByPackage.keySet());

        final Collection<IPackageCoverage> result = new ArrayList<IPackageCoverage>();
        for (final String name : packageNames) {
            Collection<IClassCoverage> c = classesByPackage.get(name);
            if (c == null) {
                c = Collections.emptyList();
            }
            Collection<ISourceFileCoverage> s = sourceFilesByPackage.get(name);
            if (s == null) {
                s = Collections.emptyList();
            }
            result.add(new PackageCoverageImpl(name, c, s));
        }
        return result;
    }

    private static <T> void addByName(final Map<String, Collection<T>> map,
                                      final String name, final T value) {
        Collection<T> list = map.get(name);
        if (list == null) {
            list = new ArrayList<T>();
            map.put(name, list);
        }
        list.add(value);
    }

    // === IBundleCoverage implementation ===

    public Collection<IPackageCoverage> getPackages() {
        return packages;
    }

}
