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

import org.jacoco.core.internal.analysis.filter.Filters;
import org.jacoco.core.internal.analysis.filter.IFilter;
import org.jacoco.core.internal.analysis.filter.IFilterContext;
import org.jacoco.core.internal.flow.ClassProbesVisitor;
import org.jacoco.core.internal.flow.MethodProbesVisitor;
import org.jacoco.core.internal.instr.InstrSupport;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.MethodNode;

import java.util.HashSet;
import java.util.Set;

/**
 * 分析一个类的结构。
 */
public class ClassAnalyzer extends ClassProbesVisitor implements IFilterContext {

    private final ClassCoverageImpl coverage;
    private final boolean[] probes;
    private final StringPool stringPool;

    private final Set<String> classAnnotations = new HashSet<String>();

    private String sourceDebugExtension;

    private final IFilter filter;

    /**
     * Creates a new analyzer that builds coverage data for a class.
     *
     * @param coverage          类数据
     * @param probes            探针
     * @param stringPool    共享池最大限度地减少{字符串}实例的数量
     */
    public ClassAnalyzer(final ClassCoverageImpl coverage, final boolean[] probes, final StringPool stringPool) {
        super();
        this.coverage = coverage;
        this.probes = probes;
        this.stringPool = stringPool;
        this.filter = Filters.all();
    }

    @Override
    public void visit(final int version, final int access, final String name,
                      final String signature, final String superName,
                      final String[] interfaces) {

        // System.out.println("----------2----------" + "ClassAnalyzer # visit");
        // version      52
        // access       33
        // name         com/dudu/common/configuration/bean/MyProperties
        // signature    null
        // superName    java/lang/Object
        // interfaces   []
        // -------------------------------------------------------------------------
        // version      52
        // access       33
        // name         com/dudu/common/configuration/example/impl/SecondarySchoolServiceImpl
        // signature    null
        // superName    java/lang/Object
        // interfaces   [com/dudu/common/configuration/example/SchoolService]

        // 向ClassCoverageImpl对象赋值, 填充stringPool.pool
        coverage.setSignature(stringPool.get(signature));
        coverage.setSuperName(stringPool.get(superName));
        coverage.setInterfaces(stringPool.get(interfaces));
    }

    @Override
    public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {

        // System.out.println("----------4----------" + "ClassAnalyzer # visitAnnotation");
        // desc         Lorg/springframework/context/annotation/Configuration;
        // visible      true
        // -------------------------------------------------------------------------
        // desc         Lorg/springframework/web/bind/annotation/RequestMapping;
        // visible      true

        classAnnotations.add(desc);
        return super.visitAnnotation(desc, visible);
    }

    @Override
    public void visitSource(final String source, final String debug) {

        // System.out.println("----------3----------" + "ClassAnalyzer # visitSource");
        // source       MyProperties.java
        // debug        null
        // -------------------------------------------------------------------------
        // source       SecondarySchoolServiceImpl.java
        // debug        null

        // 向ClassCoverageImpl对象赋值, 填充stringPool.pool
        coverage.setSourceFileName(stringPool.get(source));
        sourceDebugExtension = debug;
    }

    /**
     *
     * @param access        方法的访问标志， 此参数指示该方法是合成的和 / 或是不推荐使用的
     * @param name          方法的名称
     * @param desc          方法的描述符
     * @param signature     方法的签名。如果方法参数、返回类型和异常不使用泛型类型，则可能为null。
     * @param exceptions    法异常类的内部名称 。可能为空
     * @return
     */
    @Override
    public MethodProbesVisitor visitMethod(final int access, final String name,
                                           final String desc, final String signature,
                                           final String[] exceptions) {

        // System.out.println("----------6----------" + "ClassAnalyzer # visitMethod");
        // coverage.getName() = com/dudu/common/configuration/bean/MyProperties
        // access       2
        // name         name
        // desc         Ljava/lang/String;
        // signature    null
        // value        null
        // -------------------------------------------------------------------------

        // System.out.println(access);
        // System.out.println(name);
        // System.out.println(desc);
        // System.out.println(signature);
        // System.out.println(Arrays.toString(exceptions));
        // System.out.println(coverage.getName());
        // System.out.println(Arrays.toString(probes));

        // 检测判断 方法名, 类名
        InstrSupport.assertNotInstrumented(name, coverage.getName());
        // 指令工具 probes 探针数组
        final InstructionsBuilder builder = new InstructionsBuilder(probes);

        // 匿名内部类 创建MethodAnalyzer的子类, 并重写其方法
        return new MethodAnalyzer(builder) {

            // public void accept(final MethodNode methodNode, final MethodVisitor methodVisitor) {
            //     methodNode.accept(methodVisitor);
            // }

            /**
             * @param methodNode        MethodSanitizer 的匿名内部类
             * @param methodVisitor     MethodProbesAdapter
             */
            @Override
            public void accept(final MethodNode methodNode, final MethodVisitor methodVisitor) {

                // System.out.println("----------10---------" + "ClassAnalyzer $ MethodAnalyzer # accept");

                super.accept(methodNode, methodVisitor);

                addMethodCoverage(stringPool.get(name), stringPool.get(desc), stringPool.get(signature), builder, methodNode);
            }
        };
    }

