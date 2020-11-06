package ical.core.runnable;

import ical.database.DAOFactory;
import ical.database.dao.LessonRemainingTimeDAO;
import ical.database.entity.LessonRemainingTime;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateRemainingTimeLessonMessage implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateRemainingTimeLessonMessage.class);

    private final JDA jda;

    public UpdateRemainingTimeLessonMessage(JDA jda) {
        this.jda = jda;
    }

    @Override
    public void run() {
        try {

            LessonRemainingTimeDAO lessonRemainingTimeDAO = (LessonRemainingTimeDAO) DAOFactory.getLesson_Remaining_Time();
            for (LessonRemainingTime lessonRemainingTime : lessonRemainingTimeDAO.findAll()) {
                TextChannel channel = jda.getTextChannelById(lessonRemainingTime.getId_channel());
                if (channel != null) {
                    channel.retrieveMessageById(lessonRemainingTime.getId_message()).queue((message) -> {
                        if (message.getEmbeds().size() == 1) {
                            EmbedBuilder em = new EmbedBuilder(message.getEmbeds().get(0));
                            if (lessonRemainingTime.getLesson().timeRemainingInSeconds() <= 0) {
                                lessonRemainingTimeDAO.delete(lessonRemainingTime);
                                channel.deleteMessageById(lessonRemainingTime.getId_message()).queue();
                            } else {
                                if (message.getEmbeds().get(0).getTitle().contains("Plusieurs")) {
                                    em.setTitle(
                                            "  Plusieurs cours à venir dans "
                                                    + lessonRemainingTime.getLesson().timeRemaining(),
                                            null
                                    );
                                } else
                                    em.setTitle(
                                            "  "
                                                    + lessonRemainingTime.getLesson().getName()
                                                    + "  dans "
                                                    + lessonRemainingTime.getLesson().timeRemaining(),
                                            null
                                    );
                                channel.editMessageById(lessonRemainingTime.getId_message(), em.build()).queue();
                            }
                        }
                    }, (failure) -> {
                        // if the retrieve request failed this will be called (also async)
                        if (failure instanceof ErrorResponseException) {
                            ErrorResponseException ex = (ErrorResponseException) failure;
                            if (ex.getErrorResponse() == ErrorResponse.UNKNOWN_MESSAGE) {
                                // this means the message doesn't exist
                                LOGGER.error("Message already deleted on discord");
                            } else {
                                LOGGER.error(ex.getErrorResponse().getMeaning());
                            }
                        } else {
                            LOGGER.error("Error while searching for message", failure.fillInStackTrace());
                        }
                        if (!lessonRemainingTimeDAO.delete(lessonRemainingTime))
                            LOGGER.error("Impossible to delete the record in the database");
                    });

                } else
                    LOGGER.error("Channel id is'nt valid !");
            }


        }catch(Exception e){
            LOGGER.error(e.getMessage(),e.fillInStackTrace());
        }

    }
}
