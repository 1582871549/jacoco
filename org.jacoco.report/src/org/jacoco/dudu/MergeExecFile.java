/**
 * FileName: MergeDump
 * Author:   大橙子
 * Date:     2019/5/23 15:25
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package org.jacoco.dudu;

import org.jacoco.core.tools.ExecFileLoader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 〈一句话功能简述〉<br> 
 * 合并list集合中提供的exec文件或目录下的exec文件合并。
 *
 * @author 大橙子
 * @date 2019/5/23
 * @since 1.0.0
 */
public class MergeExecFile {

    private final File destFile ;
    private final List<File> execList;
    private static final String EXEC_NAME = "jacoco-merge.exec";

    /**
     * 初始化参数
     *
     * @param destDirectory exec合并文件的生成目录
     */
    public MergeExecFile(String destDirectory) {
        this.destFile = new File(destDirectory, EXEC_NAME);
        this.execList = new ArrayList<>();
    }

    private void executeMerge(List<String> pathList) {

        // 空对象处理
        if (pathList == null || pathList.size() == 0 || Collections.EMPTY_LIST.equals(pathList)) {
            throw new RuntimeException("list can't be empty");
        }

        // 处理该集合中exec文件
        analyzePathList(pathList);

        ExecFileLoader loader = new ExecFileLoader();

        load(loader);

        save(loader);
    }

    /**
     * 删除非必须的exec文件
     */
    private void deleteFile() {
        for (File execFile : execList) {
            if (!EXEC_NAME.equals(execFile.getName())) {
                boolean flag = execFile.delete();
                System.out.println("文件是否删除成功 : " + flag);
            }
        }
    }

    /**
     * 加载所有exec文件到fileList中
     *
     * @param pathList exec文件的文件或文件夹
     */
    private void analyzePathList(List<String> pathList) {

        // 遍历exec文件的文件或文件夹
        for (String path : pathList) {

            File file = new File(path);

            // 目标不存在则跳过
            if (!file.exists()){
                continue;
            }

            if (file.isDirectory()) {

                List<String> list = new ArrayList<>(16);

                // 遍历该目录下所有文件及文件夹
                File[] files = file.listFiles();

                // 如果目录下不存在文件，则跳过
                if (files == null || files.length == 0) {
                    continue;
                }

                for (File f : files) {
                    list.add(f.getAbsolutePath());
                }

                analyzePathList(list);

            } else {

                // 将匹配文件加载到fileList中
                if (file.getName().endsWith(".exec")) {
                    execList.add(file);
                }
            }
        }
    }

    /**
     * 加载exec文件
     *
     * @param loader 加载器
     */
    private void loadExecFile(final ExecFileLoader loader) throws IOException {
        for (File execFile : execList) {
            loader.load(execFile);
        }
    }

    /**
     * 加载exec文件
     *
     * @param loader 加载器
     */
    private void load(final ExecFileLoader loader) {
        try {
            loadExecFile(loader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将当前内容保存到给定文件中。
     * 父目录是根据需要创建的。
     * 还获得了文件系统锁，以避免并发写访问。
     * 如果内容需要追加，则为true，否则文件将被覆盖
     *
     * @param loader 加载器
     */
    private void saveExecFile(final ExecFileLoader loader) throws IOException {
        loader.save(this.destFile, false);
    }

    /**
     * 合并文件
     *
     * @param loader 加载器
     */
    private void save(final ExecFileLoader loader) {

        if (loader.getExecutionDataStore().getContents().isEmpty()) {
            System.out.println("由于缺少执行数据文件，跳过Jacoco合并执行");
            return;
        }

        System.out.println("将合并的执行数据写入 " + this.destFile.getAbsolutePath());

        try {
            saveExecFile(loader);
        } catch (final IOException e) {
            System.out.println("无法访问 " + this.destFile.getAbsolutePath());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        List<String> pathList = new ArrayList<>();

        pathList.add("D:\\aaa");
        // pathList.add("D:\\aaa\\2\\jacoco-client1.exec");

        MergeExecFile merge = new MergeExecFile("D:\\aaa");

        merge.executeMerge(pathList);

        merge.deleteFile();
    }

}
