package ical;

import ical.database.ConnectionSingleton;
import ical.manager.Listener;
import ical.util.Config;

import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;

public class Bot {

    private Bot() throws LoginException {
        ConnectionSingleton.getInstance();
        new JDABuilder()
                .setToken(Config.get("JDA_TOKEN"))
                .addEventListeners(new Listener())
                .build();

    }

    public static void main(String[] args) throws LoginException {
        new Bot();
    }

}