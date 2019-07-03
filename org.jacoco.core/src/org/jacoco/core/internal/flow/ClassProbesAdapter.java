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

import org.jacoco.core.internal.analysis.ClassAnalyzer;
import org.jacoco.core.internal.instr.InstrSupport;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.AnalyzerAdapter;

import java.util.Arrays;
import java.util.Map;

/**
 * 一个为每种方法计算探针的类访问者。
 */
public class ClassProbesAdapter extends ClassVisitor implements IProbeIdGenerator {

    private static final MethodProbesVisitor EMPTY_METHOD_PROBES_VISITOR = new MethodProbesVisitor() {};

    private final ClassProbesVisitor cv;

    // 跟踪框架
    private final boolean trackFrames;

    private int counter = 0;

    private String className;

    private Map<String, Map<String, String>> diffMethod;

    /**
     * 创建委托给给定访问者的新适配器。
     *
     * @param cv            要委托给的实例
     * @param trackFrames   如果为真则，跟踪并提供stackmap帧
     */
    public ClassProbesAdapter(final ClassProbesVisitor cv, final boolean trackFrames) {
        super(InstrSupport.ASM_API_VERSION, cv);
        this.cv = cv;
        this.trackFrames = trackFrames;
    }

    /**
     * 创建委托给给定访问者的新适配器。
     *
     * @param cv            要委托给的实例
     * @param trackFrames   如果为真则，跟踪并提供stackmap帧
     */
    public ClassProbesAdapter(final ClassProbesVisitor cv,
                              final boolean trackFrames,
                              final Map<String, Map<String, String>> diffMethod) {
        super(InstrSupport.ASM_API_VERSION, cv);
        this.cv = cv;
        this.trackFrames = trackFrames;
        this.diffMethod = diffMethod;
    }

    /**
     * @param version       版本
     * @param access        访问
     * @param className     名字
     * @param signature     签名
     * @param superName     超级名
     * @param interfaces    接口
     */
    @Override
    public void visit(final int version, final int access, final String className,
                      final String signature, final String superName,
                      final String[] interfaces) {

        // System.out.println();
        // System.out.println("----------1----------" + "ClassProbesAdapter # visit");
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

        this.className = className;
        super.visit(version, access, className, signature, superName, interfaces);
    }

    /**
     * 此方法每次调用时都必须返回一个新的{@link MethodVisitor}实例(或{null})，即不应该返回以前返回的实例
     *
     * @param access 方法的访问标志， 此参数指示该方法是合成的和 / 或是不推荐使用的
     * @param methodName 方法的名称
     * @param desc 方法的描述符
     * @param signature 方法的签名。如果方法参数、返回类型和异常不使用泛型类型，则可能为null。
     * @param exceptions 方法异常类的内部名称 。可能为空
     * @return 访问方法字节代码的对象，如果该类访问者对访问该方法的代码没有关系，则为null。
     */
    @Override
    public final MethodVisitor visitMethod(final int access, final String methodName,
                                           final String desc, final String signature, final String[] exceptions) {

        // System.out.println("----------5.5--------" + "ClassProbesAdapter # visitMethod");
        // System.out.println(access);
        // System.out.println(name);
        // System.out.println(desc);
        // System.out.println(signature);
        // System.out.println(Arrays.toString(exceptions));
        // System.out.println(this.name);

        final MethodProbesVisitor methodProbes;
        /**
         * 当访问一个方法时，我们需要一个{@link MethodProbesVisitor}来处理该方法的探测。
         * cv == ClassAnalyzer, mv = MethodAnalyzer的匿名内部子类
         */
        final MethodProbesVisitor mv = cv.visitMethod(access, methodName, desc, signature, exceptions);

        // System.out.println(name);
        // System.out.println("----------------");

        if (mv != null && (diffMethod == null || diffMethod.size() == 0 || diffMethod.get(this.className).containsKey(methodName))) {
        // if (mv != null) {

            System.out.println("++     " + methodName + "      " + this.className);
            methodProbes = mv;
        } else {
            // 在任何情况下，我们都需要访问该方法，否则探针id是不可复制的
            methodProbes = EMPTY_METHOD_PROBES_VISITOR;
        }

        // 方法消除器
        return new MethodSanitizer(null, access, methodName, desc, signature, exceptions) {

            @Override
            public void visitEnd() {

                // System.out.println("----------7----------" + "ClassProbesAdapter $ MethodSanitizer # visitEnd");

                super.visitEnd();
                // 标记流量分析器    标记标签
                LabelFlowAnalyzer.markLabels(this);

                // 初始化 MethodVisitor 类
                final MethodProbesAdapter probesAdapter = new MethodProbesAdapter(methodProbes, ClassProbesAdapter.this);

                if (trackFrames) {

                    final AnalyzerAdapter analyzer = new AnalyzerAdapter(ClassProbesAdapter.this.className, access, name, desc, probesAdapter);

                    probesAdapter.setAnalyzer(analyzer);

                    methodProbes.accept(this, analyzer);
                } else {
                    methodProbes.accept(this, probesAdapter);
                }
            }
        };
    }

    @Override
    public void visitEnd() {
        cv.visitTotalProbeCount(counter);
        super.visitEnd();
    }

    // === IProbeIdGenerator ===

    public int nextId() {
        return counter++;
    }

    public ClassAnalyzer getCv() {
        return (ClassAnalyzer) cv;
    }

    @Override
    public String toString() {
        return "ClassProbesAdapter{" +
                "cv=" + cv +
                ", className='" + className + '\'' +
                "} ";
    }
}
