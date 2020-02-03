package ical.core.runnable;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Icon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.GregorianCalendar;

public class UpdateAvatar implements Runnable {

    private JDA jda;

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateAvatar.class);

    private final Object waiter;

    public UpdateAvatar(JDA jda){
        this.jda = jda;
        this.waiter = null;
    }

    public UpdateAvatar(JDA jda, Object waiter){
        this.jda = jda;
        this.waiter = waiter;
    }

    @Override
    public void run() {
        LOGGER.info("Avatar updating..");
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(todayImage(), "png", baos );
            baos.flush();
            byte[] imageInByte = baos.toByteArray();
            baos.close();

            jda.getSelfUser().getManager().setAvatar(Icon.from(imageInByte)).queue(
                    v -> LOGGER.info("Success !"),
                    t -> LOGGER.info("Error")
            );

            if(waiter != null)
                synchronized (waiter){
                    waiter.notify();
                }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private BufferedImage todayImage() throws IOException, FontFormatException {

        int today = new GregorianCalendar().get(GregorianCalendar.DAY_OF_MONTH);
        String str = String.valueOf(today);

        BufferedImage base = ImageIO.read(UpdateAvatar.class.getResourceAsStream("/base.png"));
        Graphics2D g2 = (Graphics2D) base.getGraphics();

        InputStream is =  UpdateAvatar.class.getResourceAsStream("/font.ttf");
        Font font = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(370f);

        g2.setFont(font);
        FontMetrics metrics = g2.getFontMetrics(font);
        int width = metrics.stringWidth(str);
        int height = metrics.getHeight();

        g2.setColor(new Color(0x333333));

        int centerX = base.getWidth()/2;
        int centerY = base.getHeight()/2;

        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.drawString(str, centerX - width/2, centerY + height/2 - 10);
        return base;
    }

}
