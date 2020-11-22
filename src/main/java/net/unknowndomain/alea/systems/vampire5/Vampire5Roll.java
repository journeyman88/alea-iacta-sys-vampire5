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
import net.unknowndomain.alea.dice.standard.D10;
import net.unknowndomain.alea.pools.DicePool;
import net.unknowndomain.alea.roll.GenericResult;
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
    public GenericResult getResult()
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
        results.setPrev(results2);
        results.setVerbose(mods.contains(Modifiers.VERBOSE));
        return results;
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
