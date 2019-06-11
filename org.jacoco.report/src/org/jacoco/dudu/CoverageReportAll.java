/**
 * FileName: a
 * Author:   大橙子
 * Date:     2019/4/19 15:39
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package org.jacoco.dudu;

import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.analysis.IBundleCoverage;
import org.jacoco.core.tools.ExecFileLoader;
import org.jacoco.report.*;
import org.jacoco.report.html.HTMLFormatter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 覆盖率包括
 *
 * @author 大橙子
 * @date 2019/4/19
 * @since 1.0.0
 */
public class CoverageReportAll {

    public static void main(String[] args) {

        List<JacocoReport> list = new ArrayList<>();

        JacocoReport jacocoReport = new JacocoReport();
        jacocoReport.setTitle("第一次");
        jacocoReport.setProjectDirectory("D:\\Soft_Package\\coverage\\demo-1.0");
        jacocoReport.setExecutionDataFile("jacoco-client.exec");
        jacocoReport.setClassDirectory("target\\classes");
        jacocoReport.setSourceDirectory("src\\main\\java");
        jacocoReport.setReportDirectory("report\\aa\\bb");

        JacocoReport jacocoReport2 = new JacocoReport();
        jacocoReport2.setTitle("第二次");
        jacocoReport2.setProjectDirectory("D:\\Soft_Package\\coverage\\demo-1.0");
        jacocoReport2.setExecutionDataFile("jacoco-client1.exec");
        jacocoReport2.setClassDirectory("target\\classes");
        jacocoReport2.setSourceDirectory("src\\main\\java");
        jacocoReport2.setReportDirectory("report\\aa\\bb");

        list.add(jacocoReport);
        list.add(jacocoReport2);

        CoverageReportAll coverageReport = new CoverageReportAll();

        coverageReport.create(list, "demo");
    }

    /**
     * 创建html报告
     * @param jacocoReportList 业务对象
     */
    public void create(List<JacocoReport> jacocoReportList, String group) {

        List<IBundleCoverage> bundleCoverageList = new ArrayList<>();
        // 覆盖率html文件
        List<IReportVisitor> visitors = new ArrayList<>();

        for (JacocoReport jacocoReport : jacocoReportList) {
            // 初始化exec文件装载器
            ExecFileLoader execFileLoader = loadExecutionData(jacocoReport);
            // 覆盖率包
            IBundleCoverage bundleCoverage = getAllIBundleCoverage(execFileLoader, jacocoReport);

            bundleCoverageList.add(bundleCoverage);

            IReportVisitor reportVisitor = createReport(execFileLoader, jacocoReport);

            visitors.add(reportVisitor);
        }

        visitEnd(jacocoReportList, bundleCoverageList, visitors, group);
    }

    /**
     * 初始化exec文件装载器
     * @param jacocoReport 业务对象
     */
    private ExecFileLoader loadExecutionData(JacocoReport jacocoReport) {
        ExecFileLoader execFileLoader = null;
        try {
            execFileLoader = new ExecFileLoader();
            File executionDataFile = new File(jacocoReport.getProjectDirectory(), jacocoReport.getExecutionDataFile());
            if (!executionDataFile.exists()) {
                throw new RuntimeException("覆盖率 exec 文件不存在");
            }
            execFileLoader.load(executionDataFile);
        } catch (IOException e) {
            throw new RuntimeException("覆盖率 exec 文件加载异常", e);
        }
        return execFileLoader;
    }

