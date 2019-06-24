package org.jacoco.dudu;

import org.jacoco.core.analysis.*;
import org.jacoco.core.data.ExecutionData;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.data.SessionInfo;
import org.jacoco.core.internal.analysis.ClassCoverageImpl;
import org.jacoco.core.tools.ExecFileLoader;
import org.jacoco.report.DirectorySourceFileLocator;
import org.jacoco.report.FileMultiReportOutput;
import org.jacoco.report.IReportVisitor;
import org.jacoco.report.html.HTMLFormatter;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class CoverageReport {

    private final File projectDirectory = new File("D:\\Soft_Package\\coverage\\demo-1.0");
    private final File executionDataFile = new File(projectDirectory,"jacoco-client.exec");
    private final File classesDirectory = new File(projectDirectory,"target\\classes");
    private final File sourceDirectory = new File(projectDirectory,"src\\main\\java");
    private final File reportDirectory = new File(projectDirectory,"report");

    public static void main(String[] args) throws IOException {

        CoverageReport report = new CoverageReport();
        report.demo();
    }

    public void demo() throws IOException {

        // exec 加载器
        ExecFileLoader execFileLoader = new ExecFileLoader();

        // 初始化exec文件装载器
        execFileLoader.load(executionDataFile);

        // 数据存储
        ExecutionDataStore executionDataStore = execFileLoader.getExecutionDataStore();

        // 覆盖率生成器
        CoverageBuilder coverageBuilder = new CoverageBuilder();

        // code diff methods
        Map<String, String> diffMethod = new HashMap<>(16);

        diffMethod.put("configure", null);
        diffMethod.put("paly", null);
        diffMethod.put("listUser", null);

        // 初始化覆盖率分析器
        Analyzer analyzer = new Analyzer(executionDataStore, coverageBuilder, diffMethod);

        /*
         * 分析该class文件或目录
         * ASM 字节码层面的分析和修改工具
         * 根据源码和编译后的class文件, 在单个类文件夹上运行结构分析器来建立覆盖模型
         *
         * JaCoCo 对 exec 的解析主要是在 Analyzer 类的 analyzeClass(final byte[] source) 方法。
         * 这里面会调用 createAnalyzingVisitor 方法，生成一个用于解析的 ASM 类访问器，
         * 对方法级别的探针计算逻辑是在 ClassProbesAdapter 类的 visitMethod 方法里面。
         * 所以我们只需要改造 visitMethod 方法，使它只对提取出的每个类的新增或变更方法做解析，
         * 在它对每个类做解析的时候取出标记覆盖到的方法。
         */
        analyzer.analyzeAll(classesDirectory);

        Collection<IClassCoverage> classes = coverageBuilder.getClasses();

        // List<String> coveredMethods = new ArrayList<>(11);

        for (IClassCoverage aClass : classes) {

            ClassCoverageImpl classCoverage = (ClassCoverageImpl)aClass;

            // 打印计数器信息
            printCounterInfo(classCoverage);

            Collection<IMethodCoverage> methods = classCoverage.getMethods();       // 该类下的所有方法
            Map<String, String> coveredMethod = classCoverage.getCoveredMethods();  // 该类下的所有覆盖方法

            System.out.println("----------------------------------------------------------------------------className");
            System.out.println(classCoverage.getClassName());
            System.out.println("----------------------------------------------------------------------------coverageMethods");

            for (Map.Entry<String, String> entry : coveredMethod.entrySet()) {

                String key = entry.getKey();            // 方法名
                String value = entry.getValue();        // 类名

                if (key != null) {
                    System.out.println(value + "-" + key);
                    // coveredMethods.add(value + "-" + key);
                }
            }
            System.out.println();
        }

        /* **************************************           输出报告            *****************************************/

        System.out.println("=====================================================================");
        System.out.println();

        /*
         * 设置覆盖率html包的标题名称
         *
         * 在该对象初始化过程中会统计记录所有的指针计数器, 即该项目的全量计数器。
         */
        IBundleCoverage bundleCoverage = coverageBuilder.getBundle("title");

        // 打印计数器信息
        printCounterInfo(bundleCoverage);

        // html格式化程序
        HTMLFormatter htmlFormatter = new HTMLFormatter();

        // 写入指定目录
        FileMultiReportOutput output = new FileMultiReportOutput(reportDirectory);
        /*
         * 创建一个新的访问者，向给定的输出写报告
         * output 是 exec 文件写入报告的目录
         * 返回访问者以向其发送报告数据
         */
        IReportVisitor visitor = htmlFormatter.createVisitor(output);

        List<SessionInfo> infos = execFileLoader.getSessionInfoStore().getInfos();

        Collection<ExecutionData> contents = execFileLoader.getExecutionDataStore().getContents();

        /*
         * 用全局信息初始化报告， 必须在调用任何其他方法之前调用
         *
         * infos 收集该报告执行数据的对象列表 按时间顺序排列
         * contents 本报告考虑的所有对象的集合
         */
        visitor.visitInfo(infos, contents);

        // 源文件定位器，从文件系统给定的目录中选择源文件
        DirectorySourceFileLocator locator = new DirectorySourceFileLocator(sourceDirectory, "utf-8", 4);

        /*
         * 访问包 调用以将包添加到报告中
         *
         * bundleCoverage 要包含在报告中的包
         * locator 此包的源码目录
         */
        visitor.visitBundle(bundleCoverage, locator);

        // 发出结构信息结束的信号，以允许报表写出所有信息 必须在所有报告数据发出后调用
        visitor.visitEnd();

    }

    /**
     * 打印计数器信息
     *
     * @param node 拥有覆盖率信息的对象节点
     */
    private void printCounterInfo(ICoverageNode node) {

        System.out.println("----------------------------------------------------------------------------printCounterInfo");

        ICounter lineCounter = node.getLineCounter();                  // 行
        ICounter methodCounter = node.getMethodCounter();              // 方法
        ICounter classCounter = node.getClassCounter();                // 类
        ICounter branchCounter = node.getBranchCounter();              // 分支
        ICounter complexityCounter = node.getComplexityCounter();      // 圈
        ICounter instructionCounter = node.getInstructionCounter();    // 指令

        double lineRatio = lineCounter.getCoveredRatio();
        double methodRatio = methodCounter.getCoveredRatio();
        double classRatio = classCounter.getCoveredRatio();
        double branchRatio = branchCounter.getCoveredRatio();
        double complexityRatio = complexityCounter.getCoveredRatio();
        double instructionRatio = instructionCounter.getCoveredRatio();

        System.out.println(lineRatio);
        System.out.println(methodRatio);
        System.out.println(classRatio);
        System.out.println(branchRatio);
        System.out.println(complexityRatio);
        System.out.println(instructionRatio);
    }
}
