/*
 * Copyright 2016 Benjamin Sautner
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.nimbits.server.process;

import com.google.common.base.Optional;
import com.google.common.collect.Range;
import com.nimbits.client.model.value.Value;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class StorageIOImpl implements StorageIO {


    private boolean containsMask(Value value, Optional<String> mask) {

        if (! mask.isPresent()) {
            return true;
        }
        else if (mask.isPresent() && mask.get().equals(value.getMetaData()) ) {

            return true;
        }
        else if (mask.isPresent() && ! StringUtils.isEmpty(value.getMetaData())) {
            try {
                Pattern p = Pattern.compile(mask.get());

                Matcher m = p.matcher(value.getMetaData());
                return m.find();
            }
            catch (PatternSyntaxException ex) {

                return false;
            }


        }

        return false;

    }

    @Override
    public List<Value> filterValues(List<Value> allvalues, Optional<Range<Date>> timespan, Optional<Range<Integer>> range, Optional<String> mask ) {
        List<Value> filtered = new ArrayList<>(allvalues.size());
        for (Value value : allvalues) {
            if (( !timespan.isPresent() || timespan.get().contains(value.getTimestamp())) && containsMask(value, mask)) {

                filtered.add(value);
            }
        }
        if (range.isPresent()) {
            if (range.get().lowerEndpoint() <= filtered.size()) {
                int end = range.get().upperEndpoint() <= filtered.size() ? range.get().upperEndpoint() : filtered.size();
                return filtered.subList(range.get().lowerEndpoint(), end);
            }
            else {
                return Collections.emptyList(); //range outside max values
            }
        }
        else {
            return filtered;
        }
    }
}
