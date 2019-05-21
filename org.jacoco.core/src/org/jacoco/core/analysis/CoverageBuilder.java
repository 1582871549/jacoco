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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.jacoco.core.internal.analysis.BundleCoverageImpl;
import org.jacoco.core.internal.analysis.SourceFileCoverageImpl;

/**
 * 从单个{@link IClassCoverage}节点构建分层{ @ link ICoverageNode }结构。
 * 这些节点通过其{@link ICoverageVisitor}界面被馈送到构建器中。
 * 之后，可以使用{@link #getClasses()}获取聚合数据，
 * {@link #getSourceFiles()}或{ @ link # getBundle(字符串)}在以下层次结构中:
 *
 * <pre>
 * {@link IBundleCoverage}
 * +-- {@link IPackageCoverage}*
 *     +-- {@link IClassCoverage}*
 *     +-- {@link ISourceFileCoverage}*
 * </pre>
 */
public class CoverageBuilder implements ICoverageVisitor {

    private final Map<String, IClassCoverage> classes;

    private final Map<String, ISourceFileCoverage> sourcefiles;


    public CoverageBuilder() {
        this.classes = new HashMap<String, IClassCoverage>();
        this.sourcefiles = new HashMap<String, ISourceFileCoverage>();
    }

    /**
     * 返回当前包含在此生成器中的所有类节点。
     *
     * @return all class nodes
     */
    public Collection<IClassCoverage> getClasses() {
        return Collections.unmodifiableCollection(classes.values());
    }

    /**
     * 返回当前包含在此生成器中的所有源文件节点。
     *
     * @return all source file nodes
     */
    public Collection<ISourceFileCoverage> getSourceFiles() {
        return Collections.unmodifiableCollection(sourcefiles.values());
    }

    /**
     * 从当前包含在包中的所有节点创建包。
     *
     * @param name 捆绑包的名称
     *
     * @return bundle containing all classes and source files
     */
    public IBundleCoverage getBundle(final String name) {
        return new BundleCoverageImpl(name, classes.values(), sourcefiles.values());
    }

    /**
     * 返回执行数据不匹配的所有类。
     *
     * @see IClassCoverage#isNoMatch()
     * @return collection of classes with non-matching execution data
     */
    public Collection<IClassCoverage> getNoMatchClasses() {
        final Collection<IClassCoverage> result = new ArrayList<IClassCoverage>();
        for (final IClassCoverage c : classes.values()) {
            if (c.isNoMatch()) {
                result.add(c);
            }
        }
        return result;
    }

    // === ICoverageVisitor ===

    public void visitCoverage(final IClassCoverage coverage) {
        final String className = coverage.getName();
        final IClassCoverage dup = classes.put(className, coverage);
        if (dup != null) {
            if (dup.getId() != coverage.getId()) {
                throw new IllegalStateException(
                        "Can't add different class with same name: " + className);
            }
        } else {
            final String source = coverage.getSourceFileName();
            if (source != null) {
                final SourceFileCoverageImpl sourceFile = getSourceFile(source, coverage.getPackageName());
                sourceFile.increment(coverage);
            }
        }
    }

    private SourceFileCoverageImpl getSourceFile(final String filename,
                                                 final String packagename) {
        final String key = packagename + '/' + filename;
        SourceFileCoverageImpl sourcefile = (SourceFileCoverageImpl) sourcefiles
                .get(key);
        if (sourcefile == null) {
            sourcefile = new SourceFileCoverageImpl(filename, packagename);
            sourcefiles.put(key, sourcefile);
        }
        return sourcefile;
    }

}
