package ical.graphic;

import ical.database.entity.Lesson;
import ical.util.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Timetable {

    private ArrayList<Lesson> lessons;

    private int corner_left_x = 80;
    private int corner_left_y = 65;
    private int width_rect = 200;
    private int height_rect = 30;

    private static final Logger LOGGER = LoggerFactory.getLogger(Timetable.class);

    public Timetable(ArrayList<Lesson> lessons){
        this.lessons = lessons;
    }


    public InputStream generateTimetable() {

        try {
            BufferedImage base = ImageIO.read(Timetable.class.getResourceAsStream("/empty_planning.png"));
            Graphics2D g2 = base.createGraphics();

            fillDay(g2);
            fillHour(g2);
            fillLesson(g2);

            g2.dispose();

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(base,"png", os);
            return new ByteArrayInputStream(os.toByteArray());


        } catch (Exception e) {
            LOGGER.error(e.getMessage(),e.fillInStackTrace());
            return null;
        }
    }

    private  void fillDay(Graphics2D g2) throws IOException, FontFormatException {

        Calendar actual = Calendar.getInstance();
        actual.setTime(this.lessons.get(0).getStartDate());
        actual.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        InputStream is =  Timetable.class.getResourceAsStream("/OpenSans-Regular.ttf");
        Font font = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(14f);

        g2.setFont(font);
        g2.setColor(new Color(0x333333));

        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int initial_x = 80;

        String weekOfYear = "SEMAINE N°" + actual.get(Calendar.WEEK_OF_YEAR);
        FontMetrics metrics = g2.getFontMetrics(font);
        int width = metrics.stringWidth(weekOfYear);
        int height = metrics.getHeight();

        g2.drawString(weekOfYear, (5*width_rect-width)/2 + initial_x, (35-height/2));



        for(int i=0; i<=4; i++){

            String displayName = actual.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.FRANCE);
            displayName = displayName.substring(0,1).toUpperCase() + displayName.substring(1).toLowerCase();
            String day = Integer.toString(actual.get(Calendar.DATE));
            String month = Integer.toString(actual.get(Calendar.MONTH)+1);
            if(month.length()==1)
                month = "0"+month;
            String year = Integer.toString(actual.get(Calendar.YEAR));

            String displayDate =  displayName + " " + day+"/"+month+"/"+year;
            metrics = g2.getFontMetrics(font);
            width = metrics.stringWidth(displayDate);
            height = metrics.getHeight();


            g2.drawString(displayDate, (width_rect-width)/2 + initial_x, corner_left_y - (height_rect-height));

            actual.add(Calendar.DATE,1);
            initial_x+=200;
        }

    }

    private  void fillHour(Graphics2D g2) throws IOException, FontFormatException {

        InputStream is =  Timetable.class.getResourceAsStream("/OpenSans-Regular.ttf");
        Font font = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(10f);

        g2.setFont(font);
        g2.setColor(new Color(0x333333));

        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int initial_y = 65;
        FontMetrics metrics = g2.getFontMetrics(font);
        int max_width = metrics.stringWidth("12h30");
        int height = metrics.getHeight();

        for(int i=8; i<=18; i++){
            if(i < 10)
                g2.drawString("0"+i+"h00", 80-max_width-2, initial_y+height);
            else
                g2.drawString(i+"h00", 80-max_width-2, initial_y+height);
            if(i < 10)
                g2.drawString("0"+i+"h30", 80-max_width-2, initial_y+30+height);
            else
                g2.drawString(i+"h30", 80-max_width-2, initial_y+30+height);

            initial_y+=60;
        }

    }

    private  void fillLesson(Graphics2D g2) throws IOException, FontFormatException {

        // Set Font
        InputStream is =  Timetable.class.getResourceAsStream("/OpenSans-Regular.ttf");
        Font font = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(12f);

        g2.setFont(font);

        Map<String, java.util.List<Lesson>> lessonByName = this.lessons.stream().collect(Collectors.groupingBy(Lesson::getName));

        Map<String,Color> colorAssociation = new HashMap<>();
        for(String lessonName : lessonByName.keySet()){
            colorAssociation.put(lessonName,Tools.getRandomColor());
        }

        for(Lesson lesson : this.lessons){


            ArrayList<Lesson> manyLessons = new ArrayList<>();
            int i = 0;
            while(i < this.lessons.size()){
                if(this.lessons.get(i).getStartDate().equals(lesson.getStartDate()))
                    manyLessons.add(this.lessons.get(i));
                i++;
            }

            Calendar startDate = Calendar.getInstance();

            if(manyLessons.size() > 1){

                startDate.setTime(manyLessons.get(0).getStartDate());
                startDate.setFirstDayOfWeek(Calendar.MONDAY);

                Calendar startDay = Calendar.getInstance();
                startDay.setTime(manyLessons.get(0).getStartDate());
                startDay.set(Calendar.HOUR_OF_DAY,8);
                startDay.set(Calendar.MINUTE,0);
                int start_position = (int) TimeUnit.MILLISECONDS.toMinutes(startDate.getTimeInMillis() - startDay.getTimeInMillis());

                int pos = 0;
                int width_rect_special = width_rect/manyLessons.size();
                for(Lesson aLesson : manyLessons){

                    int minutesDuration = aLesson.getMinutesDuration();

                    int pos_rect_x = corner_left_x + (200*(startDate.get(Calendar.DAY_OF_WEEK)-2)+1)+pos*width_rect_special;
                    int pos_rect_y = corner_left_y + start_position;

                    g2.setColor(colorAssociation.get(lesson.getName()));
                    g2.fillRect(pos_rect_x,pos_rect_y,width_rect_special-1,minutesDuration-1);

                    g2.setColor(Color.white);

                    FontMetrics metrics = g2.getFontMetrics(font);
                    String nameLesson;
                    if(aLesson.getName().length()>28)
                        nameLesson = aLesson.getName().substring(0,27) + "...";
                    else
                        nameLesson = aLesson.getName();
                    int width = metrics.stringWidth(nameLesson);

                    if(width > width_rect_special*0.8){
                        int j = 0;
                        width = 0;
                        while(width <  width_rect_special*0.8){
                            width = metrics.stringWidth(nameLesson.substring(0,j));
                            j++;
                        }
                        g2.drawString(nameLesson.substring(0,j-1), (width_rect_special-width)/2+pos_rect_x, pos_rect_y+metrics.getHeight()+4);
                        width = metrics.stringWidth(nameLesson.substring(j-1));
                        g2.drawString(nameLesson.substring(j-1), (width_rect_special-width)/2+pos_rect_x, pos_rect_y+metrics.getHeight()*2+4);

                    }
                    else
                        g2.drawString(lesson.getName(), (width_rect_special-1-width)/2+pos_rect_x, pos_rect_y+metrics.getHeight()+4);

                    String professor = lesson.getProfessor().getName()
                            .replaceAll("([A-Z])", " $1") + ".";

                    width = metrics.stringWidth(professor);
                    g2.drawString(professor, (width_rect_special-1-width)/2+pos_rect_x, pos_rect_y+metrics.getHeight()*3+8+8);

                    String hours = lesson.getStartTime() + " - " + lesson.getEndTime();
                    width = metrics.stringWidth(hours);

                    g2.drawString(hours, (width_rect_special-1-width)/2+pos_rect_x, pos_rect_y+metrics.getHeight()*4+8+8);

                    pos++;



                }



            }
            else{

                startDate.setTime(lesson.getStartDate());
                startDate.setFirstDayOfWeek(Calendar.MONDAY);

                Calendar startDay = Calendar.getInstance();
                startDay.setTime(lesson.getStartDate());
                startDay.set(Calendar.HOUR_OF_DAY,8);
                startDay.set(Calendar.MINUTE,0);
                int start_position = (int) TimeUnit.MILLISECONDS.toMinutes(startDate.getTimeInMillis() - startDay.getTimeInMillis());

                int minutesDuration = lesson.getMinutesDuration();


                int pos_rect_x = corner_left_x + 200*(startDate.get(Calendar.DAY_OF_WEEK)-2)+1;
                int pos_rect_y = corner_left_y + start_position;

                g2.setColor(colorAssociation.get(lesson.getName()));
                g2.fillRect(pos_rect_x,pos_rect_y,width_rect-1,minutesDuration-1);

                g2.setColor(Color.white);

                FontMetrics metrics = g2.getFontMetrics(font);
                String nameLesson;
                if(lesson.getName().length()>53)
                    nameLesson = lesson.getName().substring(0,52) + "...";
                else
                    nameLesson = lesson.getName();
                int width = metrics.stringWidth(nameLesson);

                if(width > width_rect*0.8){
                    int j = 0;
                    width = 0;
                    while(width <  width_rect*0.8){
                        width = metrics.stringWidth(nameLesson.substring(0,j));
                        j++;
                    }
                    g2.drawString(nameLesson.substring(0,j-1), (width_rect-width)/2+pos_rect_x, pos_rect_y+metrics.getHeight()+4);
                    width = metrics.stringWidth(nameLesson.substring(j-1));
                    g2.drawString(nameLesson.substring(j-1), (width_rect-width)/2+pos_rect_x, pos_rect_y+metrics.getHeight()*2+4);

                }
                else
                    g2.drawString(nameLesson, (width_rect-1-width)/2+pos_rect_x, pos_rect_y+metrics.getHeight()+4);

                String professor = lesson.getProfessor().getName()
                        .replaceAll("([A-Z])", " $1") + ".";

                width = metrics.stringWidth(professor);
                g2.drawString(professor, (width_rect-1-width)/2+pos_rect_x, pos_rect_y+metrics.getHeight()*3+8+8);

                String hours = lesson.getStartTime() + " - " + lesson.getEndTime();
                width = metrics.stringWidth(hours);

                g2.drawString(hours, (width_rect-1-width)/2+pos_rect_x, pos_rect_y+metrics.getHeight()*4+8+8);


                

            }




        }




    }



}
