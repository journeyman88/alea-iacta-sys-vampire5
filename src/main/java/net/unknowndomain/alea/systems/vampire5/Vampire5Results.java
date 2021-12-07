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
import java.util.Collections;
import java.util.List;
import net.unknowndomain.alea.messages.MsgBuilder;
import net.unknowndomain.alea.messages.MsgStyle;
import net.unknowndomain.alea.random.SingleResult;
import net.unknowndomain.alea.roll.LocalizedResult;

/**
 *
 * @author journeyman
 */
public class Vampire5Results extends LocalizedResult
{
    private final static String BUNDLE_NAME = "net.unknowndomain.alea.systems.vampire5.RpgSystemBundle";
    
    private final List<SingleResult<Integer>> normalResults;
    private final List<SingleResult<Integer>> hungerResults;
    private int hits = 0;
    private int miss = 0;
    private boolean critical = false;
    private boolean hungerOne = false;
    private boolean hungerTen = false;
    private List<SingleResult<Integer>> hitResults = new ArrayList<>();
    private Vampire5Results prev;
    
    public Vampire5Results(List<SingleResult<Integer>> results, List<SingleResult<Integer>> hunger)
    {
        List<SingleResult<Integer>> tmp = new ArrayList<>(results.size());
        tmp.addAll(results);
        this.normalResults = Collections.unmodifiableList(tmp);
        tmp = new ArrayList<>(hunger.size());
        tmp.addAll(hunger);
        this.hungerResults = Collections.unmodifiableList(tmp);
    }
    
    public void addCritical()
    {
        addHits(2, null);
        critical = true;
    }
    
    public void addMiss()
    {
        miss++;
    }
    
    public void addHit(SingleResult<Integer> dice)
    {
        addHits(1, dice);
    }
    
    private void addHits(int value, SingleResult<Integer> dice)
    {
        hits += value;
        if (dice != null)
        {
            hitResults.add(dice);
        }
    }

    public int getHits()
    {
        return hits;
    }

    public List<SingleResult<Integer>> getNormalResults()
    {
        return normalResults;
    }

    public List<SingleResult<Integer>> accessHitResults()
    {
        return hitResults;
    }

    public List<SingleResult<Integer>> getHungerResults()
    {
        return hungerResults;
    }

    public boolean isCritical()
    {
        return critical;
    }

    public boolean isHungerOne()
    {
        return hungerOne;
    }

    public void setHungerOne(boolean hungerOne)
    {
        this.hungerOne = hungerOne;
    }

    public boolean isHungerTen()
    {
        return hungerTen;
    }

    public void setHungerTen(boolean hungerTen)
    {
        this.hungerTen = hungerTen;
    }

    public int getMiss()
    {
        return miss;
    }

    @Override
    protected void formatResults(MsgBuilder messageBuilder, boolean verbose, int indentValue)
    {
        String indent = getIndent(indentValue);
        messageBuilder.append(indent).append(translate("vampire5.results.successes", getHits())).appendNewLine();
        if (isHungerTen() || isHungerOne() || isCritical())
        {
            messageBuilder.append(indent).append(translate("vampire5.results.specialDescriptors")).append("( ");
            if (isHungerTen() && isCritical())
            {
                messageBuilder.append(translate("vampire5.results.messyCritical"), MsgStyle.BOLD);
            }
            else if (isCritical())
            {
                messageBuilder.append(translate("vampire5.results.critical"), MsgStyle.BOLD);
            }
            if (isHungerOne())
            {
                if (isCritical())
                {
                    messageBuilder.append(" | ");
                }
                messageBuilder.append(translate("vampire5.results.bestialFailure"), MsgStyle.BOLD);
            }
            messageBuilder.append(" )").appendNewLine();
        }
        if (verbose)
        {
            messageBuilder.append(indent).append("Roll ID: ").append(getUuid()).appendNewLine();
            if (!getNormalResults().isEmpty())
            {
                messageBuilder.append(indent).append(translate("vampire5.results.normalResults")).append(" [ ");
                for (SingleResult<Integer> t : getNormalResults())
                {
                    messageBuilder.append("( ").append(t.getLabel()).append(" => ");
                    messageBuilder.append(t.getValue()).append(") ");
                }
                messageBuilder.append("]\n");
            }
            if (!getHungerResults().isEmpty())
            {
                messageBuilder.append(indent).append(translate("vampire5.results.hungerResults")).append(" [ ");
                for (SingleResult<Integer> t : getHungerResults())
                {
                    messageBuilder.append("( ").append(t.getLabel()).append(" => ");
                    messageBuilder.append(t.getValue()).append(") ");
                }
                messageBuilder.append("]\n");
            }
            if (prev != null)
            {
                messageBuilder.append(indent).append(translate("vampire5.results.prevResults")).append("{\n");
                prev.formatResults(messageBuilder, verbose, indentValue + 4);
                messageBuilder.append(indent).append("}\n");
            }
        }
    }

    public Vampire5Results getPrev()
    {
        return prev;
    }

    public void setPrev(Vampire5Results prev)
    {
        this.prev = prev;
    }

    @Override
    protected String getBundleName()
    {
        return BUNDLE_NAME;
    }

}
