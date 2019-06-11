/**
 * FileName: ExecutionDataClient
 * Author:   大橙子
 * Date:     2019/4/10 10:27
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package org.jacoco.dudu;

import org.jacoco.core.data.ExecutionDataWriter;
import org.jacoco.core.runtime.RemoteControlReader;
import org.jacoco.core.runtime.RemoteControlWriter;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * 〈一句话功能简述〉<br>
 * <p>
 *     tcpClient 获取覆盖率代码
 * </p>
 *
 * @author 大橙子
 * @date 2019/4/10
 * @since 1.0.0
 */
public class ExecutionDataClient {

    /**
     * Starts the execution data request.
     *
     * @param args
     */
    public static void main(final String[] args) {

        ExecutionDataClient client = new ExecutionDataClient();

        String outPath = "D:\\Soft_Package\\coverage\\demo-1.0\\jacoco-client.exec";
        String address = "127.0.0.1";
        int port = 4399;

        String projectDirectory = "D:\\Soft_Package\\coverage\\demo-1.0";
        String executionDataFile = "jacoco-client.exec";
        String classesDirectory = "target\\classes";
        String sourceDirectory = "src\\main\\java";
        String reportDirectory = "report";

        // client.getExecFile(outPath, address, port);

        client.getReportFile(projectDirectory, executionDataFile, classesDirectory, sourceDirectory, reportDirectory);
    }

    private void getExecFile(String outPath, String address, int port) {


        try {

            final FileOutputStream localFile = new FileOutputStream(outPath);

            final ExecutionDataWriter localWriter = new ExecutionDataWriter(localFile);

            Socket socket = new Socket(InetAddress.getByName(address), port);

            boolean connected = socket.isConnected();

            if (connected) {
                System.out.println("socket 连接成功");
            } else {
                System.out.println("socket 未连接成功");
            }

            RemoteControlWriter writer = new RemoteControlWriter(socket.getOutputStream());
            RemoteControlReader reader = new RemoteControlReader(socket.getInputStream());

            reader.setSessionInfoVisitor(localWriter);
            reader.setExecutionDataVisitor(localWriter);

            writer.visitDumpCommand(true, true);

            if (!reader.read()) {
                System.out.println(" socket 连接异常 ");
            }
            socket.close();
            localFile.close();
        } catch (IOException e) {
            System.out.println(" exec 文件生成失败");
        }

    }

    public void getReportFile(String projectDirectory, String executionDataFile, String classesDirectory, String sourceDirectory, String reportDirectory){
        try {

            ReportGenerator generator = new ReportGenerator(projectDirectory, executionDataFile, classesDirectory, sourceDirectory, reportDirectory);

            generator.create();

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("获取覆盖率文件成功");
    }


}
