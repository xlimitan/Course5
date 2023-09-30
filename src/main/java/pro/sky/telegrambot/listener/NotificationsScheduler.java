package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pro.sky.telegrambot.entity.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class NotificationsScheduler {
private static Logger logger = LoggerFactory.getLogger(NotificationsScheduler.class);

private final NotificationTaskRepository repository;
private final TelegramBot telegramBot;

    public NotificationsScheduler(NotificationTaskRepository repository, TelegramBot telegramBot) {
        this.repository = repository;
        this.telegramBot = telegramBot;
    }

    @Scheduled(fixedDelay = 5000L)
    public void scheduler(){
    logger.info("schedule");
        List<NotificationTask> tasks = repository.findAllByExecDateLessThan(LocalDateTime.now());
        for (NotificationTask task : tasks) {
            String text = task.getExecDate() + ": " + task.getMessage();
            SendResponse response = telegramBot.execute(new SendMessage(task.getChatId(), text));
            if (response.isOk()) {
                repository.delete(task);
            }
        }
    }
}
