package ical.command.commands;

import ical.command.CommandContext;
import ical.database.entity.Lesson;
import ical.database.entity.Room;
import ical.manager.Listener;
import ical.manager.ScheduleManager;
import ical.util.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.tools.jstat.ParserException;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class RoomCommand extends AbstractScheduleCommand {


    private static final Logger LOGGER = LoggerFactory.getLogger(Listener.class);

    ArrayList<Room> roomsList;

    public RoomCommand(ScheduleManager scheduleManager){
        super(scheduleManager);

        try{
            roomsList = readFile();
        }catch (IOException e){
            LOGGER.error(e.getMessage());
        }


    }

    @Override
    public void handle(CommandContext ctx) {

        ArrayList<Room> rooms = null;
        if(ctx.getArgs().size() == 2){
            if(ctx.getArgs().get(0).equalsIgnoreCase("-h")){
                try{
                    int hour = Integer.parseInt(ctx.getArgs().get(1));
                    if(hour >= 0)
                        rooms = getStatusRoomAt(hour);
                    else
                        ctx.getChannel().sendMessage("C'est pas un entier positif en paramètre, coquin !").queue();
                }
                catch (NumberFormatException e){
                    ctx.getChannel().sendMessage("C'est pas un entier positif en paramètre, coquin !").queue();
                }
            }
            else{
                ctx.getChannel().sendMessage("Paramètre inconnu pour cette commande !").queue();
            }

        }
        else{
            rooms = getActualStatusRoom();
        }

        if(rooms != null)
            Collections.sort(rooms);



        if(ctx.getArgs().size() == 1){
            if(ctx.getArgs().get(0).equalsIgnoreCase("-i")){
                if(rooms !=null){
                    StringBuilder build = new StringBuilder();
                    for(Room r : rooms){
                        if(r.isAvailable()){
                            build.append("\uD83D\uDFE2  ").append("**"+r.getUsualName()).append("**\n");

                        }
                            else
                            build.append("\uD83D\uDD34  ").append("**"+r.getUsualName()).append("** : ").append(r.getLesson().getName()).append(r.getLesson().getDescription()).append("\n");
                    }


                    final EmbedBuilder eb = new EmbedBuilder();
                    eb.setTitle("Liste des salles et de leurs disponibilités : ", null);
                    eb.setColor(new Color(0xA3A21C));

                    eb.addField("", build.toString(), false);

                    ctx.getChannel().sendMessage(eb.build()).queue();
                }
            }
            else{
                ctx.getChannel().sendMessage("Paramètre inconnu pour cette commande !").queue();
            }

        }
        else{
            if(rooms !=null){
                StringBuilder build = new StringBuilder();
                for(Room r : rooms){
                    if(r.isAvailable())
                        build.append("\uD83D\uDFE2  ").append(r.getUsualName()).append("\n");
                    else
                        build.append("\uD83D\uDD34  ").append(r.getUsualName()).append("\n");
                }

                String msg = "Liste des salles et de leurs disponibilités : \n\n"+build;


                ctx.getChannel().sendMessage(msg).queue();
            }
        }




        resetStatus();

    }

    @Override
    public String getName() {
        return "room";
    }

    @Override
    public String getHelp() {
        return "Affiche le statut de chaque salle\n" +
                "Utilisation :\n\t `"+ Config.get("prefix")+"room [-h {hour}]` : statut de chaqye salle dans h+ {hour}\n"+
                "\t `"+ Config.get("prefix")+"room [-i]` : détails sur les salles occupées actuellement";
    }

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
                System.out.println(l);
                list.get(k-1).setLesson(l);
                list.get(k-1).setAvailable(false);
            }
        }

        return list;

    }



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

    private void resetStatus(){
        for(Room r : this.roomsList){
            r.setAvailable(true);
            r.setLesson(null);
        }
    }
}