    /**
     * 根据源码和编译后的class文件, 生成覆盖率报告
     *
     * 在单个类文件夹上运行结构分析器来建立覆盖模型。
     * 如果您的类在jar文件中，过程将是相似的。
     * 通常，您会为报表中需要的每个类文件夹和每个jar创建一个包。
     * 如果您有多个捆绑包，则需要在报表中添加一个分组节点
     *
     * @param jacocoReport 业务对象
     * @return iBundleCoverage
     */
    private IBundleCoverage getAllIBundleCoverage(ExecFileLoader execFileLoader, JacocoReport jacocoReport) {

        // 覆盖率生成器
        CoverageBuilder coverageBuilder = new CoverageBuilder();
        /*
         * 初始化 覆盖率分析器
         * param1 返回包含所有已加载类数据的执行数据存储。  覆盖率记录数组
         * param2 覆盖率生成器
         */
        Analyzer analyzer = new Analyzer(execFileLoader.getExecutionDataStore(), coverageBuilder);
        try {
            /*
             * 分析该class文件或目录   ASM 字节码层面的分析和修改工具
             *
             * JaCoCo 对 exec 的解析主要是在 Analyzer 类的 analyzeClass(final byte[] source) 方法。
             * 这里面会调用 createAnalyzingVisitor 方法，生成一个用于解析的 ASM 类访问器，继续跟代码，
             * 发现对方法级别的探针计算逻辑是在 ClassProbesAdapter 类的 visitMethod 方法里面。
             * 所以我们只需要改造 visitMethod 方法，使它只对提取出的每个类的新增或变更方法做解析，
             * 非指定类和方法不做处理。
             *
             * 下面的analyzeAll方法底层调用了analyzeClass(final byte[] source)方法
             */
            analyzer.analyzeAll(new File(jacocoReport.getProjectDirectory(), jacocoReport.getClassDirectory()));

        } catch (IOException e) {
            throw new RuntimeException("getAllIBundleCoverage 方法出错", e);
        }
        // 设置覆盖率html包的标题名称
        return coverageBuilder.getBundle(jacocoReport.getTitle());
    }

    /**
     * 构建报告
     *
     * @param jacocoReport 业务对象
     * @return visitor 访问者
     */
    private IReportVisitor createReport(ExecFileLoader execFileLoader, JacocoReport jacocoReport) {

        HTMLFormatter htmlFormatter = new HTMLFormatter();
        // 将文件直接写入给定的目录
        FileMultiReportOutput output = null;
        IReportVisitor visitor = null;

        if (jacocoReport.isDiff()) {
            output = new FileMultiReportOutput(new File(jacocoReport.getProjectDirectory(), jacocoReport.getDiffReportDirectory()));
        } else {
            output = new FileMultiReportOutput(new File(jacocoReport.getProjectDirectory(), jacocoReport.getReportDirectory()));
        }
        try {
            /*
             * 创建一个新的访问者，向给定的输出写报告
             * output 是 exec 文件写入报告的目录
             * 返回访问者以向其发送报告数据
             */
            visitor = htmlFormatter.createVisitor(output);

            /*
             * 用全局信息初始化报告， 必须在调用任何其他方法之前调用
             *
             * infos 收集该报告执行数据的对象列表 按时间顺序排列
             * contents 本报告考虑的所有对象的集合
             */
            visitor.visitInfo(execFileLoader.getSessionInfoStore().getInfos(), execFileLoader.getExecutionDataStore().getContents());

        } catch (IOException e) {
            throw new RuntimeException("createReport 方法出错", e);
        }
        return visitor;
    }

    /**
     *
     * @param jacocoReportList 业务对象集合
     * @param coverageList 覆盖率包
     * @param visitors 访问者集合
     * @param name 分组名称
     */
    private void visitEnd(List<JacocoReport> jacocoReportList, List<IBundleCoverage> coverageList,
                          List<IReportVisitor> visitors, String name) {
        try {
            /*
             * 由多个其他访问者组成的报告访问者。这可用于在一次运行中创建多个报告格式
             *
             * 构造器 将新访客委派给所有指定访客
             */
            MultiReportVisitor visitor = new MultiReportVisitor(visitors);

            IReportGroupVisitor groupVisitor = visitor.visitGroup(name);

            for (int i = 0; i < coverageList.size(); i++) {

                // 源文件定位器，从文件系统给定的目录中选择源文件
                DirectorySourceFileLocator locator = new DirectorySourceFileLocator(new File(jacocoReportList.get(i).getSourceDirectory()), "utf-8", 4);

                /*
                 * 访问包 调用以将包添加到报告中
                 *
                 * bundleCoverage 要包含在报告中的包
                 * locator 此包的源码目录
                 */
                groupVisitor.visitBundle(coverageList.get(i), locator);
            }
            // 必须在所有报告数据发出后调用
            visitor.visitEnd();

        } catch (IOException e) {
            throw new RuntimeException("visitEnd 方法出错", e);
        }
    }
}
