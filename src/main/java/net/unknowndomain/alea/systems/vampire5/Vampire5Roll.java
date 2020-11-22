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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.unknowndomain.alea.dice.D10;
import net.unknowndomain.alea.messages.MsgBuilder;
import net.unknowndomain.alea.messages.MsgStyle;
import net.unknowndomain.alea.messages.ReturnMsg;
import net.unknowndomain.alea.pools.DicePool;
import net.unknowndomain.alea.roll.GenericRoll;

/**
 *
 * @author journeyman
 */
public class Vampire5Roll implements GenericRoll
{
    
    public enum Modifiers
    {
        VERBOSE,
        REROLL
    }
    
    private final DicePool<D10> dicePool;
    private final DicePool<D10> hungerPool;
    private final Set<Modifiers> mods;
    
    public Vampire5Roll(Integer dice, Modifiers ... mod)
    {
        this(dice, Arrays.asList(mod));
    }
    
    public Vampire5Roll(Integer trait, Integer limit, Modifiers ... mod)
    {
        this(trait, limit, Arrays.asList(mod));
    }
    
    public Vampire5Roll(Integer dice, Collection<Modifiers> mod)
    {
        this(dice, null, mod);
    }
    
    public Vampire5Roll(Integer dice, Integer hunger, Collection<Modifiers> mod)
    {
        this.mods = new HashSet<>();
        this.mods.addAll(mod);
        int ds, dh;
        if (hunger != null)
        {
            if (hunger >= dice)
            {
                dh = dice;
                ds = 0;
            }
            else
            {
                dh = hunger;
                ds = dice - hunger;
            }
        }
        else
        {
            ds = dice;
            dh = 0;
        }
        this.dicePool = new DicePool<>(D10.INSTANCE, ds);
        this.hungerPool = new DicePool<>(D10.INSTANCE, dh);
    }
    
    @Override
    public ReturnMsg getResult()
    {
        List<Integer> resultsPool = this.dicePool.getResults();
        List<Integer> hungerRes = this.hungerPool.getResults();
        List<Integer> res = new ArrayList<>();
        res.addAll(resultsPool);
        List<Integer> hun = new ArrayList<>();
        hun.addAll(hungerRes);
        Vampire5Results results = buildIncrements(res, hun);
        Vampire5Results results2 = null;
        if (mods.contains(Modifiers.REROLL) && (results.getMiss() > 0) )
        {
            int reroll = results.getMiss();
            if (reroll > 3)
            {
                reroll = 3;
            }
            DicePool<D10> rerollPool = new DicePool<>(D10.INSTANCE, reroll);
            boolean done = false;
            res = new ArrayList<>();
            for (int i = 0; i < resultsPool.size() - reroll; i++)
            {
                res.add(resultsPool.get(i));
            }
            res.addAll(rerollPool.getResults());
            results2 = results;
            results = buildIncrements(res,hungerRes);
        }
        return formatResults(results, results2);
    }
    
    private ReturnMsg formatResults(Vampire5Results results, Vampire5Results results2)
    {
        MsgBuilder mb = new MsgBuilder();
        
        mb.append("Successes: ").append(results.getHits()).appendNewLine();
        if (results.isHungerTen() || results.isHungerOne() || results.isCritical())
        {
            mb.append("Special descriptors: ( ");
            if (results.isHungerTen() && results.isCritical())
            {
                mb.append("Messy Critical", MsgStyle.BOLD);
            }
            else if (results.isCritical())
            {
                mb.append("Critical Success", MsgStyle.BOLD);
            }
            if (results.isHungerOne())
            {
                if (results.isCritical())
                {
                    mb.append(" | ");
                }
                mb.append("Bestial Failures", MsgStyle.BOLD);
            }
            mb.append(" )").appendNewLine();
        }
        if (mods.contains(Modifiers.VERBOSE))
        {
            
            if (!results.getNormalResults().isEmpty())
            {
                mb.append("Results: ").append(" [ ");
                for (Integer t : results.getNormalResults())
                {
                    mb.append(t).append(" ");
                }
                mb.append("]\n");
            }
            if (!results.getHungerResults().isEmpty())
            {
                
                mb.append("Hunger: ").append(" [ ");
                for (Integer t : results.getHungerResults())
                {
                    mb.append(t).append(" ");
                }
                mb.append("]\n");
            }
            if (results2 != null)
            {
                mb.append("Prev : {\n");
                mb.append("    Successes: ").append(results2.getHits()).appendNewLine();
                if (results2.isHungerTen() || results2.isHungerOne() || results2.isCritical())
                {
                    mb.append("    Special descriptors: ( ");
                    if (results2.isHungerTen() && results2.isCritical())
                    {
                        mb.append("Messy Critical", MsgStyle.ITALIC);
                    }
                    else if (results2.isCritical())
                    {
                        mb.append("Critical Success", MsgStyle.ITALIC);
                    }
                    if (results2.isHungerOne())
                    {
                        if (results2.isCritical())
                        {
                            mb.append(" | ");
                        }
                        mb.append("Bestial Failures", MsgStyle.ITALIC);
                    }
                    mb.append(" )").appendNewLine();
                }
                if (!results2.getNormalResults().isEmpty())
                {
                    mb.append("    Results: ").append(" [ ");
                    for (Integer t : results2.getNormalResults())
                    {
                        mb.append(t).append(" ");
                    }
                    mb.append("]\n");
                }
                if (!results2.getHungerResults().isEmpty())
                {

                    mb.append("    Hunger: ").append(" [ ");
                    for (Integer t : results2.getHungerResults())
                    {
                        mb.append(t).append(" ");
                    }
                    mb.append("]\n");
                }
                mb.append("}\n");
            }
        }
        return mb.build();
    }
    
    private Vampire5Results buildIncrements(List<Integer> res, List<Integer> hun)
    {
        res.sort((Integer o1, Integer o2) ->
        {
            return -1 * o1.compareTo(o2);
        });
        hun.sort((Integer o1, Integer o2) ->
        {
            return -1 * o1.compareTo(o2);
        });
        Vampire5Results results = new Vampire5Results(res, hun);
        int tenCount = 0;
        int max = res.size();
        for (int i = 0; i < max; i++)
        {
            int temp = res.remove(0);
            if (temp >= 6)
            {
                results.addHit(temp);
                if (temp >= 10)
                {
                    tenCount ++;
                }
                if (tenCount > 1)
                {
                    results.addCritical();
                    tenCount = 0;
                }
            }
            else
            {
                results.addMiss();
            }
        }
        max = hun.size();
        for (int i = 0; i < max; i++)
        {
            int temp = hun.remove(0);
            if (temp >= 6)
            {
                results.addHit(temp);
            }
            if (temp <= 1)
            {
                results.setHungerOne(true);
            }
            if (temp >= 10)
            {
                results.setHungerTen(true);
                tenCount ++;
            }
            if (tenCount > 1)
            {
                results.addCritical();
                tenCount = 0;
            }
        }
        return results;
    }
}
