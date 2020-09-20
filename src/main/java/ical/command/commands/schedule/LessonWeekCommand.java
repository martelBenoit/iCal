package ical.command.commands.schedule;

import ical.command.CommandContext;
import ical.command.commands.AbstractScheduleCommand;
import ical.database.entity.Lesson;
import ical.graphic.Timetable;
import ical.manager.ScheduleManager;
import ical.util.Config;
import net.dv8tion.jda.api.EmbedBuilder;


import java.awt.*;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.UUID;

public class LessonWeekCommand extends AbstractScheduleCommand {

    public LessonWeekCommand(ScheduleManager scheduleManager) {
        super(scheduleManager);
    }

    @Override
    public void handle(CommandContext ctx) {

        if(ctx.getArgs().size() == 0){

            final ArrayList<Lesson> lessons = scheduleManager.getSchedule(ctx.getGuild().getId()).getWeekLessons();
            if (lessons.size() > 0) {
                Timetable timetable = new Timetable(lessons);
                InputStream inputStream = timetable.generateTimetable();
                String nameImg = "WEEK_"+UUID.randomUUID()+".png";
                if(inputStream != null){
                    final EmbedBuilder eb = new EmbedBuilder();
                    eb.setTitle("Les cours pr\u00e9vus cette semaine  ", null);
                    eb.setColor(new Color(238358));
                    eb.setImage("attachment://"+nameImg);
                    eb.setTimestamp(scheduleManager.getSchedule(ctx.getGuild().getId()).getCreationDate());
                    eb.setFooter("ENT","https://www-ensibs.univ-ubs.fr/skins/ENSIBS/resources/img/logo.png");

                    ctx.getChannel().sendMessage(eb.build()).addFile(inputStream,nameImg).queue();
                }

            } else
                ctx.getChannel().sendMessage("Pas de cours prévu cette semaine, repose toi !").queue();
        }
        else if(ctx.getArgs().size() == 1){
            if(ctx.getArgs().get(0).equalsIgnoreCase("-next")) {

                final ArrayList<Lesson> lessons = scheduleManager.getSchedule(ctx.getGuild().getId()).getNextWeekLessons();
                if (lessons.size() > 0) {
                    Timetable timetable = new Timetable(lessons);
                    InputStream inputStream = timetable.generateTimetable();
                    String nameImg = "WEEK_" + UUID.randomUUID() + ".png";
                    if (inputStream != null) {
                        final EmbedBuilder eb = new EmbedBuilder();
                        eb.setTitle("Les cours pr\u00e9vus la semaine prochaine ", null);
                        eb.setColor(new Color(238358));
                        eb.setImage("attachment://" + nameImg);
                        eb.setTimestamp(scheduleManager.getSchedule(ctx.getGuild().getId()).getCreationDate());
                        eb.setFooter("ENT", "https://www-ensibs.univ-ubs.fr/skins/ENSIBS/resources/img/logo.png");

                        ctx.getChannel().sendMessage(eb.build()).addFile(inputStream, nameImg).queue();
                    }
                    else
                        ctx.getChannel().sendMessage("Erreur lors de la génération du planning, ré-essaye plus tard.").queue();
                }
                else
                    ctx.getChannel().sendMessage("Pas de cours prévu cette semaine, repose toi !").queue();
            }
            else{
                ctx.getChannel().sendMessage("Option invalide ! Consulte l'aide.").queue();
            }

        }
        else
            ctx.getChannel().sendMessage("Commande invalide ! Consulte l'aide.").queue();


    }

    @Override
    public String getName() {
        return "weekLessons";
    }

    @Override
    public String getHelp() {
        return "Affiche les cours qui auront lieu dans la semaine\n" +
                "Utilisation :\n\t `"+ Config.get("prefix")+getName()+" [-next]` : affiche les cours de la semaine prochaine";
    }

}
