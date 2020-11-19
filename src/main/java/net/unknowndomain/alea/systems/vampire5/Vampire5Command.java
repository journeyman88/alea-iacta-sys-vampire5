/*
 * Copyright 2020 Marco Bignami.
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
package net.unknowndomain.alea.systems.vampire5;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import net.unknowndomain.alea.AleaListener;
import net.unknowndomain.alea.command.HelpWrapper;
import net.unknowndomain.alea.systems.RpgSystemCommand;
import net.unknowndomain.alea.systems.RpgSystemDescriptor;
import net.unknowndomain.alea.roll.GenericRoll;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.javacord.api.entity.message.MessageBuilder;

/**
 *
 * @author journeyman
 */
public class Vampire5Command extends RpgSystemCommand
{
    private static final RpgSystemDescriptor DESC = new RpgSystemDescriptor("Vampire the Masquerade 5th Edition", "vt5", "vampire-5th");
    private static final String NUMBER_PARAM = "number";
    private static final String HUNGER_PARAM = "hunger";
    private static final String REROLL_PARAM = "reroll";
    
    private static final Options CMD_OPTIONS;
    
    static{
        CMD_OPTIONS = new Options();
        CMD_OPTIONS.addOption(
                Option.builder("n")
                        .longOpt(NUMBER_PARAM)
                        .desc("Number of dice in the pool")
                        .hasArg()
                        .required()
                        .argName("diceNumber")
                        .build()
        );
        CMD_OPTIONS.addOption(
                Option.builder("u")
                        .longOpt(HUNGER_PARAM)
                        .desc("Hunger value")
                        .hasArg()
                        .argName("hungerValue")
                        .build()
        );
        CMD_OPTIONS.addOption(
                Option.builder("r")
                        .longOpt(REROLL_PARAM)
                        .desc("Enable Will usage for reroll")
                        .build()
        );
        
        CMD_OPTIONS.addOption(
                Option.builder("h")
                        .longOpt( CMD_HELP )
                        .desc( "Print help")
                        .build()
        );
        CMD_OPTIONS.addOption(
                Option.builder("v")
                        .longOpt(CMD_VERBOSE)
                        .desc("Enable verbose output")
                        .build()
        );
    };
    
    public Vampire5Command()
    {
        
    }
    
    @Override
    public RpgSystemDescriptor getCommandDesc()
    {
        return DESC;
    }
    
    @Override
    public MessageBuilder execCommand(String cmdLine)
    {
        MessageBuilder retVal = new MessageBuilder();
        Matcher prefixMatcher = PREFIX.matcher(cmdLine);
        if (prefixMatcher.matches())
        {
            String params = prefixMatcher.group(CMD_PARAMS);
            if (params == null || params.isEmpty())
            {
                return HelpWrapper.printHelp(AleaListener.PREFIX + " " + prefixMatcher.group(CMD_NAME), CMD_OPTIONS, true);
            }
            try
            {
                CommandLineParser parser = new DefaultParser();
                CommandLine cmd = parser.parse(CMD_OPTIONS, params.split(" "));
                if (cmd.hasOption(CMD_HELP))
                {
                    return HelpWrapper.printHelp(AleaListener.PREFIX + " " + prefixMatcher.group(CMD_NAME), CMD_OPTIONS, true);
                }
                
                Set<Vampire5Roll.Modifiers> mods = new HashSet<>();
                
                if (cmd.hasOption(REROLL_PARAM))
                {
                    mods.add(Vampire5Roll.Modifiers.REROLL);
                }
                if (cmd.hasOption(CMD_VERBOSE))
                {
                    mods.add(Vampire5Roll.Modifiers.VERBOSE);
                }
                String n = cmd.getOptionValue(NUMBER_PARAM);
                GenericRoll roll;
                if (cmd.hasOption(HUNGER_PARAM))
                {
                    String l = cmd.getOptionValue(HUNGER_PARAM);
                    roll = new Vampire5Roll(Integer.parseInt(n), Integer.parseInt(l), mods);
                }
                else
                {
                    roll = new Vampire5Roll(Integer.parseInt(n), mods);
                }
                retVal = roll.getResult();
            } 
            catch (ParseException | NumberFormatException ex)
            {
                retVal = HelpWrapper.printHelp(AleaListener.PREFIX + " " + prefixMatcher.group(CMD_NAME), CMD_OPTIONS, true);
            }
        }
        return retVal;
    }
    
}
