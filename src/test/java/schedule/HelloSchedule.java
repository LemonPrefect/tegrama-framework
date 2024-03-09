package schedule;

import cn.lemonprefect.tegrama.annotation.Scheduled;
import cn.lemonprefect.tegrama.schedule.Schedule;
import command.HelloCommand;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Scheduled(cron="*/10 * * * * ?")
@DisallowConcurrentExecution
public class HelloSchedule extends Schedule{
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException{
        try{
            (new HelloCommand()).hello();
        }catch(TelegramApiException e){
            logger.error(e.getLocalizedMessage());
            e.printStackTrace();
        }
    }
}
