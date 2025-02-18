/*
 * Copyright 2016 John Grosh (jagrosh).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jagrosh.jmusicbot;

// import com.example.springboot.FabBotBackendApplication;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.examples.command.AboutCommand;
import com.jagrosh.jdautilities.examples.command.PingCommand;
import com.jagrosh.jmusicbot.commands.admin.*;
import com.jagrosh.jmusicbot.commands.dj.*;
import com.jagrosh.jmusicbot.commands.general.SettingsCmd;
import com.jagrosh.jmusicbot.commands.general.SlashCommandListener;
import com.jagrosh.jmusicbot.commands.general.WeatherInformer;
import com.jagrosh.jmusicbot.commands.general.WormholeCmd;
import com.jagrosh.jmusicbot.commands.music.*;
import com.jagrosh.jmusicbot.commands.owner.*;
import com.jagrosh.jmusicbot.entities.Prompt;
import com.jagrosh.jmusicbot.gui.GUI;
import com.jagrosh.jmusicbot.settings.SettingsManager;
import com.jagrosh.jmusicbot.utils.OtherUtil;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.security.auth.login.LoginException;
import java.awt.*;
import java.util.Arrays;
/**
 *
 * @author John Grosh (jagrosh)
 */
public class JMusicBot {



    //Mongo NoSQL
    private static final String NOSQL_HOST = "mongodb+srv://Haris:Theoharis@db2mongo.ddnmcb9.mongodb.net/?retryWrites=true&w=majority"; // Vul hier je MongoDB gegevens in. Iets met mongodb+srv://......
    private static final String NOSQL_DATABASE = "fabBot"; // Vul hier je database gegevens in;


    public final static Logger LOG = LoggerFactory.getLogger(JMusicBot.class);
    public final static Permission[] RECOMMENDED_PERMS = {Permission.VOICE_DEAF_OTHERS, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY, Permission.MESSAGE_ADD_REACTION, Permission.MANAGE_SERVER, Permission.BAN_MEMBERS,
                                Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_ATTACH_FILES, Permission.MESSAGE_MANAGE, Permission.MESSAGE_EXT_EMOJI,
                                Permission.MANAGE_CHANNEL, Permission.VOICE_CONNECT, Permission.VOICE_SPEAK, Permission.NICKNAME_CHANGE, Permission.MANAGE_PERMISSIONS};

    
    public final static GatewayIntent[] INTENTS = {GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS,
            GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_BANS, GatewayIntent.GUILD_INVITES, GatewayIntent.GUILD_PRESENCES};
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        if(args.length > 0)
            switch(args[0].toLowerCase())
            {
                case "generate-config":
                    BotConfig.writeDefaultConfig();
                    return;
                default:
            }

