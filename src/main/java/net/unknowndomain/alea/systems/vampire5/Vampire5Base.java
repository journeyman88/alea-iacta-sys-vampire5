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

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.unknowndomain.alea.random.SingleResult;
import net.unknowndomain.alea.random.SingleResultComparator;
import net.unknowndomain.alea.roll.GenericRoll;

/**
 *
 * @author journeyman
 */
public abstract class Vampire5Base implements GenericRoll
{
    
    protected final Set<Vampire5Modifiers> mods;

    public Vampire5Base(Collection<Vampire5Modifiers> mod)
    {
        this.mods = new HashSet<>();
        this.mods.addAll(mod);
    }

    protected Vampire5Results buildIncrements(List<SingleResult<Integer>> res, List<SingleResult<Integer>> hun)
    {
        SingleResultComparator<Integer> comp = new SingleResultComparator<>(true);
        res.sort(comp);
        hun.sort(comp);
        Vampire5Results results = new Vampire5Results(res, hun);
        int tenCount = 0;
        int max = res.size();
        for (int i = 0; i < max; i++)
        {
            SingleResult<Integer> temp = res.remove(0);
            if (temp.getValue() >= 6)
            {
                results.addHit(temp);
                if (temp.getValue() >= 10)
                {
                    tenCount++;
                }
                if (tenCount > 1)
                {
                    results.addCritical();
                    tenCount = 0;
                }
            } else
            {
                results.addMiss();
            }
        }
        max = hun.size();
        for (int i = 0; i < max; i++)
        {
            SingleResult<Integer> temp = hun.remove(0);
            if (temp.getValue() >= 6)
            {
                results.addHit(temp);
            }
            if (temp.getValue() <= 1)
            {
                results.setHungerOne(true);
            }
            if (temp.getValue() >= 10)
            {
                results.setHungerTen(true);
                tenCount++;
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
