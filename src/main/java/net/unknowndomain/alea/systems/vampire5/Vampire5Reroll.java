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
import net.unknowndomain.alea.dice.standard.D10;
import net.unknowndomain.alea.pools.DicePool;
import net.unknowndomain.alea.roll.GenericResult;
import net.unknowndomain.alea.roll.StatefulRoll;

/**
 *
 * @author journeyman
 */
public class Vampire5Reroll extends Vampire5Base implements StatefulRoll
{
    
    private Vampire5Results prev;
    
    public Vampire5Reroll(Vampire5Modifiers ... mod)
    {
        this(Arrays.asList(mod));
    }
    
    public Vampire5Reroll(Collection<Vampire5Modifiers> mod)
    {
        super(mod);
    }
    
    @Override
    public GenericResult getResult()
    {
        Vampire5Results results = prev;
        Vampire5Results results2 = null;
        if (mods.contains(Vampire5Modifiers.REROLL) && (results.getMiss() > 0) )
        {
            List<Integer> hun = new ArrayList<>(prev.getHungerResults().size());
            hun.addAll(prev.getHungerResults());
            int reroll = results.getMiss();
            if (reroll > 3)
            {
                reroll = 3;
            }
            DicePool<D10> rerollPool = new DicePool<>(D10.INSTANCE, reroll);
            List<Integer> res = new ArrayList<>();
            for (int i = 0; i < prev.getNormalResults().size() - reroll; i++)
            {
                res.add(prev.getNormalResults().get(i));
            }
            res.addAll(rerollPool.getResults());
            results2 = results;
            results = buildIncrements(res,hun);
        }
        results.setPrev(results2);
        results.setVerbose(mods.contains(Vampire5Modifiers.VERBOSE));
        return results;
    }
    
    @Override
    public boolean loadState(GenericResult state)
    {
        boolean retVal = false;
        if (state instanceof Vampire5Results)
        {
            prev = (Vampire5Results) state;
            retVal = true;
        }
        return retVal;
    }
    
}
