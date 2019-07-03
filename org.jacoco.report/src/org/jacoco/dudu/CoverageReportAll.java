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
        jacocoReport.setReportDirectory("report");

        JacocoReport jacocoReport2 = new JacocoReport();
        jacocoReport2.setTitle("第二次");
        jacocoReport2.setProjectDirectory("D:\\Soft_Package\\coverage\\demo-1.0");
        jacocoReport2.setExecutionDataFile("jacoco-client1.exec");
        jacocoReport2.setClassDirectory("target\\classes");
        jacocoReport2.setSourceDirectory("src\\main\\java");
        jacocoReport2.setReportDirectory("report");

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

        List<IReportVisitor> visitors = new ArrayList<>();

        List<ExecFileLoader> loaders = new ArrayList<>();

        for (JacocoReport jacocoReport : jacocoReportList) {
            // 初始化exec文件装载器
            ExecFileLoader execFileLoader = loadExecutionData(jacocoReport);

            bundleCoverageList.add(getAllIBundleCoverage(execFileLoader, jacocoReport));

            visitors.add(createReport(jacocoReport));

            loaders.add(execFileLoader);
        }

        visitEnd(jacocoReportList, bundleCoverageList, visitors, loaders, group);
    }

    /**
     * 初始化exec文件装载器
     * @param jacocoReport 业务对象
     */
    private ExecFileLoader loadExecutionData(JacocoReport jacocoReport) {

        try {
            File executionDataFile = new File(jacocoReport.getProjectDirectory(), jacocoReport.getExecutionDataFile());

            if (!executionDataFile.exists()) {
                throw new RuntimeException("覆盖率 exec 文件不存在");
            }
            return load(executionDataFile);

        } catch (IOException e) {
            throw new RuntimeException("覆盖率 exec 文件加载异常", e);
        }
    }

    private ExecFileLoader load(File executionDataFile) throws IOException {

        ExecFileLoader execFileLoader = new ExecFileLoader();

        execFileLoader.load(executionDataFile);

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
        try {
            return createCoverageBuilder(execFileLoader, jacocoReport);
        } catch (IOException e) {
            throw new RuntimeException("getAllIBundleCoverage 方法出错", e);
        }
    }

    private IBundleCoverage createCoverageBuilder(ExecFileLoader execFileLoader, JacocoReport jacocoReport) throws IOException {

        CoverageBuilder coverageBuilder = new CoverageBuilder();

        Analyzer analyzer = new Analyzer(execFileLoader.getExecutionDataStore(), coverageBuilder);

        analyzer.analyzeAll(new File(jacocoReport.getProjectDirectory(), jacocoReport.getClassDirectory()));

        return coverageBuilder.getBundle(jacocoReport.getTitle());
    }

    /**
     * 构建报告
     *
     * @param jacocoReport 业务对象
     * @return visitor 访问者
     */
    private IReportVisitor createReport(JacocoReport jacocoReport) {
        try {
            return createVisitor(jacocoReport);
        } catch (IOException e) {
            throw new RuntimeException("createReport 方法出错", e);
        }
    }

    private IReportVisitor createVisitor(JacocoReport jacocoReport) throws IOException {

        HTMLFormatter htmlFormatter = new HTMLFormatter();

        File ReportDirectory = new File(jacocoReport.getProjectDirectory(), jacocoReport.getReportDirectory());

        FileMultiReportOutput output = new FileMultiReportOutput(ReportDirectory);

        return htmlFormatter.createVisitor(output);
    }

    /**
     *
     * @param jacocoReportList 业务对象集合
     * @param coverageList 覆盖率包
     * @param visitors 访问者集合
     * @param name 分组名称
     */
    private void visitEnd(List<JacocoReport> jacocoReportList,
                          List<IBundleCoverage> coverageList,
                          List<IReportVisitor> visitors,
                          List<ExecFileLoader> loaders, String name) {
        try {
            /*
             * 由多个其他访问者组成的报告访问者。这可用于在一次运行中创建多个报告格式
             *
             * 构造器 将新访客委派给所有指定访客
             */
            MultiReportVisitor visitor = new MultiReportVisitor(visitors);

            for (ExecFileLoader loader : loaders) {
                visitor.visitInfo(loader.getSessionInfoStore().getInfos(), loader.getExecutionDataStore().getContents());
            }


            IReportGroupVisitor groupVisitor = visitor.visitGroup(name);

            for (int i = 0; i < coverageList.size(); i++) {

                // 源文件定位器，从文件系统给定的目录中选择源文件
                DirectorySourceFileLocator locator = new DirectorySourceFileLocator(new File(jacocoReportList.get(i).getProjectDirectory() ,jacocoReportList.get(i).getSourceDirectory()), "utf-8", 4);

                groupVisitor.visitBundle(coverageList.get(i), locator);
            }
            // 必须在所有报告数据发出后调用
            visitor.visitEnd();

        } catch (IOException e) {
            throw new RuntimeException("visitEnd 方法出错", e);
        }
    }
}
