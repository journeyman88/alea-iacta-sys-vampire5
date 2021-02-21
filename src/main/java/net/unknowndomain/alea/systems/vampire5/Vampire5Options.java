/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.unknowndomain.alea.systems.vampire5;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import net.unknowndomain.alea.systems.RpgSystemOptions;
import net.unknowndomain.alea.systems.annotations.RpgSystemData;
import net.unknowndomain.alea.systems.annotations.RpgSystemOption;


/**
 *
 * @author journeyman
 */
@RpgSystemData(bundleName = "net.unknowndomain.alea.systems.vampire5.RpgSystemBundle")
public class Vampire5Options extends RpgSystemOptions
{
    @RpgSystemOption(name = "number", shortcode = "n", description = "vampire5.options.number", argName = "diceNumber")
    private Integer diceNumber;
    @RpgSystemOption(name = "hunger", shortcode = "u", description = "vampire5.options.hunger", argName = "hungerValue")
    private Integer hungerValue;
    @RpgSystemOption(name = "reroll", shortcode = "r", description = "vampire5.options.reroll")
    private boolean willReroll;
    
    @Override
    public boolean isValid()
    {
        return !(isHelp());
    }

    public Integer getDiceNumber()
    {
        return diceNumber;
    }

    public Integer getHungerValue()
    {
        return hungerValue;
    }
    
    public boolean isWillReroll()
    {
        return willReroll;
    }

    public Collection<Vampire5Modifiers> getModifiers()
    {
        Set<Vampire5Modifiers> mods = new HashSet<>();
        if (isVerbose())
        {
            mods.add(Vampire5Modifiers.VERBOSE);
        }
        if (isWillReroll())
        {
            mods.add(Vampire5Modifiers.REROLL);
        }
        return mods;
    }
    
}