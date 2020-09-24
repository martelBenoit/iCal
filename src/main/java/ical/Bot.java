package ical;

import ical.database.ConnectionSingleton;
import ical.manager.Listener;
import ical.util.Config;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;

public class Bot {

    private Bot() throws LoginException {
        ConnectionSingleton.getInstance();
        ArrayList<GatewayIntent> intents = new ArrayList<>();
        intents.add(GatewayIntent.GUILD_MESSAGES);
        intents.add(GatewayIntent.DIRECT_MESSAGES);
        JDABuilder.create(Config.get("JDA_TOKEN"),intents)
                .addEventListeners(new Listener())
                .disableCache(CacheFlag.ACTIVITY, CacheFlag.VOICE_STATE, CacheFlag.EMOTE, CacheFlag.CLIENT_STATUS)
                .build();


    }

    public static void main(String[] args) throws LoginException {
        new Bot();
    }

}