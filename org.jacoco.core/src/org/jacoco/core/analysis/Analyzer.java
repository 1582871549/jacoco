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

import org.jacoco.core.data.ExecutionData;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.internal.ContentTypeDetector;
import org.jacoco.core.internal.InputStreams;
import org.jacoco.core.internal.Pack200Streams;
import org.jacoco.core.internal.analysis.ClassAnalyzer;
import org.jacoco.core.internal.analysis.ClassCoverageImpl;
import org.jacoco.core.internal.analysis.StringPool;
import org.jacoco.core.internal.data.CRC64;
import org.jacoco.core.internal.flow.ClassProbesAdapter;
import org.jacoco.core.internal.instr.InstrSupport;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * {@link Analyzer}实例处理一组Java类文件，并为它们计算覆盖率数据。
 * 对于每个类文件，结果都会报告给给定的{@link ICoverageVisitor}实例。
 * 此外，{@link Analyzer}需要一个{@link ExecutionDataStore}实例来保存要分析的类的执行数据。
 * {@link Analyzer}提供了几种方法来分析来自各种来源的类。
 */
public class Analyzer {

    private final ExecutionDataStore executionData;

    private final ICoverageVisitor coverageVisitor;

    private final StringPool stringPool;

    private final Map<String, String> diffMethod;


    /**
     * 创建向给定输出报告的新分析器。
     *
     * @param executionData 执行数据
     * @param coverageVisitor   将覆盖每个分析类的数据的输出实例
     */
    public Analyzer(final ExecutionDataStore executionData,
                    final ICoverageVisitor coverageVisitor) {
        this.executionData = executionData;
        this.coverageVisitor = coverageVisitor;
        this.stringPool = new StringPool();
        this.diffMethod = null;
    }

    /**
     * 创建向给定输出报告的新分析器。
     *
     * @param executionData 执行数据
     * @param coverageVisitor   将覆盖每个分析类的数据的输出实例
     * @param diffMethod        code diff 得到的差异方法
     */
    public Analyzer(final ExecutionDataStore executionData,
                    final ICoverageVisitor coverageVisitor,
                    final Map<String, String> diffMethod) {
        this.executionData = executionData;
        this.coverageVisitor = coverageVisitor;
        this.stringPool = new StringPool();
        this.diffMethod = diffMethod;
    }

    /**
     * 创建一个ASM类访问者进行分析
     *
     * @param classid 使用{@link CRC64}计算的类id
     * @param className 类的虚拟机名称
     *
     * @return 返回ASM访问者以写入类定义
     */
    private ClassVisitor createAnalyzingVisitor(final long classid, final String className) {
        final ExecutionData data = executionData.get(classid);
        final boolean[] probes;
        final boolean noMatch;
        if (data == null) {
            probes = null;
            noMatch = executionData.contains(className);

            // data = null
            // noMatch = false
            // className = com/dudu/common/configuration/example/SchoolService

        } else {
            probes = data.getProbes();
            noMatch = false;

            // data = ExecutionData[name=com/dudu/common/configuration/bean/MyProperties, id=757a079dccba7134]
            // noMatch = false
            // className = com/dudu/common/configuration/bean/MyProperties
        }

        final ClassCoverageImpl coverage = new ClassCoverageImpl(className, classid, noMatch);

        final ClassAnalyzer analyzer = new ClassAnalyzer(coverage, probes, stringPool) {
            @Override
            public void visitEnd() {
                super.visitEnd();
                coverageVisitor.visitCoverage(coverage);
            }
        };
        return new ClassProbesAdapter(analyzer, false, diffMethod);
    }

    private void analyzeClass(final byte[] source) {
        final long classId = CRC64.classId(source);

        // 为给定字节的类创建{@link ClassReader}实例，即使其版本不受ASM支持。
        // 截取函数支持    类阅读器
        final ClassReader reader = InstrSupport.classReaderFor(source);
        if ((reader.getAccess() & Opcodes.ACC_MODULE) != 0) {
            return;
        }
        if ((reader.getAccess() & Opcodes.ACC_SYNTHETIC) != 0) {
            return;
        }
        final ClassVisitor visitor = createAnalyzingVisitor(classId, reader.getClassName());

        // System.out.println(visitor);
        /*
         * visitor必须是此类的访问者。
         * parsingOptions用于分析此类的选项
         */
        reader.accept(visitor, 0);

        // ClassProbesAdapter adapter = (ClassProbesAdapter)visitor;
        //
        // Map<String, String> coveredMethod = adapter.getCv().getCoverage().getCoveredMethods();
        //
        // for (Map.Entry<String, String> entry : coveredMethod.entrySet()) {
        //
        //     // System.out.println(entry);
        //
        //     String key = entry.getKey();
        //
        //     String value = entry.getValue();
        //
        //     if (key != null) {
        //         coveredMethods.put(value, coveredMethod);
        //     }
        // }
        // System.out.println("-----------------------------------------");
        // System.out.println(visitor);
    }

