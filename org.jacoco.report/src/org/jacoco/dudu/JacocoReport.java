/**
 * FileName: jacocoReport
 * Author:   大橙子
 * Date:     2019/4/18 16:17
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package org.jacoco.dudu;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 * @author 大橙子
 * @date 2019/4/18
 * @since 1.0.0
 */
public class JacocoReport {

    /**
     * exec文件路径  与 this.executionDataFile 内容一致 repo存放, executionDataFile拿取
     */
    private String repo;
    /**
     * 服务器ip
     */
    private String address;
    /**
     * 服务器覆盖率端口
     */
    private int port;
    /**
     * project 项目目录
     */
    private String projectDirectory;
    /**
     * exec 报告导出目录
     */
    private String executionDataFile;
    /**
     * report 报告导出目录
     */
    private String reportDirectory;
    /**
     * java 文件目录
     */
    private String sourceDirectory;
    /**
     * class 文件目录
     */
    private String classDirectory;
    /**
     * html 报告标题
     */
    private String title;

    public String getRepo() {
        return repo;
    }

    public void setRepo(String repo) {
        this.repo = repo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getProjectDirectory() {
        return projectDirectory;
    }

    public void setProjectDirectory(String projectDirectory) {
        this.projectDirectory = projectDirectory;
    }

    public String getExecutionDataFile() {
        return executionDataFile;
    }

    public void setExecutionDataFile(String executionDataFile) {
        this.executionDataFile = executionDataFile;
    }

    public String getReportDirectory() {
        return reportDirectory;
    }

    public void setReportDirectory(String reportDirectory) {
        this.reportDirectory = reportDirectory;
    }

    public String getSourceDirectory() {
        return sourceDirectory;
    }

    public void setSourceDirectory(String sourceDirectory) {
        this.sourceDirectory = sourceDirectory;
    }

    public String getClassDirectory() {
        return classDirectory;
    }

    public void setClassDirectory(String classDirectory) {
        this.classDirectory = classDirectory;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
