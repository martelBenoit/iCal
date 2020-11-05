package ical.command.commands.schedule;

import ical.command.GuildCommandContext;
import ical.command.commands.AbstractScheduleCommand;
import ical.database.entity.Lesson;
import ical.database.entity.Room;
import ical.manager.ScheduleManager;
import ical.util.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * RoomCommand class.
 *
 * @author Benoît Martel
 * @version 1.0
 * @since 1.5
 */
public class RoomCommand extends AbstractScheduleCommand {

    /**
     * the logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RoomCommand.class);

    /**
     * the rooms list.
     */
    private ArrayList<Room> roomsList;

    /**
     * Default constructor.
     *
     * @param scheduleManager the schedule manager
     */
    public RoomCommand(ScheduleManager scheduleManager){
        super(scheduleManager);

        try{
            roomsList = readFile();
        }catch (IOException e){
            LOGGER.error(e.getMessage());
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handle(GuildCommandContext ctx) {


            ArrayList<Room> rooms = null;
            if (ctx.getArgs().size() == 2) {
                if (ctx.getArgs().get(0).equalsIgnoreCase("-h")) {
                    try {
                        int hour = Integer.parseInt(ctx.getArgs().get(1));
                        if (hour >= 0)
                            rooms = getStatusRoomAt(hour);
                        else
                            ctx.getChannel()
                                    .sendMessage("C'est pas un entier positif en paramètre, coquin !")
                                    .queue((message -> message.delete().queueAfter(5, TimeUnit.SECONDS)));
                    } catch (NumberFormatException e) {
                        ctx.getChannel()
                                .sendMessage("C'est pas un entier positif en paramètre, coquin !")
                                .queue((message -> message.delete().queueAfter(5, TimeUnit.SECONDS)));
                    }
                } else {
                    ctx.getChannel().sendMessage("Paramètre inconnu pour cette commande !")
                            .queue((message -> message.delete().queueAfter(5, TimeUnit.SECONDS)));
                }

            } else {
                rooms = getActualStatusRoom();
            }

            if (rooms != null)
                Collections.sort(rooms);


            if (ctx.getArgs().size() == 1) {
                if (ctx.getArgs().get(0).equalsIgnoreCase("-i")) {
                    if (rooms != null) {
                        StringBuilder build = new StringBuilder();
                        for (Room r : rooms) {
                            if (r.isAvailable()) {
                                build.append("\uD83D\uDFE2  ").append("**").append(r.getUsualName()).append("**\n");

                            } else
                                build.append("\uD83D\uDD34  ")
                                        .append("**")
                                        .append(r.getUsualName())
                                        .append("** : ")
                                        .append(r.getLesson().getName())
                                        .append(r.getLesson().getDescription())
                                        .append("\n");
                        }

                        final EmbedBuilder eb = new EmbedBuilder();
                        eb.setTitle("Liste des salles et de leurs disponibilités : ", null);
                        eb.setColor(new Color(0xA3A21C));

                        eb.addField("", build.toString(), false);

                        ctx.getChannel().sendMessage(eb.build()).queue();
                    }
                } else {
                    ctx.getChannel()
                            .sendMessage("Paramètre inconnu pour cette commande !")
                            .queue((message -> message.delete().queueAfter(5, TimeUnit.SECONDS)));
                }

            } else {
                if (rooms != null) {
                    StringBuilder build = new StringBuilder();
                    for (Room r : rooms) {
                        if (r.isAvailable())
                            build.append("\uD83D\uDFE2  ").append(r.getUsualName()).append("\n");
                        else
                            build.append("\uD83D\uDD34  ").append(r.getUsualName()).append("\n");
                    }

                    String msg = "Liste des salles et de leurs disponibilités : \n\n" + build;


                    ctx.getChannel().sendMessage(msg).queue();
                }
            }

            resetStatus();
        }



    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "room";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getHelp() {
        return "Affiche le statut de chaque salle\n"
                + "Utilisation :\n\t `"
                + Config.get("prefix")
                +"room [-h {hour}]` : statut de chaqye salle dans h+ {hour}\n"
                + "\t `"
                + Config.get("prefix")
                +"room [-i]` : détails sur les salles occupées actuellement";
    }

    /**
     * Read file contains rooms list.
     *
     * @return the rooms list
     * @throws IOException if cannot read the file
     */
    private ArrayList<Room> readFile() throws IOException {

        ArrayList<Room> ret = new ArrayList<>();
        BufferedReader br = new BufferedReader(new InputStreamReader(
                this.getClass().getResourceAsStream("/" + "room.txt"), StandardCharsets.UTF_8));
        String line;
        while ((line = br.readLine()) != null) {
            ret.add(new Room(line,true));
        }
        br.close();

        return ret;
    }

    /**
     * Reload the rooms availability status.
     *
     * @return the rooms list with the availability status.
     */
    private ArrayList<Room> getActualStatusRoom(){

        ArrayList<Lesson> lessons = scheduleManager.getRoomSchedule().getNowLesson();
        ArrayList<Room> list = roomsList;

        int k;
        boolean found;

        for(Lesson l : lessons){
            k=0;
            found = false;
            while(k < list.size() && !found){
                if(l.getRoom().equals(list.get(k).getName()))
                    found = true;
                k++;
            }
            if(found){
                list.get(k-1).setLesson(l);
                list.get(k-1).setAvailable(false);
            }
        }

        return list;

    }

    /**
     * Reload the rooms availability status in x hour(s).
     *
     * @param hour the number of hours for which we want to know the availability of rooms
     * @return the rooms list with the availability status.
     */
    private ArrayList<Room> getStatusRoomAt(int hour){

        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, hour);
        date = calendar.getTime();

        ArrayList<Lesson> lessons = scheduleManager.getRoomSchedule().getLessonsAt(date);
        ArrayList<Room> list = roomsList;

        int k;
        boolean found;

        for(Lesson l : lessons){
            k=0;
            found = false;
            while(k < list.size() && !found){
                if(l.getRoom().equals(list.get(k).getName()))
                    found = true;
                k++;
            }
            if(found){
                list.get(k-1).setLesson(l);
                list.get(k-1).setAvailable(false);
            }
        }

        return list;

    }

    /**
     * Reset rooms availability status.
     */
    private void resetStatus(){
        for(Room r : this.roomsList){
            r.setAvailable(true);
            r.setLesson(null);
        }
    }

}
