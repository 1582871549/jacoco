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

import static java.lang.String.format;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * 字节码检测的常数和实用程序。
 */
public final class InstrSupport {

    private InstrSupport() {
    }

    /** ASM API version */
    public static final int ASM_API_VERSION = Opcodes.ASM7;

    // === Data Field ===

    /**
     * 存储类的覆盖信息的字段的名称。
     */
    public static final String DATAFIELD_NAME = "$jacocoData";

    /**
     * 存储类覆盖信息的字段的访问修饰符
     *
     * 根据Java虚拟机规范
     * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-6.html#jvms-6.5.putstatic">§6.5.putstatic</a>
     * 该字段不得为final字段:
     *
     * <p>
     * 如果字段是final 修饰的，它必须在当前类中声明，
     * 并且该指令必须出现在当前类的{@code <clinit>}方法中
     * </p>
     */
    public static final int DATAFIELD_ACC = Opcodes.ACC_SYNTHETIC | Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_TRANSIENT;

    /**
     * 存储Java 8接口覆盖信息的字段的访问修饰符
     *
     * 根据Java虚拟机规范
     * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.5-200-A.3">§4.5</a>:
     *
     * <p>
     * 接口字段必须设置其ACC_PUBLIC、ACC_STATIC和ACC_FINAL标志；
     * 他们可能设置了ACC_SYNTHETIC标志，并且不能有任何其他标志。
     * </p>
     */
    public static final int DATAFIELD_INTF_ACC = Opcodes.ACC_SYNTHETIC | Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL;

    /**
     * 存储类的覆盖信息的字段的数据类型
     * <code>boolean[]</code>).
     */
    public static final String DATAFIELD_DESC = "[Z";

    // === Init Method ===

    /**
     * 初始化方法的名称
     */
    public static final String INITMETHOD_NAME = "$jacocoInit";

    /**
     * 初始化方法的描述符。
     */
    public static final String INITMETHOD_DESC = "()[Z";

    /**
     * 初始化方法的访问修饰符
     */
    public static final int INITMETHOD_ACC = Opcodes.ACC_SYNTHETIC | Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC;

    /**
     * 接口初始化方法的名称
     *
     * 根据Java虚拟机规范 2.9
     * <a href= "https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-2.html#jvms-2.9-200">§2.9</a>:
     *
     * <blockquote>
     * <p>
     * 一个类或接口最多有一个类或接口初始化方法，
     * 并通过调用该方法进行初始化。类或接口的初始化方法具有特殊名称{@code <clinit>}，
     * 不接受参数，并且为空。
     * </p>
     * <p>
     * 类文件中名为{@code <clinit>}的其他方法无关紧要。
     * 它们不是类或接口初始化方法。
     * 它们不能被任何Java虚拟机指令调用，也绝不会被Java虚拟机本身调用。
     * </p>
     * <p>
     * 在版本号为51.0或更高的类文件中，
     * 该方法必须另外设置ACC_STATIC标志，才能成为类或接口初始化方法。
     * </p>
     * <p>
     * 这一要求是在Java SE 7中引入的。在版本号为50.0或更低的类文件中，
     * 名为{@code <clinit>}的无效且不带参数的方法被视为类或接口初始化方法，而不管其ACC_STATIC标志的设置如何。
     * </p>
     * </blockquote>
     *
     * And <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.6-200-A.6">§4.6</a>:
     *
     * <blockquote>
     * <p>
     * 类和接口初始化方法由Java虚拟机隐式调用。
     * 除了设置ACC_STRICT标志之外，它们的access_flags项的值将被忽略。
     * </p>
     * </blockquote>
     */
    static final String CLINIT_NAME = "<clinit>";

    /**
     * 接口初始化方法的描述符
     *
     * @see #CLINIT_NAME
     */
    static final String CLINIT_DESC = "()V";

    /**
     * JaCoCo 生成的接口初始化方法的访问标志
     *
     * @see #CLINIT_NAME
     */
    static final int CLINIT_ACC = Opcodes.ACC_SYNTHETIC | Opcodes.ACC_STATIC;

	private static final int MAJOR_VERSION_INDEX = 6;

	/**
	 * Gets major of bytecode version number from given bytes of class.
	 *
	 * @param b
	 *            bytes of class
	 * @return version of bytecode
	 */
	public static int getVersionMajor(final byte[] b) {
		return (short) (((b[MAJOR_VERSION_INDEX] & 0xFF) << 8)
				| (b[MAJOR_VERSION_INDEX + 1] & 0xFF));
	}

	/**
	 * Determines whether the given class file version requires stackmap frames.
	 * 
	 * @param version
	 *            class file version
	 * @return <code>true</code> if frames are required
	 */
	public static boolean needsFrames(final int version) {
		// consider major version only (due to 1.1 anomaly)
		return (version & 0xff) >= Opcodes.V1_6;
	}

    /**
     * 确保给定成员不对应于由检测过程创建的内部成员。这意味着该类已经被检测。
     *
     * @param member 方法名
     * @param owner  类名
     *
     * DATAFIELD_NAME = $jacocoData, INITMETHOD_NAME = $jacocoInit
     *
     * @throws IllegalStateException 如果member与检测方法名相同 则抛出异常
     */
    public static void assertNotInstrumented(final String member, final String owner) throws IllegalStateException {

        if (member.equals(DATAFIELD_NAME) || member.equals(INITMETHOD_NAME)) {
            throw new IllegalStateException(format("Cannot process instrumented class %s. Please supply original non-instrumented classes.", owner));
        }
    }

	/**
	 * Generates the instruction to push the given int value on the stack.
	 * Implementation taken from
	 * {@link org.objectweb.asm.commons.GeneratorAdapter#push(int)}.
	 * 
	 * @param mv
	 *            visitor to emit the instruction
	 * @param value
	 *            the value to be pushed on the stack.
	 */
	public static void push(final MethodVisitor mv, final int value) {
		if (value >= -1 && value <= 5) {
			mv.visitInsn(Opcodes.ICONST_0 + value);
		} else if (value >= Byte.MIN_VALUE && value <= Byte.MAX_VALUE) {
			mv.visitIntInsn(Opcodes.BIPUSH, value);
		} else if (value >= Short.MIN_VALUE && value <= Short.MAX_VALUE) {
			mv.visitIntInsn(Opcodes.SIPUSH, value);
		} else {
			mv.visitLdcInsn(Integer.valueOf(value));
		}
	}

    /**
     * 为给定字节的类创建{@link ClassReader}实例，即使其版本不受ASM支持。
     *
     * @param b     class 字节流
     * @return {@link ClassReader}
     */
    public static ClassReader classReaderFor(final byte[] b) {
        final byte[] originalVersion = new byte[] { b[4], b[5], b[6], b[7] };
        if (getVersionMajor(b) == Opcodes.V12 + 1) {
            b[4] = (byte) (Opcodes.V12 >>> 24);
            b[5] = (byte) (Opcodes.V12 >>> 16);
            b[6] = (byte) (Opcodes.V12 >>> 8);
            b[7] = (byte) Opcodes.V12;
        }
        final ClassReader classReader = new ClassReader(b);
        System.arraycopy(originalVersion, 0, b, 4, originalVersion.length);
        return classReader;
    }

}
