package cn.lemonprefect.tegrama.schedule;

import cn.lemonprefect.tegrama.Bot;
import cn.lemonprefect.tegrama.manager.BotManager;
import cn.lemonprefect.tegrama.manager.ConfigurationManager;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Schedule implements Job{
    protected static final Bot bot = BotManager.getBot();
    protected static final ConfigurationManager configurationManager = ConfigurationManager.getInstance();
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    public abstract void execute(JobExecutionContext context) throws JobExecutionException;
}
