package ical;

import ical.database.ConnectionSingleton;
import ical.manager.Listener;
import ical.util.Config;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;

/**
 * Bot class.
 * <br>The launcher class.
 *
 * @author Benoît Martel
 * @version 1.1
 * @since 1.0
 */
public class Bot {

    /**
     * Main method.
     * <br>This method initializes and launches the bot with the following {@link GatewayIntent}:
     * <br>
     *     <ul>
     *         <li>{@code GUILD_MESSAGES}</li>
     *         <li>{@code DIRECT_MESSAGES}</li>
     *         <li>{@code GUILD_MESSAGE_REACTIONS}</li>
     *     </ul>
     * <br>Use {@link Listener}
     * <br>The {@code JDA_TOKEN} is in the configuration file.
     * @param args no args required
     * @throws LoginException throw login exception
     */
    public static void main(String[] args) throws LoginException {

        ConnectionSingleton.getInstance();
        ArrayList<GatewayIntent> intents = new ArrayList<>();
        intents.add(GatewayIntent.GUILD_MESSAGES);
        intents.add(GatewayIntent.DIRECT_MESSAGES);
        intents.add(GatewayIntent.GUILD_MESSAGE_REACTIONS);
        JDABuilder.create(Config.get("JDA_TOKEN"),intents)
                .addEventListeners(new Listener())
                .disableCache(CacheFlag.ACTIVITY, CacheFlag.VOICE_STATE, CacheFlag.EMOTE, CacheFlag.CLIENT_STATUS)
                .build();

    }

}