     //   FabBotBackendApplication.main(args);
        startBot();
    }
    
    private static void startBot() {
        // create prompt to handle startup
        Prompt prompt = new Prompt("JMusicBot");

        // startup checks
        OtherUtil.checkVersion(prompt);
        OtherUtil.checkJavaVersion(prompt);
        
        // load config
        BotConfig config = new BotConfig(prompt);
        config.load();
        if(!config.isValid())
            return;
        LOG.info("Loaded config from " + config.getConfigLocation());



        // set up the listener
        EventWaiter waiter = new EventWaiter();
        SettingsManager settings = new SettingsManager();
        Bot bot = new Bot(waiter, config, settings);


        AboutCommand aboutCommand = new AboutCommand(Color.BLUE.brighter(),
                                "Sometimes I cry when I touch myself",
                                new String[]{"Holy fuck, I'm cumming!, Louis ahhhh, ahhhhhhhh"},
                                RECOMMENDED_PERMS);
        aboutCommand.setIsAuthor(false);
        aboutCommand.setReplacementCharacter("\uD83C\uDFB6"); // 🎶


//        CommandData commandData = new CommandData("savegif", "Save a GIF")
//                .addOption(OptionType.STRING, "file", "The GIF file", true);

        // set up the command client
        CommandClientBuilder cb = new CommandClientBuilder()
                .setPrefix(config.getPrefix())
                .setAlternativePrefix(config.getAltPrefix())
                .setOwnerId(Long.toString(config.getOwnerId()))
                .setEmojis(config.getSuccess(), config.getWarning(), config.getError())
                .setHelpWord(config.getHelp())
                .setLinkedCacheSize(200)
                .setGuildSettingsManager(settings)
                .addCommands(aboutCommand,
                        new PingCommand(),
                        new SettingsCmd(bot),

                        new NowplayingCmd(bot),
                        new PlayCmd(bot),
                        new PlayYtCmd(bot),
                        new PlaylistsCmd(bot),
                        new QueueCmd(bot),
                        new RemoveCmd(bot),
                        new SearchCmd(bot),
                        new SCSearchCmd(bot),
                        new ShuffleCmd(bot),
                        new SkipCmd(bot),
                        new YongleCmd(bot),
                        new BirdCmd(bot),
                        new PenisCmd(bot),
                        new AnnouncementCmd(bot),
                        new WeatherCmd(bot),
                        new DeafenCmd(bot),
                        new KissCmd(bot),
                        new HugCmd(bot),
                        new GoonCmd(bot),
                        new UpdateKissCmd(bot),
                        new UpdateColourCmd(bot),
                        new WormholeCmd(bot),

                        new ForceRemoveCmd(bot),
                        new ForceskipCmd(bot),
                        new MoveTrackCmd(bot),
                        new PauseCmd(bot),
                        new PlaynextCmd(bot),
                        new RepeatCmd(bot),
                        new SkiptoCmd(bot),
                        new StopCmd(bot),
                        new VolumeCmd(bot),
                        
                        new PrefixCmd(bot),
                        new SetdjCmd(bot),
                        new SkipratioCmd(bot),
                        new SettcCmd(bot),
                        new SetvcCmd(bot),
                        
                        new AutoplaylistCmd(bot),
                        new DebugCmd(bot),
                        new PlaylistCmd(bot),
                        new SetavatarCmd(bot),
                        new SetgameCmd(bot),
                        new SetnameCmd(bot),
                        new SetstatusCmd(bot),
                        new ShutdownCmd(bot)
                );
        if(config.useEval())
            cb.addCommand(new EvalCmd(bot));
        boolean nogame = false;
        if(config.getStatus()!=OnlineStatus.UNKNOWN)
            cb.setStatus(config.getStatus());
        if(config.getGame()==null)
            cb.useDefaultGame();
        else if(config.getGame().getName().equalsIgnoreCase("none"))
        {
            cb.setActivity(null);
            nogame = true;
        }
        else
            cb.setActivity(config.getGame());
        
        if(!prompt.isNoGUI())
        {
            try 
            {
                GUI gui = new GUI(bot);
                bot.setGUI(gui);
                gui.init();
            } 
            catch(Exception e) 
            {
                LOG.error("Could not start GUI. If you are "
                        + "running on a server or in a location where you cannot display a "
                        + "window, please run in nogui mode using the -Dnogui=true flag.");
            }
        }
        
        // attempt to log in and start
        try
        {
            JDA jda = JDABuilder.create(config.getToken(), Arrays.asList(INTENTS))
                    .enableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE)
                    .disableCache(CacheFlag.ACTIVITY, CacheFlag.CLIENT_STATUS, CacheFlag.EMOTE, CacheFlag.ONLINE_STATUS)
                    .setActivity(nogame ? null : Activity.playing("loading..."))
                    .setStatus(config.getStatus()==OnlineStatus.INVISIBLE || config.getStatus()==OnlineStatus.OFFLINE 
                            ? OnlineStatus.INVISIBLE : OnlineStatus.DO_NOT_DISTURB)
                    .addEventListeners(cb.build(), waiter, new Listener(bot))
                    .setBulkDeleteSplittingEnabled(true)
                    .build();
            bot.setJDA(jda);


            SlashCommandListener slashCommandListener = new SlashCommandListener();
            bot.getJDA().addEventListener(slashCommandListener);

            CommandData saveGifCommand = new CommandData("savegif", "Save a GIF attachment")
                    .addOption(OptionType.STRING, "file", "The GIF file to save", false);

            jda.upsertCommand(saveGifCommand).queue();


            Message getTrolledLol = new CustomHarisMessage();
            MessageReceivedEvent messageReceivedEvent = new MessageReceivedEvent(bot.getJDA(), 20, getTrolledLol);

            if (bot.getJDA().getTextChannelsByName("weather-channel", true).size() > 0){
                WeatherInformer weatherInformer = new WeatherInformer(bot);
                weatherInformer.execute(messageReceivedEvent, jda);
            }



        }
        catch (LoginException ex)
        {
            prompt.alert(Prompt.Level.ERROR, "JMusicBot", ex + "\nPlease make sure you are "
                    + "editing the correct config.txt file, and that you have used the "
                    + "correct token (not the 'secret'!)\nConfig Location: " + config.getConfigLocation());
            System.exit(1);
        }
        catch(IllegalArgumentException ex)
        {
            prompt.alert(Prompt.Level.ERROR, "JMusicBot", "Some aspect of the configuration is "
                    + "invalid: " + ex + "\nConfig Location: " + config.getConfigLocation());
            System.exit(1);
        }
    }


    public static String getNosqlHost() {
        return NOSQL_HOST;
    }

    public static String getNosqlDatabase() {
        return NOSQL_DATABASE;
    }

}
