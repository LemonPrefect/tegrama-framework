package cn.lemonprefect.tegrama.manager;

import cn.lemonprefect.tegrama.annotation.Scheduled;
import cn.lemonprefect.tegrama.schedule.Schedule;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;

public class ScheduleManager{

    private static Scheduler scheduler;
    private static final Logger logger = LoggerFactory.getLogger(ScheduleManager.class);
    protected static final ConfigurationManager configurationManager = ConfigurationManager.getInstance();


    private ScheduleManager(){
    }

    public static synchronized Scheduler getScheduler(){
        if(scheduler == null){
            try{
                SchedulerFactory schedulerFactory = new StdSchedulerFactory();
                scheduler = schedulerFactory.getScheduler();
                setSchedule();
            }catch(SchedulerException | ParseException e){
                logger.error(e.getLocalizedMessage());
                e.printStackTrace();
            }
        }
        return scheduler;
    }

    private static void setSchedule() throws SchedulerException, ParseException{
        String reflectionPackageName = configurationManager.getProperty("tegrama.reflect.schedule");
        logger.info("Reflecting commands from {}", reflectionPackageName);
        Reflections reflections = new Reflections(reflectionPackageName);

        for(Class<? extends Schedule> clazz : reflections.getSubTypesOf(Schedule.class)){
            String cron = clazz.getAnnotation(Scheduled.class).cron();
            String group = clazz.getAnnotation(Scheduled.class).group();
            new CronExpression(cron);

            String jobIdentityName = String.format("%sJob", clazz.getSimpleName());
            String triggerIdentityName = String.format("%sTrigger", clazz.getSimpleName());
            JobDetail jobDetail = JobBuilder.newJob(clazz)
                    .withIdentity(jobIdentityName, group)
                    .build();
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(triggerIdentityName, group)
                    .withSchedule(CronScheduleBuilder.cronSchedule(cron))
                    .build();
            logger.info("{} - {}/{} - [{}]", group, jobIdentityName, triggerIdentityName, cron);
            scheduler.scheduleJob(jobDetail, trigger);
        }
        scheduler.start();
    }
}
