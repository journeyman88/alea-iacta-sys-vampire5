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
import java.util.List;
import java.util.Locale;
import net.unknowndomain.alea.random.SingleResult;
import net.unknowndomain.alea.random.dice.DicePool;
import net.unknowndomain.alea.random.dice.bag.D10;
import net.unknowndomain.alea.roll.GenericResult;

/**
 *
 * @author journeyman
 */
public class Vampire5Roll extends Vampire5Base
{
    
    private final DicePool<D10> dicePool;
    private final DicePool<D10> hungerPool;
    
    public Vampire5Roll(Locale lang,Integer dice, Vampire5Modifiers ... mod)
    {
        this(lang, dice, Arrays.asList(mod));
    }
    
    public Vampire5Roll(Locale lang, Integer dice, Integer hunger, Vampire5Modifiers ... mod)
    {
        this(lang, dice, hunger, Arrays.asList(mod));
    }
    
    public Vampire5Roll(Locale lang, Integer dice, Collection<Vampire5Modifiers> mod)
    {
        this(lang, dice, null, mod);
    }
    
    public Vampire5Roll(Locale lang, Integer dice, Integer hunger, Collection<Vampire5Modifiers> mod)
    {
        super(lang, mod);
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
        List<SingleResult<Integer>> resultsPool = this.dicePool.getResults();
        List<SingleResult<Integer>> hungerRes = this.hungerPool.getResults();
        List<SingleResult<Integer>> res = new ArrayList<>();
        res.addAll(resultsPool);
        List<SingleResult<Integer>> hun = new ArrayList<>();
        hun.addAll(hungerRes);
        Vampire5Results results = buildIncrements(res, hun);
        Vampire5Results results2 = null;
//        if (mods.contains(Vampire5Modifiers.REROLL) && (results.getMiss() > 0) )
//        {
//            int reroll = results.getMiss();
//            if (reroll > 3)
//            {
//                reroll = 3;
//            }
//            DicePool<D10> rerollPool = new DicePool<>(D10.INSTANCE, reroll);
//            boolean done = false;
//            res = new ArrayList<>();
//            for (int i = 0; i < resultsPool.size() - reroll; i++)
//            {
//                res.add(resultsPool.get(i));
//            }
//            res.addAll(rerollPool.getResults());
//            results2 = results;
//            results = buildIncrements(res,hungerRes);
//        }
        results.setPrev(results2);
        results.setVerbose(mods.contains(Vampire5Modifiers.VERBOSE));
        return results;
    }
    
}