    /**
     * Analyzes the class definition from a given in-memory buffer.
     *
     * @param buffer
     *            class definitions
     * @param location
     *            a location description used for exception messages
     * @throws IOException
     *             if the class can't be analyzed
     */
    public void analyzeClass(final byte[] buffer, final String location)
            throws IOException {
        try {
            analyzeClass(buffer);
        } catch (final RuntimeException cause) {
            throw analyzerError(location, cause);
        }
    }

    /**
     * Analyzes the class definition from a given input stream. The provided
     * {@link InputStream} is not closed by this method.
     *
     * @param input
     *            stream to read class definition from
     * @param location
     *            a location description used for exception messages
     * @throws IOException
     *             if the stream can't be read or the class can't be analyzed
     */
    public void analyzeClass(final InputStream input, final String location)
            throws IOException {
        final byte[] buffer;
        try {
            buffer = InputStreams.readFully(input);
        } catch (final IOException e) {
            throw analyzerError(location, e);
        }
        analyzeClass(buffer, location);
    }

    private IOException analyzerError(final String location,
                                      final Exception cause) {
        final IOException ex = new IOException(
                String.format("Error while analyzing %s.", location));
        ex.initCause(cause);
        return ex;
    }

    /**
     * Analyzes all classes found in the given input stream. The input stream
     * may either represent a single class file, a ZIP archive, a Pack200
     * archive or a gzip stream that is searched recursively for class files.
     * All other content types are ignored. The provided {@link InputStream} is
     * not closed by this method.
     *
     * @param input
     *            input data
     * @param location
     *            a location description used for exception messages
     * @return number of class files found
     * @throws IOException
     *             if the stream can't be read or a class can't be analyzed
     */
    public int analyzeAll(final InputStream input, final String location)
            throws IOException {
        final ContentTypeDetector detector;
        try {
            detector = new ContentTypeDetector(input);
        } catch (final IOException e) {
            throw analyzerError(location, e);
        }
        switch (detector.getType()) {
            case ContentTypeDetector.CLASSFILE:
                analyzeClass(detector.getInputStream(), location);
                return 1;
            case ContentTypeDetector.ZIPFILE:
                return analyzeZip(detector.getInputStream(), location);
            case ContentTypeDetector.GZFILE:
                return analyzeGzip(detector.getInputStream(), location);
            case ContentTypeDetector.PACK200FILE:
                return analyzePack200(detector.getInputStream(), location);
            default:
                return 0;
        }
    }

    /**
     * 分析给定文件或文件夹中包含的所有class文件。class文件和ZIP文件都被考虑。
     * 文件夹被递归搜索。
     * @param file
     *            file or folder to look for class files
     * @return number of class files found
     * @throws IOException
     *             if the file can't be read or a class can't be analyzed
     */
    public int analyzeAll(final File file) throws IOException {
        int count = 0;
        if (file.isDirectory()) {
            for (final File f : file.listFiles()) {
                count += analyzeAll(f);
            }
        } else {
            final InputStream in = new FileInputStream(file);
            try {
                count += analyzeAll(in, file.getPath());
            } finally {
                in.close();
            }
        }
        return count;
    }

    /**
     * Analyzes all classes from the given class path. Directories containing
     * class files as well as archive files are considered.
     *
     * @param path
     *            path definition
     * @param basedir
     *            optional base directory, if <code>null</code> the current
     *            working directory is used as the base for relative path
     *            entries
     * @return number of class files found
     * @throws IOException
     *             if a file can't be read or a class can't be analyzed
     */
    public int analyzeAll(final String path, final File basedir)
            throws IOException {
        int count = 0;
        final StringTokenizer st = new StringTokenizer(path,
                File.pathSeparator);
        while (st.hasMoreTokens()) {
            count += analyzeAll(new File(basedir, st.nextToken()));
        }
        return count;
    }

    private int analyzeZip(final InputStream input, final String location)
            throws IOException {
        final ZipInputStream zip = new ZipInputStream(input);
        ZipEntry entry;
        int count = 0;
        while ((entry = nextEntry(zip, location)) != null) {
            count += analyzeAll(zip, location + "@" + entry.getName());
        }
        return count;
    }

    private ZipEntry nextEntry(final ZipInputStream input,
                               final String location) throws IOException {
        try {
            return input.getNextEntry();
        } catch (final IOException e) {
            throw analyzerError(location, e);
        }
    }

    private int analyzeGzip(final InputStream input, final String location)
            throws IOException {
        GZIPInputStream gzipInputStream;
        try {
            gzipInputStream = new GZIPInputStream(input);
        } catch (final IOException e) {
            throw analyzerError(location, e);
        }
        return analyzeAll(gzipInputStream, location);
    }

    private int analyzePack200(final InputStream input, final String location)
            throws IOException {
        InputStream unpackedInput;
        try {
            unpackedInput = Pack200Streams.unpack(input);
        } catch (final IOException e) {
            throw analyzerError(location, e);
        }
        return analyzeAll(unpackedInput, location);
    }

}
