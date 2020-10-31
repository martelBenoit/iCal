package ical.graphic;

import ical.database.entity.Lesson;
import ical.util.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Timetable {

    private final ArrayList<Lesson> lessons;
    private final Map<String,Color> colorAssociation;

    private final int corner_left_x = 80;
    private final int corner_left_y = 70;
    private final int width_rect = 270;
    private final int height_rect = 35;
    private final int titleBoxHeight = 40;
    private final int dayBoxHeight = 35;


    private final Font titleFont = new Font("DejaVu Sans",Font.BOLD,24);
    private final Font dayFont = new Font("DejaVu Sans",Font.BOLD,19);
    private final Font hoursFont = new Font("DejaVu Sans",Font.ITALIC,15);
    private final Font titleLessonFont = new Font("DejaVu Sans",Font.BOLD,20);
    private final Font descriptionLessonFont = new Font("DejaVu Sans",Font.PLAIN,14);
    private final Font roomNameFont = new Font("DejaVu Sans",Font.BOLD,18);

    private final Color colorTitle = new Color(0x333333);
    private final Color colorTextBlack = new Color( 58, 61, 62 );
    private final Color colorTextWhite = new Color( 220, 220, 220 );
    private final Color backgroundDS = new Color(163, 38, 14);

    private FontMetrics fontMetrics;

    private final Calendar calendar;

    private final BufferedImage laptop_white =
            ImageIO.read(Timetable.class.getResourceAsStream("/laptop_white.png"));
    private final BufferedImage laptop_black =
            ImageIO.read(Timetable.class.getResourceAsStream("/laptop_black.png"));


    private static final Logger LOGGER = LoggerFactory.getLogger(Timetable.class);

    public Timetable(ArrayList<Lesson> lessons) throws IOException {

        this.lessons = lessons;
        this.calendar = Calendar.getInstance();
        this.calendar.setTime(this.lessons.get(0).getStartDate());
        this.calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        colorAssociation = new HashMap<>();
        Map<String, java.util.List<Lesson>> lessonByName =
                lessons.stream().collect(Collectors.groupingBy(lesson -> clearLessonName(lesson.getName())));
        for(String lessonName : lessonByName.keySet()){
            Color value = Tools.getRandomColor(Tools.stringToSeed(clearLessonName(lessonName)),0);
            int i = 1;

            while(colorAssociation.containsValue(value)){
                value = Tools.getRandomColor(Tools.stringToSeed(clearLessonName(lessonName)),i);
                i++;
            }

            colorAssociation.put(lessonName,value);
        }


    }

    public InputStream generateTimetable() {

        try {

            BufferedImage base = ImageIO.read(Timetable.class.getResourceAsStream("/empty_planning_v3.png"));
            Graphics2D g2 = base.createGraphics();

            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            fillWeekNumberBox(g2);
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

    private void fillWeekNumberBox(Graphics2D g2){

        String weekOfYear = "SEMAINE N°" + calendar.get(Calendar.WEEK_OF_YEAR);

        g2.setFont(titleFont);
        g2.setColor(colorTitle);

        fontMetrics = g2.getFontMetrics(titleFont);
        int width = fontMetrics.stringWidth(weekOfYear);
        int height = fontMetrics.getHeight();


        g2.drawString(
                weekOfYear,
                (5*width_rect-width)/2 + corner_left_x,
                titleBoxHeight-titleBoxHeight/2+height/2-4
        );

    }

    private void fillDay(Graphics2D g2) {

        Calendar calendar = this.calendar;


        int offset_x = corner_left_x;

        for(int i=0; i<=4; i++){

            String displayName = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.FRANCE);
            displayName = displayName.substring(0,1).toUpperCase() + displayName.substring(1).toLowerCase();

            String day = Integer.toString(calendar.get(Calendar.DATE));
            if(day.length()==1)
                day = "0"+day;

            String month = Integer.toString(calendar.get(Calendar.MONTH)+1);
            if(month.length()==1)
                month = "0"+month;

            String year = Integer.toString(calendar.get(Calendar.YEAR));

            String displayDate =  displayName + " " + day+"/"+month+"/"+year;

            g2.setFont(dayFont);
            g2.setColor(colorTitle);

            fontMetrics = g2.getFontMetrics(dayFont);
            int widthText = fontMetrics.stringWidth(displayDate);
            int heightText = fontMetrics.getHeight();


            g2.drawString(
                    displayDate,
                    (width_rect-widthText)/2 + offset_x,
                    corner_left_y + dayBoxHeight/2 - heightText
            );

            calendar.add(Calendar.DATE,1);
            offset_x+=width_rect;
        }

    }

    private  void fillHour(Graphics2D g2){

        g2.setFont(hoursFont);
        g2.setColor(colorTitle);
        fontMetrics = g2.getFontMetrics(hoursFont);

        int offset_y = corner_left_y;

        int max_width = fontMetrics.stringWidth("00h00");
        int textHeight = fontMetrics.getHeight();

        for(int i=8; i<=18; i++){
            if(i < 10)
                g2.drawString("0"+i+"h00", corner_left_x-max_width-2, offset_y+textHeight);
            else
                g2.drawString(i+"h00", corner_left_x-max_width-2, offset_y+textHeight);
            if(i < 10)
                g2.drawString("0"+i+"h30", corner_left_x-max_width-2, offset_y+height_rect+textHeight);
            else
                g2.drawString(i+"h30", corner_left_x-max_width-2, offset_y+height_rect+textHeight);

            offset_y+=2*height_rect;
        }

    }

    private  void fillLesson(Graphics2D g2) {

        for(Lesson lesson : this.lessons){

            Calendar startDate = Calendar.getInstance();

            ArrayList<Lesson> manyLessons = new ArrayList<>();

            for(Lesson aLesson : this.lessons)
                if(aLesson.getStartDate().equals(lesson.getStartDate()))
                    manyLessons.add(aLesson);

            int widthText;
            int heightText;

            startDate.setTime(manyLessons.get(0).getStartDate());
            startDate.setFirstDayOfWeek(Calendar.MONDAY);

            Calendar startDay = Calendar.getInstance();
            startDay.setTime(manyLessons.get(0).getStartDate());
            startDay.set(Calendar.HOUR_OF_DAY,8);
            startDay.set(Calendar.MINUTE,0);
            int startTimeOffset =
                    (int) TimeUnit.MILLISECONDS.toMinutes(
                            startDate.getTimeInMillis() - startDay.getTimeInMillis()
                    ) * height_rect / 30;

            int pos = 0;
            int resizedWidthRect = width_rect/manyLessons.size();

            for(Lesson aLesson : manyLessons){

                int minutesDuration = aLesson.getMinutesDuration()*height_rect/30;

                int xPosRect = corner_left_x
                        + (width_rect*(startDate.get(Calendar.DAY_OF_WEEK)-2)+1)
                        + pos*resizedWidthRect;
                int yPosRect = corner_left_y + startTimeOffset+1;

                if(lesson.getName().contains("DS"))
                    g2.setColor(backgroundDS);
                else
                    g2.setColor(colorAssociation.get(clearLessonName(lesson.getName())));

                g2.fillRect(xPosRect,yPosRect,width_rect-1,minutesDuration-1);

                if(lesson.getName().toLowerCase().contains("distant")
                        || lesson.getName().toLowerCase().contains("distanc"))
                {
                    if(getAppropriateTextColor(g2.getColor())==colorTextBlack)
                        g2.drawImage(laptop_black.getScaledInstance(
                                                                40,
                                                                40,
                                                                Image.SCALE_SMOOTH
                                                            ),
                                xPosRect+width_rect-10-40,
                                yPosRect+minutesDuration-40,
                                null
                        );
                    else
                        g2.drawImage(laptop_white.getScaledInstance(
                                40,
                                40,Image.SCALE_SMOOTH),
                                xPosRect+width_rect-10-40,
                                yPosRect+minutesDuration-40,null
                        );
                }


                if(minutesDuration < 90) {
                    g2.setFont(titleLessonFont.deriveFont(Font.BOLD, 12));
                    fontMetrics = g2.getFontMetrics(titleLessonFont.deriveFont(Font.BOLD, 12));
                }
                else {
                    g2.setFont(titleLessonFont);
                    fontMetrics = g2.getFontMetrics(titleLessonFont);
                }
                Color textColor = getAppropriateTextColor(g2.getColor());
                g2.setColor(textColor);
                heightText = fontMetrics.getHeight();

                String displayNameLesson = clearLessonName(lesson.getName());

                String[] nameLesson =
                        splitText(displayNameLesson,resizedWidthRect / fontMetrics.stringWidth("0"));

                int yLastPos = 0;
                for(int i = 0; i < nameLesson.length; i++){
                    widthText = fontMetrics.stringWidth(nameLesson[i]);
                    yLastPos = yPosRect+heightText+4+heightText*i;
                    g2.drawString(nameLesson[i],(resizedWidthRect-widthText)/2+xPosRect,yLastPos);
                }

                if(minutesDuration < 90) {
                    g2.setFont(descriptionLessonFont.deriveFont(Font.BOLD, 10));
                    fontMetrics = g2.getFontMetrics(descriptionLessonFont.deriveFont(Font.BOLD, 10));
                }
                else {
                    g2.setFont(descriptionLessonFont);
                    fontMetrics = g2.getFontMetrics(descriptionLessonFont);
                }

                g2.setColor(textColor);

                heightText = fontMetrics.getHeight();

                String professor = lesson.getProfessor().getName()
                        .replaceAll("([A-Z])", " $1") + ".";

                widthText = fontMetrics.stringWidth(professor);
                yLastPos = heightText+yLastPos+4;
                g2.drawString(professor, (resizedWidthRect-1-widthText)/2+xPosRect, yLastPos);


                String room = clearRoomName(lesson.getRoom());
                String[] rooms = room.split(",");

                int yPosRectRoom = yPosRect+minutesDuration-1-heightText-10;
                if(rooms.length > 0 && !rooms[0].equals("")){
                    int heightLine = heightText;
                    heightText = heightLine* rooms.length;

                    if(minutesDuration < 90) {
                        g2.setFont(roomNameFont.deriveFont(Font.BOLD, 10));
                        fontMetrics = g2.getFontMetrics(roomNameFont.deriveFont(Font.BOLD, 10));
                    }
                    else {
                        g2.setFont(roomNameFont);
                        fontMetrics = g2.getFontMetrics(roomNameFont);
                    }

                    int maxWidthText = 0;
                    for(int i = 0; i < room.length(); i++)
                        if(fontMetrics.stringWidth(rooms[0]) > maxWidthText)
                            maxWidthText = fontMetrics.stringWidth(rooms[0]);

                    int xPosRectRoom = xPosRect+10;
                    yPosRectRoom = yPosRect+minutesDuration-1-heightText-10;
                    int widthRectRoom = (int)(maxWidthText+maxWidthText*0.2);

                    for(int i = 0; i < rooms.length; i++){
                        widthText = fontMetrics.stringWidth(rooms[i]);
                        g2.drawString(rooms[i],
                                xPosRectRoom+(widthRectRoom-widthText)/2,
                                yPosRectRoom+heightLine+heightLine*i);
                    }

                    g2.setStroke(new BasicStroke(2));
                    g2.drawRoundRect(
                            xPosRectRoom,
                            yPosRectRoom,
                            widthRectRoom,
                            (int)(heightText+heightText*0.2),
                            (int)(widthRectRoom*0.25),
                            (int)((heightText+heightText*0.2)*0.25)
                    );

                }


                if(lesson.getName().toLowerCase().contains("+mr"))
                {
                    g2.setFont(g2.getFont().deriveFont(Font.BOLD, g2.getFont().getSize()-4));
                    fontMetrics = g2.getFontMetrics(g2.getFont());

                    int heightRectMR = (int)(fontMetrics.getHeight()+fontMetrics.getHeight()*0.2);
                    int widthRectMR = (int)(fontMetrics.stringWidth("MR")+fontMetrics.stringWidth("MR")*0.2);
                    int xPosRectMR = xPosRect+10;
                    int yPosRectMR = (int)(yPosRectRoom-heightRectMR-heightRectMR*0.25);

                    int xMR =xPosRectMR+(widthRectMR-fontMetrics.stringWidth("MR"))/2;
                    int yMR =(int)(yPosRectMR+fontMetrics.getHeight()-fontMetrics.getHeight()*0.1);

                    g2.drawString("MR",xMR,yMR);
                    g2.drawRoundRect(
                            xPosRectMR,
                            yPosRectMR,
                            widthRectMR,
                            heightRectMR,
                            (int)(widthRectMR*0.25),
                            (int)(heightRectMR*0.25)
                    );

                }


                pos++;

            }


        }

    }

    private String[] splitText(String text, int maxLineLength){

        String[] tokens = text.split("\\s+");
        StringBuilder output = new StringBuilder(text.length());
        int lineLen = 0;
        for (int i = 0; i < tokens.length; i++) {
            String word = tokens[i];

            if (lineLen + (" " + word).length() > maxLineLength) {
                if (i > 0) {
                    output.append("\n");
                }
                lineLen = 0;
            }
            if (i < tokens.length - 1 && (lineLen + (word + " ").length() + tokens[i + 1].length() <=
                    maxLineLength)) {
                word += " ";
            }
            output.append(word);
            lineLen += word.length();
        }

        return output.toString().split("\n");
    }

    private Color getAppropriateTextColor(Color background){
        if((background.getRed()*0.3+background.getGreen()*0.59+ background.getBlue()*0.11) >= 128)
            return colorTextBlack;
        else
            return colorTextWhite;
    }

    private String clearLessonName(String lessonName){
        String res = lessonName;
        res = res.replace(" - (ING+MR)","");
        res = res.replace(" (ING+MR)","");
        res = res.replace(" - CM","");
        res = res.replace(" CM", "");
        res = res.replace(" - TD","");
        res = res.replace(" TD","");
        res = res.replace(" - TP","");
        res = res.replace(" TP","");
        res = res.replace(" - Présentiel","");
        res = res.replace(" - (Présentiel)","");
        res = res.replace(" (Présentiel)","");
        res = res.replace(" (présentiel)","");
        res = res.replace(" (Distanciel)","");
        res = res.replace(" - (Distant)","");
        res = res.replace(" (Distant)","");
        res = res.replace(" (distant)","");
        return res;
    }

    private String clearRoomName(String roomName){
        String res = roomName;
        res = res.replace("V-TO-ENSIBS-","");
        res = res.replace("V-TO-ENSIbs-","");
        res = res.replace("V-TO-ENSIbs - ","");
        res = res.replace("V-TO-ENSIbs -","");
        return res;
    }


}
