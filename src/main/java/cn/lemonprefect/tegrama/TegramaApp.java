package cn.lemonprefect.tegrama;

import cn.lemonprefect.tegrama.manager.BotManager;
import cn.lemonprefect.tegrama.manager.ScheduleManager;

public class TegramaApp{
    public TegramaApp(){
        System.out.println("""

                 _____                                    \s
                /__   \\___  __ _ _ __ __ _ _ __ ___   __ _\s
                  / /\\/ _ \\/ _` | '__/ _` | '_ ` _ \\ / _` |
                 / / |  __/ (_| | | | (_| | | | | | | (_| |
                 \\/   \\___|\\__, |_|  \\__,_|_| |_| |_|\\__,_|
                           |___/                          \s
                                """);

        BotManager.getBot();
        ScheduleManager.getScheduler();
    }
}