    private void addMethodCoverage(final String name, final String desc,
                                   final String signature, final InstructionsBuilder builder,
                                   final MethodNode methodNode) {

        // 计算单个方法的过滤覆盖率。
        final MethodCoverageCalculator mcc = new MethodCoverageCalculator(builder.getInstructions());

        filter.filter(methodNode, this, mcc);

        /**
         * 使用给定参数创建方法覆盖数据对象。
         * @param name          方法的名称
         * @param desc          方法的描述符
         * @param signature     方法的签名。如果方法参数、返回类型和异常不使用泛型类型，则可能为null。
         */
        // final MethodCoverageImpl mc = new MethodCoverageImpl(name, desc, signature);

        final MethodCoverageImpl mc = new MethodCoverageImpl(name, desc, signature, coverage);

        // 记录方法是否被覆盖
        mcc.calculate(mc);

        if (mc.containsCode()) {
            // 记录类是否被覆盖 只考虑实际包含代码的方法
            coverage.addMethod(mc);
        }

    }

    @Override
    public FieldVisitor visitField(final int access, final String name,
                                   final String desc, final String signature, final Object value) {

        // System.out.println("----------5----------" + "ClassAnalyzer # visitField");
        // coverage.getName() = com/dudu/common/configuration/bean/MyProperties
        // access       2
        // name         name
        // desc         Ljava/lang/String;
        // signature    null
        // value        null
        // -------------------------------------------------------------------------
        // coverage.getName() = com/dudu/common/configuration/bean/MyProperties
        // access       2
        // name         sex
        // desc         Ljava/lang/String;
        // signature    null
        // value        null
        // -------------------------------------------------------------------------
        // coverage.getName() = com/dudu/service/impl/UserServiceImpl
        // access       10
        // name         list
        // desc         Ljava/util/List;
        // signature    Ljava/util/List<Ljava/lang/String;>;
        // value        null
        // -------------------------------------------------------------------------
        // coverage.getName() = com/dudu/service/impl/UserServiceImpl
        // access       10
        // name         listError
        // desc         Ljava/util/List;
        // signature    Ljava/util/List<Ljava/lang/String;>;
        // value        null
        // -------------------------------------------------------------------------
        // coverage.getName() = com/dudu/service/impl/UserServiceImpl
        // access       10
        // name         nameError
        // desc         Ljava/util/List;
        // signature    Ljava/util/List<Ljava/lang/String;>;
        // value        null
        InstrSupport.assertNotInstrumented(name, coverage.getName());
        return super.visitField(access, name, desc, signature, value);
    }

    @Override
    public void visitTotalProbeCount(final int count) {
        // nothing to do
    }

    // IFilterContext implementation

    public String getClassName() {
        return coverage.getName();
    }

    public String getSuperClassName() {
        return coverage.getSuperName();
    }

    public Set<String> getClassAnnotations() {
        return classAnnotations;
    }

    public String getSourceFileName() {
        return coverage.getSourceFileName();
    }

    public String getSourceDebugExtension() {
        return sourceDebugExtension;
    }

    // ====================================

    public ClassCoverageImpl getCoverage() {
        return coverage;
    }

    @Override
    public String toString() {
        return "ClassAnalyzer{" +
                "coverage=" + coverage +
                "} ";
    }
}
