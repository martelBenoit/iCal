package ical.command.commands;

import ical.command.CommandContext;
import ical.database.entity.Lesson;
import ical.database.entity.Room;
import ical.manager.Listener;
import ical.manager.ScheduleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

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

        ArrayList<Room> rooms = getActualStatusRoom();
        StringBuilder build = new StringBuilder();
        for(Room r : rooms){
            if(r.isAvailable())
                build.append("\uD83D\uDFE2  ").append(r.getUsualName()).append("\n");
            else
                build.append("\uD83D\uDD34  ").append(r.getUsualName()).append("\n");
        }

        String msg = "Liste des salles et de leurs disponibilités : \n\n"+build;

        ctx.getChannel().sendMessage(msg).queue();
        resetStatus();

    }

    @Override
    public String getName() {
        return "room";
    }

    @Override
    public String getHelp() {
        return "Affiche le statut de chaque salle";
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
                list.get(k-1).setAvailable(false);
            }
        }

        return list;

    }

    private void resetStatus(){
        for(Room r : this.roomsList){
            r.setAvailable(true);
        }
    }
}
